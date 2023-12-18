/* Treecols.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:52     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.HeadersElement;

/**
 * A treecols.
 * <p>Default {@link #getZclass}: z-treecols (since 5.0.0)
 * @author tomyeh
 */
public class Treecols extends HeadersElement {
	/** Returns the tree that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Tree getTree() {
		return (Tree) getParent();
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Tree related 
	 * components, please refer to {@link Tree} and {@link Treecol} instead.
	 */
	public void setWidth(String width) {
		// Don't remove this method, it's to override super.setWidth().
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Tree related 
	 * components, please refer to {@link Tree} and {@link Treecol} instead.
	 */
	public void setHflex(String flex) {
		// Don't remove this method, it's to override super.setHflex().
	}

	//super//
	public String getZclass() {
		return _zclass == null ? "z-treecols" : _zclass;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tree))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Treecol))
			throw new UiException("Unsupported child for treecols: " + child);
		super.beforeChildAdded(child, refChild);
	}
}
