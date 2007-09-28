/* ColumnMovedEvent.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed July 4 11:43:18     2007, Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.event;

import org.zkoss.yuiext.grid.Column;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Represents an event caused by a column being moved.
 * 
 * @author jumperchen
 */
public class ColumnMovedEvent extends Event {
	private final Column _movedCol;

	private final int _oldIndex, _newIndex;

	/**
	 * Constructs a ColumnMoved event.
	 */
	public ColumnMovedEvent(String name, Component target, Column movedCol,
			int oldIndex, int newIndex) {
		super(name, target);
		_movedCol = movedCol;
		_oldIndex = oldIndex;
		_newIndex = newIndex;
	}

	/**
	 * Returns the moved column.
	 * 
	 */
	public Column getMovedColumn() {
		return _movedCol;
	}

	/**
	 * Returns old index of the moved column from the list of Columns children.
	 */
	public int getOldIndex() {
		return _oldIndex;
	}

	/**
	 * Returns new index of the moved column from the list of Columns children.
	 */
	public int getNewIndex() {
		return _newIndex;
	}
}
