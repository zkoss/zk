/* HibernateUtil.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  4 17:18:21     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkplus.hibernate;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Executions;
import com.potix.util.logging.Log;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * <p>Utitlity to access Hibernate Session. This implemenation works with the Hibernate's 
 * thread session context (version 3.1+). That is, you have to specified 
 * hibernate's configuration file "hibernate.cfg.xml" to as follows:</p>
 *
 * <pre><code>
 *  <session-factory>
 *		...
 *		<property name="current_session_context_class">thread</property>
 *  </session-factory>
 * </code><pre>
 *
 * <p> Also notice that the zkplus.jar must be put under application's WEB-INF/lib because
 * the SessionFactory is stored as a class static member.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class HibernateUtil {
	private static final Log log = Log.lookup(HibernateUtil.class);
	
	private static SessionFactory _factory;

	/**
	 * Get the singleton hibernate Session Factory.
	 */
	public static SessionFactory getSessionFactory() {
		return initSessionFactory();
	}
	
	/**
	 * Wrapping HibernateUtil.getSessionFactory().getCurrentSession() into a simple API.
	 */
	public static Session currentSession() throws HibernateException {
		return getSessionFactory().getCurrentSession();
	}
	
	/**
	 * Wrapping HibernateUtil.getSessionFactory().getCurrentSession().close() into a simple API.
	 */
	public static void closeSession() throws HibernateException {
		currentSession().close();
	}
	
	/**
	 * Used in {@link HibernateSessionWebAppListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static SessionFactory initSessionFactory() {
		if (_factory == null) {
			try {
			    // Create the SessionFactory from hibernate.cfg.xml
			    _factory = new Configuration().configure().buildSessionFactory();
			} catch (Throwable ex) {
			    // Make sure you log the exception, as it might be swallowed
			    log.error("Initial SessionFactory creation failed." + ex);
			    throw new ExceptionInInitializerError(ex);
			}

		}
		return _factory;
	}
	
	/**
	 * Used in {@link HibernateSessionWebAppListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static void cleanupSessionFactory() {
		if (_factory != null) {
			_factory.close(); // Free all resources
			_factory = null;
		}
	}
}
