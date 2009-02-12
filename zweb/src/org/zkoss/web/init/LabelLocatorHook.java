/* LabelLocatorHook.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 16:49:15     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.init;


import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

//import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.util.resource.ServletLabelLocator;
import org.zkoss.web.util.resource.ServletLabelResovler;

/**
 * Used to hook a label locator to locate resources from the servlet context.
 *
 * <p>Note: you don't need to specify this in web.xml if you use ZK
 * because org.zkoss.zk.ui.DHtmlLayoutServlet will register the label locator
 * automatically.
 *
 * <p>If you don't use ZK, you could declare
<pre><code>
&lt;listener&gt;
	&lt;description&gt;Load i3-label.properties from this Web app&lt;/description&gt;
	&lt;display-name&gt;Locating i3-label.properties&lt;/display-name&gt;
	&lt;listener-class&gt;org.zkoss.web.init.LabelLocatorHook&lt;/listener-class&gt;
&lt;/listener&gt;
</code></pre>
 *
 * @author tomyeh
 */
public class LabelLocatorHook implements ServletContextListener {
	//private static final Log log = Log.lookup(LabelLocatorHook.class);

	public void contextDestroyed(ServletContextEvent sce) {
	}
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();
		//if (log.debugable()) log.debug("Hook label locator for "+ctx);

		Labels.register(new ServletLabelLocator(ctx));
		Labels.setVariableResolver(new ServletLabelResovler());
	}
}
