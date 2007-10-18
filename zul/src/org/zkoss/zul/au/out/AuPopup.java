/* AuPopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 16:53:37     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.au;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to pop up a component.
 *
 * <p>data[0]: the component UUID<br/>
 * data[1]: the x coordination<br/>
 * data[2]: the y coordination
 *
 * <p>Note: the first argument is always the component itself.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuPopup extends AuResponse {
	/** Constructs an instance to pop up the specified component.
	 * Note: the component must implement the zkType.context JavaScript function.
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param x the x coordination
	 * @param y the y coordination
	 */
	public AuPopup(Component comp, String x, String y) {
		super("popup", comp, new String[] {comp.getUuid(), x, y});
	}
}
