/* AbstractChartModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 03 11:50:19     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.potix.zk.ui.UiException;

import com.potix.zul.html.event.ChartDataEvent;
import com.potix.zul.html.event.ChartDataListener;

/**
 * A skeletal implementation for {@link ChartModel}.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
abstract public class AbstractChartModel implements ChartModel {
	private final List _listeners = new ArrayList(3);

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
}
