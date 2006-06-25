/* Menubar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:34:31     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.io.IOException;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.html.impl.XulElement;

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getSclass}: menubar.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Menubar extends XulElement {
	private boolean _autodrop;

	public Menubar() {
		setSclass("menubar");
		setMold("horizontal");
	}

	/** Returns the orient (the same as {@link #getMold}).
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return getMold();
	}
	/** Sets the orient.
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		setMold(orient);
	}

	/** Returns whether to automatically drop down menus if user moves mouse
	 * over it.
	 * <p>Default: false.
	 */
	public final boolean isAutodrop() {
		return _autodrop;
	}
	/** Sets whether to automatically drop down menus if user moves mouse
	 * over it.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("zk_autodrop", autodrop);
		}
	}

	//-- Component --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _autodrop ?  attrs + " zk_autodrop=\"true\"": attrs;
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Menu) && !(child instanceof Menuitem))
			throw new UiException("Unsupported child for menubar: "+child);
		return super.insertBefore(child, insertBefore);
	}

	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		if ("vertical".equals(getOrient())) {
			final StringBuffer sb = new StringBuffer(32)
				.append("<tr id=\"").append(child.getUuid()).append("!chdextr\"");
			final String height = ((XulElement)child).getHeight();
			if (height != null)
				sb.append(" height=\"").append(height).append('"');
			sb.append('>');
			out.insert(0, sb);
			out.append("</tr>");
		}
	}
}
