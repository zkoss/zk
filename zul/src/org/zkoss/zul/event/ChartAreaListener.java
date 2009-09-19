/* ChartAreaListener.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 14:04:21     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zul.Area;

/**
 * Identifies area inside a chart that can be customized.
 *
 * @author henrichen
 * @see org.zkoss.zul.Chart
 */
public interface ChartAreaListener {
	/** Called when the chart is being cut into areas. When this method is called, the 
	 * area has been initiated with its default value already. Each time the 
	 * chart is repainted, the area is new created.
	 *
	 * @param area the area that cutting a chart.
	 * Note: when this method is called, the area has been initiated with
	 * its default value already.
	 * @param data generic data used to pass data from {@link org.zkoss.zul.impl.ChartEngine}.
	 */
	public void onRender(Area area, Object data) throws Exception;
}
