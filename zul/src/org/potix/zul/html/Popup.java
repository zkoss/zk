/* Popup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 23 09:49:49     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.sys.ComponentsCtrl;
import com.potix.zul.html.impl.XulElement;

/**
 * A container that is displayed as a popup.
 * The popup window does not have any special frame.
 * Popups can be displayed when an element is clicked by assigning
 * the id of the popup to either the {@link #setPopup},
 * {@link #setContext} or {@link #setTooltip} attribute of the element.
 *
 * <p>Default {@link #getSclass}: ctxpopup.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Popup extends XulElement {
	public Popup() {
		super.setVisible(false);
		if (!(this instanceof Menupopup)) setSclass("ctxpopup");
	}

	//super//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("You cannot make it visible manually");
	}
	public void setId(String id) {
		if (zidRequired()) {
			final String oldid = getId();
			if (!Objects.equals(id, oldid)) {
				super.setId(id);
				smartUpdate("zid", id);
			}
		} else {
			super.setId(id);
		}
	}
	public String getOuterAttrs() {
	//Note: don't generate zk_type here because Menupopup's zk_type diff

		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_OPEN);

		if (zidRequired()) {
			final String id = getId();
	 		if (!ComponentsCtrl.isAutoId(id))
				HTMLs.appendAttribute(sb, "zid", id);
		}
		return sb.toString();
	}
	private boolean zidRequired() {
		return !(getParent() instanceof Menu);
	}
}
