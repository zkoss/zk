/* WebAppCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:07:30     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Session;

import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.UiFactory;

/**
 * Additional interface of {@link org.zkoss.zk.ui.WebApp} for implementation.
 * <p>Note: applications shall never access this interface.
 * 
 * @author tomyeh
 */
public interface WebAppCtrl {
	/** Initializes this WebApplication.
	 */
	public void init();
	/** Destroys this Web applicaiton.
	 */
	public void destroy();

	/** Returns the UI engine for this session.
	 */
	public UiEngine getUiEngine();
	/** Returns the desktop cache.
	 * A shortcut of {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#getDesktopCache}.
	 */
	public DesktopCache getDesktopCache(Session sess);
	/** Returns the desktop cache provider.
	 */
	public DesktopCacheProvider getDesktopCacheProvider();
	/** Returns the UI factory.
	 */
	public UiFactory getUiFactory();

	/** Notification that the session is about to be passivated
	 * (aka., serialized).
	 */
	public void sessionWillPassivate(Session sess);
	/** Notification that the session has just been activated
	 * (aka., deserialized).
	 */
	public void sessionDidActivate(Session sess);
}
