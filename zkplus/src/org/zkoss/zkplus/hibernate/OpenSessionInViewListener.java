/* OpenSessionInViewListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  5 10:11:55     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.hibernate;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.util.logging.Log;

import org.hibernate.StaleObjectStateException;

import java.util.List;

/**
 * Listener to init and cleanup the hibernate session automatically, implement
 * the Hibernate's "Open Session In View" pattern without JTA support. This listener 
 * is used with {@link HibernateUtil}, or it will not work.
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate "OpenSessionInView" Listener&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.OpenSessionInViewListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author henrichen
 */
public class OpenSessionInViewListener implements ExecutionInit, ExecutionCleanup {
	private static final Log log = Log.lookup(OpenSessionInViewListener.class);

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) {
		if (parent == null) { //the root execution of a servlet request
			log.debug("Starting a database transaction: "+exec);
			HibernateUtil.currentSession().beginTransaction();
		}
	}
	
	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, List errs) {
		if (parent == null) { //the root execution of a servlet request
			try {
				if (errs == null || errs.isEmpty()) {
					// Commit and cleanup
					log.debug("Committing the database transaction: "+exec);
					HibernateUtil.currentSession().getTransaction().commit();
				} else {
					final Throwable ex = (Throwable) errs.get(0);
					if (ex instanceof StaleObjectStateException) {
						// default implementation does not do any optimistic concurrency 
						// control; it simply rollback the transaction.
						handleStaleObjectStateException(exec, (StaleObjectStateException)ex);
					} else {
						// default implementation log the stacktrace and then rollback
						// the transaction.
						handleOtherException(exec, ex);
					}
				}
			} finally {
				HibernateUtil.closeSession(); //always close it
			}
		}
	}
	
	/**
	 * <p>Default StaleObjectStateException handler. This implementation
	 * does not implement optimistic concurrency control! It simply rollback 
	 * the transaction.</p>
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
	protected void handleStaleObjectStateException(Execution exec, StaleObjectStateException ex) {
		log.error("This listener does not implement optimistic concurrency control!");
		rollback(exec, ex);
	}
	
	/**
	 * <p>Default other exception (other than StaleObjectStateException) handler. 
	 * This implementation simply rollback the transaction.</p>
	 * 
	 * <p>Application developer might want to extends this class and override 
	 * this method to do other things like compensate for any permanent changes 
	 * during the conversation, and finally restart business conversation... 
	 * what can be done here depends on the applications design.</p>
	 *
	 * @param exec the exection to clean up.
	 * @param ex the Throwable other than StaleObjectStateException being thrown (and not handled) during the execution
	 */			
	protected void handleOtherException(Execution exec, Throwable ex) {
		// Rollback only
		ex.printStackTrace();
		rollback(exec, ex);
	}

	/**
	 * rollback the current session.
	 *
	 * @param exec the exection to clean up.
	 * @param ex the StaleObjectStateException being thrown (and not handled) during the execution
	 */	
	private void rollback(Execution exec, Throwable ex) {
		try {
			if (HibernateUtil.currentSession().getTransaction().isActive()) {
				log.debug("Trying to rollback database transaction after exception:"+ex);
				HibernateUtil.currentSession().getTransaction().rollback();
			}
		} catch (Throwable rbEx) {
			log.error("Could not rollback transaction after exception! Original Exception:\n"+ex, rbEx);
		}
	}
}
