/* EventThreadCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar  6 23:18:09     2006, Created by tomyeh@potix.com
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
 * Used to clean the event processing thread.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, an instance of the specified class is constructed and {@link #cleanup}
 * is invoked when the event processing thread has processed an event.</li>
 * </ol>
 *
 * <p>Thus, the typical use is to cleaup un-closed transactions
 * when {@link #cleanup} is called.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface EventThreadCleanup {
	/** Cleans up the event processing thread.
	 * It is called, after a event processing thread has processed an event.
	 */
	public void cleanup(Component comp, Event evt);
}
