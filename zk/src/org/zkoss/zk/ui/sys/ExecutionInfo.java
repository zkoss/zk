/* ExecutionInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 27 10:03:50 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * The infomation about the event being served by the current execution.
 * @see ExecutionCtrl#getExecutionInfo
 * @author tomyeh
 * @since 5.0.6
 */
public interface ExecutionInfo {
	/** Returns the thread serving the event.
	 */
	public Thread getThread();
	/** Returns the event being served (never null).
	 * <p>Notice: don't change the state of any UI objects (such as components
	 * and desktops) since they might belong to other
	 */
	public Event getEvent();
	/** Returns the method that serves the event, or null if it
	 * is served by a listener ({@link #getEventListener}), or
	 * a zscript ({@link #getEventZScript}).
	 */
	public Method getEventMethod();
	/** Returns the listener that serves the event, or null if it
	 * is served by a method ({@link #getEventMethod}), or
	 * a zscript ({@link #getEventZScript}).
	 */
	public EventListener getEventListener();
	/** Returns the zscript that serves the event, or null if it
	 * is served by a listener ({@link #getEventListener}), or
	 * a method ({@link #getEventMethod}).
	 * @since 5.0.8
	 */
	public ZScript getEventZScript();
	/** @deprecated As of release 5.0.8, replaced with {@link #getEventZScript}
	 */
	public ZScript getEventZscript();
}
