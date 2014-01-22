/* ChartDataEvent.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:59:03     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
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
	private final Comparable _category;
	private final Object _data;
	private int _sIndex = -1;
	private int _cIndex = -1;

	public ChartDataEvent(ChartModel model, int type, Comparable series, Comparable category, int seriesIndex, int categoryIndex, Object data) {
		if (model == null)
			throw new NullPointerException();
		_model = model;
		_type = type;
		_series = series;
		_data = data;
		_sIndex = seriesIndex;
		_cIndex = categoryIndex;
		_category = category;
	}
	
	/**
	 * Returns the series index, if any.
	 * @since 7.0.1
	 */
	public int getSeriesIndex() {
		return _sIndex;
	}

	/**
	 * Returns the category index, if any.
	 * @since 7.0.1
	 */
	public int getCategoryIndex() {
		return _cIndex;
	}

	/** Returns the category of the chart data model.
	 * @since 7.0.1
	 */
	public Comparable getCategory() {
		return _category;
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
	/** Returns the series of the chart data model.
	 */
	public Comparable getSeries() {
		return _series;
	}
	/** Returns customer data. Depends on the implementation.
	 */
	public Object getData() {
		return _data;
	}
}
