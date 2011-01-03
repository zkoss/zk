/* Scheduler.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 22 09:58:11 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * A scheduler that is able to schedule a task to execute asynchronously.
 * A task is represented as event listener.
 * @author tomyeh
 * @since 5.0.6
 */
public interface Scheduler {
	/** Schedules the task to execute asynchronously.
	 * @param task the task to execute
	 * @param event the event to be passed to the task (i.e., the event listener).
	 * It could null or any instance as long as the task recognizes it.
	 */
	public void schedule(EventListener task, Event event);
}
