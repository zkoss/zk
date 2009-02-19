/* ListDataEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:03:55     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.ListModel;

/**
 * Defines an event that encapsulates changes to a list. 
 *
 * @author tomyeh
 */
public class ListDataEvent {
	/** Identifies one or more changes in the lists contents. */
	public static final int CONTENTS_CHANGED = 0;
    /** Identifies the addition of one or more contiguous items to the list. */    
	public static final int INTERVAL_ADDED = 1;
    /** Identifies the removal of one or more contiguous items from the list. */   
	public static final int INTERVAL_REMOVED = 2;

	private final ListModel _model;
	private final int _type, _index0, _index1;

	/** Contructor.
	 *
	 * @param type one of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED}.
	 * @param index0 the lower index of the change range.
	 * For simple element, index0 is the same as index1.
	 * -1 means the first element (the same as 0).
	 * @param index1 the upper index of the change range.
	 * -1 means the last element.
	 */
	public ListDataEvent(ListModel model, int type, int index0, int index1) {
		if (model == null)
			throw new IllegalArgumentException();
		_model = model;
		_type = type;
		_index0 = index0;
		_index1 = index1;
	}
	/** Returns the list model that fires this event.
	 */
	public ListModel getModel() {
		return _model;
	}
	/** Returns the event type. One of {@link #CONTENTS_CHANGED},
	 * {@link #INTERVAL_ADDED}, {@link #INTERVAL_REMOVED}.
	 */
	public int getType() {
		return _type;
	}
	/** Returns the lower index of the change range.
	 * For a single element, this value is the same as that returned by
	 * {@link #getIndex1}.
	 */
	public int getIndex0() {
		return _index0;
	}
	/** Returns the upper index of the change range.
	 * For a single element, this value is the same as that returned by
	 * {@link #getIndex0}.
	 */
	public int getIndex1() {
		return _index1;
	}

	//Object//
	public String toString() {
		return "[ListDataEvent type=" + _type +", index="+_index0+", "+_index1+']';
	}
}
