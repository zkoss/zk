/* SimplePieModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 11:20:14     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Pie chart data model implementation of {@link PieModel}.
 * Piechart model is an one series of (Category, value) data objects.
 *
 * @author henrichen
 * @see PieModel
 * @see Chart
 */
public class SimplePieModel extends AbstractChartModel implements PieModel {
	private static final long serialVersionUID = 20091008183556L;
	private List<Comparable<?>> _categoryList = new ArrayList<Comparable<?>>();
	private Map<Comparable<?>, Number> _categoryMap = new HashMap<Comparable<?>, Number>();
	
	//-- PieModel --//
	public Comparable<?> getCategory(int index) {
		return _categoryList.get(index);
	}

	public Collection<Comparable<?>> getCategories() {
		return _categoryList;
	}
	
	public Number getValue(Comparable category) {
		return _categoryMap.get(category);
	}

	public void setValue(Comparable<?> category, Number value) {
		if (!_categoryMap.containsKey(category)) {
			_categoryList.add(category);
		} else {
			Number ovalue = _categoryMap.get(category);
			if (Objects.equals(ovalue, value)) {
				return;
			}
		}
		_categoryMap.put(category, value);
		fireEvent(ChartDataEvent.CHANGED, null, category);
	}
	
	public void removeValue(Comparable<?> category) {
		_categoryMap.remove(category);
		_categoryList.remove(category);
		fireEvent(ChartDataEvent.REMOVED, null, category);
	}
	
	public void clear() {
		_categoryMap.clear();
		_categoryList.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}

	@Override
	public Object clone() {
		SimplePieModel clone = (SimplePieModel) super.clone();
		if (_categoryList != null)
			clone._categoryList = new ArrayList<Comparable<?>>(_categoryList);
		if (_categoryMap != null)
			clone._categoryMap = new HashMap<Comparable<?>, Number>(_categoryMap);
		return clone;
	}
}

