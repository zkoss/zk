/* FlashChart.java

	Purpose:
		
	Description:
		
	History:
		Nov 26, 2009 12:19:18 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import java.util.List;

import org.zkoss.zul.ChartModel;

/**
 * A generic flashchart component.
 * 
 * @author Joy Lo
 * @date Created at Nov 25, 2009 4:28:49 PM
 * @since 5.0.0
 */
public interface FlashChart extends Flash {
	/**
	 * Sets the type of chart
	 * <p>Default: "pie"
	 * <p>Types: pie, line, bar, column
	 */
	public void setType(String type);
	/**
	 * Returns the type of chart
	 */
	public String getType();
	/**
	 * Sets allowScriptAccess as a param of flash object
	 * <p>Default: always 
	 * @param allowScriptAccess
	 */
	public void setAllowScriptAccess(String allowScriptAccess);
	/**
	 * Returns a string of allowScriptAccess value
	 */
	public String getAllowScriptAccess();
	/**
	 * Sets the model of chart.
	 * <p>Only implement models which matched the allowed types
	 * @param model
	 * @see #setType(String)
	 */
	public void setModel(ChartModel model);
	/**
	 * Returns the model of chart
	 */
	public ChartModel getModel();
	/**
	 * Sets a list to JSONModel, needs to transfer the data type to JSON. 
	 * @param list
	 */
	public void setJSONModel(List list);
	/**
	 * Returns a string which prepares to use in javascript as a chart data array
	 */
	public String getJSONModel();
}
