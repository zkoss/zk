/* EventListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 22 16:06:46     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.UiException;

/**
 * An listener that will be notified when an event occurs, if it is
 * registered to {@link org.zkoss.zk.ui.Component#addEventListener}.
 *
 * <p>If an event listener is also implemented {@link Express}, the event
 * listener is evaluated before all other listeners, including the onXxx member
 * declared as part of a ZUML page.
 *
 * @author tomyeh
 */
public interface EventListener {
	/** Notifies this listener that an event occurs.
	 * To get the event, you have to register it first by use of
	 * {@link org.zkoss.zk.ui.Component#addEventListener} or
	 * {@link org.zkoss.zk.ui.Page#addEventListener}.
	 *
	 * <p>If you want to forward the event to other component,
	 * use {@link Events#sendEvent}.
	 */
	public void onEvent(Event event);
	/** Returns whether the client shall send the event back as soon as
	 * it detects the event. Returning true might cause much more traffic
	 * between client and server, so be careful.
	 *
	 * <p>Note: due to performance issue, this method is used only by
	 * {@link org.zkoss.zk.ui.Component#addEventListener};
	 * and is ignored by {@link org.zkoss.zk.ui.Page#addEventListener}
	 */
	public boolean isAsap();
}
