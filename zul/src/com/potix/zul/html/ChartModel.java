/* ChartModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:22:44     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zul.html.event.ChartDataListener;

/**
 * Chart Model is a set of series. Each series is a set of data objects.
 * <ul>
 * <li>PieModel: one series of data objects. Each data object contains the category name and numeric value of a pie.</li>
 * <li>CategoryModel: n series of data objects. Each data object contains the category name and associated numeric value .</li>
 * <li>XyModel: n series of data objects. Each data object contains an (x,y) pair.</li>
 * <li>XYZModel: n series of data objects. Each data object contains an (x,y,z) combination.</li>
 * </ul>
 *
 * This interface defines the data model for components like {@link Chart}
 * use to get the value of each data and the size of the chart data.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see Chart
 * @see AreaRenderer
 */
public interface ChartModel {
	/** Returns the data object of the specified series and data index.
	 */
	public Object getData(int seriesIndex, int dataIndex);
	/** Returns the length of the chart data of a specified series.
	 */
	public int getSize(int seriesIndex);
	/** Returns the number of series.
	 */
	public int getSeriesCount();

	/** Adds a listener to the chart that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addChartDataListener(ChartDataListener l);
    /** Removes a listener from the chart that's notified each time
     * a change to the data model occurs. 
     */
	public void removeChartDataListener(ChartDataListener l) ;
	
	/** Gets the original data model used by the real chart engine. */
	public Object getNativeModel();
}
