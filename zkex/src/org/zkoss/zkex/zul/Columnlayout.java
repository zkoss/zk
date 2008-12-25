/* Columnlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 10:42:53 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.impl.XulElement;

/**
 * A columnlayout lays out a container which can have multiple columns, and each
 * column may contain one or more panel.<br>
 * Use Columnlayout need assign width (either present or pixel) on every
 * Columnchildren, or we cannot make sure about layout look.
 * 
 * <p>Default {@link #getZclass}: z-column-layout.
 * 
 * @author gracelin
 * @since 3.5.0
 */
public class Columnlayout extends XulElement implements org.zkoss.zkex.zul.api.Columnlayout {

	public Columnlayout() {
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Columnchildren))
			throw new UiException("Unsupported child for Columnlayout: "
					+ child);
		return super.insertBefore(child, insertBefore);
	}
	public String getZclass() {
		return _zclass == null ? "z-column-layout" : _zclass;
	}

	/**
	 * When add child, layout will be rerender
	 * 
	 * @see ComponentCtrl#onChildAdded
	 */
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		smartUpdate("z.childchg", true);
	}

	/**
	 * When remove child, layout will be rerender
	 * 
	 * @see ComponentCtrl#onChildRemoved
	 */
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		smartUpdate("z.childchg", true);
	}
}
