/* ConfigParser.java

	Purpose:
		
	Description:
		
	History:
		Sat Jul  4 20:45:16     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.idom.Element;

/**
 * A configuration parser is an application-specific parser
 * to parse the confingurations in zk.xml.
 *
 * <p>You can specify it in /metainfo/config.xml or WEB-INF/zk.xml
 * as follows:
 *
 * <pre><code>&lt;system-config>
	&lt;au-writer-class>org.zkoss.zkmax.au.http.SmartAuWriter</au-writer-class>
	&lt;config-parser-class>org.zkoss.zkmax.init.ConfigParser</config-parser-class>
&lt;/system-config>
 *</code></pre>
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface ConfigParser {
	/** Called to parse application-specific elements.
	 *
	 * @param config the configuration to store the info.
	 * The info can be stored with {@link Configuration#setAttribute}.
	 * @param el the element in zk.xml to be parsed
	 * @return whether the specified element is parsed by this parser.
	 * Return false if this parser doesn't recoginize it.
	 */
	public boolean parse(Configuration config, Element el);
}
