/* PagingListener.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 23 18:05:15 2015, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

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
 * Provide a shortcut for PagingEventListener
 *
 * @author Christopher
 * @see PagingEvent
 * @see SerializableEventListener
 * @see CloneableEventListener
 * @since 8.0.0
 */
public interface PagingListener extends SerializableEventListener<Event>, CloneableEventListener<Event> {
	public abstract void onEvent(Event event) throws Exception;

	public abstract Object willClone(Component comp);
}
