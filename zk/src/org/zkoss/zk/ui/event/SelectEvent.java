/* SelectEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:05:51     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Set;
import java.util.Collections;

import org.zkoss.zk.ui.Component;

/**
 * Represents an event cause by user's the list selection is changed
 * at the client.
 * 
 * @author tomyeh
 */
public class SelectEvent extends Event {
	private final Set _selectedItems;
	private final Component _ref;

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set selectedItems) {
		this(name, target, selectedItems, null);
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set selectedItems,
			Component ref) {
		super(name, target);
		_selectedItems = selectedItems != null ?
			selectedItems: Collections.EMPTY_SET;
		_ref = ref;
	}
	/** Returns the selected items (never null).
	 */
	public final Set getSelectedItems() {
		return _selectedItems;
	}

	/** Returns the reference item that is the component causing the onSelect 
	 * event(select or deselect) to be fired.
	 *
	 * <p>It is null, if the onSelect event is not caused by listbox or tree or combobox.
	 * Note: if not multiple, the {@link #getReference} is the same with the first item of {@link #getSelectedItems}.
	 * @since 3.0.2
	 */
	public Component getReference() {
		return _ref;
	} 
}
