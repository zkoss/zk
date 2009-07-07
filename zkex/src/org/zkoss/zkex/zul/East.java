/* East.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 27, 2007 3:36:47 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkex.zul;

/**
 * This component is a east region.
 * <p>Default {@link #getZclass}: z-east.(since 3.5.0)
 * 
 * <p>Default: {@link #getCmargins()} is "0,5,5,0" (since 3.5.0)</p>
 * @author jumperchen
 * @since 3.0.0
 */
public class East extends LayoutRegion implements org.zkoss.zkex.zul.api.East {
	public East() {
		setCmargins("0,5,5,0");
	}

	public String getZclass() {
		return _zclass == null ? "z-east" : _zclass;
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
