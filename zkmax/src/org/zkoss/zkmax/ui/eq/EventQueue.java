/* EventQueue.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May  2 15:35:25     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.eq;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * An event queue.
 * An event queue is a many-to-many 'channel' to publish events and to subscribe
 * listeners.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public interface EventQueue {
	/** Publishes an event the queue.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * @exception IllegalStateException if this method is called
	 * not within an activated execution (such as a working thread).
	 */
	public void publish(Event event);
	/** Subscribes a listener to queue.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 */
	public void subscribe(EventListener listener);
	/** Unsubscribes a listener from the queue.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * @return true if the listener was subscribed.
	 */
	public boolean unsubscribe(EventListener listener);
}
