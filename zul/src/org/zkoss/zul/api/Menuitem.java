/* Menuitem.java

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

import org.zkoss.zk.ui.WrongValueException;

/**
 * A single choice in a {@link Menupopup} element. It acts much like a button
 * but it is rendered on a menu.
 * 
 * <p>
 * Default {@link #getZclass}: z-menu-item. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Menuitem extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Sets whether the check mark shall be displayed in front of each item.
	 * 
	 */
	public void setCheckmark(boolean checkmark);

	/**
	 * Sets whether it is disabled.
	 * 
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isDisabled();

	/**
	 * Returns the value.
	 * <p>
	 * Default: "".
	 */
	public String getValue();

	/**
	 * Sets the value.
	 */
	public void setValue(String value);

	/**
	 * Returns whether it is checked.
	 * <p>
	 * Default: false.
	 */
	public boolean isChecked();

	/**
	 * Sets whether it is checked.
	 * <p>
	 * This only applies when {@link org.zkoss.zul.Menuitem#isCheckmark()} =
	 * true. (since 3.5.0)
	 */
	public void setChecked(boolean checked);

	/**
	 * Returns whether the menuitem check mark will update each time the menu
	 * item is selected.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutocheck();

	/**
	 * Sets whether the menuitem check mark will update each time the menu item
	 * is selected.
	 * <p>
	 * This only applies when {@link org.zkoss.zul.Menuitem#isCheckmark()} =
	 * true. (since 3.5.0)
	 */
	public void setAutocheck(boolean autocheck);

	/**
	 * Returns the href.
	 * <p>
	 * Default: null. If null, the button has no function unless you specify the
	 * onClick handler.
	 */
	public String getHref();

	/**
	 * Sets the href.
	 */
	public void setHref(String href) throws WrongValueException;

	/**
	 * Returns the target frame or window.
	 * 
	 * <p>
	 * Note: it is useful only if href ({@link #setHref}) is specified (i.e.,
	 * use the onClick listener).
	 * 
	 * <p>
	 * Default: null.
	 */
	public String getTarget();

	/**
	 * Sets the target frame or window.
	 * 
	 * @param target
	 *            the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target);

	/**
	 * Returns whether this is an top-level menu, i.e., not owning by another
	 * {@link Menupopup}.
	 */
	public boolean isTopmost();

}
