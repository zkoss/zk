/* Popup.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 09:49:49     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.XulElement;

/**
 * A container that is displayed as a popup.
 * The popup window does not have any special frame.
 * Popups can be displayed when an element is clicked by assigning
 * the id of the popup to either the {@link #setPopup},
 * {@link #setContext} or {@link #setTooltip} attribute of the element.
 *
 * <p>Default {@link #getZclass}: z-popup.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Popup extends XulElement {
	static {
		addClientEvent(Popup.class, Events.ON_OPEN, CE_IMPORTANT);
	}
	
	public Popup() {
		super.setVisible(false);
	}
	protected Popup(boolean visible) {
		// some sub class should construct without setVisible(false)
		if (!visible)
			super.setVisible(false);
	}

	/**
	 * Opens this popup to the specified location at the client.
	 *
	 * <p>In most cases, the popup is shown automatically when specified
	 * in the tooltip, popup and context properties
	 * ({@link XulElement#setTooltip}, {@link XulElement#setPopup},
	 * and {@link XulElement#setContext}).
	 * However, if you want to show it manually, you can invoke this
	 * method directly.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @since 3.0.0
	 */
	public void open(String x, String y) {
		response("popup", new AuInvoke(this, "open", new Object[] {null, new Object[] {x, y}, null}));
	}
	/**
	 * Opens this popup to the specified location at the client.
	 *
	 * <p>In most cases, the popup is shown automatically when specified
	 * in the tooltip, popup and context properties
	 * ({@link XulElement#setTooltip}, {@link XulElement#setPopup},
	 * and {@link XulElement#setContext}).
	 * However, if you want to show it manually, you can invoke this
	 * method directly.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @since 3.0.0
	 */
	public void open(int x, int y) {
		open(Integer.toString(x), Integer.toString(y));
	}
	/**
	 * Opens this popup right below the specified component at the client.
	 * <p>By default the position "at_pointer" is assumed.(since 5.0.0)
	 * 
	 * @see Popup#open(Component, String)
	 * @param ref the reference component to position the popup.
	 * It cannot be null.
	 * @since 3.0.0
	 */
	public void open(Component ref) {
		open(ref, "at_pointer");
	}
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
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the popup appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the popup appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the popup appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the popup appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the popup appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the popup at the horizontal position of the mouse cursor.</li>
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
	public void open(Component ref, String position) {
		response("popup", new AuInvoke(this, "open", new Object[] {ref.getUuid(), null, position}));
	}
	/**
	 * Closes this popup at the client.
	 *
	 * <p>In most cases, the popup is closed automatically when the user
	 * clicks outside of the popup.
	 * @since 3.0.0
	 */
	public void close() {
		response("popup", new AuInvoke(this, "close"));
	}

	//super//
	/** Not allowed.
	 * Use {@link #open} to open, and {@link #close} to close.
	 */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use open/close instead");
	}
	public String getZclass() {
		return _zclass == null ? "z-popup" : _zclass;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			disableClientUpdate(true);
			try {
				super.setVisible(evt.isOpen()); // Bug B50-3178065
			} finally {
				disableClientUpdate(false);
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
