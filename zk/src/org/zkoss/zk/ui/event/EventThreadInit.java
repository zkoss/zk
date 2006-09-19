/* EventThreadInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 28 19:17:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Used to initialize the event processing thread.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time the engine (in the main thread) is about to process
 * an event, it creates a new instance of the specified class, invokes
 * {@link #prepare}, and then pass it to the thread processing thread.
 * <li>After the thread processing thread is resumed, it invokes {@link #init}
 * to do the initialization.</li>
 * </ol>
 *
 * <p>In other words, {@link #prepare} is called in the servlet thread, while
 * {@link #init} is called in the event processing thread.
 * Therefore, the typical use is retrieving thread-relevant info in
 * {@link #prepare}, and then 
 * apply them in the event processing thread when {@link #init} is called.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface EventThreadInit {
	/** Prepares the initialization at the servlet thread.
	 * <p>It is invoked in the servlet thread (and before {@link #init}).
	 *
	 * @exception UiException to prevent an event from be processed
	 */
	public void prepare(Component comp, Event evt)
	throws UiException;

	/** Initialize the event processing thread before processing the event.
	 *
	 * <p>Unlike {@link #prepare}, it is invoked in the event processing
	 * thread (and after {@link #prepare}).
	 *
	 * <p>Any exception being thrown by this method is ignored (but logged).
	 */
	public void init(Component comp, Event evt);
}
