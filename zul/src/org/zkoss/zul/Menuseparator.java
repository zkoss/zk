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

import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * Used to create a separator between menu items.
 *
 *<p>Default {@link #getSclass}: z-menu-sp. (since 3.1.0)
 * 	If {@link #getMold()} == "v30", null is assumed for backward compatible.
 * 
 * @author tomyeh
 */
public class Menuseparator extends XulElement {
	public Menuseparator() {
		init();
	}

	private void init() {
		if (Utils.isThemeV30()) setMold("v30");
	}	
	
	public String getSclass() {
		String scls = super.getSclass();
		return (scls == null || scls.length() == 0) && !"v30".equals(getMold()) ? "z-menu-sp" : scls;
	}
	//-- Component --//
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}
