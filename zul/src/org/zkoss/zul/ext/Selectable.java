package org.zkoss.zul.ext;

import java.util.Set;

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
 * Indicate a selectable collection or component.
 * @author henrichen
 *
 */
public interface Selectable {
	/**
	 * Returns current selection.
	 * @return current selection.
	 */
	public Set getSelection();
	
	/**
	 * Add the specified object into selection.
	 * @param obj the object to be as selection.
	 */
	public void addSelection(Object obj);
	
	/**
	 * Remove the specified object from selection.
	 * @param obj the object to be remove from selection.
	 */
	public void removeSelection(Object obj);
	
	/**
	 * Clear all selection.
	 */
	public void clearSelection();
}
