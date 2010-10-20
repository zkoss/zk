/* Center.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:21:34 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A center region of a borderlayout.
 * <strong>Note:</strong> This component doesn't support the following method,
 * including {@link #setSplittable(boolean)}, {@link #setOpen(boolean)},
 * {@link #setCollapsible(boolean)}, {@link #setMaxsize(int)},
 * {@link #setMinsize(int)}, {@link #setHeight(String)},
 * {@link #setWidth(String)}, {@link #getSize()}, {@link #setSize(String)},
 * and {@link #setVisible(boolean)}.
 * 
 * <p>Default {@link #getZclass}: z-center.
 * @author jumperchen
 * @since 5.0.0
 */
public class Center extends LayoutRegion implements org.zkoss.zul.api.Center {

	public Center() {
	}

	public String getZclass() {
		return _zclass == null ? "z-center" : _zclass;
	}

	/**
	 * Center region can't be enabled the collapsed margin functionality.
	 */
	public void setCmargins(String cmargins) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Center region can't be enabled the split functionality.
	 */
	public void setSplittable(boolean splittable) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Center region can't be closed.
	 */
	public void setOpen(boolean open) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Center region can't be enabled the collapse functionality.
	 */
	public void setCollapsible(boolean collapsible) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Center region can't be enabled the maxsize.
	 */
	public void setMaxsize(int maxsize) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Center region can't be enabled the minsize.
	 */
	public void setMinsize(int minsize) {
		throw new UnsupportedOperationException("readonly");
	}
	
	/**
	 * Returns {@link Borderlayout#CENTER}.
	 */
	public String getPosition() {
		return Borderlayout.CENTER;
	}

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 */
	public void setHeight(String height) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 */
	public void setWidth(String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * This component can't be hidden.
	 */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * The size can't be returned in this component.
	 */
	public String getSize() {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * The size can't be specified in this component.
	 */
	public void setSize(String size) {
		throw new UnsupportedOperationException("readonly");
	}

	/** Returns the defaul collapsed margin ([3, 3, 3, 3]).
	 * @since 5.0.5
	 */
	//@Override
	protected int[] getDefaultCmargins() {
		return new int[] { 3, 3, 3, 3 };
	}
}
