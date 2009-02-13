/* WebAppCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:07:30     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Additional interface of {@link org.zkoss.zk.ui.WebApp} for implementation.
 * <p>Note: applications shall never access this interface.
 * 
 * @author tomyeh
 */
public interface WebAppCtrl {
	/** Initializes this Web application.
	 *
	 * @param context the servlet context, if servlets are used.
	 * Currently, ZK supports only servlets. In the future there might be
	 * more to support.
	 * @param config the configuration (never null)
	 */
	public void init(Object context, Configuration config);
	/** Destroys this Web applicaiton.
	 * <p>Note; once destroyed, this instance cannot be used anymore.
	 */
	public void destroy();

	/** Returns the UI engine for this Web application (never null).
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
	/** Returns the UI factory for this Web application (never null).
	 */
	public UiFactory getUiFactory();
	/** Returns the failover manager, or null if not available.
	 */
	public FailoverManager getFailoverManager();
	/** Returns the ID generator, or null if not available.
	 *
	 * @since 2.4.1
	 */
	public IdGenerator getIdGenerator();

	/** Returns the session cache (never null).
	 * The session cache is used to store ZK sessions.
	 * @since 3.0.5
	 */
	public SessionCache getSessionCache();

	/** Notification that the session is about to be passivated
	 * (aka., serialized).
	 */
	public void sessionWillPassivate(Session sess);
	/** Notification that the session has just been activated
	 * (aka., deserialized).
	 */
	public void sessionDidActivate(Session sess);

	/** Called when the native session of the specified session
	 * has been destroyed
	 * @since 3.0.8
	 */
	void sessionDestroyed(Session sess);
}
