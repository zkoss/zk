/* SEORenderer.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 21 18:14:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Page;

/**
 * A plugin that an application could add to generate application-specific
 * SEO content. The generated content won't be visible to the end users,
 * but that will be used by the spider of search engines.
 * <p>You could specify the class of an implementation of the SEO renderer
 * as a listener in WEB-INF/zk.xml. For example,
 * <pre><code>
&lt;listener>
	&lt;listener-class>com.foo.MySEORenderer&lt;/listener-class>
&lt;/listener></code></pre>
 *
 * <p>Also notice that you could generate JavaScript snippet too, since it will
 * be interpreted by the browser. For example, if you'd like to inject some JavaScript
 * code to every page, you could implement this class and register it as a listener.
 *
 * <p>Notice that the SEO render, if specified, is always called, even if
 * <a href="http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_system-config_Element/The_crawlable_Element">the crawlable option</a> is not enabled.
 * @author tomyeh
 * @since 5.0.6
 */
public interface SEORenderer {
	public void render(Page page, Writer out) throws IOException;
}
