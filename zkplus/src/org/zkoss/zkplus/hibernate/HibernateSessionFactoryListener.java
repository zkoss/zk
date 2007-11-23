/* HibernateSessionFactoryListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  5 10:11:55     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.hibernate;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.WebAppInit;
import org.zkoss.zk.ui.util.WebAppCleanup;

import org.hibernate.SessionFactory;

/**
 * Listener to init and cleanup the hibernate session factory automatically. 
 * This listener is used with {@link OpenSessionInViewListener} and 
 * {@link HibernateUtil}, or it will not work.
 *
 * <p>In WEB-INF/zk.xml, add following lines:
 * <pre><code>
 * 	&lt;listener>
 *		&lt;description>Hibernate SessionFactory Lifecycle&lt;/description>
 *		&lt;listener-class>org.zkoss.zkplus.hibernate.HibernateSessionFactoryListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * Since ZK 3.0.1, if your hibernate configuration file name is not the default "hibernate.cfg.xml", you can 
 * specify it in WEB-INF/zk.xml. Just add following lines:
 * <pre><code>
 *	&lt;preference>
 *	&lt;name>HibernateUtil.config&lt;/name>
 *	&lt;value>YOUR-HIBERNATE-FILENAME&lt;/value>
 * </code></pre>
 * </p>
 *
 * @author henrichen
 */
public class HibernateSessionFactoryListener implements WebAppInit, WebAppCleanup {
    public void init(WebApp app)  {
        HibernateUtil.initSessionFactory(app);
    }

    public void cleanup(WebApp wapp) {
        HibernateUtil.cleanupSessionFactory();
    }
}
