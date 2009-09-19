/* Menu.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;


/**
 * An element, much like a button, that is placed on a menu bar. When the user
 * clicks the menu element, the child {@link Menupopup} of the menu will be
 * displayed. This element is also used to create submenus (of {@link Menupopup}
 * .
 * 
 * <p>
 * Default {@link #getZclass}: z-mean. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Menu extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Returns whether this is an top-level menu, i.e., not owning by another
	 * {@link Menupopup}.
	 */
	public boolean isTopmost();

	/**
	 * Returns the {@link Menupopup} it owns, or null if not available.
	 */
	public org.zkoss.zul.api.Menupopup getMenupopupApi();
}
