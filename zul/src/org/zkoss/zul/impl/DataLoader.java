/* DataLoader.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 28, 2009 5:05:54 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.event.ListDataEvent;

/**
 * DataLoader is used with {@link org.zkoss.zul.Grid}, {@link org.zkoss.zul.Listbox}, and 
 * {@link org.zkoss.zul.Tree} to handle data loading from model to server and then to client side.
 *  
 * @author henrichen
 * @since 5.0.0
 */
public interface DataLoader {
	/**
	 * Callback from the owner component and give a chance to initialize things. 
	 * @param owner the owner component of this DataLoader.
	 */
	public void init(Component owner);
	
	/**
	 * Returns the associated owner component of this DataLoader. 
	 */
	public Component getOwner();
	
	/**
	 * Returns current offset of the data chunk.
	 * @return current offset of the data chunk.
	 */
	public int getOffset();
	
	/**
	 * Returns current limit size of the data chunk to be shown on the screen.
	 * @returns current limit size of the data chunk to be shown on the screen.
	 */
	public int getLimit();
	
	/**
	 * Handle event when ListDataEvent is fired from owner component.
	 */
	public void doListDataChange(ListDataEvent event);
	
	/**
	 * Returns the total size of the data.
	 * @return the total size of the data.
	 */
	public int getTotalSize();
	
	/**
	 * Given index and get element from the data model.
	 * @param index
	 * @return the item of the specified index
	 */
	public Object getModelElementAt(int index);
	
	/**
	 * New a component item.
	 * @param renderer the associated renderer for the item.
	 * @param index the index of the item
	 * @return the component
	 */
	public Object newComponentItem(Object renderer, int index);
	
	/** Synchronizes the owner component to be consistent with the specified model.
	 * 
	 * @param offset the lower index of the range to do data synchronize.
	 * @param limit the size of the range to do data synchronize. -1 means the current range.
	 */
	public void syncModel(int offset, int limit);
	
	/** Render the items of the specified range.
	 * 
	 * @param offset the lower index of the range to do item rendering.
	 * @param limit the size of the range to do item rendering. -1 means the current range.
	 */
	public void renderItems(int offset, int limit);
}
