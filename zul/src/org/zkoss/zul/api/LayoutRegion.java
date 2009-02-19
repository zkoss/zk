/* LayoutRegion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:17:16 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.api;

/**
 * This class represents a region in a layout manager.
 * <p>
 * Events:<br/>
 * onOpen, onSize.<br/>
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public abstract interface LayoutRegion extends
		org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the title.
	 * <p>
	 * Default: null.
	 * 
	 * @since 3.5.0
	 */
	public String getTitle();

	/**
	 * Sets the title.
	 * 
	 * @since 3.5.0
	 */
	public void setTitle(String title);

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
	 * Returns whether enable the split functionality.
	 * <p>
	 * Default: false.
	 */
	public boolean isSplittable();

	/**
	 * Sets whether enable the split functionality.
	 */
	public void setSplittable(boolean splittable);

	/**
	 * Sets the maximum size of the resizing element.
	 */
	public void setMaxsize(int maxsize);

	/**
	 * Returns the maximum size of the resizing element.
	 * <p>
	 * Default: 2000.
	 */
	public int getMaxsize();

	/**
	 * Sets the minimum size of the resizing element.
	 */
	public void setMinsize(int minsize);

	/**
	 * Returns the minimum size of the resizing element.
	 * <p>
	 * Default: 0.
	 */
	public int getMinsize();

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
	 * Returns the collapsed margins, which is a list of numbers separated by
	 * comma.
	 * 
	 * <p>
	 * Default: "5,5,5,5".
	 * 
	 * @since 3.5.0
	 */
	public String getCmargins();

	/**
	 * Sets the collapsed margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 * 
	 * @since 3.5.0
	 */
	public void setCmargins(String cmargins);

	/**
	 * Returns whether set the initial display to collapse.
	 * <p>
	 * Default: false.
	 */
	public boolean isCollapsible();

	/**
	 * Sets whether set the initial display to collapse.
	 * 
	 * <p>
	 * It only applied when {@link #getTitle()} is not null. (since 3.5.0)
	 */
	public void setCollapsible(boolean collapsible);

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
	 * Returns whether it is opne (i.e., not collapsed. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 * <p>
	 * Default: true.
	 */
	public boolean isOpen();

	/**
	 * Opens or collapses the splitter. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 */
	public void setOpen(boolean open);

	/**
	 * Returns this regions position (north/south/east/west/center).
	 * 
	 * @see org.zkoss.zul.Borderlayout#NORTH
	 * @see org.zkoss.zul.Borderlayout#SOUTH
	 * @see org.zkoss.zul.Borderlayout#EAST
	 * @see org.zkoss.zul.Borderlayout#WEST
	 * @see org.zkoss.zul.Borderlayout#CENTER
	 */
	abstract public String getPosition();

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link org.zkoss.zul.LayoutRegion#setHeight(String)} and {@link org.zkoss.zul.LayoutRegion#setWidth(String)}. If this region
	 * is {@link org.zkoss.zul.North} or {@link org.zkoss.zul.South}, this method will invoke
	 * {@link org.zkoss.zul.LayoutRegion#setHeight(String)}. If this region is {@link org.zkoss.zul.West} or
	 * {@link org.zkoss.zul.East}, this method will invoke {@link org.zkoss.zul.LayoutRegion#setWidth(String)}.
	 * Otherwise it will throw a {@link UnsupportedOperationException}.
	 */
	abstract public void setSize(String size);

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link org.zkoss.zul.LayoutRegion#getHeight()} and {@link org.zkoss.zul.LayoutRegion#getWidth()}. If this region is
	 * {@link org.zkoss.zul.North} or {@link org.zkoss.zul.South}, this method will invoke
	 * {@link org.zkoss.zul.LayoutRegion#getHeight()}. If this region is {@link org.zkoss.zul.West} or {@link org.zkoss.zul.East},
	 * this method will invoke {@link org.zkoss.zul.LayoutRegion#getWidth()}. Otherwise it will throw a
	 * {@link UnsupportedOperationException}.
	 */
	abstract public String getSize();

}
