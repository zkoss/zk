/* LabelLocatorHook.java

{{IS_NOTE
	$Id: LabelLocatorHook.java,v 1.7 2006/04/10 03:02:01 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 16:49:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.init;


import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

//import com.potix.util.logging.Log;
import com.potix.util.resource.Labels;
import com.potix.web.util.resource.ServletLabelLocator;

/**
 * Used to hook a label locator to locate resources from the servlet context.
 *
 * <p>Note: you don't need to specify this in web.xml if you use ZK
 * because com.potix.zk.ui.DHtmlLayoutServlet will register the label locator
 * automatically.
 *
 * <p>If you don't use ZK, you could declare
<pre><code>
&lt;listener&gt;
	&lt;description&gt;Load i3-label.properties from this Web app&lt;/description&gt;
	&lt;display-name&gt;Locating i3-label.properties&lt;/display-name&gt;
	&lt;listener-class&gt;com.potix.web.init.LabelLocatorHook&lt;/listener-class&gt;
&lt;/listener&gt;
</code></pre>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/04/10 03:02:01 $
 */
public class LabelLocatorHook implements ServletContextListener {
	//private static final Log log = Log.lookup(LabelLocatorHook.class);

	public void contextDestroyed(ServletContextEvent sce) {
	}
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();
		//if (log.debugable()) log.debug("Hook label locator for "+ctx);

		Labels.the().register(new ServletLabelLocator(ctx));
	}
}
