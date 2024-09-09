/* SimpleDesktopCache.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Apr 18 13:00:34     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.util.CacheMap;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DesktopRecycle;
import org.zkoss.zk.ui.util.Monitor;

/**
 * A simple implementation of {@link DesktopCache}. It manages
 * all desktop in a {@link CacheMap} instance.
 *
 * @author tomyeh
 */
public class SimpleDesktopCache implements DesktopCache, java.io.Serializable {
	private static final Logger log = LoggerFactory.getLogger(SimpleDesktopCache.class);
	private static final long serialVersionUID = 20060622L;

	/** Used to purge obsolete desktops. */
	private final Cache _desktops;
	//to reduce the chance that two browsers with the same desktop ID
	//it is possible if we re-boot the server

	private transient Timer _cleaner;
	public SimpleDesktopCache(Configuration config) {
		_desktops = new Cache(config);

		// ZK-5435
		int desktopMaxInactiveInterval = config.getDesktopMaxInactiveInterval();
		if (desktopMaxInactiveInterval >= 0) {
			_cleaner = new Timer();
			_cleaner.scheduleAtFixedRate(new CleanerTask(), desktopMaxInactiveInterval * 1_000L, desktopMaxInactiveInterval * 1_000L);
		}
	}

	//-- DesktopCache --//

	public Desktop getDesktopIfAny(String desktopId) {
		final boolean old = _desktops.disableExpunge(true);
		try {
			synchronized (_desktops) {
				return _desktops.get(desktopId);
			}
		} finally {
			_desktops.disableExpunge(old);
		}
	}

	public Desktop getDesktop(String desktopId) {
		final Desktop desktop;
		synchronized (_desktops) {
			desktop = _desktops.get(desktopId);
		}
		if (desktop == null)
			throw new ComponentNotFoundException("Desktop not found: " + desktopId);
		return desktop;
	}

	public void addDesktop(Desktop desktop) {
		//final boolean added;
		final Desktop old;
		synchronized (_desktops) {
			old = _desktops.put(desktop.getId(), desktop);
		}
		if (old != null) {
			_desktops.put(old.getId(), old); //recover
			if (log.isWarnEnabled()) {
				log.warn(desktop == old ? "Register a desktop twice: " + desktop
						: "Replicated ID: " + desktop + "; already used by " + old);
			}
		}
		//if (log.isDebugEnabled()) log.debug("After added, desktops: "+_desktops);
	}

	public void removeDesktop(Desktop desktop) {
		final boolean oldexp = _desktops.disableExpunge(true);
		try {
			final Desktop old;
			synchronized (_desktops) {
				old = _desktops.remove(desktop.getId());
			}
			if (old == null)
				log.warn("Removing non-existent desktop: " + desktop);
			else
				desktopDestroyed(desktop);
		} finally {
			_desktops.disableExpunge(oldexp);
		}
	}

	private static void desktopDestroyed(Desktop desktop) {
		final Session sess = desktop.getSession();
		final Execution exec = new ExecutionImpl(desktop.getWebApp().getServletContext(), null, null, desktop, null);
		final DesktopCtrl desktopCtrl = (DesktopCtrl) desktop;
		final Execution oldExec = desktop.getExecution();
		final Visualizer oldVi = desktopCtrl.getVisualizer();
		final Execution currentExec = Executions.getCurrent();

		try {
			// For ZK-1890: Can't subscribe eventqueue in desktop cleanup
			ExecutionsCtrl.setCurrent(exec);
			final UiVisualizer uv = new UiVisualizer(exec, true, false);
			desktopCtrl.setVisualizer(uv);
			desktopCtrl.setExecution(exec);

			final WebApp wapp = desktop.getWebApp();
			((DesktopCtrl) desktop).invokeDesktopCleanups();
			final Configuration config = wapp.getConfiguration();
			config.invokeDesktopCleanups(desktop);
			//Feature 1767347: call DesktopCleanup before desktopDestroyed
			//such that app dev has a chance to manipulate the desktop
			((WebAppCtrl) wapp).getUiEngine().desktopDestroyed(desktop);

			final Monitor monitor = desktop.getWebApp().getConfiguration().getMonitor();
			if (monitor != null) {
				try {
					monitor.desktopDestroyed(desktop);
				} catch (Throwable ex) {
					log.error("", ex);
				}
			}

			final DesktopRecycle dtrc = config.getDesktopRecycle();
			if (dtrc != null) {
				try {
					dtrc.afterRemove(sess, desktop);
				} catch (Throwable ex) {
					log.error("", ex);
				}
			}
		} finally {
			// reset
			//ZK-3214: Desktop session might not current session
			ExecutionsCtrl.setCurrent((sess != Sessions.getCurrent() || (oldVi != null && oldVi.isRecovering())) ? currentExec : null);
			desktopCtrl.setVisualizer(oldVi);
			desktopCtrl.setExecution(oldExec);
		}
	}

