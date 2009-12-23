/* DesktopEventQueue.java

	Purpose:
		
	Description:
		
	History:
		Fri May  2 17:44:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zk.ui.event.EventQueue;

/**
 * The default implementation of the desktop-level event queue ({@link EventQueue}).
 * @author tomyeh
 * @since 5.0.0
 */
public class DesktopEventQueue implements EventQueue {
	private final Component _dummy = new AbstractComponent();
	private final List _listeners = new LinkedList();

	public DesktopEventQueue() {
		_dummy.addEventListener("onQueue", new EventListener() {
			public void onEvent(Event event) throws Exception {
				final Event evt = (Event)event.getData();
				for (Iterator it = _listeners.iterator(); it.hasNext();)
					((EventListener)it.next()).onEvent(evt);
			}
		});
	}

	/** Returns if there is listener being registered.
	 */
	public boolean isIdle() {
		return _listeners.isEmpty();
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
	public void close() {
		_listeners.clear();
	}
}
