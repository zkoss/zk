/* DesktopCache.java

	Purpose:

	Description:

	History:
		Tue Apr 18 10:38:22     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;

import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;

/**
 * The cache used to store desktops.
 *
 * @author tomyeh
 */
public interface DesktopCache {

	/** Returns the desktop for the specified desktop ID.
	 * @exception ComponentNotFoundException is thrown if the desktop
	 * is not found
	 */
	public Desktop getDesktop(String desktopId);

	/** Returns the desktop for the specified desktop ID, or null if not found.
	 */
	public Desktop getDesktopIfAny(String desktopId);

	/** Adds a desktop to this session.
	 * It must be called when a desktop is created.
	 * <p>Application shall never access this method.
	 */
	public void addDesktop(Desktop desktop);

	/** Removes a desktop from this session.
	 * It must be called when a desktop is remove.
	 * <p>Application shall never access this method.
	 */
	public void removeDesktop(Desktop desktop);

	/** Notification that the session is about to be passivated
	 * (a.k.a., serialized).
	 */
	public void sessionWillPassivate(Session sess);

	/** Notification that the session has just been activated
	 * (a.k.a., deserialized).
	 */
	public void sessionDidActivate(Session sess);

	/** Called when to stop and cleanup this cache.
	 * Once stopped, the caller shall not access it any more.
	 * It cannot be called other than the implementation of
	 * {@link DesktopCacheProvider}.
	 */
	public void stop();

	/** Returns all the desktops in this cache.
	 * It is used only internally.
	 * @since 10.1.0
	 */
	public List<Desktop> getAllDesktops();
}
