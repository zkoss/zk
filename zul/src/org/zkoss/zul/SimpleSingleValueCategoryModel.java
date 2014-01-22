/* SimpleSingleValueCategoryModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 11:20:14     2006, Created by henrichen
		    June                2013, Renamed by rwenzel

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A Pie chart data model implementation of {@link SingleValueCategoryModel}.
 * SimpleSingleValueCategoryModel used by PieChart and FunnelChart is a series of (Category, singleValue) data objects.
 *
 * @author henrichen, rwenzel
 * @see SingleValueCategoryModel
 * @see Chart
 */
public class SimpleSingleValueCategoryModel extends AbstractChartModel implements SingleValueCategoryModel {
	private static final long serialVersionUID = 20091008183556L;
	private List<Comparable<?>> _categoryList = new ArrayList<Comparable<?>>();
	private Map<Comparable<?>, Number> _categoryMap = new HashMap<Comparable<?>, Number>();
	
	//-- SingleValueCategoryModel --//
	public Comparable<?> getCategory(int index) {
		return _categoryList.get(index);
	}

	public Collection<Comparable<?>> getCategories() {
		return _categoryList;
	}
	
	public Number getValue(Comparable<?> category) {
		return _categoryMap.get(category);
	}

	public void setValue(Comparable<?> category, Number value) {
		if (!_categoryMap.containsKey(category)) {
			final int cIndex = _categoryList.size();
			_categoryList.add(category);
			_categoryMap.put(category, value);
			fireEvent(ChartDataEvent.ADDED, null, category, 0, cIndex, value);
		} else {
			Number ovalue = _categoryMap.get(category);
			if (Objects.equals(ovalue, value)) {
				return;
			}
			final int cIndex = _categoryList.indexOf(category);
			_categoryMap.put(category, value);
			fireEvent(ChartDataEvent.CHANGED, null, category, 0, cIndex, value);
		}
	}
	
	public void removeValue(Comparable<?> category) {
		_categoryMap.remove(category);

		final int cIndex = _categoryList.indexOf(category);
		
		Comparable<?> value = _categoryList.remove(category);
		fireEvent(ChartDataEvent.REMOVED, null, category, 0, cIndex, value);
	}
	
	public void clear() {
		_categoryMap.clear();
		_categoryList.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null, -1, -1, null);
	}

	
	public Object clone() {
		SimpleSingleValueCategoryModel clone = (SimpleSingleValueCategoryModel) super.clone();
		if (_categoryList != null)
			clone._categoryList = new ArrayList<Comparable<?>>(_categoryList);
		if (_categoryMap != null)
			clone._categoryMap = new HashMap<Comparable<?>, Number>(_categoryMap);
		return clone;
	}
}

