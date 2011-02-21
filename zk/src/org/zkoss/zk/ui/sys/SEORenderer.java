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
 * <p>You could specify the class of an implementation of the SEO renderer in
 * a preference called <code>org.zkoss.zk.ui.sys.SEORenderer.class</code>.
 * <p>For example, in WEB-INF/zk.xml, you could specify
 * <pre><code>
&lt;preference>
	&lt;name>org.zkoss.zk.ui.sys.SEORenderer.class&lt;/name>
	&lt;value>com.foo.MySEORenderer&lt;/value>
&lt;/preference></code></pre>
 *
 * <p>Notice that the SEO render, if specified, is always called, even if
 * <a href="http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_system-config_Element/The_crawlable_Element">the crawlable option</a> is not enabled.
 * @author tomyeh
 * @since 5.0.6
 */
public interface SEORenderer {
	public void render(Page page, Writer out) throws IOException;
}
