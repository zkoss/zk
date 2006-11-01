/* AuDoModal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 23:40:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.au;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * Make a window as normal window.
 * <p>data[0]: component's UUID
 * 
 * @author tomyeh
 */
public class AuDoModal extends AuResponse {
	public AuDoModal(Component comp) {
		super("doModal", comp, comp.getUuid());
	}
}
