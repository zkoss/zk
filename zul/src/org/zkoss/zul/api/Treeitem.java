/* Treeitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

;
/**
 * A treeitem.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>onOpen is sent when a tree item is opened or closed by user.</li>
 * <li>onDoubleClick is sent when user double-clicks the treeitem.</li>
 * <li>onRightClick is sent when user right-clicks the treeitem.</li>
 * </ol>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treeitem extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns whether it is checkable.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isCheckable();

	/**
	 * Sets whether it is checkable.
	 * <p>
	 * Default: true.
	 * 
	 */
	public void setCheckable(boolean checkable);

	/**
	 * Unload the tree item
	 * <p>
	 * To load the tree item, with {@link Tree#renderItemApi(Treeitem)},
	 * {@link Tree#renderItemApi(Treeitem, Object)}, or
	 * {@link Tree#renderItems(java.util.Set)}
	 * 
	 */
	public void unload();

	/**
	 * Sets whether it is disabled.
	 * 
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isDisabled();

	/**
	 * Return true whether all children of this tree item, if any, is loaded
	 * 
	 * @return true whether all children of this tree item is loaded
	 */
	public boolean isLoaded();

	/**
	 * return the index of this container
	 * 
	 * @return the index of this container
	 */
	public int indexOf();

	/**
	 * Returns the treerow that this tree item owns (might null). Each tree
	 * items has exactly one tree row.
	 */
	public org.zkoss.zul.api.Treerow getTreerowApi();

	/**
	 * Returns the treechildren that this tree item owns, or null if doesn't
	 * have any child.
	 */
	public org.zkoss.zul.api.Treechildren getTreechildrenApi();

	/**
	 * Returns whether the element is to act as a container which can have child
	 * elements.
	 */
	public boolean isContainer();

	/**
	 * Returns whether this element contains no child elements.
	 */
	public boolean isEmpty();

	/**
	 * Returns the value. It could be anything you want.
	 * <p>
	 * Default: null.
	 * <p>
	 * Note: the value is not sent to the browser, so it is OK to be anything.
	 */
	public Object getValue();

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value. Note: the value is not sent to the browser, so it
	 *            is OK to be anything.
	 */
	public void setValue(Object value);

	/**
	 * Returns whether this container is open.
	 * <p>
	 * Default: true.
	 */
	public boolean isOpen();

	/**
	 * Sets whether this container is open.
	 */
	public void setOpen(boolean open);

	/**
	 * Returns whether this item is selected.
	 */
	public boolean isSelected();

	/**
	 * Returns whether this item is selected.
	 */
	public void setSelected(boolean selected);

	/**
	 * Returns the level this cell is. The root is level 0.
	 */
	public int getLevel();

	/**
	 * Returns the label of the {@link Treecell} it contains, or null if no such
	 * cell.
	 */
	public String getLabel();

	/**
	 * Sets the label of the {@link Treecell} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setLabel(String label);

	/**
	 * Returns the image of the {@link Treecell} it contains.
	 */
	public String getImage();

	/**
	 * Sets the image of the {@link Treecell} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setImage(String image);

	/**
	 * Returns the parent tree item, or null if this item is already the top
	 * level of the tree. The parent tree item is actually the grandparent if
	 * any.
	 * 
	 */
	public org.zkoss.zul.api.Treeitem getParentItemApi();

	/**
	 * Returns the tree owning this item.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

}
