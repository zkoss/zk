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
package com.potix.zul.html;

import java.io.IOException;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.html.impl.XulElement;

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
public class Menupopup extends XulElement {
	private String _position;

	public Menupopup() {
		setSclass("menupopup");
		super.setVisible(false);
	}

	/** Returns the position where the popup appears relative to
	 * the element the user clicked to invoke the popup.
	 * This allows you to place the menu on one side on a button.
	 *
	 * <p>Default: null (depending the context).
	 *
	 * <p>Allowed value:
	 * <ul>
	 * <li>after_start: The popup appears underneath the element
	 * with the popup's upper-left corner aligned with the lower-left
	 * corner of the element. The left edges of the element and the
	 * popup are aligned. This is typically used for drop-down menus.</li>
	 * </ul>
	 */
	public String getPosition() {
		return _position;
	}
	/** Sets the position where the popup appears relative to
	 * the element the user clicked to invoke the popup.
	 */
	public void setPosition(String position) {
		if (!Objects.equals(_position, position)) {
			_position = position;
			smartUpdate("zk_pos", _position);
		}
	}

	//-- super --//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("You cannot make it visible manually");
	}

	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _position != null ?
			attrs + " zk_pos=\""+_position+'"': attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Menu)
		&& !(parent instanceof Popupset)) //since Popup extends Menupopup
			throw new UiException("Unsupported parent for menupopup: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Menuitem)
		&& !(child instanceof Menuseparator) && !(child instanceof Menu))
			throw new UiException("Unsupported child for menupopup: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
