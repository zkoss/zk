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
import org.zkoss.zul.impl.XulElement;

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
	 * Opens this popup right below the specified component at the client.
	 * <p>In most cases, the popup is shown automatically when specified
	 * in the tooltip, popup and context properties
	 * ({@link XulElement#setTooltip}, {@link XulElement#setPopup},
	 * and {@link XulElement#setContext}).
	 * However, if you want to show it manually, you can invoke this
	 * method directly.
	 * <p> Possible values for the position attribute are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned on the left.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned on the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned on the left.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned on the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned on the top.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned on the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned on the top.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned on the bottom.</li>
	 *  <li><b>overlap</b><br/> the popup overlaps the anchor, with the topleft 
	 *  	corners of both the anchor and popup aligned.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the anchor, with the topleft corner of the popup at the horizontal position of the mouse pointer.</li>
	 * </ul></p>
	 * 
	 * @param ref the reference component to position the popup.
	 * It cannot be null. 
	 * @param position the descriptions above are for a locale where the UI is 
	 * displayed left to right and top to bottom. In this case, before is the top,
	 * after is the bottom, start is the left and end is the right. For right to left locales,
	 * start is the right and end is the left. 
	 * 
	 * @since 3.6.1
	 */
	public void open(Component ref, String position);
	
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
