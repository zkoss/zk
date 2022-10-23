/* SelectData.java

	Purpose:

	Description:

	History:
		11:11 AM 2022/2/9, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.event.MouseEvent;

/**
 * Represents an action is triggered by a user to do a list selection being
 * changed at the client.
 * @author jumperchen
 */
public class SelectData implements ActionData {
	private final List<Integer> _referencePath;
	private final int _referenceIndex;
	private final int _keys;
	private final List<List<Integer>> _selectionPaths;
	private final List<Integer> _selectedItems;

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

	@JsonCreator
	protected SelectData(Map data) {
		_referenceIndex = (int) data.get("referenceIndex");
		_referencePath = (List<Integer>) data.get("referencePath");
		_selectionPaths = (List<List<Integer>>) data.get("itemsPath");
		_selectedItems = (List<Integer>) data.get("itemsIndex");
		_keys = AuRequests.parseKeys(data);
	}

	/** Returns the selected items (never null).
	 */
	public List<Integer> getSelectedItems() {
		return _selectedItems;
	}

	/** Returns the selected paths of {@link ITree} (never null).
	 */
	public List<List<Integer>> getSelectionPaths() {
		return _selectionPaths;
	}

	/** Returns the reference item that is the component causing the onSelect
	 * event(select or deselect) to be fired.
	 *
	 * <b>Note:</b> if not multiple, the {@link #getReference} is the same with the first item of {@link #getSelectedItems}.
	 */
	public int getReference() {
		return _referenceIndex;
	}

	/** Returns the reference item path that is the component causing the onSelect
	 * event(select or deselect) to be fired, such as {@link ITree}
	 *
	 * <b>Note:</b> if not multiple, the {@link #getReferencePath()} is the same with the first item of {@link #getSelectionPaths()}.
	 */
	public List<Integer> getReferencePath() {
		return _referencePath;
	}

	/** Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 */
	public final int getKeys() {
		return _keys;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "SelectData{" + "referencePath=" + _referencePath
				+ ", referenceIndex=" + _referenceIndex + ", keys=" + _keys
				+ ", selectionPaths=" + _selectionPaths + ", selectedItems=" + _selectedItems
				+ '}';
	}
}
