/* HibernateUtil.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  4 17:18:21     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.hibernate;

import org.zkoss.util.logging.Log;
import org.zkoss.lang.JVMs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
/**
 * <p>Utitlity to access Hibernate Session. This implemenation works with the Hibernate's 
 * thread session context (version 3.1+). That is, you have to specified 
 * hibernate's configuration file "hibernate.cfg.xml" to as follows:</p>
 *
 * <pre><code>
 *  &lt;session-factory>
 *		...
 *		&lt;property name="current_session_context_class">thread&lt;/property>
 *  &lt;/session-factory>
 * </code><pre>
 *
 * <p> Also notice that the zkplus.jar must be put under application's WEB-INF/lib because
 * the SessionFactory is stored as a class static member.
 *
 * @author henrichen
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
	 * Used in {@link HibernateSessionFactoryListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static SessionFactory initSessionFactory() {
		if (_factory == null) {
			try {
			    // Create the SessionFactory per JavaVM version and allow JDK 1.4 compatibility
					if (JVMs.isJava5()) {
				    _factory = java5Factory();
				  } else {
				  	_factory = java4Factory();
				  }
			} catch (Throwable ex) {
			    // Make sure you log the exception, as it might be swallowed
			    log.error("Initial SessionFactory creation failed." + ex);
			    throw new ExceptionInInitializerError(ex);
			}
		}
		return _factory;
	}
	
	private static SessionFactory java5Factory() {
    return new AnnotationConfiguration().configure().buildSessionFactory();
  }
  
  private static SessionFactory java4Factory() {
    return new Configuration().configure().buildSessionFactory();
  }
	
	/**
	 * Used in {@link HibernateSessionFactoryListener} to init
	 * Hibernate SessionFactory.
	 */
	/* package */ static void cleanupSessionFactory() {
		if (_factory != null) {
			_factory.close(); // Free all resources
			_factory = null;
		}
	}
}
