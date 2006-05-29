/* AuAlert.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:26:35     2005, Created by tomyeh@potix.com
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
 * A response to ask client to show an alert.
 *
 * <p>data[0]: component's UUID or null (ie., empty for client)
 * data[1]: the alert message
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:54 $
 */
public class AuAlert extends AuResponse {
	public AuAlert(String message) {
		super("alert", new String[] {null, message});
	}
	public AuAlert(Component comp, String message) {
		super("alert", comp, new String[] {comp.getUuid(), message});
	}
}
