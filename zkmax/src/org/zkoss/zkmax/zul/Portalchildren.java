/* Portalchildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2008 4:19:52 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

/**
 * The column of Portallayout. <br> 
 * Child of Portalchildren can only be Panel.
 * 
 * <p>Default {@link #getZclass}: z-portal-children.
 * @author jumperchen
 * @since 3.5.0
 */
public class Portalchildren extends XulElement implements org.zkoss.zkmax.zul.api.Portalchildren {	
	public Portalchildren() {
	}

	public String getZclass() {
		return _zclass == null ? "z-portal-children" : _zclass;
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Portallayout))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Panel))
			throw new UiException("Unsupported child for Portalchildren: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
}
