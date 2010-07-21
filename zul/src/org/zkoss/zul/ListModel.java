/* ListModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 17:44:08     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * This interface defines the methods that components like {@link Listbox}
 * and {@link Grid} use to get the content of items.
 *
 * <p>If the list model is used with sortable listbox or grid,
 * the developer must also implement {@link ListModelExt}.
 *
 * <p>If the model also provides the selection, it could implement
 * {@link Selectable}.
 *
 * @author tomyeh
 * @see Grid
 * @see Listbox
 * @see ListitemRenderer
 * @see ListModelExt
 * @see Selectable
 */
public interface ListModel {
	/** Returns the value at the specified index.
	 */
	public Object getElementAt(int index);
	/** Returns the length of the list.
	 */
	public int getSize();

	/** Adds a listener to the list that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addListDataListener(ListDataListener l);
    /** Removes a listener from the list that's notified each time
     * a change to the data model occurs. 
     */
	public void removeListDataListener(ListDataListener l) ;
}
