/* Monitor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Mar 14 23:25:45     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.List;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;

/**
 * A listener to monitor the status of ZK engine.
 * It is usually used to accumulate the statistic data about the number
 * of sessions, desktops and updates.
 *
 * @author tomyeh
 */
public interface Monitor {
	/** Called when a new session is created.
	 * @param sess the session being created
	 */
	public void sessionCreated(Session sess);
	/** Called when a session is being destroyed.
	 * @param sess the session being destroyed
	 */
	public void sessionDestroyed(Session sess);
	/** Called when a desktop is created.
	 *
	 * @param desktop the desktop being created
	 */
	public void desktopCreated(Desktop desktop);
	/** Called when a desktop is being destroyed.
	 *
	 * @param desktop the desktop being destroyed
	 */
	public void desktopDestroyed(Desktop desktop);
	/** Called when an asynchronous updated is called (and not yet processed).
	 *
	 * @param desktop the desktop that the update is sent to
	 * @param requests a list of {@link org.zkoss.zk.au.AuRequest} that
	 * are being processed.
	 */
	public void beforeUpdate(Desktop desktop, List requests);
	/** Called when an asynchronous updated has been processed.
	 *
	 * @param desktop the desktop that the update is sent to
	 */
	public void afterUpdate(Desktop desktop);
}