	/** Invokes {@link DesktopCtrl#sessionWillPassivate} for each
	 * desktops it cached.
	 */
	public void sessionWillPassivate(Session sess) {
		final boolean old = _desktops.disableExpunge(true);
		try {
			ArrayList<Desktop> desktops = null;
			synchronized (_desktops) {
				desktops = new ArrayList<Desktop>(_desktops.values());
			}

			for (Desktop desktop : desktops)
				((DesktopCtrl) desktop).sessionWillPassivate(sess);

		} finally {
			_desktops.disableExpunge(old);
		}
	}

	/** Invokes {@link DesktopCtrl#sessionDidActivate} for each
	 * desktops it cached.
	 */
	public void sessionDidActivate(Session sess) {
		final boolean old = _desktops.disableExpunge(true);

		try {
			ArrayList<Desktop> desktops = null;
			synchronized (_desktops) {
				desktops = new ArrayList<Desktop>(_desktops.values());
			}

			for (Desktop desktop : desktops)
				((DesktopCtrl) desktop).sessionDidActivate(sess);

			int lifetime = _desktops.getLifetime();
			if (lifetime >= 0 && lifetime < Integer.MAX_VALUE / 4) {
				// enable cleaner timer
				_cleaner = new Timer();
				_cleaner.scheduleAtFixedRate(new CleanerTask(), lifetime, lifetime);
			}
		} finally {
			_desktops.disableExpunge(old);
		}
	}

	public void stop() {
		if (log.isDebugEnabled())
			log.debug("Invalidated and remove: {}", _desktops);
		final boolean old = _desktops.disableExpunge(true);
		try {
			ArrayList<Desktop> desktops = null;
			synchronized (_desktops) {
				desktops = new ArrayList<Desktop>(_desktops.values());
				_desktops.clear();
			}
			for (Desktop desktop : desktops) {
				desktopDestroyed(desktop);
			}
		} finally {
			_desktops.disableExpunge(old);
		}

		// ZK-5435
		if (_cleaner != null) {
			_cleaner.cancel();
		}
	}

	/** Holds desktops. */
	private static class Cache extends CacheMap<String, Desktop> { //serializable
		private AtomicBoolean _expungeDisabled = new AtomicBoolean(false);

		private Cache(Configuration config) {
			super(16);

			int v = config.getSessionMaxDesktops();
			setMaxSize(v >= 0 ? v : Integer.MAX_VALUE / 4);

			v = config.getDesktopMaxInactiveInterval();
			setLifetime(v >= 0 ? v * 1000 : Integer.MAX_VALUE / 4);
		}

		private boolean disableExpunge(boolean disable) {
			return _expungeDisabled.getAndSet(disable);
		}

		protected boolean shallExpunge() {
			return !_expungeDisabled.get() && (super.shallExpunge() || sizeWithoutExpunge() > (getMaxSize() / 2));
			//2012-12-07 Ian: expunge should be triggered often
			//1) disable expunge if serialization/activation
			//2) to minimize memory use, expunge even if no GC
		}

		protected int canExpunge(int size, Value<Desktop> v) {
			if (v.getValue().getExecution() != null)
				return EXPUNGE_NO | EXPUNGE_CONTINUE;
			return super.canExpunge(size, v);
		}

		protected void onExpunge(Value<Desktop> v) {
			super.onExpunge(v);

			desktopDestroyed(v.getValue());
			if (log.isDebugEnabled())
				log.debug("Expunge desktop: {}", v.getValue());
		}

		private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
			s.defaultReadObject();
			disableExpunge(false);
		}

		private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			final boolean old = disableExpunge(true);
			try {
				s.defaultWriteObject();
			} finally {
				disableExpunge(old);
			}
		}
	}

	// ZK-5435
	private class CleanerTask extends TimerTask {
		public void run() {
			synchronized (_desktops) {
				_desktops.expunge();
			}
		}
	}
}
