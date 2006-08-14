/* SimplePieModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 11:20:14     2006, Created by henrichen@potix.com
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

import org.jfree.data.general.*;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Pie chart data model implementation of {@link PieModel}.
 * Piechart model is an one series of (Category, value) data objects.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see PieModel
 * @see Chart
 */
public class SimplePieModel extends AbstractChartModel implements PieModel {
	private Map _categoryMap = new LinkedHashMap(13);
	
	//-- PieModel --//
	public Collection getCategories() {
		return _categoryMap.keySet();
	}
	
	public Number getValue(Comparable category) {
		return (Number)_categoryMap.get(category);
	}

	public void setValue(Comparable category, Number value) {
		Number ovalue = (Number)_categoryMap.get(category);
		if (Objects.equals(ovalue, value)) {
			return;
		}
		_categoryMap.put(category, value);
		fireEvent(ChartDataEvent.CHANGED, null, category);
	}
	
	public void removeValue(Comparable category) {
		_categoryMap.remove(category);
		fireEvent(ChartDataEvent.REMOVED, null, category);
	}
	
	public void clear() {
		_categoryMap.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}
}

