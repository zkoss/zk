/* Deferrable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May  4 15:55:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

/**
 * Used to decorate {@link EventListener} to denote whether the event
 * for the listener can be deferred.
 *
 * <p>By default (without implementing {@link Deferrable}), the event is
 * sent to the server immediately when it is triggered at the client.
 *
 * <p>To make a listener deferrable, you have to implement {@link Deferrable}
 * and return true for {@link #isDeferrable}.
 * Then, the event won't be sent until another non-deferrable
 * event is about to send to the server.
 *
 * <p>The deferrable events are used to improve the performance by minimizing
 * the traffic between the clients and the server.
 * It is usully used
 * for event listeners that maintains the application states, rather
 * than generating visual responses.
 *
 * @author tomyeh
 * @see EventListener
 */
public interface Deferrable {
	/** Returns whether the event can be deferred for this listener.
	 * If false is returned, it is the same as not implementing this
	 * interface at all.
	 */
	public boolean isDeferrable();
}
