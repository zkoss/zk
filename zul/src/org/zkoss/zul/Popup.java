/* Popup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 09:49:49     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.event.Events;
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
 * <p>Default {@link #getSclass}: ctxpopup.
 *
 * @author tomyeh
 */
public class Popup extends XulElement {
	public Popup() {
		super.setVisible(false);
		if (!(this instanceof Menupopup)) setSclass("ctxpopup");
	}

	/**
	 * Pop ups this component at the client.
	 *
	 * <p>In most cases, the popup is shown automatically when specified
	 * in the tooltip, popup and context properties
	 * ({@link XulElement#setTooltip}, {@link XulElement#setPopup},
	 * and {@link XulElement#setContext}).
	 * However, if you want to show it manually, you can invoke this
	 * method directly.
	 *
	 * @since 3.0.0
	 */
	public void popup(String x, String y) {
		response("popup", new AuPopup(this, x, y));
	}

	//super//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("You cannot make it visible manually");
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
	implements ZidRequired, Floating {
		//ZidRequired//
		public boolean isZidRequired() {
			return !(getParent() instanceof Menu);
		}
		//Floating//
		public boolean isFloating() {
			return true;
		}
	}
}
