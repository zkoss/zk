/* Hbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 22:16:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;

/**
 * A hbox.
 *
 * @author tomyeh
 */
public class Hbox extends Box {
	public Hbox() {
		super("horizontal");
	}
	/** Constructor a horizontal box by assigning an array of children.
	 *
	 * @param children an array of children to be added
	 * @since 2.4.0
	 */
	public Hbox(Component[] children) {
		super("horizontal", children);
	}
}
