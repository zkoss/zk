/* ChartDataListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:57:21     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.event;

import com.potix.zk.ui.UiException;

/**
 * Defines the methods used in listener when the content of
 * {@link com.potix.zul.html.ChartModel} is changed.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see com.potix.zul.html.ChartModel
 * @see ChartDataEvent
 */
public interface ChartDataListener {
	/** Sent when the contents of the list has changed.
	 */
	public void onChange(ChartDataEvent event);
}
