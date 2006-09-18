/* AuDoModal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 23:40:32     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.au;

import com.potix.zk.ui.Component;
import com.potix.zk.au.AuResponse;

/**
 * Make a window as normal window.
 * <p>data[0]: component's UUID
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuDoModal extends AuResponse {
	public AuDoModal(Component comp) {
		super("doModal", comp, comp.getUuid());
	}
}
