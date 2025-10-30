/* Panelchildren.java

	Purpose:

	Description:

	History:
		Jun 10, 2008 11:47:15 AM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * Panelchildren is used for {@link Panel} component to manage each child who will
 * be shown in the body of Panel.
 * Note that the size of Panelchildren is automatically calculated by Panel so
 * {@link #setWidth(String)}, {@link #setHeight(String)}, {@link #setHflex(String)}
 * and {@link #setVflex(String)} are read-only.
 *
 * <p>Default {@link #getZclass}: z-panelchildren.
 *
 * @author jumperchen
 * @since 3.5.0
 */
public class Panelchildren extends XulElement {

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Panel))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	/**
	 * This method is unsupported. Please use {@link Panel#setWidth(String)} instead.
	 */
	public void setWidth(String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * This method is unsupported. Please use {@link Panel#setHeight(String)} instead.
	 */
	public void setHeight(String height) {
		throw new UnsupportedOperationException("readonly");
	}

	public String getHflex() {
		Component parent = getParent();
		return parent == null ? null : ((Panel) parent).getHflex();
	}

	public String getVflex() {
		Component parent = getParent();
		return parent == null ? null : ((Panel) parent).getVflex();
	}

	/**
	 * This method is unsupported. Please use {@link Panel#setHflex(String)} instead.
	 * @since 6.0.0
	 */
	public void setHflex(String flex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * This method is unsupported. Please use {@link Panel#setVflex(String)} instead.
	 * @since 6.0.0
	 */
	public void setVflex(String flex) {
		throw new UnsupportedOperationException("readonly");
	}

	protected void smartUpdate(String attr, Object value) {
		super.smartUpdate(attr, value); // provides a bridge so it can be called by Panel
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-panelchildren" : _zclass;
	}
}
