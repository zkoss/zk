/* AbstractGroupsModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  2 08:45:01     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;

/**
 * A skeletal implementation for {@link GroupsModel}.
 * 
 * @author tomyeh
 * @since 3.5.0
 */
abstract public class AbstractGroupsModel implements GroupsModel {
	private transient List _listeners = new LinkedList();

	/** Fires a {@link GroupsDataEvent} for all registered listener
	 * (thru {@link #addGroupsDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int groupIndex, int index0, int index1) {
		final GroupsDataEvent evt =
			new GroupsDataEvent(this, type, groupIndex, index0, index1);
		for (Iterator it = _listeners.iterator(); it.hasNext();)
			((GroupsDataListener)it.next()).onChange(evt);
	}

	//-- GroupsModel --//
	public void addGroupsDataListener(GroupsDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	public void removeGroupsDataListener(GroupsDataListener l) {
		_listeners.remove(l);
	}

	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList();
		Serializables.smartRead(s, _listeners);
	}
}
