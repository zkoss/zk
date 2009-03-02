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

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents an event cause by user's the list selection is changed
 * at the client.
 * 
 * @author tomyeh
 */
public class SelectEvent extends Event {
	private final Set _selectedItems;
	private final Component _ref;
	private final int _keys;

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = MouseEvent.ALT_KEY;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = MouseEvent.CTRL_KEY;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = MouseEvent.SHIFT_KEY;

	/** Converts an AU request to a select event.
	 * @since 5.0.0
	 */
	public static final SelectEvent getSelectEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Set items = AuRequests.convertToItems(request);
		final String[] data = request.getData();
		if (data == null || data.length > 3)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});
		final Component ref = data.length >= 2 && data[1] != null ?
			request.getDesktop().getComponentByUuidIfAny(data[1]): null;
		return new SelectEvent(request.getName(), comp, items, ref,
			data.length < 3 ? 0: AuRequests.parseKeys(data[2]));
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set selectedItems) {
		this(name, target, selectedItems, null, 0);
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set selectedItems,
	Component ref) {
		this(name, target, selectedItems, ref, 0);
	}
	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 3.6.0
	 */
	public SelectEvent(String name, Component target, Set selectedItems,
	Component ref, int keys) {
		super(name, target);
		_selectedItems = selectedItems != null ?
			selectedItems: Collections.EMPTY_SET;
		_ref = ref;
		_keys = keys;
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

	/** Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 3.6.0
	 */
	public final int getKeys() {
		return _keys;
	}
}
