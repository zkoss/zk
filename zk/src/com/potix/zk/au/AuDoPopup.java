/* AuDoPopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:23:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;

/**
 * A response to make a window as popup at the client.
 * <p>data[0]: the uuid of the component to popup
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuDoPopup extends AuResponse {
	public AuDoPopup(Component comp) {
		super("doPop", comp, comp.getUuid());
	}
}
