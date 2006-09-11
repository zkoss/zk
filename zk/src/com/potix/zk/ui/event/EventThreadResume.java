/* EventThreadResume.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  2 20:53:32     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Used to listen after the event processing thread is resumed.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, an instance of the specified class is constructed and
 * {@link #afterResume}
 * is invoked, after the thread is resumed.</li>
 * </ol>
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface EventThreadResume {
	/** Called after the event thread is resumed.
	 *
	 * <p>Unlike {@link EventThreadSuspend#beforeSuspend}, you cannot prevent
	 * a thread from resuming (by throwing an exception).
	 * If an exception is thrown, it is only logged.
	 *
	 * <p>This method is called in two situations:
	 * <ul>
	 * <li>Case 1: it is resumed. In this case, the aborted argument is
	 * false, and {@link #afterResume} is called in the resumed event processing thread.</li>
	 * <li>Case 2: it is aborted. In this case, the aborted argument is
	 * true, and {@link #afterResume} is NOT called in the resumed thread.</li>
	 * </ul>
	 *
	 * @param aborted whether it is caused by aborting (i.e., the thread
	 * will be destroyed). If false, it is resumed normally.
	 */
	public void afterResume(Component comp, Event evt, boolean aborted);
}
