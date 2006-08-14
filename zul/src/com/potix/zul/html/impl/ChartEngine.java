/* ChartEngine.java

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
package com.potix.zul.html.impl;

/**
 * Chart engine is an engine that do the real chart rendering.
 *
 * This interface defines the chart engine for components like {@link com.potix.zul.html.Chart}
 * use to get the value of each data and the size of the chart data.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see com.potix.zul.html.Chart
 * @see com.potix.zul.html.ChartModel
 * @see com.potix.zul.html.event.ChartAreaListener
 */
public interface ChartEngine {
	/**
	 * Draw the chart and render into image format as an byte array.
	 * @param data the data used in drawing a chart; depends on implementation.
	 */
	public byte[] drawChart(Object data);
}
		
