/* EventThreadSuspend.java

	Purpose:
		
	Description:
		
	History:
		Tue May  2 20:53:27     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Used to listen when the event processing thread is going to suspend.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, an instance of the specified class is constructed and
 * {@link #beforeSuspend}
 * is invoked, before the thread is going to suspend.
 * If {@link #beforeSuspend} throws a exception, the thread won't
 * be suspended.</li>
 * </ol>
 * 
 * @author tomyeh
 */
public interface EventThreadSuspend {
	/** Called before the event processing thread is suspended.
	 * It is called in the context of the event processing thread.
	 *
	 * <p>If developers want to disable the suspend/resume feature or
	 * to limit number of suspended thread, they could throw
	 * an exception to prevent the event thread from suspending.
	 *
	 * @param obj which object that {@link org.zkoss.zk.ui.Executions#wait}
	 * is called with.
	 * @exception Exception preventing the thread from suspending.
	 */
	public void beforeSuspend(Component comp, Event evt, Object obj)
	throws Exception;

	/** Called in the servlet thread after the event processing thread has been
	 * suspended.
	 *
	 * <p>Unlike {@link #beforeSuspend}, any exception thrown by this method
	 * are simply logged and ignored.
	 */
	public void afterSuspend(Component comp, Event evt) throws Exception;
}
