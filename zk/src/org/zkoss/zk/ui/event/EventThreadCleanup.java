/* EventThreadCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar  6 23:18:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Used to clean the event processing thread.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, an instance of the specified class is constructed and {@link #cleanup}
 * is invoked when the event processing thread has processed an event,
 * and then {@link #complete} of the same instance is called in the servlet
 * thread.</li>
 * </ol>
 *
 * <p>Thus, the typical use is to cleaup un-closed transactions
 * when {@link #cleanup} is called.
 * 
 * @author tomyeh
 */
public interface EventThreadCleanup {
	/** Cleans up the event processing thread.
	 * It is called, after a event processing thread has processed an event.
	 *
	 * <p>If this method threw an exception, it will be propagated back to
	 * the servlet thread and then reported to the user.
	 *
	 * <p>Note: {@link #cleanup} is called first in the event processing thread,
	 * and then {@link #complete} is called in the servlet thread.
	 *
	 * @param ex the exception being thrown (and not handled) during
	 * the processing of the event, or null it is executed successfully.
	 */
	public void cleanup(Component comp, Event evt, Throwable ex);
	/** Called in the serlvet thread to clean up.
	 * It is called after {@link #cleanup} is called.
	 *
	 * <p>Note: {@link #cleanup} is called first in the event processing thread,
	 * and then {@link #complete} is called in the servlet thread.
	 */
	public void complete(Component comp, Event evt);
}
