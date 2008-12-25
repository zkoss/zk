/* Panelchildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 10, 2008 11:47:15 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
 * Note that the size of Panelchildren is automatically calculated by Panel so both
 * {@link #setWidth(String)} and {@link #setHeight(String)} are read-only.
 * 
 * <p>Default {@link #getZclass}: z-panel-children.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Panelchildren extends XulElement implements org.zkoss.zul.api.Panelchildren {
	
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Panel))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
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

	// super
	public String getZclass() {
		return _zclass == null ?  "z-panel-children" : _zclass;
	}
}
