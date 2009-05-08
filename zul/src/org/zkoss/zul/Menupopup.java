/* Menupopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * A container used to display menus. It should be placed inside a
 * {@link Menu}.
 *
 * <p>Supported event: onOpen.<br/>
 * Note: to have better performance, onOpen is sent only if
 * non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 *
 * <p>To load the content dynamically, you can listen to the onOpen event,
 * and then create menuitem when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen}
 * is true.
 *
 * <p>Default {@link #getZclass}: z-menu-popup. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Menupopup extends Popup implements org.zkoss.zul.api.Menupopup {
	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-menu-popup" : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_OPEN);
		if (typeRequired())
				sb.append(" z.type=\"zul.menu2.Mpop2\"");
			//to minimize HTML's size, generate z.type only if necessary
		return sb.toString();
	}
	private boolean typeRequired() {
		return !(getParent() instanceof Menu);
	}

	//-- Component --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Menuitem)
		&& !(child instanceof Menuseparator) && !(child instanceof Menu))
			throw new UiException("Unsupported child for menupopup: "+child);
		super.beforeChildAdded(child, refChild);
	}
}
