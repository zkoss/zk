/* ListDataListener.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:05:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CloneableEventListener;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;

/**
 * Defines the methods used to listener when the content of
 * {@link org.zkoss.zul.ListModel} is changed.
 *
 * @author Christopher
 * @see org.zkoss.zul.ListModel
 * @see PagingEvent
 */
@SuppressWarnings("serial")
public abstract class PagingListener implements SerializableEventListener<Event>, CloneableEventListener<Event>{
	public abstract void onEvent(Event event) throws Exception;
	public abstract Object willClone(Component comp);
}
