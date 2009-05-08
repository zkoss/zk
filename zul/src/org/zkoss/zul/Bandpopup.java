/* Bandpopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:31:44     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * The popup that belongs to a {@link Bandbox} instance.
 *
 * <p>Developer usually listen to the onOpen event that is sent to
 * {@link Bandbox} and then creates proper components as children
 * of this component.
 *
 * @author tomyeh
 */
public class Bandpopup extends XulElement implements org.zkoss.zul.api.Bandpopup {
	public Bandpopup() {
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-band-popup" : _zclass;
	}

	public boolean setVisible(boolean visible) {
		if (!visible)
			throw new UnsupportedOperationException("Use Bandbox.setOpen(false) instead");
		return true;
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Bandbox))
			throw new UiException("Bandpopup's parent must be Bandbox");
		super.beforeParentChanged(parent);
	}
}
