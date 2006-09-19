/* HibernateSessionFactoryListener.java

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

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.WebAppInit;
import com.potix.zk.ui.util.WebAppCleanup;

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
 *		&lt;listener-class>com.potix.zkplus.hibernate.HibernateSessionFactoryListener&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * </p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class HibernateSessionFactoryListener implements WebAppInit, WebAppCleanup {
    public void init(WebApp wapp)  {
        HibernateUtil.initSessionFactory();
    }

    public void cleanup(WebApp wapp) {
        HibernateUtil.cleanupSessionFactory();
    }
}
