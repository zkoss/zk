/* HibernateSessionExecutionListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  5 10:11:55     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zkplus.hibernate;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.ExecutionInit;
import com.potix.zk.ui.util.ExecutionCleanup;
import com.potix.util.logging.Log;

import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;

/**
 * Listener to init and cleanup the hibernate session automatically, implement
 * the Hibernate "Open Session In View" pattern without JTA support. This listener 
 * is used with {@link HibernateUtil} and {@link ExecutionSessionContext}, or it 
 * will not work.
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate "Open Session In View" Session Lifecycle&lt;/description>
 *		&lt;listener-class>com.potix.zkplus.hibernate.HibernateSessionExecutionListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class HibernateSessionExecutionListener implements ExecutionInit, ExecutionCleanup {
	private static final Log log = Log.lookup(HibernateSessionExecutionListener.class);

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) {
		if (parent == null) { //the root execution of a servlet request
			log.debug("Starting a database transaction: "+exec);
			HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		}
	}
	
	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, Throwable ex) {
		if (parent == null) { //the root execution of a servlet request
			if (ex == null) {
				// Commit and cleanup
				log.debug("Committing the database transaction: "+exec);
				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
			} else if (ex instanceof StaleObjectStateException) {
				// default implementation does not do any optimistic concurrency 
				// control; it simply rollback the transaction and rethrow the exception
				handleException(exec, (StaleObjectStateException)ex);
			} else {
				// Rollback only
				ex.printStackTrace();
				rollback(exec, ex);
				// Let others handle it
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	
	/**
	 * <p>Default StaleObjectStateException handler. This implementation
	 * does not implement optimistic concurrency control! It simply rollback 
	 * the transaction and rethrow the exception.</p>
	 * 
	 * <p>Application developer might want to extends this class and override 
	 * this method to do other things like compensate for any permanent changes 
	 * during the conversation, and finally restart business conversation. 
	 * Or maybe give the user of the application a chance to merge some of his 
	 * work with fresh data... what can be done here depends on the applications 
	 * design.</p>
	 *
	 * @param exec the exection to clean up.
	 * @param ex the StaleObjectStateException being thrown (and not handled) during the execution
	 */			
	protected void handleException(Execution exec, StaleObjectStateException ex) {
		log.error("This listener does not implement optimistic concurrency control!");
		rollback(exec, ex);
		throw ex;
	}

	/**
	 * rollback the current session.
	 *
	 * @param exec the exection to clean up.
	 * @param ex the StaleObjectStateException being thrown (and not handled) during the execution
	 */	
	private void rollback(Execution exec, Throwable ex) {
		try {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			if (factory.getCurrentSession().getTransaction().isActive()) {
				log.debug("Trying to rollback database transaction after exception:"+exec);
				factory.getCurrentSession().getTransaction().rollback();
			}
		} catch (Throwable rbEx) {
			log.error("Could not rollback transaction after exception!", rbEx);
		}
	}		
}
