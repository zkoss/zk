/* EventInterceptor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 15 21:53:44     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.event.Event;

/**
 * The event interceptor used to intercept the processing of events.
 *
 * <p>To register an event intercetper to a desktop, invoke
 * {@link org.zkoss.zk.ui.Desktop#addEventInterceptor}.
 * To register an event intercepter to a configuration, invoke
 * {@link Configuration#addEventInterceptor}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface EventInterceptor {
	/** Called before sending an event
	 * (with {@link org.zkoss.zk.ui.event.Events#sendEvent}).
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
	 * @param event the event being processed
	 */
	public void afterProcessEvent(Event event);
}
