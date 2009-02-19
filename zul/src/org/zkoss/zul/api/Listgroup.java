/* Listgroup.java

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

import java.util.List;

/**
 * Adds the ability for single level grouping to the Listbox.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>onOpen is sent when this listgroup is opened or closed by user.</li>
 * </ol>
 * 
 * <p>
 * Default {@link #getZclass}: z-list-group.
 * 
 * @author jumperchen
 * @since 3.5.2
 */
public interface Listgroup extends Listitem {

	/**
	 * Returns a list of all {@link Listitem} are grouped by this listgroup.
	 */
	public List getItems();

	/**
	 * Returns the number of items.
	 */
	public int getItemCount();

	/**
	 * Returns the number of visible descendant {@link Listitem}.
	 * 
	 */
	public int getVisibleItemCount();

	/**
	 * Returns the index of Listgroupfoot
	 * <p>
	 * -1: no Listgroupfoot
	 */
	public int getListgroupfootIndex();

	/**
	 * Returns the Listfoot, if any. Otherwise, null is returned.
	 */
	public org.zkoss.zul.api.Listfoot getListfootApi();

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
}
