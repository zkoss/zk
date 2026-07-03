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
	private WebApp _wapp;
	/** Live per-session caches, tracked so {@link #stop} can cancel their cleaner
	 * timers (ZK-5435) without destroying passivatable desktops. Weak keys: an
	 * armed cache is held reachable by its own running timer, so an abandoned one
	 * drops out by itself once its timer is cancelled. Every access — and
	 * {@link #_stopped} — is guarded by this set's monitor. */
	private final Set<SimpleDesktopCache> _caches = Collections
			.newSetFromMap(new WeakHashMap<SimpleDesktopCache, Boolean>());
	/** Latched by {@link #stop} so a late activation cannot re-arm a cancelled timer. */
	private boolean _stopped;

	//-- DesktopCacheProvider --//
	public DesktopCache getDesktopCache(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl) sess;
		DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc != null)
			return dc;
		synchronized (this) {
			dc = sessCtrl.getDesktopCache();
			if (dc == null) {
				dc = new SimpleDesktopCache(_wapp.getConfiguration());
				sessCtrl.setDesktopCache(dc);
			}
		}
		registerCleaner(dc); // track on creation only, not per request
		return dc;
	}

	private void registerCleaner(DesktopCache dc) {
		if (dc instanceof SimpleDesktopCache) {
			final SimpleDesktopCache sdc = (SimpleDesktopCache) dc;
			synchronized (_caches) {
				if (_stopped)
					sdc.shutdownCleaner();
				else
					_caches.add(sdc);
			}
		}
	}

	public void sessionDestroyed(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl) sess;
		final DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc != null) {
			sessCtrl.setDesktopCache(null);
			if (dc instanceof SimpleDesktopCache) {
				synchronized (_caches) {
					_caches.remove(dc);
				}
			}
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
			dc.sessionDidActivate(sess); // re-arms the transient cleaner timer
			registerCleaner(dc); // track the reactivated cache (or cancel if stopped)
		}
	}

	public void start(WebApp wapp) {
		synchronized (_caches) {
			_stopped = false;
		}
		_wapp = wapp;
	}

	public void stop(WebApp wapp) {
		// cancel the cleaner timers without destroying (possibly passivating) desktops
		final SimpleDesktopCache[] caches;
		synchronized (_caches) {
			_stopped = true;
			caches = _caches.toArray(new SimpleDesktopCache[0]);
			_caches.clear();
		}
		for (SimpleDesktopCache dc : caches)
			dc.shutdownCleaner();
		_wapp = null;
	}
}
