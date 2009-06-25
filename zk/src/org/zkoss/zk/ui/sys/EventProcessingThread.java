/* EventProcessingThread.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb 13 09:23:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Represents an event processing thread.
 *
 * @author tomyeh
 */
public interface EventProcessingThread {
	/** Returns whether it is ceased.
	 *
	 * <p>The event processing thread is ceased automatically.
	 * If you want to cease it manually, you can invoke
	 * {@link DesktopCtrl#ceaseSuspendedThread}.
	 *
	 * <p>An event processing thread is ceased if {@link DesktopCtrl#ceaseSuspendedThread}
	 * was called manually, or if it is dead ({@link #isAlive} returns false).
	 * That is, it returns true if it is going to die, or dead already.
	 */
	public boolean isCeased();
	/** Returns whether it is suspended.
	 *
	 * @see DesktopCtrl#getSuspendedThreads
	 * @see DesktopCtrl#ceaseSuspendedThread
	 */
	public boolean isSuspended();

	/** Returns whether this thread is idle, i.e., not processing any event.
	 */
	public boolean isIdle();

	/** Returns the event being processed by this thread, or null if idle.
	 */
	public Event getEvent();
	/** Returns the component being processed by this thread, or null if idle.
	 */
	public Component getComponent();

	/** Sends the specified component and event and processes the event
	 * synchronously.
	 * Used to implements {@link org.zkoss.zk.ui.event.Events#sendEvent}.
	 * @since 3.0.2
	 */
	public void sendEvent(Component comp, Event event)
	throws Exception;
}
