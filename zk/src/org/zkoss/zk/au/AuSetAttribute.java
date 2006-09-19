/* AuSetAttribute.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:28:05     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to set the attribute of the specified component at the client.
 * <p>data[0]: the uuid of the component<br/>
 * data[1]: the attribute name<br/>
 * data[2]: the attribute value
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuSetAttribute extends AuResponse {
	public AuSetAttribute(Component comp, String attr, String val) {
		super("setAttr", comp, new String[] {comp.getUuid(), attr, val});
	}
}
