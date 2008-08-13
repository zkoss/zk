/* Menubar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:34:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getMoldSclass}: z-menubar, if {@link #getOrient()} == vertical,
 *  z-menubar-vertical will be added .(since 3.5.0)
 * 	If {@link #getMold()} == "v30", menubar is assumed for backward compatible.
 *
 * @author tomyeh
 */
public class Menubar extends XulElement {
	private boolean _autodrop;
	private String _orient = "horizontal";

	public Menubar() {
		Utils.updateMoldByTheme(this);
		setMoldSclass("z-menubar");
	}
	/**
	 * @param orient either horizontal or vertical
	 */
	public Menubar(String orient) {
		this();
		setOrient(orient);
	}
	public void setMold(String mold) {
		if ("v30".equals(mold)) setMoldSclass("menubar");
		else setMoldSclass("z-menubar");
		super.setMold(mold);
	}
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either horizontal or vertical
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);
		
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}
	protected String getRealSclass() {
		final String sclass = super.getRealSclass();
		return sclass != null && "vertical".equals(_orient) ?
				sclass + " " + getMoldSclass() + "-vertical" : sclass;
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
			smartUpdate("z.autodrop", autodrop);
		}
	}

	//-- Component --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _autodrop ?  attrs + " z.autodrop=\"true\"": attrs;
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
			if (child instanceof HtmlBasedComponent) {
				final String height = ((HtmlBasedComponent)child).getHeight();
				if (height != null)
					sb.append(" height=\"").append(height).append('"');
			}
			sb.append('>');
			if (JVMs.isJava5()) out.insert(0, sb); //Bug 1682844
			else out.insert(0, sb.toString());
			out.append("</tr>");
		}
	}
}
