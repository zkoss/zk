/* Menuseparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:59:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.impl.XulElement;

/**
 * Used to create a separator between menu items.
 *
 *<p>Default {@link #getMoldSclass}: z-menu-separator. (since 3.5.0)
 * 
 * @author tomyeh
 */
public class Menuseparator extends XulElement {

	//-- Component --//
	public String getMoldSclass() {
		return _moldSclass == null ? "z-menu-separator" : super.getMoldSclass();
	}
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}
