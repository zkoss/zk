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

import org.hibernate.SessionFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static final String HIBERNATE_SESSION_FACTORY = "ZK_HIBERNATE_SESSION_FACTORY";
	private static final Log log = Log.lookup(HibernateUtil.class);
	/**
	 * Get the singleton hibernate Session Factory of the WebApp. This method
	 * must be called within an ZK Execution.
	 */
	public static SessionFactory getSessionFactory() {
		return initSessionFactory(Executions.getCurrent().getDesktop().getWebApp());
	}
	
	/**
	 * Used in {@link HibernateSessionWebAppListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static SessionFactory initSessionFactory(WebApp app) {
		SessionFactory factory = (SessionFactory) app.getAttribute(HIBERNATE_SESSION_FACTORY);
		if (factory == null) {
			try {
			    // Create the SessionFactory from hibernate.cfg.xml
			    factory = new Configuration().configure().buildSessionFactory();
			} catch (Throwable ex) {
			    // Make sure you log the exception, as it might be swallowed
			    log.error("Initial SessionFactory creation failed." + ex);
			    throw new ExceptionInInitializerError(ex);
			}

			app.setAttribute(HIBERNATE_SESSION_FACTORY, factory);
		}
		return factory;
	}
	
	/**
	 * Used in {@link HibernateSessionWebAppListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static void cleanupSessionFactory(WebApp app) {
		SessionFactory factory = (SessionFactory) app.getAttribute(HIBERNATE_SESSION_FACTORY);
		if (factory != null) {
			factory.close(); // Free all resources
			app.removeAttribute(HIBERNATE_SESSION_FACTORY);
		}
	}
}
