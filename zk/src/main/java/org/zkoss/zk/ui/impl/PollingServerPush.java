/* PollingServerPush.java

	Purpose:

	Description:

	History:
		Fri Aug  3 18:53:21     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.Scheduler;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A server-push implementation that is based on client-polling.
 *
 * <p>Developer can control the frequency of the polling by setting the preferences as follows:
 * <dl>
 * <dt><code>PollingServerPush.delay.min</code></dt>
 * <dd>The minimal delay to send the second polling request (unit: milliseconds). Default: 1000.</dd>
 * <dt><code>PollingServerPush.delay.max</code></dt>
 * <dd>The maximal delay to send the second polling request (unit: milliseconds). Default: 15000.</dd>
 * <dt><code>PollingServerPush.delay.factor</code></dt>
 * <dd>the delay factor. The real delay is the processing time multiplies the delay
 * factor. For example, if the last request
 * took 1 second to process, then the client polling will be delayed
 * for <code>1 x factor</code> seconds. Default: 5.
 * The larger the factor is, the longer delay it tends to be.</dd>
 *
 * <p>Another way to control the frequency is to instantiate an instance
 * with {@link #PollingServerPush(int, int, int)}, and then assign it
 * with {@link org.zkoss.zk.ui.sys.DesktopCtrl#enableServerPush(ServerPush)}.
 *
 * <pre><code>desktop.enableServerPush(new PollingServerPush(1000, 5000, -1));</code></pre>
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PollingServerPush implements ServerPush {
	private static final Logger log = LoggerFactory.getLogger(PollingServerPush.class);
	/** Denote a server-push thread gives up the activation (timeout). */
	private static final int GIVEUP = -99;

	private Desktop _desktop;
	/** List of ThreadInfo. */
	private final List<ThreadInfo> _pending = new LinkedList<ThreadInfo>();
	/** The active thread. */
	private ThreadInfo _active;
	/** The info to carry over from onPiggyback to the server-push thread. */
	private ExecutionCarryOver _carryOver;
	private final int _min, _max, _factor;
	/** A mutex that is used by this object to wait for the server-push thread
	 * to complete.
	 */
	private final Object _mutex = new Object();

	public PollingServerPush() {
		this(-1, -1, -1);
	}

	/**
	 * @param min the minimal delay before sending the second polling request
	 * (unit: milliseconds).
	 * If negative, the default is used (see {@link PollingServerPush}).
	 * @param max the maximal delay before sending the second polling request
	 * (unit: milliseconds).
	 * If negative, the default is used (see {@link PollingServerPush}).
	 * @param factor the delay factor.
	 * If negative, the default is used (see {@link PollingServerPush}).
	 * @since 5.0.4
	 */
	public PollingServerPush(int min, int max, int factor) {
		_min = min;
		_max = max;
		_factor = factor;
	}

	/** Sends an AU response to the client to start the server push.
	 * It is called by {@link #start}.
	 * <p>The derived class usually overrides this method to support
	 * different devices, such as ZK Mobile.
	 * <p>The default implementation is to send {@link AuScript} containing
	 * the script returned by {@link #getStartScript}.
	 * Devices that don't support scripts could override this method
	 * to send a custom AU response ({@link org.zkoss.zk.au.AuResponse}).
	 */
	protected void startClientPush() {
		Clients.response("zk.clientpush", new AuScript(null, getStartScript()));
	}

	/** Sends an AU response the client to stop the server push.
	 * <p>The derived class usually overrides this method to support
	 * different devices, such as ZK Mobile.
	 * <p>The default implementation is to send {@link AuScript} containing
	 * the script returned by {@link #getStopScript}.
	 * Devices that don't support scripts could override this method
	 * to send a custom AU response ({@link org.zkoss.zk.au.AuResponse}).
	 */
	protected void stopClientPush() {
		Clients.response("zk.clientpush", new AuScript(null, getStopScript()));
	}

	/** Returns the JavaScript codes to enable (a.k.a., start) the server push.
	 * It is called by {@link #startClientPush} to prepare the script
	 * of {@link AuScript} that will be sent to the client.
	 */
	protected String getStartScript() {
		final String start = _desktop.getWebApp().getConfiguration().getPreference("PollingServerPush.start", null);
		if (start != null)
			return start;

		final StringBuffer sb = new StringBuffer(128)
				.append("zk.load('zk.cpsp');zk.afterLoad(function(){zk.cpsp.start('").append(_desktop.getId())
				.append('\'');

		final int min = _min > 0 ? _min : getIntPref("PollingServerPush.delay.min"),
				max = _max > 0 ? _max : getIntPref("PollingServerPush.delay.max"),
				factor = _factor > 0 ? _factor : getIntPref("PollingServerPush.delay.factor");
		if (min > 0 || max > 0 || factor > 0)
			sb.append(',').append(min).append(',').append(max).append(',').append(factor);

		return sb.append(");});").toString();
	}

	private int getIntPref(String key) {
		final String s = _desktop.getWebApp().getConfiguration().getPreference(key, null);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException ex) {
				log.warn("Not a number specified at " + key);
			}
		}
		return -1;
	}

	/** Returns the JavaScript codes to disable (a.k.a., stop) the server push.
	 * It is called by {@link #stopClientPush} to prepare the script
	 * of {@link AuScript} that will be sent to the client.
	 */
	protected String getStopScript() {
		final String stop = _desktop.getWebApp().getConfiguration().getPreference("PollingServerPush.stop", null);
		return stop != null ? stop : "zk.cpsp.stop('" + _desktop.getId() + "');";
	}

	//ServerPush//
	public boolean isActive() {
		return _active != null && _active.nActive > 0;
	}

	public void start(Desktop desktop) {
		if (_desktop != null) {
			log.warn("Ignored: Sever-push already started");
			return;
		}

		_desktop = desktop;
		startClientPush();
	}

	/**
	 * ZK-1777 resume serverpush after DesktopRecycling
	 */
	public void resume() {
		if (_desktop == null) {
			throw new IllegalStateException(
					"ServerPush cannot be resumed without desktop, or has been stopped!call #start(desktop)} instead");
		}
		startClientPush();
	}

	public void stop() {
		if (_desktop == null) {
			log.warn("Ignored: Sever-push not started");
			return;
		}

		final Execution exec = Executions.getCurrent();
		final boolean inexec = exec != null && exec.getDesktop() == _desktop;
		//it might be caused by DesktopCache expunge (when creating another desktop)
		try {
			if (inexec && _desktop.isAlive()) //Bug 1815480: don't send if timeout
				stopClientPush();
		} finally {
			_desktop = null; //to cause DesktopUnavailableException being thrown
			wakePending();

			//if inexec, either in working thread, or other event listener
			//if in working thread, we cannot notify here (too early to wake).
			//if other listener, no need notify (since onPiggyback not running)
			if (!inexec) {
				synchronized (_mutex) {
					_mutex.notify(); //wake up onPiggyback
				}
			}
		}
	}

	private void wakePending() {
		synchronized (_pending) {
			for (ThreadInfo info : _pending) {
				synchronized (info) {
					info.notify();
				}
			}
			_pending.clear();
		}
	}

	public void onPiggyback() {
		// B65-ZK-2105: Need to check if desktop is null.
		if (_desktop == null)
			return;
		final Configuration config = _desktop.getWebApp().getConfiguration();
		long tmexpired = 0;
		for (int cnt = 0; !_pending.isEmpty();) {
			//Don't hold the client too long.
			//In addition, an ill-written code might activate again
			//before onPiggyback returns. It causes dead-loop in this case.
			if (tmexpired == 0) { //first time
				tmexpired = System.currentTimeMillis() + (config.getMaxProcessTime() >> 1);
				cnt = _pending.size() + 3;
			} else if (--cnt < 0 || System.currentTimeMillis() > tmexpired) {
				break;
			}

			final ThreadInfo info;
			synchronized (_pending) {
				if (_pending.isEmpty())
					return; //nothing to do
				info = _pending.remove(0);
			}

			//Note: we have to sync _mutex before info. Otherwise,
			//sync(info) might cause deactivate() to run before _mutex.wait
			synchronized (_mutex) {
				_carryOver = new ExecutionCarryOver(_desktop);

				synchronized (info) {
					if (info.nActive == GIVEUP)
						continue; //give up and try next
					info.nActive = 1; //granted
					info.notify();
				}

				if (_desktop == null) //just in case
					break;

				try {
					_mutex.wait(); //wait until the server push is done
				} catch (InterruptedException ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
	}

	public <T extends Event> void schedule(EventListener<T> listener, T event, Scheduler<T> scheduler) {
		scheduler.schedule(listener, event); //delegate back
	}

	public boolean activate(long timeout) throws InterruptedException, DesktopUnavailableException {
		final Thread curr = Thread.currentThread();
		if (_active != null && _active.thread.equals(curr)) { //re-activate
			++_active.nActive;
			return true;
		}

		final ThreadInfo info = new ThreadInfo(curr);
		synchronized (_pending) {
			if (_desktop != null)
				_pending.add(info);
		}

		boolean loop;
		do {
			loop = false;
			synchronized (info) {
				if (_desktop != null) {
					if (info.nActive == 0) //not granted yet
						info.wait(timeout <= 0 ? 10 * 60 * 1000 : timeout);

					if (info.nActive <= 0) { //not granted
						boolean bTimeout = timeout > 0;
						boolean bDead = _desktop == null || !_desktop.isAlive();
						if (bTimeout || bDead) { //not timeout
							info.nActive = GIVEUP; //denote timeout (and give up)
							synchronized (_pending) { //undo pending
								_pending.remove(info);
							}

							if (bDead)
								throw new DesktopUnavailableException("Stopped");
							return false; //timeout
						}

						if (log.isDebugEnabled()) {
							log.debug("Executions.activate() took more than 10 minutes");
						}
						loop = true; //try again
					}
				}
			}
		} while (loop);

		if (_desktop == null)
			throw new DesktopUnavailableException("Stopped");

		_carryOver.carryOver();
		_active = info;
		return true;

		//Note: we don't mimic inEventListener since 1) ZK doesn't assume it
		//2) Window depends on it
	}

	public boolean deactivate(boolean stop) {
		boolean stopped = false;
		if (_active != null && Thread.currentThread().equals(_active.thread)) {
			if (--_active.nActive <= 0) {
				if (stop)
					stopClientPush();

				_carryOver.cleanup();
				_carryOver = null;
				_active.nActive = 0; //just in case
				_active = null;

				if (stop) {
					wakePending();
					_desktop = null;
					stopped = true;
				}

				//wake up onPiggyback
				synchronized (_mutex) {
					_mutex.notify();
				}

				try {
					Thread.sleep(100);
				} catch (Throwable ignored) {
				}
				//to minimize the chance that the server-push thread
				//activate again, before onPiggback polls next _pending
			}
		}
		return stopped;
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
}
