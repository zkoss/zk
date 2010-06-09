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
	public void init(Component owner, int offset, int limit);
	
	/**
	 * Callback from the owner component and give a chance to reset things.
	 */
	public void reset();
	
	/**
	 * Returns the associated owner component of this DataLoader. 
	 */
	public Component getOwner();
	
	/**
	 * Returns the requested <b>visible</b> offset of the current loaded data chunk.
	 * @return the requested <b>viaible</b> offset of the current loaded data chunk.
	 */
	public int getOffset();

	/**
	 * Returns the required <b>visible</b> limit size of the current loaded data chunk to be shown on the screen.
	 * @return the required <b>visible</b> limit size of the current loaded data chunk to be shown on the screen.
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
	
	/** Synchronizes the owner component to be consistent with the specified model.
	 * 
	 * @param offset the starting index of the range to do data synchronize.
	 * @param limit the size of the range to do data synchronize. -1 means the current range.
	 */
	public void syncModel(int offset, int limit);
	
	/** Used to update some extra control information to the client.
	 */
	public void updateModelInfo();
	
	/** Return the renderer to do rendering.
	 * 
	 */
	public Object getRealRenderer();
	
	/**
	 * Sets whether to always load all items from ListModel. 
	 */
	public void setLoadAll(boolean b);
}
