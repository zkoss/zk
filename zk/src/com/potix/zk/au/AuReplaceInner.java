/* AuReplaceInner.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:19:47     2005, Created by tomyeh@potix.com
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
 * A response to ask client to replaces the INNER content of the specified
 * component.
 * <p>data[0]: the uuid of the component to replace<br>
 * data[1]: the new INNER content
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:56 $
 */
public class AuReplaceInner extends AuResponse {
	public AuReplaceInner(Component comp, String content) {
		super("inner", comp, new String[] {comp.getUuid(), content});
	}
	public AuReplaceInner(Page page, String content) {
		super("inner", page, new String[] {page.getId(), content});
	}
}
