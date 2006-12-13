/* ListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 17:44:08     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataListener;

/**
 * This interface defines the methods components like {@link Listbox}
 * use to get the value of each cell and the length of the list.
 *
 * @author tomyeh
 * @see Listbox
 * @see ListitemRenderer
 */
public interface ListModel {
	/** Returns the value at the specified index.
	 */
	public Object getElementAt(int index);
	/** Returns the length of the list.
	 */
	public int getSize();
	/** Returns the index of a given element.
	 */
	public int indexOf(Object elm);

	/** Adds a listener to the list that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addListDataListener(ListDataListener l);
    /** Removes a listener from the list that's notified each time
     * a change to the data model occurs. 
     */
	public void removeListDataListener(ListDataListener l) ;
}
