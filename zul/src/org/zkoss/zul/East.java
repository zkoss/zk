/* East.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:20:42 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * An east region of a border layout.
 * <p>Default {@link #getZclass}: z-east.(since 3.5.0)
 * 
 * <p>Default: {@link #getCmargins()} is "0,3,3,0"</p>
 * @author jumperchen
 * @since 5.0.0
 */
public class East extends LayoutRegion implements org.zkoss.zul.api.East {
	public East() {
		setCmargins("0,3,3,0");
	}

	/**
	 * Returns {@link Borderlayout#EAST}.
	 */
	public String getPosition() {
		return Borderlayout.EAST;
	}

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 */
	public void setHeight(String height) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getWidth()}.
	 */
	public String getSize() {
		return getWidth();
	}

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setWidth(String)}.
	 */
	public void setSize(String size) {
		setWidth(size);
	}
}
