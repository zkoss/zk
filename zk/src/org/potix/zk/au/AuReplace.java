/* AuReplace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:47:52     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;

/**
 * A response to ask client to replaces the content of the specified
 * component.
 * <p>data[0]: the uuid of the component to replace<br>
 * data[1]: the new content
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuReplace extends AuResponse {
	public AuReplace(Component comp, String content) {
		super("outer", comp, new String[] {comp.getUuid(), content});
	}
	public AuReplace(Page page, String content) {
		super("outer", page, new String[] {page.getId(), content});
	}
}
