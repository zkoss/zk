/* GroupsDataListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  1 11:05:39     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

/**
 * Defines the methods used to listener when the content of
 * {@link org.zkoss.zul.GroupsModel} is changed.
 *
 * @author tomyeh
 * @see org.zkoss.zul.GroupsModel
 * @see GroupsDataEvent
 * @since 3.5.0
 */
public interface GroupsDataListener {
	/** Sent when the contents of the list has changed.
	 */
	public void onChange(GroupsDataEvent event);
}
