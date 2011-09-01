/* ListDataListener.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:05:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zk.ui.UiException;

/**
 * Defines the methods used to listener when the content of
 * {@link org.zkoss.zul.ListModel} is changed.
 *
 * @author tomyeh
 * @see org.zkoss.zul.ListModel
 * @see ListDataEvent
 */
public interface ListDataListener {
	/** Sent when the contents of the list has changed.
	 */
	public void onChange(ListDataEvent event);
}
