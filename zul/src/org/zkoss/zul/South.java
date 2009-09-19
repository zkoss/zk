/* South.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:21:53 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A south region of a border layout.
 * <p>Default {@link #getZclass}: z-south.(since 3.5.0)
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public class South extends LayoutRegion implements org.zkoss.zul.api.South {
	public South() {
	}

	/**
	 * Returns {@link Borderlayout#SOUTH}.
	 */
	public String getPosition() {
		return Borderlayout.SOUTH;
	}

	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 */
	public void setWidth(String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()}.
	 */
	public String getSize() {
		return getHeight();
	}

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setHeight(String)}.
	 */
	public void setSize(String size) {
		setHeight(size);
	}
}