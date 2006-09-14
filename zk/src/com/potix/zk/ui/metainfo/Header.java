/* Header.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 14 22:02:29     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import com.potix.zk.ui.Page;

/**
 * Represents a header element, such as &lt;link&gt; and &lt;meta&gt;.
 * They are usually represented as directive in ZUML.
 * For example, the link and meta directives represent &lt;link&gt;
 * and &lt;meta&gt; HTML tags, respectively.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Header {
	/** Returns as HTML tag(s) representing this header element.
	 *
	 * @param page the page containing this header element.
	 * It is used to evaluate EL expression, if any, contained in the value.
	 */
	public String toHTML(Page page);
}
