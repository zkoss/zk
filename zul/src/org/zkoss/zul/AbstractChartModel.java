/* AbstractChartModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 03 11:50:19     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

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

import org.zkoss.zul.event.ChartDataEvent;
import org.zkoss.zul.event.ChartDataListener;

/**
 * A skeletal implementation for {@link ChartModel}.
 *
 * @author henrichen
 */
abstract public class AbstractChartModel implements ChartModel, java.io.Serializable {
	private transient List _listeners = new LinkedList();

	/** Fires a {@link ChartDataEvent} for all registered listener
	 * (thru {@link #addChartDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, Comparable series, Object data) {
		final ChartDataEvent evt = new ChartDataEvent(this, type, series, data);
		for (Iterator it = _listeners.iterator(); it.hasNext();)
			((ChartDataListener)it.next()).onChange(evt);
	}

	//-- ChartModel --//
	public void addChartDataListener(ChartDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	public void removeChartDataListener(ChartDataListener l) {
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
