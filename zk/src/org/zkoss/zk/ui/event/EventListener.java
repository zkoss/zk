/* EventListener.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 22 16:06:46     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.UiException;

/**
 * An event listener that will be notified when an event occurs, if it is
 * registered to {@link org.zkoss.zk.ui.Component#addEventListener}.
 *
 * <p>If an event listener also implements {@link Deferrable}
 * and {@link Deferrable#isDeferrable} returns true, the event
 * for the listener will NOT be sent to the server immediately (when it
 * is fired at the client). It is called the deferrable event listener.
 * It helps to improve the performance since the deferred events will
 * be 'packed' together and sent to the server at once. It is usully used
 * for event listeners that maintains the application states, rather
 * than generating visual responses.
 *
 * <p>By default (i.e., if {@link Deferrable} is not implemented), the event
 * listener is not deferrable. It is also called the ASAP event listener.
 *
 * <p>By default, The event listener is invoked after the event handlers registered
 * in a ZUML page (i.e., the onXXX attribute).
 * If you prefer a listener to invoke earlier than the ZUML's event handlers,
 * you could register it with a priority equaling or higher than 1000
 * (with {@link org.zkoss.zk.ui.Component#addEventListener(int, String, EventListener)}).
 * @author tomyeh
 * @see Deferrable
 * @see SerializableEventListener
 */
public interface EventListener {
	/** Notifies this listener that an event occurs.
	 * To get the event, you have to register it first by use of
	 * {@link org.zkoss.zk.ui.Component#addEventListener} or
	 * {@link org.zkoss.zk.ui.Page#addEventListener}.
	 *
	 * <p>If you want to forward the event to other component,
	 * use {@link Events#sendEvent}.
	 */
	public void onEvent(Event event) throws Exception;
}
