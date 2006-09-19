/* AuClientInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 28 11:39:43     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au;

/**
 * A response to ask the browser to send back its information.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuClientInfo extends AuResponse {
	public AuClientInfo() {
		super("clientInfo");
	}
}
