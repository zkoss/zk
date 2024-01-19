/* ListDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:03:55     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import java.io.Serializable;

import org.zkoss.zul.ListModel;

/**
 * Defines an event that encapsulates changes to a list. 
 *
 * Note: Since 10.0.0, it should be serializable
 * @author tomyeh
 */
public class ListDataEvent implements java.io.Serializable {
	private static final long serialVersionUID = 20240119101433L;

	/** Identifies one or more changes in the lists contents. */
	public static final int CONTENTS_CHANGED = 0;
	/** Identifies the addition of one or more contiguous items to the list. */
	public static final int INTERVAL_ADDED = 1;
	/** Identifies the removal of one or more contiguous items from the list. */
	public static final int INTERVAL_REMOVED = 2;
	/** Identifies the structure of the lists has changed. @since 5.0.7*/
	public static final int STRUCTURE_CHANGED = 3;
	/** Identifies the selection of the lists has changed.
	 * Notice that the objects being selected can be found by calling
	 * {@link org.zkoss.zul.ext.Selectable#getSelection}.
	 * Moreover, {@link #getIndex0} and {@link #getIndex1} are both meaningless.
	 * @since 6.0.0
	 */
	public static final int SELECTION_CHANGED = 4;
	/** Identified the state of {@link org.zkoss.zul.ext.Selectable#isMultiple} is changed.
	 * @since 6.0.0
	 */
	public static final int MULTIPLE_CHANGED = 6;
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

	private transient ListModel _model;
	private final int _type, _index0, _index1;

	/** Constructor.
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
		return "[ListDataEvent type=" + _type + ", index=" + _index0 + ", " + _index1 + ']';
	}

	//Serializable//
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		if (_model instanceof Serializable) {
			s.writeObject(_model);
		} else if (_model != null) {
			throw new java.io.NotSerializableException(_model.getClass().getName());
		} else {
			s.writeObject(null);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		_model = (ListModel) s.readObject();
	}
}
