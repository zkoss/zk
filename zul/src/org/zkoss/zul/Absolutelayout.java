/* Absolutelayout.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:05:38 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

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
 * <p>An Absolutelayout component can contain absolute positioned multiple
 * absolutechildren components.
 * 
 * <p>Default {@link #getZclass}: z-absolutelayout.
 * 
 * @author ashish
 * @since 6.0.0
 */
public class Absolutelayout extends XulElement {

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Absolutechildren))
			throw new UiException("Unsupported child for Absolutelayout: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}

	/**
	 * The default zclass is "z-absolutelayout"
	 */
	public String getZclass() {
		return (this._zclass != null ? this._zclass : "z-absolutelayout");
	}
}