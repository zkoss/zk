/* EventQueueImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May  2 17:44:12     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.eq;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zkmax.ui.eq.EventQueue;

/**
 * A simple implementation of the event queue ({@link EventQueue}).
 * @author tomyeh
 * @since 3.5.0
 */
/*package*/ class EventQueueImpl implements EventQueue {
	private final Component _dummy = new AbstractComponent();
	private final List _listeners = new LinkedList();

	/*package*/ EventQueueImpl() {
		_dummy.addEventListener("onQueue", new EventListener() {
			public void onEvent(Event event) throws Exception {
				final Event evt = (Event)event.getData();
				for (Iterator it = _listeners.iterator(); it.hasNext();)
					((EventListener)it.next()).onEvent(evt);
			}
		});
	}

	//EventQueue//
	public void publish(Event event) {
		if (event == null)
			throw new IllegalArgumentException();
		Events.postEvent("onQueue", _dummy, event);
	}
	public void subscribe(EventListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		_listeners.add(listener);
	}
	public boolean unsubscribe(EventListener listener) {
		return _listeners.remove(listener);
	}
}
