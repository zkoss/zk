/* AuFocus.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:52:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to set focus to the specified component at the client.
 * <p>data[0]: the uuid of the component to set focus
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuFocus extends AuResponse {
	public AuFocus(Component comp) {
		super("focus", comp, comp.getUuid());
	}

	/** Default: zk.focus (i.e., only one response of this class
	 * for the same component will be sent to the client in an execution)
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return "zk.focus";
	}
}
