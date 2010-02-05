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

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.au.out.AuScript;

/**
 * A server-push implementation that is based on client-polling.
 *
 * <p>Developer can control the frequency of the polling by setting the preferences as follows:
 * <dl>
 * <dt><code>PollingServerPush.delay.min</code></dt>
 * <dd>The minimal delay to send a polling request (unit: milliseconds). Default: 1000.</dd>
 * <dt><code>PollingServerPush.delay.max</code></dt>
 * <dd>The maximal delay to send a polling request (unit: milliseconds). Default: 15000.</dd>
 * <dt><code>PollingServerPush.delay.factor</code></dt>
 * <dd>the delay factor. The real delay is the processing time multiplies the delay
 * factor. For example, if the last request
 * took 1 second to process, then the client polling will be delayed
 * for <code>1 x factor</code> seconds. Default: 5.
 * The larger the factor is, the longer delay it tends to be.</dd>
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PollingServerPush implements ServerPush {
	private static final Log log = Log.lookup(PollingServerPush.class);
	/** Denote a server-push thread gives up the activation (timeout). */
	private static final int GIVEUP = -99;

	private Desktop _desktop;
	/** List of ThreadInfo. */
	private final List _pending = new LinkedList();
	/** The active thread. */
	private ThreadInfo _active;
	/** The info to carray over from onPiggyback to the server-push thread. */
	private ExecutionCarryOver _carryOver;
	/** A mutex that is used by this object to wait for the server-push thread
	 * to complete.
	 */
	private final Object _mutex = new Object();

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
		Clients.response(new AuScript(null, getStartScript()));
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
		Clients.response(new AuScript(null, getStopScript()));
	}
	/** Returns the JavaScript codes to enable (aka., start) the server push.
	 * It is called by {@link #startClientPush} to prepare the script
	 * of {@link AuScript} that will be sent to the client.
	 */
	protected String getStartScript() {
		final String start = _desktop.getWebApp().getConfiguration()
			.getPreference("PollingServerPush.start", null);
		if (start != null)
			return start;

		final String dtid = _desktop.getId();
		final StringBuffer sb = new StringBuffer(128)
			.append("zk.load('zk.cpsp');zk.afterLoad(function(){zk.cpsp.start('")
			.append(dtid).append('\'');

		final int min = getIntPref("PollingServerPush.delay.min"),
			max = getIntPref("PollingServerPush.delay.max"),
			factor = getIntPref("PollingServerPush.delay.factor");
		if (min > 0  || max > 0 || factor > 0)
			sb.append(',').append(min).append(',').append(max)
				.append(',').append(factor);

		return sb.append(");},'").append(dtid).append("');").toString();
	}
	private int getIntPref(String key) {
		final String s = _desktop.getWebApp().getConfiguration()
			.getPreference(key, null);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException ex) {
				log.warning("Not a number specified at "+key);
			}
		}
		return -1;
	}
			
	/** Returns the JavaScript codes to disable (aka., stop) the server push.
	 * It is called by {@link #stopClientPush} to prepare the script
	 * of {@link AuScript} that will be sent to the client.
	 */
	protected String getStopScript() {
		final String stop = _desktop.getWebApp().getConfiguration()
			.getPreference("PollingServerPush.stop", null);
		return stop != null ? stop:
			"zk.cpsp.stop('" + _desktop.getId() + "');";
	}

	//ServerPush//
	public boolean isActive() {
		return _active != null && _active.nActive > 0;
	}
	public void start(Desktop desktop) {
		if (_desktop != null) {
			log.warning("Ignored: Sever-push already started");
			return;
		}

		_desktop = desktop;
		startClientPush();
	}
	public void stop() {
		if (_desktop == null) {
			log.warning("Ignored: Sever-push not started");
			return;
		}

		final boolean inexec = Executions.getCurrent() != null;
		if (inexec) //Bug 1815480: don't send if timeout
			stopClientPush();

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
	private void wakePending() {
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

	/** @deprecated */
	public void setDelay(int min, int max, int factor) {
	}

	public void onPiggyback() {
		final Configuration config = _desktop.getWebApp().getConfiguration();
		long tmexpired = 0;
		for (int cnt = 0; !_pending.isEmpty();) {
			//Don't hold the client too long.
			//In addition, an ill-written code might activate again
			//before onPiggyback returns. It causes dead-loop in this case.
			if (tmexpired == 0) { //first time
				tmexpired = System.currentTimeMillis()
					+ (config.getMaxProcessTime() >> 1);
				cnt = _pending.size() + 3;
			} else if (--cnt < 0 || System.currentTimeMillis() > tmexpired) {
				break;
			}

			final ThreadInfo info;
			synchronized (_pending) {
				if (_pending.isEmpty())
					return; //nothing to do
				info = (ThreadInfo)_pending.remove(0);
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

	public boolean activate(long timeout)
	throws InterruptedException, DesktopUnavailableException {
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
						info.wait(timeout <= 0 ? 10*60*1000: timeout);

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

						log.debug("Executions.activate() took more than 10 minutes");
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
		if (_active != null &&
		Thread.currentThread().equals(_active.thread)) {
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

				try {Thread.sleep(100);} catch (Throwable ex) {}
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
