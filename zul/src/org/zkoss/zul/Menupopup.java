/* Menupopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A container used to display menus. It should be placed inside a
 * {@link Menu}.
 *
 * <p>Four events: onPopupHiding, onPupupHidden, onPopupShowing
 * and onPopupShown.
 *
 * <p>Default {@link #getSclass}: menupopup.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Menupopup extends Popup {
	public Menupopup() {
		setSclass("menupopup");
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return typeRequired() ? attrs + " z:type=\"zul.menu.Mpop\"": attrs;
			//to minimize HTML's size, generate z:type only if necessary
	}
	private boolean typeRequired() {
		return !(getParent() instanceof Menu);
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Menuitem)
		&& !(child instanceof Menuseparator) && !(child instanceof Menu))
			throw new UiException("Unsupported child for menupopup: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
