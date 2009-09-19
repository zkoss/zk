/* LayoutRegion.java

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
package org.zkoss.zul.api;

/**
 * A layout region in a border layout.
 * <p>
 * Events:<br/>
 * onOpen, onSize.<br/>
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public interface LayoutRegion extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the border.
	 * <p>
	 * The border actually controls what CSS class to use: If border is null, it
	 * implies "none".
	 * 
	 * <p>
	 * If you also specify the CSS class ({@link org.zkoss.zul.LayoutRegion#setClass}), it overwrites
	 * whatever border you specify here.
	 * 
	 * <p>
	 * Default: "normal".
	 */
	public String getBorder();

	/**
	 * Sets the border (either none or normal).
	 * 
	 * @param border
	 *            the border. If null or "0", "none" is assumed.
	 */
	public void setBorder(String border);

	/**
	 * Sets whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * 
	 */
	public void setFlex(boolean flex);

	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * Default: "0,0,0,0".
	 */
	public String getMargins();

	/**
	 * Sets margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 */
	public void setMargins(String margins);

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutoscroll();

	/**
	 * Sets whether enable overflow scrolling.
	 */
	public void setAutoscroll(boolean autoscroll);

	/**
	 * Returns this regions position (north/south/east/west/center).
	 * 
	 * @see org.zkoss.zul.Borderlayout#NORTH
	 * @see org.zkoss.zul.Borderlayout#SOUTH
	 * @see org.zkoss.zul.Borderlayout#EAST
	 * @see org.zkoss.zul.Borderlayout#WEST
	 * @see org.zkoss.zul.Borderlayout#CENTER
	 */
	public String getPosition();

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link org.zkoss.zul.LayoutRegion#setHeight(String)} and {@link org.zkoss.zul.LayoutRegion#setWidth(String)}. If this region
	 * is {@link org.zkoss.zul.North} or {@link org.zkoss.zul.South}, this method will invoke
	 * {@link org.zkoss.zul.LayoutRegion#setHeight(String)}. If this region is {@link org.zkoss.zul.West} or
	 * {@link org.zkoss.zul.East}, this method will invoke {@link org.zkoss.zul.LayoutRegion#setWidth(String)}.
	 * Otherwise it will throw a {@link UnsupportedOperationException}.
	 */
	public void setSize(String size);

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link org.zkoss.zul.LayoutRegion#getHeight()} and {@link org.zkoss.zul.LayoutRegion#getWidth()}. If this region is
	 * {@link org.zkoss.zul.North} or {@link org.zkoss.zul.South}, this method will invoke
	 * {@link org.zkoss.zul.LayoutRegion#getHeight()}. If this region is {@link org.zkoss.zul.West} or {@link org.zkoss.zul.East},
	 * this method will invoke {@link org.zkoss.zul.LayoutRegion#getWidth()}. Otherwise it will throw a
	 * {@link UnsupportedOperationException}.
	 */
	public String getSize();
}
