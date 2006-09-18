/* SelectEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:05:51     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import java.util.Set;
import java.util.Collections;

import com.potix.zk.ui.Component;

/**
 * Represents an event cause by user's the list selection is changed
 * at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SelectEvent extends Event {
	private final Set _selectedItems;

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set selectedItems) {
		super(name, target);
		_selectedItems = selectedItems != null ?
			selectedItems: Collections.EMPTY_SET;
	}
	/** Returns the selected items (never null).
	 */
	public final Set getSelectedItems() {
		return _selectedItems;
	}
}
