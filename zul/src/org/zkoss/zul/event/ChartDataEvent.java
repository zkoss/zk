/* ChartDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:59:03     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.ChartModel;

/**
 * Defines an event that encapsulates changes to a chart's data model. 
 *
 * @author henrichen
 */
public class ChartDataEvent {
	/** Identifies one or more changes in the lists contents. */
	public static final int CHANGED = 0;
    /** Identifies the addition of one or more contiguous items to the list. */    
	public static final int ADDED = 1;
    /** Identifies the removal of one or more contiguous items from the list. */   
	public static final int REMOVED = 2;

	/** Identifies one or more changes in the charts contents. */
	private final ChartModel _model;
	private final int _type;
	private final Comparable _series;
	private final Object _data;

	public ChartDataEvent(ChartModel model, int type, Comparable series, Object data) {
		if (model == null)
			throw new NullPointerException();
		_model = model;
		_type = type;
		_series = series;
		_data = data;
	}
	/** Returns the chart model that fires this event.
	 */
	public ChartModel getModel() {
		return _model;
	}
	/** Returns the event type: CHANGED, ADDED, REMOVED.
	 */
	public int getType() {
		return _type;
	}
	/** Returns the series index of the chart data model.
	 */
	public Comparable getSeries() {
		return _series;
	}
	/** Returns customed data. Depends on the implementation.
	 */
	public Object getData() {
		return _data;
	}
}
