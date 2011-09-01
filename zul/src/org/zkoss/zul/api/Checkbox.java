/* Checkbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A checkbox.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.CheckEvent is sent when a checkbox is checked or
 * unchecked by user.</li>
 * </ol>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Checkbox extends org.zkoss.zul.impl.api.LabelImageElement,
org.zkoss.zk.ui.ext.Disable {
	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisabled();

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is checked.
	 * <p>
	 * Default: false.
	 */
	public boolean isChecked();

	/**
	 * Sets whether it is checked.
	 */
	public void setChecked(boolean checked);

	/**
	 * Returns the value.
	 * <p>
	 * Default: "".
	 * @since 5.0.4
	 */
	public String getValue();

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value; If null, it is considered as empty.
	 * @since 5.0.4
	 */
	public void setValue(String value);
	
	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 */
	public String getName();

	/**
	 * Sets the name of this component.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * 
	 * @param name
	 *            the name of this component.
	 */
	public void setName(String name);

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex();

	/**
	 * Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException;
}
