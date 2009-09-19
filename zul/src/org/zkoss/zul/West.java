/* West.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:17:16 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A west region of a border layout.
 * <p>Default {@link #getZclass}: z-west.(since 3.5.0)
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public class West extends LayoutRegion implements org.zkoss.zul.api.West {
	public West() {
	}

	/**
	 * Returns {@link Borderlayout#WEST}.
	 */
	public String getPosition() {
		return Borderlayout.WEST;
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
