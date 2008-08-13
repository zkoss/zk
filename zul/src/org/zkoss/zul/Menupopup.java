/* Menupopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:18     2005, Created by tomyeh
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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.Utils;

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
 * <p>Default {@link #getMoldSclass}: z-menupopup. (since 3.5.0)
 * 	If {@link #getMold()} == "v30", menupopup is assumed for backward compatible.
 *
 * @author tomyeh
 */
public class Menupopup extends Popup {
	private boolean _checkmark;
	public Menupopup() {
		Utils.updateMoldByTheme(this);
		setMoldSclass("z-menupopup");
	}

	public void setMold(String mold) {
		if ("v30".equals(mold)) setMoldSclass("menupopup");
		else setMoldSclass("z-menupopup");
		super.setMold(mold);
	}
	
	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public final boolean isCheckmark() {
		return _checkmark;
	}
	/** Sets whether the check mark shall be displayed in front
	 * of each item.
	 * @since 3.5.0
	 */
	public void setCheckmark(boolean checkmark) {
		if (_checkmark != checkmark) {
			_checkmark = checkmark;
			invalidate();
		}
	}
	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_OPEN);
		if (typeRequired())
			if ("v30".equals(getMold())) 
				sb.append(" z.type=\"zul.menu.Mpop\"");
			else
				sb.append(" z.type=\"zul.menu2.Mpop2\"");
			//to minimize HTML's size, generate z.type only if necessary
		return sb.toString();
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
