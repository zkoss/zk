/* EventThreadSuspend.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  2 20:53:27     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface EventThreadSuspend {
	/** Called before the event thread suspends.
	 *
	 * <p>If developers want to disable the suspend/resume feature or
	 * to limit number of suspended thread, they could throw
	 * {@link UiException} to prevent the event thread from suspending.
	 *
	 * @param obj which object that {@link com.potix.zk.ui.Executions#wait}
	 * is called with.
	 * @exception UiException preventing the thread from suspending.
	 */
	public void beforeSuspend(Component comp, Event evt, Object obj)
	throws UiException;
}
