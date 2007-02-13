/* EventProcessingThread.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb 13 09:23:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
	 */
	public boolean isCeased();
	/** Returns whether this thread is idle, i.e., not processing any event.
	 */
	public boolean isIdle();

	/** Returns the event being processed by this thread, or null if idle.
	 */
	public Event getEvent();
	/** Returns the component being processed by this thread, or null if idle.
	 */
	public Component getComponent();
}
