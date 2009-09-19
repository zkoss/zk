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
 * 
 * <p>Default {@link #getZclass}: z-center.(since 3.5.0)
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
}
