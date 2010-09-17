/* SelectEvent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:05:51     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.Set;
import java.util.Collections;
import java.util.Map;
import java.util.List;

import static org.zkoss.lang.Generics.cast;

import org.zkoss.zk.ui.Desktop;
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
public class SelectEvent<T extends Component> extends Event {
	private final Set<T> _selectedItems;
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
	public static final <C extends Component> SelectEvent<C> getSelectEvent(AuRequest request) {
		final Map data = request.getData();
		final Desktop desktop = request.getDesktop();
		final List<String> sitems = cast((List)data.get("items"));
		final Set<C> items = AuRequests.convertToItems(desktop, sitems);
		return new SelectEvent<C>(request.getCommand(), request.getComponent(),
			items, desktop.getComponentByUuidIfAny((String)data.get("reference")),
			AuRequests.parseKeys(data));
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems) {
		this(name, target, selectedItems, null, 0);
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems,
	Component ref) {
		this(name, target, selectedItems, ref, 0);
	}
	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 3.6.0
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems,
	Component ref, int keys) {
		super(name, target);

		if (selectedItems != null)
			_selectedItems = selectedItems;
		else
			_selectedItems = Collections.emptySet();
		_ref = ref;
		_keys = keys;
	}
	/** Returns the selected items (never null).
	 */
	public final Set<T> getSelectedItems() {
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
