/* Popup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 09:49:49     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zk.ui.ext.render.ZidRequired;
import org.zkoss.zk.ui.ext.render.Floating;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.au.out.AuPopup;

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
public class Popup extends XulElement implements org.zkoss.zul.api.Popup {
	public Popup() {
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
		response("popup", new AuPopup(this, x, y));
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
	 * Opens this popup right below the specified component at the cleint.
	 * <p>In most cases, the popup is shown automatically when specified
	 * in the tooltip, popup and context properties
	 * ({@link XulElement#setTooltip}, {@link XulElement#setPopup},
	 * and {@link XulElement#setContext}).
	 * However, if you want to show it manually, you can invoke this
	 * method directly.
	 * <p>By default the position "at_pointer" is assumed.(since 3.6.1)
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
	public void open(Component ref, String position) {
		response("popup", new AuInvoke(this, "context", new Object[] {ref.getUuid(), position}));
	}
	/**
	 * Closes this popup at the client.
	 *
	 * <p>In most cases, the popup is closed automatically when the user
	 * clicks outside of the popup.
	 * @since 3.0.0
	 */
	public void close() {
		response("popup", new AuPopup(this, false));
	}

	//super//
	/** Not allowd.
	 * Use {@link #open} to open, and {@link #close} to close.
	 */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use open/close instead");
	}
	public String getZclass() {
		return _zclass == null ? "z-popup" : _zclass;
	}
	public String getOuterAttrs() {
	//Note: don't generate z.type here because Menupopup's z.type diff
		final StringBuffer sb = appendAsapAttr(null, Events.ON_OPEN);
		final String attrs = super.getOuterAttrs();
		return sb != null ? sb.append(attrs).toString(): attrs;
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements ZidRequired, Floating, Openable {
		//ZidRequired//
		public boolean isZidRequired() {
			return !(getParent() instanceof Menu);
		}
		//Floating//
		public boolean isFloating() {
			return true;
		}
		/**
		 * @since 3.5.0
		 */
		public void setOpenByClient(boolean open) {
			if (open) smartUpdate("z.closemask", true);
				//make sure to remove the progress bar at client side, if open.
		}
	}
}
