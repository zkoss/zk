/* AuEndModal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 23:42:27     2005, Created by tomyeh@potix.com
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
 * Make a window as an embedded window.
 * <p>data[0]: component's UUID
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuEndModal extends AuResponse {
	public AuEndModal(Component comp) {
		super("endModal", comp.getUuid()); //comp-indepedent
	}
}
