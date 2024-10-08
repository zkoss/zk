/* OpenEntityManagerInViewListener.java

 {{IS_NOTE
 Purpose:

 Description:

 History:
 Mon Dec 17 2007, Created by jeffliu
 }}IS_NOTE

 Copyright (C) 2006 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 }}IS_RIGHT
 */

package org.zkoss.zkplus.jpa;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * Listener to init and cleanup the JPA entityManager automatically
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener&gt;
 *		&lt;description&gt;Hibernate "OpenEntityManagerInView" Listener&lt;/description&gt;
 *		&lt;listener-class&gt;org.zkoss.zkplus.jpa.OpenEntityManagerInViewListener&lt;/listener-class&gt;
 *	&lt;/listener&gt;
 * </code></pre>
 * </p>
 * <p>Applicable to EJB version 3.2.ga or later</p>
 * @author Jeff
 * @since 3.0.2
 */
public class OpenEntityManagerInViewListener implements ExecutionCleanup, ExecutionInit {
	private static final Logger log = LoggerFactory.getLogger(OpenEntityManagerInViewListener.class);

	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, List errs) throws Exception {
		if (parent == null) {
			try {
				if (errs == null || errs.isEmpty()) {
					if (log.isDebugEnabled()) {
						log.debug(
								"JPA: Committing the database transaction: {} for entityManager:{}",
								exec, JpaUtil.getEntityManager());
					}
					JpaUtil.getEntityManager().getTransaction().commit();
				} else {
					final Throwable ex = (Throwable) errs.get(0);
					handleException(exec, ex);
				}
			} finally {
				if (JpaUtil.getEntityManager().isOpen()) {
					if (log.isDebugEnabled()) {
						log.debug(
								"JPA: close a database transaction: {} for entityManager:{}",
								exec, JpaUtil.getEntityManager());
					}
					JpaUtil.getEntityManager().close();
				} else if (log.isDebugEnabled()) {
					log.debug(
							"JPA: the database transaction is not open: {} for entityManager:{}",
							exec, JpaUtil.getEntityManager());
				}
			}
		}
	}

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) throws Exception {

		if (parent == null) {
			if (log.isDebugEnabled()) {
				log.debug(
						"JPA: Starting a database transaction: {} for entityManager:{}",
						exec, JpaUtil.getEntityManager());
			}
			JpaUtil.getEntityManager().getTransaction().begin();
		}
	}

	/**
	 * <p>Default exception handler.
	 * This implementation simply rollback the transaction.</p>
	 *
	 * <p>Application developer might want to extends this class and override
	 * this method to do other things like compensate for any permanent changes
	 * during the conversation, and finally restart business conversation...
	 * what can be done here depends on the applications design.</p>
	 *
	 * @param exec the execution to clean up.
	 * @param ex the Throwable which is not handled during the execution
	 */
	protected void handleException(Execution exec, Throwable ex) {
		// Rollback only
		log.error("Exception occurred during execution cleanup", ex);
		rollback(exec, ex);
	}

	/**
	 * rollback the current entityManager.
	 *
	 * @param exec the execution to clean up.
	 * @param ex the Exception being thrown (and not handled) during the execution
	 */
	private void rollback(Execution exec, Throwable ex) {
		try {
			if (JpaUtil.getEntityManager().getTransaction().isActive()) {
				if (log.isDebugEnabled()) {
					log.debug(
							"Trying to rollback database transaction after exception:{}",
							String.valueOf(ex));
				}
				JpaUtil.getEntityManager().getTransaction().rollback();
			}
		} catch (Throwable rbEx) {
			log.error(
					"Could not rollback transaction after exception! Original Exception:\n{}",
					ex, rbEx);
		}
	}

}
