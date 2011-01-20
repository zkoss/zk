/* FusionchartCategoryModel.java

	Purpose:
		
	Description:
		
	History:
		Dec 30, 2010 5:48:47 PM, Created by jimmyshiau

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.fusionchart;

import java.util.*;
import org.zkoss.zul.*;
import org.zkoss.zul.fusionchart.Fusionchart.FusionchartData;
import org.zkoss.zul.fusionchart.FusionchartCategoryModel.FusionchartSeries;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A Category data model implementation of {@link CategoryModel}. A Category
 * model is an N series of (category, value) data objects.
 * 
 * @author jimmyshiau
 * @see CategoryModel
 * @see Fusionchart
 */
public class FusionchartXYModel extends AbstractFusionchartModel implements XYModel {
	private static final long serialVersionUID = 20091008182904L;
	protected Map _seriesMap = new HashMap(13); // (series, XYPair)
	protected List _seriesList = new ArrayList(13);
	private boolean _autoSort = true;

	// -- XYModel --//
	public Comparable getSeries(int index) {
		return (Comparable) _seriesList.get(index);
	}

	public Collection getSeries() {
		return _seriesList;
	}

	public int getDataCount(Comparable series) {
		final List xyPairs = (List) _seriesMap.get(series);
		return xyPairs != null ? xyPairs.size() : 0;
	}

	public Number getX(Comparable series, int index) {
		final List xyPairs = (List) _seriesMap.get(series);

		if (xyPairs != null) {
			return ((FusionchartXYPair) xyPairs.get(index)).getX();
		}
		return null;
	}

	public Number getY(Comparable series, int index) {
		final List xyPairs = (List) _seriesMap.get(series);

		if (xyPairs != null) {
			return ((FusionchartXYPair) xyPairs.get(index)).getY();
		}
		return null;
	}
	
	public FusionchartData getFusionchartData(Comparable series, int index) {
		final List xyPairs = (List) _seriesMap.get(series);

		if (xyPairs != null) {
			return ((FusionchartXYPair) xyPairs.get(index)).getFusionchartData();
		}
		return null;
	}

	public void setValue(Comparable series, Number x, Number y, int index) {
		removeValue0(series, index);
		addValue0(series, x, new FusionchartData(y), index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}

	public void addValue(Comparable series, Number x, Number y) {
		addValue(series, x, y, -1);
	}
	
	public void addValue(Comparable series, Number x, Number y, String link) {
		addValue(series, x, y, link, -1);
	}
	
	public void addValue(Comparable series, Number x, Number y, int index) {
		addValue0(series, x, new FusionchartData(y), index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}
	
	public void addValue(Comparable series, Number x, Number y, String link, int index) {
		addValue0(series, x, new FusionchartData(y, null, link), index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}
	
	public void addValue(Comparable series, Number x, Number y, String link, int alpha, int index) {
		addValue0(series, x, new FusionchartData(y, null, link, alpha), index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}

	private void addValue0(Comparable series, Number x, FusionchartData data, int index) {
		List xyPairs = (List) _seriesMap.get(series);
		if (xyPairs == null) {
			xyPairs = new ArrayList(13);
			_seriesMap.put(series, xyPairs);
			_seriesList.add(series);
		}
		if (index >= 0)
			xyPairs.add(index, new FusionchartXYPair(x, data));
		else
			xyPairs.add(new FusionchartXYPair(x, data));
		
		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setOwner(this);
		}
	}

	public void setAutoSort(boolean auto) {
		_autoSort = auto;
	}

	public boolean isAutoSort() {
		return _autoSort;
	}

	public void removeSeries(Comparable series) {
		_seriesMap.remove(series);
		_seriesList.remove(series);
		// bug 2555730: Unnecessary String cast on 'series' in
		// SimpleCategoryModel
		fireEvent(ChartDataEvent.REMOVED, series, null);
	}

	public void removeValue(Comparable series, int index) {
		removeValue0(series, index);
		fireEvent(ChartDataEvent.REMOVED, series, null);
	}

	private void removeValue0(Comparable series, int index) {
		List xyPairs = (List) _seriesMap.get(series);
		if (xyPairs == null) {
			return;
		}
		xyPairs.remove(index);
		
		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setOwner((AbstractFusionchartModel)null);
		}
	}

	public void clear() {
		_seriesMap.clear();
		_seriesList.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}

	// -- internal class --//
	protected static class FusionchartXYPair implements java.io.Serializable {
		private static final long serialVersionUID = 20091008182941L;
		private Number _x;
		private FusionchartData _data;

		protected FusionchartXYPair(Number x, FusionchartData data) {
			_x = x;
			_data = data;
		}

		public FusionchartData getFusionchartData() {
			return _data;
		}
		
		public Number getX() {
			return _x;
		}
		
		public Number getY() {
			return _data.getValue();
		}
	}


}
