/* ServerPushEventQueue.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 23 19:30:21     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.event.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.lang.Threads;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.AuRequest;

/**
 * The default implementation of the server-push based event queue
 * ({@link EventQueue}).
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public class ServerPushEventQueue implements EventQueue {
	private static final Log log = Log.lookup(ServerPushEventQueue.class);
	/** A map of (Desktop, DesktopThread). */
	private final Map _dts = new HashMap();

	/** Publishes an event.
	 * Unlike {@link DesktopEventQueue}, an event can be published
	 * without the current execution (i.e., not in an event listener).
	 */
	public void publish(Event event) {
		if (event == null)
			throw new IllegalArgumentException();

		synchronized (_dts) {
			for (Iterator it = _dts.values().iterator(); it.hasNext();)
				((DesktopThread)it.next()).publish(event);
		}
	}
	public void subscribe(EventListener listener) {
		subscribe(listener, null, false);
	}
	public void subscribe(EventListener listener, EventListener callback) {
		subscribe(listener, callback, true);
	}
	public void subscribe(EventListener listener, boolean async) {
		subscribe(listener, null, async);
	}
	private void subscribe(EventListener listener, EventListener callback, boolean async) {
		if (listener == null)
			throw new IllegalArgumentException();
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("execution required");

		final Desktop desktop = exec.getDesktop();
		synchronized (_dts) {
			boolean startRequired = false;
			DesktopThread dtthd = (DesktopThread)_dts.get(desktop);
			if (dtthd == null) {
				desktop.addListener(new EQCleanup());
					//OK to call addListener since it is the current desktop
				_dts.put(desktop, dtthd = new DesktopThread(desktop));
				startRequired = true;
			}

			dtthd.subscribe(listener, callback, async);

			if (startRequired) {
				if (!desktop.isServerPushEnabled()) {
					dtthd.enableCurrentServerPush();
					desktop.addListener(new EQService(dtthd));
						//add here since desktop is the current desktop
				}
				dtthd.start();
			}
		}
	}
	public boolean isSubscribed(EventListener listener) {
		if (listener == null)
			return false;

		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("execution required");

		final Desktop desktop = exec.getDesktop();
		synchronized (_dts) {
			DesktopThread dtthd = (DesktopThread)_dts.get(desktop);
			return dtthd != null && dtthd.isSubscribed(listener);
		}
	}
	public boolean unsubscribe(EventListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();

		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("execution required");

		final Desktop desktop = exec.getDesktop();
		synchronized (_dts) {
			final DesktopThread dtthd = (DesktopThread)_dts.get(desktop);
			if (dtthd != null && dtthd.unsubscribe(listener)) {
				if (dtthd.isIdle()) {
					dtthd.cease();
					_dts.remove(desktop);
					dtthd.disableServerPush();
				}
				return true;
			}
		}
		return false;
	}
	public void close() {
		synchronized (_dts) {
			for (Iterator it = _dts.values().iterator(); it.hasNext();) {
				final DesktopThread dtthd = (DesktopThread)it.next();
				dtthd.cease();
				dtthd.disableServerPush();
			}
		}
		_dts.clear();
	}

	private class DesktopThread extends Thread {
		private final Desktop _desktop;
		private final DesktopEventQueue _que = new DesktopEventQueue();
		private final List _evts = new LinkedList();
		private final Object _mutex = new Object();
		private transient boolean _ceased;
		/** Indicates whether the server push is enabled by the event queue. */
		private boolean _spEnabled;

		private DesktopThread(Desktop desktop) {
			Threads.setDaemon(this, true);
			_desktop = desktop;
		}
		private void publish(Event event) {
			if (!_ceased) {
				final Execution exec = Executions.getCurrent();
				if (exec != null && exec.getDesktop() == _desktop) {
					//same desktop no need of working thread
					List evts = new LinkedList();
					synchronized (_mutex) {
						evts.addAll(_evts);
						_evts.clear(); 
					}
					evts.add(event);
					process(evts);
				} else {
					synchronized (_mutex) {
						_evts.add(event);
						_mutex.notify();
					}
				}
			}
		}
		private void subscribe(EventListener listener, EventListener callback, boolean async) {
			if (callback != null)
				_que.subscribe(listener, callback);
			else
				_que.subscribe(listener, async);
		}
		private boolean isSubscribed(EventListener listener) {
			return _que.isSubscribed(listener);
		}
		private boolean unsubscribe(EventListener listener) {
			return _que.unsubscribe(listener);
		}
		private void cease() {
			synchronized (_mutex) {
				_evts.clear();
				_ceased = true;
				_mutex.notify();
			}
		}
		private boolean isIdle() {
			return _que.isIdle();
		}

		private void process(List evts) {
			Throwable ex = null;
			do {
				for (Iterator it = evts.iterator(); !_ceased && it.hasNext();) {
					final Event evt = (Event)it.next();
					try {
						_que.publish(evt);
					} catch (Throwable t) {
						if (ex == null) ex = t;
					}
				}

				//To process as many as events, check _evts again
				evts.clear();
				synchronized (_mutex) {
					evts.addAll(_evts);
					_evts.clear();
				}
			} while (!_ceased && !evts.isEmpty());

			if (!_ceased && ex != null)
				log.realCauseBriefly("Unable to process events", ex);
		}
		public void run() {
			l_out:
			while (!_ceased) {
				try {
					List evts = new LinkedList();
					synchronized (_mutex) {
						while (_evts.isEmpty()) {
							_mutex.wait(30*60*1000); //30 mins
							if (_ceased)
								break l_out;
						}
						evts.addAll(_evts);
						_evts.clear(); 
					}

					Executions.activate(_desktop);
					try {
						process(evts);
					} finally { //just in case
						Executions.deactivate(_desktop);
					}
				} catch (DesktopUnavailableException ex) {
					break;
				} catch (Throwable ex) {
					if (!_ceased) log.realCauseBriefly(ex);
				}
			}

			try {
				_evts.clear();
				_que.close();
			} catch (Throwable ex) {
				log.warningBriefly("Failed to clean up", ex);
			}

			synchronized (_dts) {
				_dts.remove(_desktop);
			}
			disableServerPush();
		}
		/** _desktop must be the current desktop. */
		private void enableCurrentServerPush() {
			_spEnabled = true;
			_desktop.enableServerPush(true);
		}
		private void disableServerPush() {
			if (_spEnabled) {
				final Execution exec = Executions.getCurrent();
				if (exec != null && exec.getDesktop() == _desktop) {
					_spEnabled = false;

					if (_desktop.isAlive())
						try {
							_desktop.enableServerPush(false);
						} catch (Throwable ex) {
							log.warningBriefly("Ingored: unable to stop server push", ex);
						}
				}
				//if not current desktop, it is handled by EQService
			}
		}
	}
	private class EQCleanup implements DesktopCleanup {
		public void cleanup(Desktop desktop) throws Exception {
			final DesktopThread dtthd;
			synchronized (_dts) {
				dtthd = (DesktopThread)_dts.remove(desktop);
			}

			if (dtthd != null) {
				dtthd.cease();
				dtthd.disableServerPush();
			}
		}
	}
	//Used to stop the server push if the event queue is closed by other thread
	private static class EQService implements AuService {
		private DesktopThread _dtthd;
		private EQService(DesktopThread dtthd) {
			_dtthd = dtthd;
		}
		public boolean service(AuRequest request, boolean everError) {
			if (_dtthd._ceased) {
				_dtthd.disableServerPush();
				_dtthd._desktop.removeListener(this);
			}
			return false;
		}
	}
}
