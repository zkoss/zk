/* AbstractListModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:19:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * A skeletal implementation for {@link ListModel}.
 *
 * @author tomyeh
 */
abstract public class AbstractListModel
implements ListModel, Selectable, java.io.Serializable {
	private transient List _listeners = new LinkedList();
	private Set _selection = new HashSet();
	
	/** Fires a {@link ListDataEvent} for all registered listener
	 * (thru {@link #addListDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int index0, int index1) {
		final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
		for (Iterator it = _listeners.iterator(); it.hasNext();)
			((ListDataListener)it.next()).onChange(evt);
	}

	//-- ListModel --//
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	//Selectable
	public Set getSelection() {
		return Collections.unmodifiableSet(_selection);
	}
	
	public void addSelection(Object obj) {
		_selection.add(obj);
	}
	
	public void removeSelection(Object obj) {
		_selection.remove(obj);
	}
	
	public void clearSelection() {
		_selection.clear();
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
