/* Floating.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr  3 10:01:50     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to denote a component is floating (aka., absolute position).
 * Some components, say, hbox, make no space for floating components.
 *
 * @author tomyeh
 */
public interface Floating {
	/** Returns whether the component is floating, aka., absolute position.
	 */
	public boolean isFloating();
}
