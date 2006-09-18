/* ListDataEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:03:55     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.event;

import com.potix.zul.html.ListModel;

/**
 * Defines an event that encapsulates changes to a list. 
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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

	public ListDataEvent(ListModel model, int type, int index0, int index1) {
		if (model == null)
			throw new NullPointerException();
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
	 * {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}.
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
}
