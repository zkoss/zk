/* Center.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 27, 2007 3:36:16 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zulex;

/**
 * This component is a center region. The default class of CSS is specified
 * "layout-region-center".
 * @author jumperchen
 * @since 3.0.0
 */
public class Center extends LayoutRegion {
	public Center() {
		addSclass("layout-region-center");
	}

	/**
	 * Center region can't be enabled the split functionality.
	 */
	public void setSplitable(boolean splitable) {
		throw new UnsupportedOperationException("readonly");
	}
	
	/**
	 * Center region can't be enabled the collapse functionality.
	 */
	public void setCollapsible(boolean collapsible) {
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
}
