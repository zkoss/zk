/* Anchorlayout.java

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
 * An anchorlayout lays out a container which can resize 
 * it's children base on its width and height<br>
 * 
 * <p>Default {@link #getZclass}: z-anchorlayout.
 * 
 * @author peterkuo
 * @since 6.0.0
 */
public class Anchorlayout extends XulElement{

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Anchorchildren))
			throw new UiException("Unsupported child for Anchorlayout: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
	
	public String getZclass() {
		return _zclass == null ? "z-anchorlayout" : _zclass;
	}
}
