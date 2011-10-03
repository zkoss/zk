/* Absolutechildren.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:05:38 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * <p>A container component that can contain any other ZK component and can only 
 * be contained as direct child of Absolutelayout component. It can be absolutely
 * positioned within Absolutelayout component by either setting "x" and "y"
 * attribute or calling {@link #setX(int)} and {@link #setY(int)} methods.
 * 
 * <p>Default {@link #getZclass}: z-absolutechildren.
 * 
 * @author ashish
 * @since 6.0.0
 */
public class Absolutechildren extends XulElement {

	private int _x;
	private int _y;

	/**
	 * Sets current "x" position within parent container component.
	 * <p>Default: 0
	 * @param x the x position
	 */
	public void setX(int x) {
		if (_x != x) {
			_x = x;
			smartUpdate("x", _x);
		}
	}

	/**
	 * Sets current "y" position within parent container component.
	 * <p>Default: 0
	 * @param y the y position
	 */
	public void setY(int y) {
		if (_y != y) {
			_y = y;
			smartUpdate("y", _y);
		}
	}

	/**
	 * Returns the current "x" position within parent container component
	 */
	public int getX() {
		return _x;
	}

	/**
	 * Returns current "y" position within parent container component
	 */
	public int getY() {
		return _y;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		if (_x != 0)
			render(renderer, "x", _x);
		if (_y != 0)
			render(renderer, "y", _y);
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Absolutelayout))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	public String getZclass() {
		return (this._zclass != null ? this._zclass : "z-absolutechildren");
	}
}