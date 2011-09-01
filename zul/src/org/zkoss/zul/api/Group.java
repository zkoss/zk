/* Group.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.List;

/**
 * Adds the ability for single level grouping to the Grid.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 * 
 * <p>
 * Default {@link #getZclass}: z-group.
 * 
 * <p>
 * Note: All the child of this component are automatically applied the
 * group-cell CSS, if you don't want this CSS, you can invoke the
 * {@link Label#setSclass(String)} after the child added.
 * 
 * @author jumperchen
 * @since 3.5.2
 */
public interface Group extends Row {

	/**
	 * Returns a list of all {@link Row} are grouped by this group.
	 */
	public List getItems();

	/**
	 * Returns the number of items.
	 */
	public int getItemCount();

	/**
	 * Returns the number of visible descendant {@link Row}.
	 * 
	 */
	public int getVisibleItemCount();

	/**
	 * Returns the index of Groupfoot
	 * <p>
	 * -1: no Groupfoot
	 */
	public int getGroupfootIndex();

	/**
	 * Returns the Groupfoot, if any. Otherwise, null is returned.
	 */
	public org.zkoss.zul.api.Groupfoot getGroupfootApi();

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
	 * Returns the value of the {@link Label} it contains, or null if no such
	 * cell.
	 */
	public String getLabel();

	/**
	 * Sets the value of the {@link Label} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setLabel(String label);

}
