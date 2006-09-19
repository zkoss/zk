/* AuEndPopup.java

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
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to restore a popup window back to embeded at the client
 * <p>data[0]: the uuid of the component to become embeded
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuEndPopup extends AuResponse {
	public AuEndPopup(Component comp) {
		super("endPop", comp, comp.getUuid());
	}
}
