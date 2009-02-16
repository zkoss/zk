/* AuCloseErrorBox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 18:17:07     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to close the error box belonging
 * the specified component, if any.
 *
 * <p>data[0]: the component's UUID.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuCloseErrorBox extends AuResponse {
	
	/**
	 * @param uuid the uuid of the components whose error box, if any,
	 * shall be closed. It can be more than one component, For example, if the uuid
	 * has many components, it shall be the format as "cmp1,cmp2,cmp3,...".
	 * @since 3.6.0
	 */
	public AuCloseErrorBox(String uuid) {
		super("closeErrbox", uuid); //component-independent
	}
	/**
	 * @param comp the component whose error box, if any, shall be closed.
	 */
	public AuCloseErrorBox(Component comp) {
		this(comp.getUuid()); //component-independent
	}
}
