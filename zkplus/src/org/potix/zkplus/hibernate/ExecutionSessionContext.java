/* ExecutionSessionContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  4 18:12:15     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkplus.hibernate;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Executions;
import com.potix.util.logging.Log;

import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.cfg.Configuration;

import javax.transaction.Synchronization;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A {@link org.hibernate.context.CurrentSessionContext} impl which scopes the 
 * notion of current session by the current ZK {@link com.potix.zk.ui.Execution}.
 * Like the hibernate {@link org.hibernate.context.ThreadLocalSessionContext}
 * counterpart, Execution do not give us a nice hook to perform any type of 
 * cleanup making it questionable for this impl to actually generate Session 
 * instances.  In the interest of usability, it was decided to have this 
 * impl actually generate a session upon first request and then clean it up
 * after the {@link org.hibernate.Transaction} associated with that session
 * is committed/rolled-back.  In order for ensuring that happens, the sessions
 * generated here are unusable until after 
 * {@link org.hibernate.classic.Session#beginTransaction()}
 * has been called. If <tt>close()</tt> is called on a session managed by
 * this class, it will be automatically unbound.
 * <p/>
 * Additionally, the static {@link #bind} and {@link #unbind} methods are
 * provided to allow application code to explcitily control opening and
 * closing of these sessions.  This, with some from of interception,
 * is the preferred approach.  It also allows easy framework integration
 * and one possible approach for implementing long-sessions.
 * <p/>
 * The {@link #buildOrObtainSession}, {@link #isAutoCloseEnabled},
 * {@link #isAutoFlushEnabled}, {@link #getConnectionReleaseMode}, and
 * {@link #buildCleanupSynch} methods are all provided to allow easy
 * subclassing (for long- running session scenarios, for example).
 *
 * <p>20060904, Henri Chen: This imiplementation is written after the 
 * {@link org.hibernate.context.ThreadLocalSessionContext} implmentaion. This class
 * should be able to just subclass ThreadLocalSessionContext class. Unfortunately, 
 * the original ThreadLocal implementation of {@link #doBind} and {@link #doUnbind} 
 * are all private methods that no way to overried them. Therefore,
 * the whole class has to be reimplmented and the sessionMap changed to be stored
 * in Execution's attribute of ZK.</p>
 *
 * <p>This implementation is to be used with org.hibernate.transaction.JDBCTransactionFactory
 * but avoid the single thread limitation of the ThreadLocalSessionContext and thus 
 * workable with ZK's threading model. You should set the hibernate configuration
 * as dipicted below:
 * <pre><code>
 * hibernate.transaction.factory_class = org.hibernate.transaction.JDBCTransactionFactory
 * hibernate.current_session_context_class = com.potix.zkplus.hibernate.ExecutionSessionContext
 * </code><pre>
 * </p>
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @author <a href="mailto:henrichen@potix.com">Henri Chen</a>
 */
public class ExecutionSessionContext implements CurrentSessionContext {
	private static final String HIBERNATE_SESSION_MAP = "ZK_HIBERNATE_SESSION_MAP";
	private static final Log log = Log.lookup(ExecutionSessionContext.class);
	private static final Class[] SESS_PROXY_INTERFACES = new Class[] {
			org.hibernate.classic.Session.class,
	        org.hibernate.engine.SessionImplementor.class,
	        org.hibernate.jdbc.JDBCContext.Context.class,
	        org.hibernate.event.EventSource.class
	};

	protected final SessionFactoryImplementor factory;

