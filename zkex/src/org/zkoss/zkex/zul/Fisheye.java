/* Fisheye.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 8, 2008 11:59:54 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.XulElement;

/**
 * A fisheye item.
 * 
 * <p>Default {@link #getZclass}: z-fisheye.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Fisheye extends LabelImageElement implements org.zkoss.zkex.zul.api.Fisheye {
	public Fisheye() {
	}
	public Fisheye(String label, String image) {
		super(label, image);
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-fisheye" : _zclass;
	}
	
	/**
	 * This method is unsupported. Please use {@link Fisheyebar#setItemWidth(int)} instead.
	 */
	public void setWidth(String width) {
		throw new UnsupportedOperationException("readonly");
	}
	/**
	 * This method is unsupported. Please use {@link Fisheyebar#setItemHeight(int)} instead.
	 */
	public void setHeight(String height) {
		throw new UnsupportedOperationException("readonly");
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Fisheyebar))
			throw new UiException("Unsupported parent for fisheye: "+parent);
		super.beforeParentChanged(parent);
	}
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}
}
