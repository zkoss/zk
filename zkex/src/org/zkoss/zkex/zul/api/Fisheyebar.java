/* Fisheyebar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A fisheye bar is a bar of {@link Fisheye} that is a menu similar to the fish
 * eye menu on the Mac OS.
 * 
 * <p>
 * Default {@link org.zkoss.zkex.zul.Fisheyebar#getZclass}: z-fisheyebar.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public interface Fisheyebar extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the item width of {@link Fisheye}.
	 */
	public int getItemWidth();

	/**
	 * Sets the item width of {@link Fisheye}.
	 */
	public void setItemWidth(int itemwd) throws WrongValueException;

	/**
	 * Returns the item height of {@link Fisheye}.
	 */
	public int getItemHeight();

	/**
	 * Sets the item height of {@link Fisheye}.
	 */
	public void setItemHeight(int itemhgh) throws WrongValueException;

	/**
	 * Returns the item maximal width of {@link Fisheye}.
	 */
	public int getItemMaxWidth();

	/**
	 * Sets the item maximal width of {@link Fisheye}.
	 */
	public void setItemMaxWidth(int itemmaxwd) throws WrongValueException;

	/**
	 * Returns the item maximal height of {@link Fisheye}.
	 */
	public int getItemMaxHeight();

	/**
	 * Sets the item maximal height of {@link Fisheye}.
	 */
	public void setItemMaxHeight(int itemmaxhgh) throws WrongValueException;

	/**
	 * Returns the item padding of {@link Fisheye}.
	 */
	public int getItemPadding();

	/**
	 * Sets the item padding of {@link Fisheye}.
	 */
	public void setItemPadding(int itemPadding) throws WrongValueException;

	/**
	 * Returns the orientation of {@link Fisheye}.
	 */
	public String getOrient();

	/**
	 * Sets the orientation of {@link Fisheye}.
	 */
	public void setOrient(String orient) throws WrongValueException;

	/**
	 * Returns the attach edge.
	 * <p>
	 * Default: center
	 */
	public String getAttachEdge();

	/**
	 * Returns the attach edge.
	 */
	public void setAttachEdge(String attachEdge) throws WrongValueException;

	/**
	 * Returns the label edge.
	 * <p>
	 * Default: bottom
	 */
	public String getLabelEdge();

	/**
	 * Returns the label edge.
	 */
	public void setLabelEdge(String labelEdge) throws WrongValueException;

}
