/* SimpleCategoryModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 11:25:51     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Category data model implementation of {@link CategoryModel}.
 * A Category model is an N series of (category, value) data objects.
 *
 * @author henrichen
 * @see CategoryModel
 * @see Chart
 */
public class SimpleCategoryModel extends AbstractChartModel implements CategoryModel {
	private static final long serialVersionUID = 20091008183445L;
	private Map<Comparable<?>, LinkedList<Comparable<?>>> _seriesMap = new HashMap<Comparable<?>, LinkedList<Comparable<?>>>(); // (series, category)
	private List<Comparable<?>> _seriesList = new ArrayList<Comparable<?>>();

	private List<Comparable<?>> _categoryList = new ArrayList<Comparable<?>>();
	
	private Map<List<Comparable<?>>, Number> _valueMap = new LinkedHashMap<List<Comparable<?>>, Number>();
	
	//-- CategoryModel --//
	public Comparable<?> getSeries(int index) {
		return _seriesList.get(index);
	}

	public Comparable<?> getCategory(int index) {
		return _categoryList.get(index);
	}
	
	public Collection<Comparable<?>> getSeries() {
		return _seriesList;
	}

	public Collection<Comparable<?>> getCategories() {
		return _categoryList;
	}
	
	public Collection<List<Comparable<?>>> getKeys() {
		return _valueMap.keySet();
	}
	
	public Number getValue(Comparable<?> series, Comparable<?> category) {
		List<Comparable<?>> key = new ArrayList<Comparable<?>>(2);
		key.add(series);
		key.add(category);
		Number num = _valueMap.get(key);
		return num;
	}

	public void setValue(Comparable<?> series, Comparable<?> category, Number value) {
		List<Comparable<?>> key = new ArrayList<Comparable<?>>(2);
		key.add(series);
		key.add(category);
		
		if (!_valueMap.containsKey(key)) {
			if (!_categoryList.contains(category))
				_categoryList.add(category);
			
			LinkedList<Comparable<?>> list = _seriesMap.get(series);
			if (list == null) {
				list = new LinkedList<Comparable<?>>();
				list.add(category);
				_seriesMap.put(series, list);
				_seriesList.add(series);
			} else {
				list.add(category);
			}
			
			_valueMap.put(key, value);

			final int cIndex = list.indexOf(category);
			final int sIndex = _seriesList.indexOf(series);
			
			//bug 2555730: Unnecessary String cast on 'series' in SimpleCategoryModel
			fireEvent(ChartDataEvent.ADDED, series, category, sIndex, cIndex, value);
		} else {
			Number ovalue = _valueMap.get(key);
			if (Objects.equals(ovalue, value)) {
				return;
			}
			
			_valueMap.put(key, value);


			final int cIndex = _seriesMap.get(series).indexOf(category);
			final int sIndex = _seriesList.indexOf(series);
			
			//bug 2555730: Unnecessary String cast on 'series' in SimpleCategoryModel
			fireEvent(ChartDataEvent.CHANGED, series, category, sIndex, cIndex, value);
		}
	}
	
	public void removeValue(Comparable<?> series, Comparable<?> category) {
		List<Comparable<?>> key = new ArrayList<Comparable<?>>(2);
		key.add(series);
		key.add(category);
						
		if (_valueMap.remove(key) == null)
			return;
		final List<Comparable<?>> cateList =  _seriesMap.get(series);
		final int cIndex =  cateList.indexOf(category);
		final int sIndex = _seriesList.indexOf(series);
		final Number value = getValue(series, category);
		
		cateList.remove(cIndex);
		if (cateList.isEmpty()) {
			_seriesList.remove(series);
			_seriesMap.remove(series);
		}
		
		boolean clear = true;
		for (List<Comparable<?>> cate : _seriesMap.values()) {
			if (cate.contains(category)) {
				clear = false;
				break;
			}
		}
		
		if (clear)
			_categoryList.remove(category);
		
		//bug 2555730: Unnecessary String cast on 'series' in SimpleCategoryModel
		fireEvent(ChartDataEvent.REMOVED, series, category, sIndex, cIndex, value);
	}
	
	public void clear() {
		_seriesMap.clear();
		_seriesList.clear();
		_categoryList.clear();
		_valueMap.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null, -1, -1, null);
	}
	
	public Object clone() {
		SimpleCategoryModel clone = (SimpleCategoryModel) super.clone();
		if (_seriesMap != null)
			clone._seriesMap = new HashMap<Comparable<?>, LinkedList<Comparable<?>>>(_seriesMap);
		if (_seriesList != null)
			clone._seriesList = new ArrayList<Comparable<?>>(_seriesList);
		if (_categoryList != null)
			clone._categoryList = new ArrayList<Comparable<?>>(_categoryList);
		if (_valueMap != null)
			clone._valueMap = new LinkedHashMap<List<Comparable<?>>, Number>(_valueMap);
		return clone;
	}
}
