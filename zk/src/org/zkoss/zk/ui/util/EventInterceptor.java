/* EventInterceptor.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 15 21:53:44     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.event.Event;

/**
 * The event interceptor used to intercept the processing of events.
 *
 * <p>To register an event intercetper to a desktop, invoke
 * {@link org.zkoss.zk.ui.Desktop#addListener}.
 * To register an event intercepter to a configuration, invoke
 * {@link Configuration#addListener}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface EventInterceptor {
	/** Called before sending an event
	 * (with {@link org.zkoss.zk.ui.event.Events#sendEvent}).
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup}).
	 *
	 * @param event the event being sent
	 * @return the event to be sent (so it is usually the same event passed
	 * by the event argument). If null, the event is dropped.
	 * Note: multiple interceptors might be registered, and
	 * the returned value of an interceptor will be used for
	 * the following interceptor.
	 */
	public Event beforeSendEvent(Event event);
	/** Called before posting an event
	 * (with {@link org.zkoss.zk.ui.event.Events#postEvent}).
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup}).
	 *
	 * @param event the event being posted
	 * @return the event to be posted (so it is usually the same event passed
	 * by the event argument). If null, the event is dropped.
	 * Note: multiple interceptors might be registered, and
	 * the returned value of an interceptor will be used for
	 * the following interceptor.
	 */
	public Event beforePostEvent(Event event);
	/** Called before processing an event.
	 * By processing, we mean handing over the event to the event listeners
	 * and handlers.
	 *
	 * <p>Note: it executes in the event processing thread, so you can
	 * update the desktop whatever you need.
	 * For example, you might create a highlighted window and return null
	 * to prevent the user from accessing, if the system is too busy.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup}).
	 *
	 * @param event the event being processed
	 * @return the event to be processed (so it is usually the same event passed
	 * by the event argument). If null, the event is dropped.
	 * Note: multiple interceptors might be registered, and
	 * the returned value of an interceptor will be used for
	 * the following interceptor.
	 */
	public Event beforeProcessEvent(Event event);
	/** Called after processing an event.
	 * By processing, we mean handing over the event to the event listeners
	 * and handlers.
	 *
	 * <p>If this method throws an exception, it will abort the execution
	 * and shows an error message to the end user (unless it is cleaned
	 * up by {@link org.zkoss.zk.ui.event.EventThreadCleanup}).
	 *
	 * @param event the event being processed
	 */
	public void afterProcessEvent(Event event);
}
