/* Menubar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:34:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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

import org.zkoss.zul.impl.XulElement;

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getZclass}: z-menubar-hor, if {@link #getOrient()} == vertical,
 *  z-menubar-ver will be added.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Menubar extends XulElement implements org.zkoss.zul.api.Menubar {
	private boolean _autodrop;
	private String _orient = "horizontal";

	public Menubar() {
	}
	/**
	 * @param orient either horizontal or vertical
	 */
	public Menubar(String orient) {
		this();
		setOrient(orient);
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
			smartUpdate("orient", _orient);
		}
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
			smartUpdate("autodrop", autodrop);
		}
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-menubar" +
				("vertical".equals(getOrient()) ? "-ver" : "-hor") : _zclass;
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Menu) && !(child instanceof Menuitem) && !(child instanceof Menuseparator))
			throw new UiException("Unsupported child for menubar: "+child);
		return super.insertBefore(child, insertBefore);
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (_autodrop) render(renderer, "autodrop", _autodrop);
		if ("vertical".equals(getOrient())) render(renderer, "orient", _orient);
		
	}
	
}
