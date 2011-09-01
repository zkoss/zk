/* Toolbarbutton.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;

/**
 * A toolbar button.
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 *
 * <p>Default {@link #getZclass}: z-toolbarbutton.(since 5.0.0)
 *
 * @author tomyeh
 */
public class Toolbarbutton extends Button implements org.zkoss.zul.api.Toolbarbutton {
	public Toolbarbutton() {
	}
	public Toolbarbutton(String label) {
		super(label);
	}
	public Toolbarbutton(String label, String image) {
		super(label, image);
	}
	
	// super
	public String getZclass() {
		return _zclass == null ? "z-toolbarbutton" : _zclass;
	}
}
