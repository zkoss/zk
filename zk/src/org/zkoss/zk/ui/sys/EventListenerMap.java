/* EventListenerMap.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 19:08:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;

/**
 * Represents a map of event handlers and listeners.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public interface EventListenerMap {
	/** Services the event under thegiven page and scope.
	 * @param scope the scope used to evaluate ZSCRIPT if any
	 * @param comp the component used to serve the event.
	 * @param cmd the real name of the event,
	 * used to retrieve event listener/handler
	 */
	public void service(Event event, Scope scope, Component comp, String cmd)
	throws Exception;
}
