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
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * Listener to init and cleanup the JPA entityManager automatically
 * 
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate "OpenEntityManagerInView" Listener&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.jpa.OpenEntityManagerInViewListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 * 
 * @author Jeff
 * @since 3.0.2
 */
public class OpenEntityManagerInViewListener implements ExecutionCleanup,
		ExecutionInit {
	private static final Log log = Log.lookup(OpenEntityManagerInViewListener.class);
	
	//-- ExecutionCleanup --//
	public void cleanup(Execution exec, Execution parent, List errs)throws Exception{ 
		if(parent == null){
			try
			{
				if (errs == null || errs.isEmpty()) {
					log.debug("JPA: Committing the database transaction: "+exec+" for entityManager:"+JpaUtil.getEntityManager());
					JpaUtil.getEntityManager().getTransaction().commit();
				}else
				{
					final Throwable ex = (Throwable) errs.get(0);
					handleException(exec,ex);
				}
			}finally{
				if(JpaUtil.getEntityManager().isOpen()){
					log.debug("JPA: close a database transaction: "+exec+" for entityManager:"+JpaUtil.getEntityManager());
					JpaUtil.getEntityManager().close();
				}else
					log.debug("JPA: the database transaction is not open: "+exec+" for entityManager:"+JpaUtil.getEntityManager());
			}
		}
	}

	//-- ExecutionInit --//
	public void init(Execution exec, Execution parent) throws Exception {
		
		if(parent==null){
			log.debug("JPA: Starting a database transaction: "+exec+" for entityManager:"+JpaUtil.getEntityManager());
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
	 * @param exec the exection to clean up.
	 * @param ex the Throwable which is not handled during the execution
	 */
	protected void handleException(Execution exec, Throwable ex) {
		// Rollback only
		ex.printStackTrace();
		rollback(exec, ex);
	}
	
	/**
	 * rollback the current entityManager.
	 *
	 * @param exec the exection to clean up.
	 * @param ex the Exception being thrown (and not handled) during the execution
	 */	
	private void rollback(Execution exec, Throwable ex) {
		try {
			if (JpaUtil.getEntityManager().getTransaction().isActive()) {
				log.debug("Trying to rollback database transaction after exception:"+ex);
				JpaUtil.getEntityManager().getTransaction().rollback();
			}
		} catch (Throwable rbEx) {
			log.error("Could not rollback transaction after exception! Original Exception:\n"+ex, rbEx);
		}
	}
	

}
