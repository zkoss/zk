/* ProxyEvent.java

	Purpose:
		
	Description:
		
	History:
		Wed May  4 12:18:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Used to proxy another event.
 * It is used internally for {@link org.zkoss.zk.ui.Execution#postEvent(int, Component, Event)}
 * to handle if the real target of an event is different from {@link Event#getTarget}.
 *
 * @author tomyeh
 * @since 5.0.7
 */
public class ProxyEvent extends Event {
	private final Event _event;
	private final Component _realTarget;

	/** Constructor.
	 *
	 * <p>Notice that {@link #getTarget} is the same as the target of the given
	 * event. To retrieve the real target (passed thru the argument), please
	 * use {@link #getRealTarget} instead.
	 *
	 * @param realTarget the real target. If null, it means broadcast.
	 * @param event the proxied event.
	 */
	public ProxyEvent(Component realTarget, Event event) {
		super(event.getName(), event.getTarget(), event.getData());
		_realTarget = realTarget;
		_event = event;
	}
	/** Returns the real target.
	 */
	public Component getRealTarget() {
		return _realTarget;
	}
	/** Returns the proxied event.
	 */
	public Event getProxiedEvent() {
		return _event;
	}
}
