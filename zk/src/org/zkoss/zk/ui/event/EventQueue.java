/* EventQueue.java

	Purpose:
		
	Description:
		
	History:
		Fri May  2 15:35:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * An event queue.
 * An event queue is a many-to-many 'channel' to publish events and to subscribe
 * listeners.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface EventQueue {
	/** Publishes an event the queue.
	 *
	 * <p>If this is a desktop-level event queue, this method must be called
	 * within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * <p>On the other hand, if this is an application-level event queue,
	 * it is OK to be called without the current execution.
	 *
	 * @exception IllegalStateException if this method is called
	 * not within an activated execution (such as a working thread),
	 * and this is a desktop-level event queue.
	 */
	public void publish(Event event);
	/** Subscribes a listener to queue.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * <p>Note: if this is an application-level event queue, the listener
	 * shall not access the component associated with the event
	 * {@link Event#getTarget}.
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

	/** Closes the event queue.
	 * After closed, application cannot access any of its method.
	 * <p>Don't call this method directly. It is called only internally.
	 * Rather, use {@link EventQueues#remove} instead.
	 */
	public void close();
}
