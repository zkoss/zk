/* PieModel.java

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

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A Pie chart data model implementation of {@link ChartModel}.
 * Piechart model is an one series data objects.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see ChartModel
 * @see Chart
 * @see SimpleChartEngine
 */
public class PieModel extends BaseChartModel
implements java.io.Serializable {
	private static final long serialVersionUID = 20060809L;
	
	public PieModel() {
		setNativeModel(new DefaultPieDataset());
	}

	/** Set a pair of category and data value. Set with same category name will 
	 * override the old one.
	 * @param category The category name of a data object.
	 * @param value The value of a data object.
	 */
	public void setValue(Comparable category, Number value) {
		((DefaultPieDataset)_dataset).setValue(category, value);
	}
	
	/** Given category and return associated dataIndex. If not exists, return -1;
	 * @param category the specified category
	 */
	public int indexOfCategory(Comparable category) {
		return ((DefaultPieDataset)_dataset).getIndex(category);
	}
		
	/** Remove a specific data object */
	public void removeValue(Comparable category) {
		((DefaultPieDataset)_dataset).remove(category);
	}
	
	/** Clear the model */
	public void clear() {
		int size = getSize(0);
		for(final Iterator it=((DefaultPieDataset)_dataset).getKeys().iterator(); it.hasNext();) {
			removeValue((Comparable)it.next());
		}
	}
	
	//-- ChartModel --//
	/**
	 * The returned object is an Object[], Object[0] is the numeric value of the
	 * pie(category) and object[1] is the category name.
	 *
	 * @param seriesIndex this parameter is ignored, calling with 0 is ok.
	 * @param dataIndex specify the nth category-number.
	 */
	public Object getData(int seriesIndex, int dataIndex) {
		//ignore the seriesIndex because only one series index exists
		Object[] data = new Object[2];
		data[1] = ((DefaultPieDataset)_dataset).getKey(dataIndex);
		data[0] = ((DefaultPieDataset)_dataset).getValue((Comparable)data[1]);
		
		return data;
	}
	
	/** Return the size of the specified series. 
	 * @param seriesIndex this parameter is ignored, calling with 0 is ok.
	 */
	public int getSize(int seriesIndex) {
		return ((DefaultPieDataset)_dataset).getItemCount();
	}
	
	/** Returns the number of series; always return 1.
	 */
	public int getSeriesCount() {
		return 1;
	}
}
