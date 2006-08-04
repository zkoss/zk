/* PiechartModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 03 12:51:14     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.zul.html.event.ChartDataEvent;
import com.potix.zul.html.event.ChartDataListener;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * A Piechart data model implementation of {@link ChartModel}.
 * Piechart model is an one series data objects.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see ChartModel
 */
public class PiechartModel extends AbstractChartModel
implements java.io.Serializable {
    private static final long serialVersionUID = 20060803L;

	private List _indexList = new ArrayList(13);
	private Map _map = new HashMap(13);

	/** Set a pair of category and data value. Set with same category name will 
	 * override the old one.
	 * @param category The category name of a data object.
	 * @param value The value of a data object.
	 */
	public void setValue(String category, Double value) {
		if (!_map.containsKey(category)) {
			_indexList.add(category);
		} else {
			Object v = _map.get(category);
			if (Objects.equals(value, v)) {
				return;
			}
		}
		_map.put(category, value);

		//since our listener doesn't care which data object is changed, we put dataIndex to always 0.
		fireEvent(ChartDataEvent.ADDED, null, new Object[] {category, value}); 
	}
	
	/** Given category and return associated dataIndex. If not exists, return -1;
	 * @param category the specified category
	 */
	public int indexOf(String category) {
		return _indexList.indexOf(category);
	}
		
	/** Remove a specific data object */
	public void removeValue(String category) {
		_indexList.remove(category);
		_map.remove(category);
	}
	
	/** Clear the model */
	public void clear() {
		_indexList.clear();
		_map.clear();
	}

	//-- ChartModel --//
	/**
	 * The returned object is an Object[], Object[0] is the category while 
	 * object[1] is the numeric value of the pie.
	 *
	 * @param seriesIndex this parameter is ignored, calling with 0 is ok.
	 * @param dataIndex specify the nth category-number.
	 */
	public Object getData(int seriesIndex, int dataIndex) {
		//ignore the seriesIndex because only one series index exists
		Object[] data = new Object[2];
		data[0] = _indexList.get(dataIndex);
		data[1] = _map.get(data[0]);
		
		return data;
	}
	
	/** Return the size of the specified series. 
	 * @param seriesIndex this parameter is ignored, calling with 0 is ok.
	 */
	public int getSize(int seriesIndex) {
		return _indexList.size();
	}
	
	/** Returns the number of series; always return 1.
	 */
	public int getSeriesCount() {
		return 1;
	}
	
	//-- Object --//
	public int hashCode() {
		return _map.hashCode() ^ _indexList.hashCode();
	}
	
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || !(other instanceof PiechartModel)) {
			return false;
		}
		final PiechartModel otherm = (PiechartModel) other;
		return _indexList.equals(otherm._indexList) && _map.equals(otherm._map);
	}
}
