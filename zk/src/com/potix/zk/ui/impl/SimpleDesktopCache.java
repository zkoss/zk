/* SimpleDesktopCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 13:00:34     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Iterator;
import java.util.ArrayList;

import com.potix.util.CacheMap;
import com.potix.util.prefs.Apps;
import com.potix.util.logging.Log;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.util.Monitor;
import com.potix.zk.ui.sys.DesktopCache;
import com.potix.zk.ui.sys.WebAppCtrl;

/**
 * A simple implementation of {@link DesktopCache}. It manages
 * all desktop in a {@link CacheMap} instance.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SimpleDesktopCache implements DesktopCache {
	private static final Log log = Log.lookup(SimpleDesktopCache.class);

	/** Used to purge obsolete desktops. */
	private final Cache _desktops = new Cache();
	/** The next available ID. */
	private int _nextId = ((int)System.currentTimeMillis()) & 0xffff;
		//to reduce the chance that two browsers with the same desktop ID
		//it is possible if we re-boot the server

	public SimpleDesktopCache() {
	}

	//-- DesktopCache --//
	public int getNextId() {
		synchronized (this) {
			return _nextId++;
		}
	}
	public Desktop getDesktop(String desktopId) {
		synchronized (_desktops) {
			final Desktop desktop = (Desktop)_desktops.get(desktopId);
			if (desktop == null)
				throw new ComponentNotFoundException("Desktop not found: "+desktopId);
			return desktop;
		}
	}
	public void addDesktop(Desktop desktop) {
		final boolean added;
		synchronized (_desktops) {
			final Object old = _desktops.put(desktop.getId(), desktop);
			if (old != null) {
				_desktops.put(((Desktop)old).getId(), old); //recover
				log.warning(
					desktop == old ? "Register a desktop twice: "+desktop:
						"Replicated ID: "+desktop+"; already used by "+old);
			}
			//if (log.debugable()) log.debug("After added, desktops: "+_desktops);
		}

	}
	public void removeDesktop(Desktop desktop) {
		synchronized (_desktops) {
			if (_desktops.remove(desktop.getId()) == null)
				log.warning("Removing non-existent desktop: "+desktop);
		}
		desktopDestroyed(desktop);
	}
	private static void desktopDestroyed(Desktop desktop) {
		final WebApp wapp = desktop.getWebApp();
		((WebAppCtrl)wapp).getUiEngine().cleanup(desktop);

		wapp.getConfiguration().invokeDesktopCleanups(desktop);

		final Monitor monitor = desktop.getWebApp().getConfiguration().getMonitor();
		if (monitor != null) {
			try {
				monitor.desktopDestroyed(desktop);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}

	public void stop() {
		synchronized (_desktops) {
			if (log.debugable()) log.debug("Invalidated and remove: "+_desktops);
			for (Iterator it = new ArrayList(_desktops.values()).iterator();
			it.hasNext();) {
				desktopDestroyed((Desktop)it.next());
			}
			_desktops.clear();
		}
	}

	/** Holds desktops. */
	private static class Cache extends CacheMap {
		private Cache() {
			super(5);
			setMaxSize(
				Apps.getInteger("com.potix.zk.ui.desktop.numPerSessions", 10));

			final int v =
				Apps.getInteger("com.potix.zk.ui.desktop.MaxInactiveInterval", 3600);
			setLifetime(v >= 0 ? v * 1000: Integer.MAX_VALUE);
		}
		/** To save memory, expunge whever necessary (not just when GC).
		protected java.lang.ref.ReferenceQueue newQueue() {
			return null;
		}*/
		protected int canExpunge(Value v) {
			if (((Desktop)v.getValue()).getExecution() != null)
				return EXPUNGE_NO|EXPUNGE_CONTINUE;
			return super.canExpunge(v);
		}
		protected void onExpunge(Value v) {
			super.onExpunge(v);

			desktopDestroyed((Desktop)v.getValue());
			if (log.debugable()) log.debug("Expunge desktop: "+v.getValue());
		}
	}
}
