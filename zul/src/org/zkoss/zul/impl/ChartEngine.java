/* ChartEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:22:44     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

/**
 * Chart engine is an engine that do the real chart rendering.
 *
 * This interface defines the chart engine for components like {@link org.zkoss.zul.Chart}
 * use to get the value of each data and the size of the chart data.
 *
 * @author henrichen
 * @see org.zkoss.zul.Chart
 * @see org.zkoss.zul.ChartModel
 * @see org.zkoss.zul.event.ChartAreaListener
 */
public interface ChartEngine {
	/**
	 * Draw the chart and render into image format as an byte array.
	 * @param data the data used in drawing a chart; depends on implementation.
	 */
	public byte[] drawChart(Object data);
}
		
