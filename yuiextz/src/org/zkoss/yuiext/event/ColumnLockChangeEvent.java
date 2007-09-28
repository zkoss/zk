/* ColumnLockChangeEvent.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed July 4 18:41:32     2007, Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.event;

import java.util.Set;
import java.util.Collections;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Represents an event caused by user's locking or unlocking on the column.
 * 
 * @author jumperchen
 */
public class ColumnLockChangeEvent extends Event {
	private final Set _lockedColumns;

	/**
	 * Constructs a columnlockchange event.
	 * 
	 * @param lockedColumns
	 *            a set of columns that shall be locked.
	 */
	public ColumnLockChangeEvent(String name, Component target,
			Set lockedColumns) {
		super(name, target);
		_lockedColumns = lockedColumns != null ? lockedColumns
				: Collections.EMPTY_SET;
	}

	/**
	 * Returns the locked columns (never null).
	 */
	public final Set getLockedColumns() {
		return _lockedColumns;
	}
}
