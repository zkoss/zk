/* HibernateSessionFactoryListener.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  5 10:11:55     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.hibernate;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.WebAppInit;
import org.zkoss.zk.ui.util.WebAppCleanup;

import org.hibernate.SessionFactory;

/**
 * <p>Listener to init and cleanup the hibernate session factory automatically. 
 * This listener is used with {@link OpenSessionInViewListener} and 
 * {@link HibernateUtil}, or it will not work.</p>
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate SessionFactory Lifecycle&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.HibernateSessionFactoryListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * <p>Since ZK 3.0.1, if your hibernate configuration file name is not the default "hibernate.cfg.xml", you can 
 * specify it in WEB-INF/zk.xml. Just add following lines:</p>
 * <pre><code>
 *	&lt;preference>
 *	&lt;name>HibernateUtil.config&lt;/name>
 *	&lt;value>YOUR-HIBERNATE-CONFIG-FILENAME&lt;/value>
 * </code></pre>
 * </p>
 * <p>Sometimes, when using ZK with other backend framework, the Hibernate's SessionFactory 
 * might has to be created earlier. Therefore, since ZK 3.0.1, we have implemented this 
 * listener to be also a {@link javax.servlet.ServletContextListener} that you can 
 * choose to specify it in web.xml instead and make it initialized right when your applcation 
 * is deployed.</p>
 * <p>In WEB-INF/web.xml, add following lines:
 * <pre><code>
 *	&lt;context-param>
 *		&lt;param-name>HibernateUtil.config&lt;/param-name>
 *		&lt;param-value>YOUR-HIBERNATE-CONFIG-FILENAME&lt;/param-value>
 *	&lt;/context-param>
 *	&lt;listener>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.HibernateSessionFactoryListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * <p>Applicable to Hibernate version 3.2.ga or later</p>
 * @author henrichen
 */
public class HibernateSessionFactoryListener 
implements WebAppInit, WebAppCleanup,  ServletContextListener {
	//WebAppInit//
    public void init(WebApp app)  {
        HibernateUtil.initSessionFactory(app);
    }

	//WebAppCleanup//
    public void cleanup(WebApp wapp) {
        HibernateUtil.cleanupSessionFactory();
    }
	
	//ServletContextListener//
	/**
	 *@since 3.0.1
	 */
	public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.cleanupSessionFactory();
    }
	
	/**
	 *@since 3.0.1
	 */
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();
		String resource = ctx.getInitParameter(HibernateUtil.CONFIG);
        HibernateUtil.initSessionFactory(resource);
    }
}
