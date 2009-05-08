/* ColumnChildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 17:45:11 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

/**
 * The column of Columnlayout. <br> 
 * Child of Columnchildren only can be Panel.
 * 
 * <p>Default {@link #getZclass}: z-column-children.
 * @author gracelin
 * @since 3.5.0
 */
public class Columnchildren extends XulElement implements org.zkoss.zkex.zul.api.Columnchildren {

	public Columnchildren() {
	}

	public String getZclass() {
		return _zclass == null ? "z-column-children" : _zclass;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Columnlayout))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Panel))
			throw new UiException("Unsupported child for Columnchildren: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
}
