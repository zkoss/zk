/* CategoryModel.java

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

import org.jfree.data.general.*;
import org.jfree.data.category.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A Category data model implementation of {@link ChartModel}.
 * A Category model is an N series data objects.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see ChartModel
 * @see Chart
 * @see SimpleChartEngine
 */
public class CategoryModel extends BaseChartModel
implements java.io.Serializable {
	private static final long serialVersionUID = 20060809L;
	
	public CategoryModel() {
		setNativeModel(new DefaultCategoryDataset());
	}

	/** Set a combination of series, category, and data value. Set with same series and category set will 
	 * override the old one.
	 * @param series The series name of the data object.
	 * @param category The category name of a data object.
	 * @param value The value of a data object.
	 */
	public void setValue(Comparable series, Comparable category, Number value) {
		((DefaultCategoryDataset)_dataset).setValue(value, series, category);
	}
	
	/** Given series and return associated dataIndex. If not exists, return -1;
	 * @param series the specified series
	 */
	public int indexOfSeries(Comparable series) {
		return ((DefaultCategoryDataset)_dataset).getRowIndex(series);
	}

	/** Given category and return associated dataIndex. If not exists, return -1;
	 * @param category the specified category
	 */
	public int indexOfCategory(Comparable category) {
		return ((DefaultCategoryDataset)_dataset).getColumnIndex(category);
	}
		
	/** Remove a specific data object */
	public void removeValue(Comparable series, Comparable category) {
		((DefaultCategoryDataset)_dataset).removeValue(series, category);
	}

	/** Remove a specified category */
	public void removeCategory(Comparable category) {
		((DefaultCategoryDataset)_dataset).removeColumn(category);
	}

	/** Remove a specified series */
	public void removeSeries(Comparable series) {
		((DefaultCategoryDataset)_dataset).removeRow(series);
	}
	
	/** Clear the model */
	public void clear() {
		for(final Iterator it=((DefaultCategoryDataset)_dataset).getRowKeys().iterator(); it.hasNext();) {
			((DefaultCategoryDataset)_dataset).removeRow((Comparable)it.next());
		}
	}

	//-- ChartModel --//
	/**
	 * The returned object is an Object[], Object[0] is the numeric value,  Object[1] is the category name, and  
	 * Object[2] is the series name.
	 *
	 * @param seriesIndex this parameter is ignored, calling with 0 is ok.
	 * @param dataIndex specify the nth category-number.
	 */
	public Object getData(int seriesIndex, int dataIndex) {
		//ignore the seriesIndex because only one series index exists
		Object[] data = new Object[3];
		data[2] = ((DefaultCategoryDataset)_dataset).getRowKey(seriesIndex);
		data[1] = ((DefaultCategoryDataset)_dataset).getColumnKey(dataIndex);
		data[0] = ((DefaultCategoryDataset)_dataset).getValue((Comparable)data[2],  (Comparable)data[1]);
		
		return data;
	}
	
	/** Return the count of the categories.
	 */
	public int getSize(int seriesIndex) {
		return ((DefaultCategoryDataset)_dataset).getColumnCount();
	}
	
	/** Returns the count of series.
	 */
	public int getSeriesCount() {
		return ((DefaultCategoryDataset)_dataset).getRowCount();
	}
}
