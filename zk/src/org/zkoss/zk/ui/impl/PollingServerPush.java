/* PollingServerPush.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  3 18:53:21     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import org.zkoss.lang.D;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.AuScript;

/**
 * A server-push implementation that is based on client-polling.
 *
 * @author tomyeh
 */
public class PollingServerPush implements ServerPush {
//	private static final Log log = Log.lookup(PollingServerPush.class);

	private Desktop _desktop;
	/** List of ThreadInfo. */
	private final List _pending = new LinkedList();
	/** The active thread. */
	private ThreadInfo _active;
	/** The info to carray over from onPiggyback to the server-push thread. */
	private CarryOver _carryOver;
	/** A mutex that is used by this object to wait for the server-push thread
	 * to complete.
	 */
	private final Object _mutex = new Object();

	/** Returns the JavaScript codes to enable (aka., start) the server push.
	 */
	protected String getStartScript() {
		final String start = _desktop.getWebApp()
			.getConfiguration().getPreference("PollingServerPush.start", null);
		return start != null ? start:
			"zk.load('zul.cpsp',function(){zkCpsp.start('"
				+ _desktop.getId() + "');});";
	}
	/** Returns the JavaScript codes to disable (aka., stop) the server push.
	 */
	protected String getStopScript() {
		final String stop = _desktop.getWebApp()
			.getConfiguration().getPreference("PollingServerPush.stop", null);
		return stop != null ? stop:
			"zkCpsp.stop('" + _desktop.getId() + "');";
	}


	//ServerPush//
	public void start(Desktop desktop) {
		_desktop = desktop;
		Clients.response(new AuScript(null, getStartScript()));
	}
	public void stop() {
		Clients.response(new AuScript(null, getStopScript()));
		_desktop = null;

		synchronized (_pending) {
			for (Iterator it = _pending.iterator(); it.hasNext();) {
				final ThreadInfo info = (ThreadInfo)it.next();
				synchronized (info) {
					info.notify();
				}
			}
			_pending.clear();
		}
	}

	public void onPiggyback() {
		while (!_pending.isEmpty()) {
			final ThreadInfo info;
			synchronized (_pending) {
				if (_pending.isEmpty())
					return; //nothing to do
				info = (ThreadInfo)_pending.remove(0);
			}

			synchronized (_mutex) {
				_carryOver = new CarryOver();

				synchronized (info) {
					info.notify();
				}
				try {
					_mutex.wait(); //wait until the server push is done
				} catch (InterruptedException ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
	}

	public void activate() {
		if (D.ON && Events.inEventListener())
			throw new IllegalStateException("No need to activate in the event listener");

		final Thread curr = Thread.currentThread();
		if (_active != null && _active.thread.equals(curr)) { //re-activate
			++_active.nActive;
			return;
		}

		final ThreadInfo info = new ThreadInfo(curr);
		synchronized (_pending) {
			if (_desktop != null)
				_pending.add(info);
		}

		try {
			synchronized (info) {
				if (_desktop != null)
					info.wait();
			}
		} catch (InterruptedException ex) {
			throw UiException.Aide.wrap(ex);
		}

		if (_desktop == null)
			throw new DesktopUnavailableException("Stopped");

		_carryOver.carryOver();
		_active = info;
	}
	public void deactivate() {
		if (_active != null &&
		Thread.currentThread().equals(_active.thread)) {
			if (--_active.nActive < 0) {
				_carryOver.cleanup();
				_carryOver = null;
				_active = null;

				//wake up onPiggyback
				synchronized (_mutex) {
					_mutex.notify();
				}
			}
		}
	}

	/** The info of a server-push thread.
	 * It is also a mutex used to start a pending server-push thread.
	 */
	private static class ThreadInfo {
		private final Thread thread;
		/** # of activate() was called. */
		private int nActive;
		private ThreadInfo(Thread thread) {
			this.thread = thread;
		}
		public String toString() {
			return "[" + thread + ',' + nActive + ']';
		}
	}
	/** The info to carry over from onPiggyback to the server-push thread.
	 */
	private static class CarryOver {
		/** The execution of onPiggyback. */
		private final Execution _exec;
		/** Part of the command: locale. */
		private final Locale _locale;
		/** Part of the command: time zone. */
		private final TimeZone _timeZone;

		private CarryOver() {
			_exec = Executions.getCurrent();
			_locale = Locales.getCurrent();
			_timeZone = TimeZones.getCurrent();
		}
		/** Carry over the info stored in the constructor to
		 * the current thread.
		 */
		private void carryOver() {
			ExecutionsCtrl.setCurrent(_exec);
			if (_locale != null && Locales.getThreadLocal() == null)
				Locales.setThreadLocal(_locale);
			if (_timeZone != null && TimeZones.getThreadLocal() == null)
				TimeZones.setThreadLocal(_timeZone);
		}
		/** Cleans up the info carried from onPiggyback to the current thread.
		 * <p>Note: {@link #carryOver} and {@link #cleanup} must be
		 * called in the same thread.
		 */
		private void cleanup() {
			ExecutionsCtrl.setCurrent(null);
		}
	}
}
