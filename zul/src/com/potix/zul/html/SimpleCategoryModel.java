/* SimpleCategoryModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 11:25:51     2006, Created by henrichen@potix.com
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
import org.jfree.data.category.*;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Category data model implementation of {@link CategoryModel}.
 * A Category model is an N series of (category, value) data objects.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see CategoryModel
 * @see Chart
 * @see SimpleChartEngine
 * @see SimplePieModel
 */
public class SimpleCategoryModel extends BaseChartModel implements CategoryModel {
	private Map _seriesMap = new LinkedHashMap(13); //(series, SimplePieModel)
	
	//-- CategoryModel --//
	public Collection getSeries() {
		return _seriesMap.keySet();
	}

	public Collection getCategories(Comparable series) {
		SimplePieModel pmodel = (SimplePieModel) _seriesMap.get(series);
		if (pmodel != null) {
			return pmodel.getCategories();
		}
		return new ArrayList(0);
	}
		
	
	public Number getValue(Comparable series, Comparable category) {
		SimplePieModel pmodel = (SimplePieModel) _seriesMap.get(series);
		if (pmodel != null) {
			return (Number) pmodel.getValue(category);
		}
		return null;
	}

	public void setValue(Comparable series, Comparable category, Number value) {
		SimplePieModel pmodel = (SimplePieModel) _seriesMap.get(series);
		if (pmodel == null) {
			pmodel = new SimplePieModel();
			_seriesMap.put(series, pmodel);
		} else {
			Number ovalue = pmodel.getValue(category);
			if (Objects.equals(ovalue, value)) {
				return;
			}
		}
		pmodel.setValue(category, value);
		fireEvent(ChartDataEvent.CHANGED, (String) series, category);
	}
	
	public void removeSeries(Comparable series) {
		_seriesMap.remove(series);
		fireEvent(ChartDataEvent.CHANGED, (String) series, null);
	}
	
	public void removeValue(Comparable series, Comparable category) {
		SimplePieModel pmodel = (SimplePieModel) _seriesMap.get(series);
		if (pmodel == null) {
			return;
		}
		pmodel.removeValue(category);
		fireEvent(ChartDataEvent.REMOVED, (String)series, category);
	}
	
	public void clear() {
		_seriesMap.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}
}

