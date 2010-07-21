package org.zkoss.zul.ext;

import java.util.Set;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/* Selectable.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 24, 2009 12:15:21 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

/**
 * Indicate a selectable collection or component. Generally used with {@link ListModel}
 * and {@link Listbox}.
 * @author henrichen
 * @see ListModel
 * @see Listbox
 */
public interface Selectable {
	/**
	 * Returns current selection.
	 * @return current selection.
	 */
	public Set getSelection();
	
	/**
	 * Add the specified object into selection.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Listbox}).
	 * If it is called by an application, the component's selection status
	 * won't be changed.
	 * @param obj the object to be as selection.
	 */
	public void addSelection(Object obj);
	
	/**
	 * Remove the specified object from selection.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Listbox}).
	 * If it is called by an application, the component's selection status
	 * won't be changed.
	 * @param obj the object to be remove from selection.
	 */
	public void removeSelection(Object obj);
	
	/**
	 * Clear all selection.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Listbox}).
	 * If it is called by an application, the component's selection status
	 * won't be changed.
	 */
	public void clearSelection();
}
