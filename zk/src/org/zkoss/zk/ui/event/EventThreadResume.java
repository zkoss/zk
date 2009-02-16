/* EventThreadResume.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  2 20:53:32     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.Component;

/**
 * Used to listen after the event processing thread is resumed.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, when a suspended event thread is being resumed,
 * an instance of the specified class is constructed and
 * {@link #beforeResume} is called first in the main thread (i.e., the servlet
 * thread), right before resuming.
 * And then, {@link #afterResume} is invoked in the event processing thread,
 * after the thread is resumed.</li>
 * </ol>
 *
 * <p>In addition to resuming normally, a suspended event thread might be aborted
 * (usually caused by detroying the desktop owning the event thread).
 * In this case, {@link #abortResume} is called instead of {@link #beforeResume}
 * and {@link #afterResume}.
 *
 * @author tomyeh
 */
public interface EventThreadResume {
	/** Called just before the suspended event thread is resumed.
	 * Unlike {@link #afterResume}, it executes in the main thread that
	 * resumes the suspended event processing thread.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user.
	 * Note: {@link EventThreadCleanup#cleanup} won't be called if an
	 * exception is thrown in this method, since it executes in
	 * the main thread.
	 */
	public void beforeResume(Component comp, Event evt) throws Exception;
	/** Called after the suspended event thread is resumed.
	 * Unlike {@link #beforeResume}, it executes in the 
	 * event processing thread that is resumed.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup#cleanup}).
	 */
	public void afterResume(Component comp, Event evt) throws Exception;

 	/** Called when the suspended event thread is aborted.
 	 * It is called in the main thread (i.e., the servlet thread).
 	 *
 	 * <p>If a suspended event thread is resumed normally, {@link #beforeResume}
 	 * and {@link #afterResume} are called.
 	 * On the other hand, if it is aborted (usually caused by destroying
 	 * the desktop that owns this thread), {@link #abortResume} is called instead.
 	 *
 	 * <p>Note: if the suspended thread is aborted, none of {@link #beforeResume},
 	 * {@link #afterResume}, {@link EventThreadCleanup#cleanup}, and
 	 * {@link EventThreadCleanup#complete} will be called.
 	 * Thus, you have to do necessary cleanups in this method.
 	 */
 	public void abortResume(Component comp, Event evt) throws Exception;
}
