/* GroupsDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep  1 11:05:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.GroupsModel;

/**
 * Defines an event that encapsulates changes to a list of groups. 
 * 
 * @author tomyeh
 * @since 3.5.0
 * @see org.zkoss.zul.GroupsModel
 * @see GroupsDataListener
 */
public class GroupsDataEvent {
	/** Identifies one or more changes in the contents of a particular group.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * in the range of the particular group.
	 * <p>Note: {@link #getGroupIndex} must be non-negative.
	 */
	public static final int CONTENTS_CHANGED = 0;
	/** Identifies the addition of one or more contiguous items to a
	 * particular group.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * in the range of the particular group.
	 * <p>Note: {@link #getGroupIndex} must be non-negative.
	 */
	public static final int INTERVAL_ADDED = 1;
	/** Identifies the removal of one or more contiguous items from a
	 * particular group.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * in the range of the particular group.
	 * <p>Note: {@link #getGroupIndex} must be non-negative.
	 */
	public static final int INTERVAL_REMOVED = 2;
	/** Identifies the structure of the lists has changed. @since 5.0.7*/
	public static final int STRUCTURE_CHANGED = 3;
	/** Identifies one or more changes in the groups contents.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * the indices of groups.
	 * Note: {@link #getGroupIndex} is ignored.
	 */
	public static final int GROUPS_CHANGED = 4;
	/** Identifies the addition of one or more contiguous items to the list.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * the indices of groups.
	 * Note: {@link #getGroupIndex} is ignored.
	 */
	public static final int GROUPS_ADDED = 5;
	/** Identifies the removal of one or more contiguous items from the list.
	 * The indices ({@link #getIndex0} and {@link #getIndex1} ) are
	 * the indices of groups.
	 * Note: {@link #getGroupIndex} is ignored.
	 */
	public static final int GROUPS_REMOVED = 6;

	/** Identifies the Group is reset.
	 *  @since 5.0.0
	 */
	public static final int GROUPS_RESET = 7;

	/** Identifies the selection of the lists has changed.
	 * Notice that the objects being selected can be found by calling
	 * {@link org.zkoss.zul.ext.Selectable#getSelection}.
	 * Moreover, {@link #getIndex0} and {@link #getIndex1} are both meaningless.
	 * @since 6.0.0
	 */
	public static final int SELECTION_CHANGED = 8;
	/** Identified the state of {@link org.zkoss.zul.ext.Selectable#isMultiple} is changed.
	 * @since 6.0.0
	 */
	public static final int MULTIPLE_CHANGED = 9;
	/** Identifies the Group opened.
	 *  @since 7.0.6
	 */
	public static final int GROUPS_OPENED = 10;
	/**
	 * Identified the state that Component's client update to be disabled
	 * @since 8.0.0
	 */
	public static final int DISABLE_CLIENT_UPDATE = 11;
	/**
	 * Identified the state that Component's client update to be enabled
	 * @since 8.0.0
	 */
	public static final int ENABLE_CLIENT_UPDATE = 12;

	/**
	 * Identifies the Group open status to update after interval added.
	 * @since 8.0.4
	 */
	public static final int INTERVAL_ADDED_WHEN_GROUPS_OPENED = 13;
	/**
	 * Identifies the Group open status to update after interval removed.
	 * @since 8.0.4
	 */
	public static final int INTERVAL_REMOVED_WHEN_GROUPS_CLOSED = 14;

	private final GroupsModel _model;
	private final int _type, _groupIndex, _index0, _index1;

	/** Constructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED},
	 * {@link #GROUPS_CHANGED},
	 * {@link #GROUPS_ADDED}, or {@link #GROUPS_REMOVED}..
	 * @param groupIndex the index of the group being changed.
	 * It is used only if type is one of 
	 * {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}.
	 * @param index0 the lower index of the change range.
	 * For simple element, index0 is the same as index1.
	 * -1 means the first element (the same as 0).
	 * @param index1 the upper index of the change range.
	 * -1 means the last element.
	 */
	public GroupsDataEvent(GroupsModel model, int type, int groupIndex, int index0, int index1) {
		if (model == null)
			throw new IllegalArgumentException();
		if (type < STRUCTURE_CHANGED && groupIndex < 0)
			throw new IllegalArgumentException("groupIndex required for type " + type);
		_model = model;
		_type = type;
		_groupIndex = groupIndex;
		_index0 = index0;
		_index1 = index1;
	}

	/** Returns the list model that fires this event.
	 */
	public GroupsModel getModel() {
		return _model;
	}

	/** Returns the event type. One of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED},
	 * {@link #GROUPS_CHANGED},
	 * {@link #GROUPS_ADDED}, or {@link #GROUPS_REMOVED}.
	 */
	public int getType() {
		return _type;
	}

	/** Returns the index of the group.
	 * It is used only if {@link #getType} is one of 
	 * {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED}.
	 */
	public int getGroupIndex() {
		return _groupIndex;
	}

	/** Returns the lower index of the change range.
	 * For a single element/group, this value is the same as that returned by
	 * {@link #getIndex1}.
	 */
	public int getIndex0() {
		return _index0;
	}

	/** Returns the upper index of the change range.
	 * For a single element/group, this value is the same as that returned by
	 * {@link #getIndex0}.
	 */
	public int getIndex1() {
		return _index1;
	}

	//Object//
	public String toString() {
		return "[GroupsDataEvent type=" + _type + ", index=" + _index0 + ", " + _index1 + ']';
	}
}
