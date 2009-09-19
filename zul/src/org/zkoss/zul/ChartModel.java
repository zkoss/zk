/* ChartModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:22:44     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ChartDataListener;

/**
 * Chart Model is used to hold the data model for the chart.
 * <ul>
 * <li>{@link PieModel}: one series of data objects. Each data object contains the category name and numeric value of a pie.</li>
 * <li>{@link CategoryModel}: n series of data objects. Each data object contains the category name and associated numeric value .</li>
 * <li>{@link XYModel}: n series of data objects. Each data object contains an (x,y) pair.</li>
 * <li>{@link XYZModel}: n series of data objects. Each data object contains an (x,y,z) pair; since 3.5.0</li>
 * </ul>
 *
 * This interface defines the data model for components like {@link Chart}
 * use to get the value of data.
 *
 * @author henrichen
 * @see Chart
 * @see PieModel
 * @see CategoryModel
 * @see XYModel
 * @see XYZModel
 * @see org.zkoss.zul.event.ChartAreaListener
 */
public interface ChartModel {
	/** Adds a listener to the chart that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addChartDataListener(ChartDataListener l);
    /** Removes a listener from the chart that's notified each time
     * a change to the data model occurs. 
     */
	public void removeChartDataListener(ChartDataListener l) ;
}
