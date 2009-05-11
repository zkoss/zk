/* Listfoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 13 12:42:31     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
public class Listfoot extends XulElement implements org.zkoss.zul.api.Listfoot {
	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}
	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listbox getListboxApi() {
		return getListbox();
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Listfooter))
			throw new UiException("Unsupported child for listfoot: "+child);
		super.beforeChildAdded(child, refChild);
	}
	public String getZclass() {
		return _zclass == null ? "z-listfoot" : _zclass;
	}
}
