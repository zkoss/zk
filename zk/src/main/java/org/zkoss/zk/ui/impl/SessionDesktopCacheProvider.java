/* SessionDesktopCacheProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:52:51     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.SessionCtrl;

/**
 * A implementation of {@link DesktopCacheProvider} that stores all desktops
 * from the same session in one desktop cache.
 *
 * <p>Implementation Note: we don't store the desktop cache in the session's
 * attribute (rather, we use {@link SessionCtrl#setDesktopCache}. Reason:
 * the desktop cache is serialized only if the session is serializable.
 *
 * @author tomyeh
 * @see GlobalDesktopCacheProvider
 */
public class SessionDesktopCacheProvider implements DesktopCacheProvider {
	private static final Logger log = LoggerFactory.getLogger(SessionDesktopCacheProvider.class);
	private WebApp _wapp;
	/** ZK-6102: tracks the per-session caches that are still alive so their
	 * cleaner timers (ZK-5435) can be cancelled when the web app is stopped.
	 * Weak keys so a cache whose session is serialized out or garbage-collected
	 * drops out by itself; a running cleaner timer keeps its cache reachable.
	 */
	private final Set<DesktopCache> _caches = Collections
			.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<DesktopCache, Boolean>()));

	//-- DesktopCacheProvider --//
	public DesktopCache getDesktopCache(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl) sess;
		DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc == null) {
			synchronized (this) {
				dc = sessCtrl.getDesktopCache();
				if (dc == null) {
					dc = new SimpleDesktopCache(_wapp.getConfiguration());
					sessCtrl.setDesktopCache(dc);
					_caches.add(dc);
				}
			}
		}
		return dc;
	}

	public void sessionDestroyed(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl) sess;
		final DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc != null) {
			sessCtrl.setDesktopCache(null);
			_caches.remove(dc);
			dc.stop();
		}
	}

	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionWillPassivate}.
	 */
	public void sessionWillPassivate(Session sess) {
		final DesktopCache dc = ((SessionCtrl) sess).getDesktopCache();
		if (dc != null)
			dc.sessionWillPassivate(sess);
	}

	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionDidActivate}.
	 */
	public void sessionDidActivate(Session sess) {
		final DesktopCache dc = ((SessionCtrl) sess).getDesktopCache();
		if (dc != null) {
			dc.sessionDidActivate(sess);
			// ZK-6102: a reactivated cache reaches the session by deserialization
			// (not via getDesktopCache) and re-arms its cleaner timer here, so it
			// must be tracked too; otherwise stop() cannot cancel that timer.
			_caches.add(dc);
		}
	}

	public void start(WebApp wapp) {
		_wapp = wapp;
	}

	public void stop(WebApp wapp) {
		// ZK-6102: stop any cache still alive at shutdown so its cleaner timer
		// thread is cancelled; otherwise the timer leaks past web app undeploy.
		final DesktopCache[] caches;
		synchronized (_caches) {
			caches = _caches.toArray(new DesktopCache[0]);
			_caches.clear();
		}
		for (DesktopCache dc : caches) {
			// isolate failures: a throwing dc.stop() (e.g. an app DesktopCleanup)
			// must not abort the sweep and leak the remaining caches' timers.
			try {
				dc.stop();
			} catch (Throwable ex) {
				log.warn("Failed to stop desktop cache: " + dc, ex);
			}
		}
		_wapp = null;
	}
}
