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

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
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
 * <p>Since ZK 3.0.1, if your hibernate configuration file name is not the default "hibernate.cfg.xml", you can 
 * specify it in WEB-INF/zk.xml. Just add following lines:
 * <pre><code>
 *	&lt;preference>
 *	&lt;name>HibernateUtil.config&lt;/name>
 *	&lt;value>YOUR-HIBERNATE-FILENAME&lt;/value>
 * </code></pre>
 * </p>
 
 * <p> Also notice that the zkplus.jar must be put under application's WEB-INF/lib because
 * the SessionFactory is stored as a class static member.
 *
 * @author henrichen
 */
public class HibernateUtil {
	/** A preference or a library property used to configure {@link HibernateUtil}.
	 * It first looks up {@link org.zkoss.zk.ui.util.Configuration#getPreference}, and then
	 * {@link Library#getProperty}. Ignored if none of them is specified.
	 */
	public static final String CONFIG = "HibernateUtil.config";
	private static final Log log = Log.lookup(HibernateUtil.class);
	
	private static SessionFactory _factory;

	/**
	 * Get the singleton hibernate Session Factory.
	 */
	public static SessionFactory getSessionFactory() {
		return initSessionFactory((WebApp)null);
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
	 * @param app web applicaton, given null will try to get it from current Execution.
	 * @since 3.0.1
	 */
	/* package */ static SessionFactory initSessionFactory(WebApp app) {
		if (_factory == null) {
			//read hibernate.config preference
			if (app == null) {
				final Execution exec = Executions.getCurrent();
				if (exec != null) {
					final Desktop desktop = exec.getDesktop();
					if (desktop != null) {
						app = desktop.getWebApp();
					}
				}
			}
			String resource = null;
			if (app != null) {
				resource = app.getConfiguration().getPreference(CONFIG, null);
			}
			if (resource == null)
				resource = Library.getProperty(CONFIG);
			return initSessionFactory(resource);
		}
		return _factory;
	}
	/*package*/ static SessionFactory initSessionFactory(String resource) {
		if (_factory == null) {
			try {
			    // Create the SessionFactory per JavaVM version and allow JDK 1.4 compatibility
				if (JVMs.isJava5()) {
				    _factory = java5Factory(resource);
				} else {
				  	_factory = java4Factory(resource);
				}
				log.info("Hibernate configuration file loaded: "+ (resource == null ? "hibernate.cfg.xml" : resource));
			} catch (Throwable ex) {
			    // Make sure you log the exception, as it might be swallowed
			    log.error("Initial SessionFactory creation failed." + ex);
			    throw new ExceptionInInitializerError(ex);
			}
		}
		return _factory;
	}

	//We have to put the instantiation of AnnotationConfiguration
	//in a separate method. Otherwise, it will be loaded even if
	//isJava5 is false
	/*private*/ static SessionFactory java5Factory(String resource) {
		return resource == null ? 
			new AnnotationConfiguration().configure().buildSessionFactory() :
			new AnnotationConfiguration().configure(resource).buildSessionFactory();
	}  
	/*private */ static SessionFactory java4Factory(String resource) {
		return resource == null ?
			new Configuration().configure().buildSessionFactory() :
			new Configuration().configure(resource).buildSessionFactory();
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
