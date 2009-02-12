/* AuPopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 16:53:37     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to open or to close a popup.
 *
 * <p>data[0]: the component UUID<br/>
 * data[1]: what operation to do. 0: close, 1: open by ref, 2: open by (x,y)
 * data[2]: the x coordination<br/>
 * data[3]: the y coordination
 *
 * <p>Note: the first argument is always the component itself.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuPopup extends AuResponse {
	/** Constructs an instance to open the popup at specified location.
	 * Note: the component must implement the zkType.context JavaScript function.
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param x the x coordination
	 * @param y the y coordination
	 */
	public AuPopup(Component comp, String x, String y) {
		super("popup", comp, new String[] {comp.getUuid(), "2", x, y});
	}
	/** Constructs an instance to open the popup at specified location.
	 * Note: the component must implement the zkType.context JavaScript function.
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param ref the reference component
	 */
	public AuPopup(Component comp, Component ref) {
		super("popup", comp, new String[] {comp.getUuid(), "1", ref.getUuid()});
	}
	/** Constructs an instance to close the popup.
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param option ignored.
	 */
	public AuPopup(Component comp, boolean option) {
		super("popup", comp, new String[] {comp.getUuid(), "0"});
	}

}
