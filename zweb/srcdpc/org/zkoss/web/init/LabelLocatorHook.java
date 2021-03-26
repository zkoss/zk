/* LabelLocatorHook.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 16:49:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.init;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.zkoss.util.resource.Labels;
import org.zkoss.web.util.resource.ServletLabelLocator;
import org.zkoss.web.util.resource.ServletRequestResolver;

/**
 * @deprecated As of release 6.0.0, we don't support the loading of
 * zk-label.properties without installing ZK.
 * Used to hook a label locator to locate resources from the servlet context.
 *
 * <p>Note: you don't need to specify this in web.xml if you use ZK
 * because org.zkoss.zk.ui.DHtmlLayoutServlet will register the label locator
 * automatically.
 *
 * <p>If you don't use ZK, you could declare
<pre><code>
&lt;listener&gt;
	&lt;description&gt;Load zk-label.properties from this Web app&lt;/description&gt;
	&lt;display-name&gt;Locating zk-label.properties&lt;/display-name&gt;
	&lt;listener-class&gt;org.zkoss.web.init.LabelLocatorHook&lt;/listener-class&gt;
&lt;/listener&gt;
</code></pre>
 *
 * @author tomyeh
 */
public class LabelLocatorHook implements ServletContextListener {
	//private static final Logger log = LoggerFactory.getLogger(LabelLocatorHook.class);

	public void contextDestroyed(ServletContextEvent sce) {
	}

	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();
		//if (log.isDebugEnabled()) log.debug("Hook label locator for "+ctx);

		Labels.register(new ServletLabelLocator(ctx));
		Labels.setVariableResolver(new ServletRequestResolver());
	}
}
