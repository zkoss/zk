/* AuCloseErrorBox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 18:17:07     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;

/**
 * A response to ask the client to close the error box belonging
 * the specified component, if any.
 *
 * <p>data[0]: the component's UUID.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuCloseErrorBox extends AuResponse {
	/**
	 * @param comp the component whose error box, if any, shall be closed.
	 */
	public AuCloseErrorBox(Component comp) {
		super("closeErrbox", comp.getUuid()); //component-independent
	}
}
