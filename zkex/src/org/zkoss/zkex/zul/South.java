/* South.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 27, 2007 3:35:53 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkex.zul;

/**
 * This component is a south region.
 * <p>Default {@link #getZclass}: z-south.(since 3.5.0)
 * 
 * @author jumperchen
 * @since 3.0.0
 */
public class South extends LayoutRegion implements org.zkoss.zkex.zul.api.South {
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
