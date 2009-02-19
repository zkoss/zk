/* ChartDataListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:57:21     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zk.ui.UiException;

/**
 * Defines the methods used in listener when the content of
 * {@link org.zkoss.zul.ChartModel} is changed.
 *
 * @author henrichen
 * @see org.zkoss.zul.ChartModel
 * @see ChartDataEvent
 */
public interface ChartDataListener {
	/** Sent when the contents of the list has changed.
	 */
	public void onChange(ChartDataEvent event);
}
