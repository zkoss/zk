/* Listfoot.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 13 12:42:31     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

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
 * A row of {@link Listfooter}.
 *
 * <p>Like {@link Listhead}, each listbox has at most one {@link Listfoot}.
 * <p>Default {@link #getZclass}: z-listfoot (since 5.0.0)
 *
 * @author tomyeh
 */
public class Listfoot extends XulElement {
	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Listbox getListbox() {
		return (Listbox) getParent();
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Listbox related 
	 * components, please refer to {@link Listbox} and {@link Listheader} instead.
	 */
	public void setWidth(String width) {
		// Don't need to remove this method, it's used to override super.setWidth();
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Listbox related 
	 * components, please refer to {@link Listbox} and {@link Listheader} instead.
	 */
	public void setHflex(String flex) {
		// Don't need to remove this method, it's used to override super.setHflex();
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Listfooter))
			throw new UiException("Unsupported child for listfoot: " + child);
		super.beforeChildAdded(child, refChild);
	}

	public String getZclass() {
		return _zclass == null ? "z-listfoot" : _zclass;
	}
}
