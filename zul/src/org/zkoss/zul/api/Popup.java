/* Popup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.Component;

/**
 * A container that is displayed as a popup. The popup window does not have any
 * special frame. Popups can be displayed when an element is clicked by
 * assigning the id of the popup to either the {@link #setPopup},
 * {@link #setContext} or {@link #setTooltip} attribute of the element.
 * 
 * <p>
 * Default {@link #getZclass}: z-popup.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Popup extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Opens this popup to the specified location at the client.
	 * 
	 * <p>
	 * In most cases, the popup is shown automatically when specified in the
	 * tooltip, popup and context properties (
	 * {@link org.zkoss.zul.impl.XulElement#setTooltip},
	 * {@link org.zkoss.zul.impl.XulElement#setPopup}, and
	 * {@link org.zkoss.zul.impl.XulElement#setContext}). However, if you want
	 * to show it manually, you can invoke this method directly.
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	public void open(String x, String y);

	/**
	 * Opens this popup to the specified location at the client.
	 * 
	 * <p>
	 * In most cases, the popup is shown automatically when specified in the
	 * tooltip, popup and context properties (
	 * {@link org.zkoss.zul.impl.XulElement#setTooltip},
	 * {@link org.zkoss.zul.impl.XulElement#setPopup}, and
	 * {@link org.zkoss.zul.impl.XulElement#setContext}). However, if you want
	 * to show it manually, you can invoke this method directly.
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	public void open(int x, int y);

	/**
	 * Opens this popup right below the specified component at the cleint.
	 * <p>
	 * In most cases, the popup is shown automatically when specified in the
	 * tooltip, popup and context properties (
	 * {@link org.zkoss.zul.impl.XulElement#setTooltip},
	 * {@link org.zkoss.zul.impl.XulElement#setPopup}, and
	 * {@link org.zkoss.zul.impl.XulElement#setContext}). However, if you want
	 * to show it manually, you can invoke this method directly.
	 * 
	 * @param ref
	 *            the reference component to position the popup. It cannot be
	 */
	public void open(Component ref);

	/**
	 * Closes this popup at the client.
	 * 
	 * <p>
	 * In most cases, the popup is closed automatically when the user clicks
	 * outside of the popup.
	 * 
	 */
	public void close();

}