	public ExecutionSessionContext(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

	public final Session currentSession() throws HibernateException {
		Session current = existingSession( factory );
		if (current == null) {
			current = buildOrObtainSession();
			// register a cleanup synch
			current.getTransaction().registerSynchronization( buildCleanupSynch() );
			// wrap the session in the transaction-protection proxy
			if ( needsWrapping( current ) ) {
				current = wrap( current );
			}
			// then bind it
			doBind( current, factory );
		}
		return current;
	}

	private boolean needsWrapping(Session session) {
		// try to make sure we don't wrap and already wrapped session
		return session != null
		       && ! Proxy.isProxyClass( session.getClass() )
		       || ( Proxy.getInvocationHandler( session ) != null
		       && ! ( Proxy.getInvocationHandler( session ) instanceof TransactionProtectionWrapper ) );
	}

	protected SessionFactoryImplementor getFactory() {
		return factory;
	}

	/**
	 * Strictly provided for subclassing purposes; specifically to allow long-session
	 * support.
	 * <p/>
	 * This implementation always just opens a new session.
	 *
	 * @return the built or (re)obtained session.
	 */
	protected Session buildOrObtainSession() {
		return factory.openSession(
				null,
		        isAutoFlushEnabled(),
		        isAutoCloseEnabled(),
		        getConnectionReleaseMode()
			);
	}

	protected CleanupSynch buildCleanupSynch() {
		return new CleanupSynch( factory );
	}

	/**
	 * Mainly for subclass usage.  This impl always returns true.
	 *
	 * @return Whether or not the the session should be closed by transaction completion.
	 */
	protected boolean isAutoCloseEnabled() {
		return true;
	}

	/**
	 * Mainly for subclass usage.  This impl always returns true.
	 *
	 * @return Whether or not the the session should be flushed prior transaction completion.
	 */
	protected boolean isAutoFlushEnabled() {
		return true;
	}

	/**
	 * Mainly for subclass usage.  This impl always returns after_transaction.
	 *
	 * @return The connection release mode for any built sessions.
	 */
	protected ConnectionReleaseMode getConnectionReleaseMode() {
		return factory.getSettings().getConnectionReleaseMode();
	}

	protected Session wrap(Session session) {
		TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper( session );
		Session wrapped = ( Session ) Proxy.newProxyInstance(
				Session.class.getClassLoader(),
		        SESS_PROXY_INTERFACES,
		        wrapper
			);
		// yick!  need this for proper serialization/deserialization handling...
		wrapper.setWrapped( wrapped );
		return wrapped;
	}

	/**
	 * Associates the given session with the current thread of execution.
	 *
	 * @param session The session to bind.
	 */
	public static void bind(org.hibernate.Session session) {
		SessionFactory factory = session.getSessionFactory();
		cleanupAnyOrphanedSession( factory );
		doBind( session, factory );
	}

	private static void cleanupAnyOrphanedSession(SessionFactory factory) {
		Session orphan = doUnbind( factory, false );
		if ( orphan != null ) {
			log.warning( "Already session bound on call to bind(); make sure you clean up your sessions!" );
			try {
				if ( orphan.getTransaction() != null && orphan.getTransaction().isActive() ) {
					try {
						orphan.getTransaction().rollback();
					}
					catch( Throwable t ) {
						log.debug( "Unable to rollback transaction for orphaned session", t );
					}
				}
				orphan.close();
			}
			catch( Throwable t ) {
				log.debug( "Unable to close orphaned session", t );
			}
		}
	}

	/**
	 * Unassociate a previously bound session from the current thread of execution.
	 *
	 * @return The session which was unbound.
	 */
	public static Session unbind(SessionFactory factory) {
		return doUnbind( factory, true );
	}
	
	private static Session existingSession(SessionFactory factory) {
		Map sessionMap = sessionMap();
		if ( sessionMap == null ) {
			return null;
		}
		else {
			return ( Session ) sessionMap.get( factory );
		}
	}

	protected static Map sessionMap() {
		return ( Map ) Executions.getCurrent().getAttribute(HIBERNATE_SESSION_MAP);
	}

	protected static void doBind(org.hibernate.Session session, SessionFactory factory) {
		Map sessionMap = sessionMap();
		if ( sessionMap == null ) {
			sessionMap = new HashMap();
			Executions.getCurrent().setAttribute(HIBERNATE_SESSION_MAP, sessionMap );
		}
		sessionMap.put( factory, session );
	}

	protected static Session doUnbind(SessionFactory factory, boolean releaseMapIfEmpty) {
		Map sessionMap = sessionMap();
		Session session = null;
		if ( sessionMap != null ) {
			session = ( Session ) sessionMap.remove( factory );
			if ( releaseMapIfEmpty && sessionMap.isEmpty() ) {
				Executions.getCurrent().removeAttribute(HIBERNATE_SESSION_MAP);
			}
		}
		return session;
	}

	/**
	 * JTA transaction synch used for cleanup of the internal session map.
	 */
	protected static class CleanupSynch implements Synchronization, Serializable {
		private final SessionFactory factory;

		public CleanupSynch(SessionFactory factory) {
			this.factory = factory;
		}

		public void beforeCompletion() {
		}

		public void afterCompletion(int i) {
			unbind( factory );
		}
	}

	private class TransactionProtectionWrapper implements InvocationHandler, Serializable {
		private final Session realSession;
		private Session wrappedSession;

		public TransactionProtectionWrapper(Session realSession) {
			this.realSession = realSession;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				// If close() is called, guarantee unbind()
				if ( "close".equals( method.getName()) ) {
					unbind( realSession.getSessionFactory() );
				}
				else if ( "toString".equals( method.getName() )
					     || "equals".equals( method.getName() )
					     || "hashCode".equals( method.getName() ) ) {
					// allow these to go through the the real session
				}
				else if ( !realSession.getTransaction().isActive() ) {
					// limit the methods available if no transaction is active
					if ( "beginTransaction".equals( method.getName() )
					     || "getTransaction".equals( method.getName() )
					     || "isTransactionInProgress".equals( method.getName() )
					     || "isOpen".equals( method.getName() )
					     || "setFlushMode".equals( method.getName() )
					     || "getSessionFactory".equals( method.getName() ) ) {
						log.finer( "allowing method [" + method.getName() + "] in non-transacted context" );
					}
					else if ( "reconnect".equals( method.getName() )
					          || "disconnect".equals( method.getName() ) ) {
						log.finer( "allowing deprecated method [" + method.getName() + "] in non-transacted context" );
					}
					else {
						throw new HibernateException( method.getName() + " is not valid without active transaction" );
					}
				}
				return method.invoke( realSession, args );
			}
			catch ( InvocationTargetException e ) {
				if ( e.getTargetException() instanceof RuntimeException ) {
					throw ( RuntimeException ) e.getTargetException();
				}
				else {
					throw e;
				}
			}
		}

		public void setWrapped(Session wrapped) {
			this.wrappedSession = wrapped;
		}


		// serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		private void writeObject(ObjectOutputStream oos) throws IOException {
			// if a ExecutionSessionContext-bound session happens to get
			// serialized, to be completely correct, we need to make sure
			// that unbinding of that session occurs.
			oos.defaultWriteObject();
			if ( existingSession( factory ) == wrappedSession ) {
				unbind( factory );
			}
		}

		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
			// on the inverse, it makes sense that if a ExecutionSessionContext-
			// bound session then gets deserialized to go ahead and re-bind it to
			// the ExecutionSessionContext session map.
			ois.defaultReadObject();
			realSession.getTransaction().registerSynchronization( buildCleanupSynch() );
			doBind( wrappedSession, factory );
		}
	}
}
