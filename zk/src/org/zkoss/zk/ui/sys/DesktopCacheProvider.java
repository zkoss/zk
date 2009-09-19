/* DesktopCacheProvider.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 10:54:20     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;

/**
 * Used to retrieve a desktop cache ({@link DesktopCache}).
 *
 * @author tomyeh
 */
public interface DesktopCacheProvider {
	/** Returns a desktop cache of the specified session.
	 *
	 * <p>The implementation might ignore session and use a global cache
	 * instead. Moreover, it might implement a cache supporting clustering.
	 */
	public DesktopCache getDesktopCache(Session session);
	/** Called when a session is destroyed.
	 * If the provider is session-based, it has to remove relevant desktops.
	 * If not, it might ignore this method.
	 *
	 * <p>Application shall never access this method.
	 */
	public void sessionDestroyed(Session session);

	/** Notification that the session is about to be passivated
	 * (aka., serialized).
	 *
	 * <p>It is called when {@link org.zkoss.zk.ui.sys.WebAppCtrl#sessionWillPassivate} is called.
	 */
	public void sessionWillPassivate(Session sess);
	/** Notification that the session has just been activated
	 * (aka., deserialized).
	 *
	 * <p>It is called when {@link org.zkoss.zk.ui.sys.WebAppCtrl#sessionDidActivate} is called.
	 */
	public void sessionDidActivate(Session sess);

	/** Starts the provider.
	 */
	public void start(WebApp wapp);
	/** Stops the provider.
	 */
	public void stop(WebApp wapp);
}
