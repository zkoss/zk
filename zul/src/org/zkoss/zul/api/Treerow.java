/* Treerow.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A treerow.
 * <p>
 * Default {@link #getZclass}: z-treerow (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treerow extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the {@link Tree} instance containing this element.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

	/**
	 * Returns the level this cell is. The root is level 0.
	 */
	public int getLevel();

	/**
	 * Returns the {@link Treechildren} associated with this {@link Treerow}. In
	 * other words, it is {@link Treeitem#getTreechildrenApi} of
	 * {@link #getParent} .
	 * 
	 * @see org.zkoss.zul.Treechildren#getLinkedTreerow
	 */
	public org.zkoss.zul.api.Treechildren getLinkedTreechildrenApi();

	/** Returns the label of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @since 5.0.8
	 */
	public String getLabel();
	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treecell automatically
	 * if they don't exist.
	 * @since 5.0.8
	 */
	public void setLabel(String label);
	/** Returns the image of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @since  5.0.8
	 */
	public String getImage();
	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treerow and treecell automatically
	 * if they don't exist.
	 * @since 5.0.8
	 */
	public void setImage(String image);
}
