/* AbstractChartModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 03 11:50:19     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.io.Serializables;
import org.zkoss.zul.event.ChartDataEvent;
import org.zkoss.zul.event.ChartDataListener;

/**
 * A skeletal implementation for {@link ChartModel}.
 *
 * @author henrichen
 */
public abstract class AbstractChartModel implements ChartModel, java.io.Serializable {
	private static final long serialVersionUID = 20091007120455L;
	protected List<ChartDataListener> _listeners = new LinkedList<ChartDataListener>();

	/** Fires a {@link ChartDataEvent} for all registered listener
	 * (thru {@link #addChartDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 * @since 7.0.1
	 */
	protected void fireEvent(int type, Comparable<?> series, Comparable<?> category, int seriesIndex, int categoryIndex,
			Object data) {
		final ChartDataEvent evt = new ChartDataEvent(this, type, series, category, seriesIndex, categoryIndex, data);
		for (ChartDataListener l : _listeners)
			l.onChange(evt);
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
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList<ChartDataListener>();
		Serializables.smartRead(s, _listeners);
	}

	public Object clone() {
		final AbstractChartModel clone;
		try {
			clone = (AbstractChartModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._listeners = new LinkedList<ChartDataListener>();
		return clone;
	}
}
