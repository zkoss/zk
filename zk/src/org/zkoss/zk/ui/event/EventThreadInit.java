/* EventThreadInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 28 19:17:40     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

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
 * @author tomyeh
 */
public interface EventThreadInit {
	/** Prepares the initialization at the servlet thread.
	 *
	 * <p>It is invoked in the servlet thread (and before {@link #init}).
	 * Thus, you can NOT manipulate the deskop in this method.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user.
	 * Note: {@link EventThreadCleanup#cleanup} won't be called if an
	 * exception is thrown in this method, since it executes in
	 * the main thread.
	 *
	 * <p>In addition to throwing an exception, you can prevent an event
	 * from processing by returning false in {@link #init}.
	 * The event is ignored 'silently' then.
	 */
	public void prepare(Component comp, Event event) throws Exception;

	/** Initialize the event processing thread before processing the event.
	 *
	 * <p>The simplest form is
	 * <code>void init(Component c, Event e) {return true;}</code>
	 *
	 * <p>Unlike {@link #prepare}, it is invoked in the event processing
	 * thread (and after {@link #prepare}).
	 * Thus, you can manipulate the desktop in this method such as
	 * creating a component.
	 *
	 * <p>If you want to prevent an event from processing, you can return
	 * false in this method.
	 * For example, you might create a highlighted window and return false
	 * to prevent the user from accessing, if the system is too busy.
	 *
	 * <p>If the use of the event thread is disabled
	 * ({@link org.zkoss.zk.ui.util.Configuration#isEventThreadEnabled}
	 * returns false), this method is also invoked in the Servlet thread.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup#cleanup}).
	 *
	 * @return if it is initialized successfully.
	 * If false is returned, the event is ignored, i.e., no event
	 * handler/listener will be invoked.
	 */
	public boolean init(Component comp, Event event) throws Exception;
}
