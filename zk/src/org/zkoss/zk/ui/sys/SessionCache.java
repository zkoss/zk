/* SessionCache.java

	Purpose:
		
	Description:
		
	History:
		Sun Apr 20 19:18:35     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;

/**
 * The cache for storing ZK sessions.
 * The default implementation (@{link org.zkoss.zk.ui.http.SimpleSessionCache})
 * uses an attribute of the native session
 * to store the ZK session.
 * Provide your implementation if you want to use other ways to store
 * the ZK sessions (such as using a map).
 *
 * @author tomyeh
 * @since 3.0.5
 * @see org.zkoss.zk.ui.util.Configuration#setSessionCacheClass
 */
public interface SessionCache {
	/** Initializes the session cache.
	 * It is called right after being instantiated.
	 */
	public void init(WebApp wapp);
	/** Destroys the session cache
	 * @since 3.6.0
	 */
	public void destroy(WebApp wapp);
	/** Puts a ZK session to the cache.
	 * You can retrieve the native session by {@link Session#getNativeSession}.
	 * @param sess the ZK session.
	 */
	public void put(Session sess);
	/** Retrieves a ZK session from the cache, or null if the ZK session
	 * is not stored.
	 */
	public Session get(Object navsess);
	/** Removes the ZK session from the cache.
	 */
	public void remove(Session sess);
}
