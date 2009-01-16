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
	/** Sets the UI engine for this Web application.
	 * The UI engine is stateful so it is not safe to change it
	 * if there are active sessions.
	 * @param engine the UI engine
	 * @exception IllegalArgumentException if engine is null.
	 * @since 3.5.3
	 */
	public void setUiEngine(UiEngine engine);

	/** Returns the desktop cache.
	 * A shortcut of {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#getDesktopCache}.
	 */
	public DesktopCache getDesktopCache(Session sess);
	/** Returns the desktop cache provider.
	 */
	public DesktopCacheProvider getDesktopCacheProvider();
	/** Sets the desktop provider for this Web application.
	 * @param provider the desktop cache provider
	 * @exception IllegalArgumentException if provider is null.
	 * @since 3.5.3
	 */
	public void setDesktopCacheProvider(DesktopCacheProvider provider);

	/** Returns the UI factory for this Web application (never null).
	 */
	public UiFactory getUiFactory();
	/** Sets the UI factory for this Web application.
	 * @param factory the UI factory
	 * @exception IllegalArgumentException if factory is null.
	 * @since 3.5.3
	 */
	public void setUiFactory(UiFactory factory);

	/** Returns the failover manager, or null if not available.
	 */
	public FailoverManager getFailoverManager();
	/** Sets the failover manager for this Web application.
	 * @param manager the failover manager.
	 * If null, it means no failover manager at all.
	 * @since 3.5.3
	 */
	public void setFailoverManager(FailoverManager manager);

	/** Returns the ID generator, or null if not available.
	 *
	 * @since 2.4.1
	 */
	public IdGenerator getIdGenerator();
	/** Sets the ID generator for this Web application.
	 * @param generator the ID generator.
	 * If null, it means no (custom) ID generator at all.
	 * @since 3.5.3
	 */
	public void setIdGenerator(IdGenerator generator);

	/** Returns the session cache (never null).
	 * The session cache is used to store ZK sessions.
	 * @since 3.0.5
	 */
	public SessionCache getSessionCache();
	/** Sets the session cache for this Web application.
	 * @param cache the session cache
	 * @exception IllegalArgumentException if cache is null.
	 * @since 3.5.3
	 */
	public void setSessionCache(SessionCache cache);

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
	 * @since 3.5.1
	 */
	void sessionDestroyed(Session sess);
}
