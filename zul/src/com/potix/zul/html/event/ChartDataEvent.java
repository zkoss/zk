/* ChartDataEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:59:03     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.event;

import com.potix.zul.html.ChartModel;

/**
 * Defines an event that encapsulates changes to a chart's data model. 
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
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
	private final String _seriesName;
	private final Object _data;

	public ChartDataEvent(ChartModel model, int type, String seriesName, Object data) {
		if (model == null)
			throw new NullPointerException();
		_model = model;
		_type = type;
		_seriesName = seriesName;
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
	public String getSeriesName() {
		return _seriesName;
	}
	/** Returns customed data. Depends on the implementation.
	 */
	public Object getData() {
		return _data;
	}
}
