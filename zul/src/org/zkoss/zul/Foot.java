/* Foot.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 12:05:37     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

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
 * Defines a set of footers ({@link Footer}) for a grid ({@link Grid}).
 * <p>Default {@link #getZclass}: z-foot.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Foot extends XulElement implements org.zkoss.zul.api.Foot {
	/** Returns the grid that it belongs to.
	 * It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}
	/** Returns the grid that it belongs to.
	 * It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Grid getGridApi() {
		return getGrid();
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Footer))
			throw new UiException("Unsupported child for foot: "+child);
		super.beforeChildAdded(child, refChild);
	}

	public String getZclass() {
		return _zclass == null ? "z-foot" : _zclass;
	}
}
