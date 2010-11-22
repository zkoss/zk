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

	/** Map(desktop, DesktopInfo). */
	private final Map<Desktop, DesktopInfo> _dtInfos = new HashMap<Desktop, DesktopInfo>();
	private boolean _closed;

	/** Publishes an event.
	 * Unlike {@link DesktopEventQueue}, an event can be published
	 * without the current execution (i.e., not in an event listener).
	 */
	public void publish(Event event) {
		if (event == null)
			throw new IllegalArgumentException();

		synchronized (_dtInfos) {
			for (DesktopInfo di: _dtInfos.values())
				di.publish(event);
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
		DesktopInfo di;
		synchronized (_dtInfos) {
			di = _dtInfos.get(desktop);
			if (di == null)
				_dtInfos.put(desktop,
					di = new DesktopInfo(desktop, new EQService(), new EQCleanup()));
		}
		di.subscribe(listener, callback, async);
	}
	public boolean isSubscribed(EventListener listener) {
		if (listener == null)
			return false;

		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("execution required");

		final Desktop desktop = exec.getDesktop();
		final DesktopInfo di;
		synchronized (_dtInfos) {
			di = _dtInfos.get(desktop);
		}
		return di != null && di.isSubscribed(listener);
	}
	public boolean unsubscribe(EventListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();

		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("execution required");

		final Desktop desktop = exec.getDesktop();
		synchronized (_dtInfos) {
			final DesktopInfo di = _dtInfos.get(desktop);
			if (di != null && di.unsubscribe(listener)) {
				if (di.isIdle()) {
					_dtInfos.remove(desktop);
					di.close();
				}
				return true;
			}
		}
		return false;
	}
	public void close() {
		_closed = true;
		final Execution exec = Executions.getCurrent();
		if (exec != null)
			close(exec.getDesktop());
			//queues of other desktops will be closed in EQService
	}
	private void close(Desktop desktop) {
		final DesktopInfo di;
		synchronized (_dtInfos) {
			di = _dtInfos.remove(desktop);
		}
		if (di != null)
			di.close();
	}
	public boolean isClose() {
		return _closed;
	}

	private static class DesktopInfo implements java.io.Serializable {
		private final Desktop _desktop;
		private final DesktopEventQueue _que;
		private final EQService _service;
		private final EQCleanup _cleanup;
		/** Indicates whether the server push is enabled by the event queue. */
		private boolean _spEnabled;

		private DesktopInfo(Desktop desktop, EQService service, EQCleanup cleanup) {
			_desktop = desktop;
			_que = new DesktopEventQueue();
			_spEnabled = !desktop.isServerPushEnabled();
			if (_spEnabled)
				desktop.enableServerPush(true);
			desktop.addListener(_service = service);
			desktop.addListener(_cleanup = cleanup);
				//OK to call addListener since it is the current desktop
		}

		private void publish(Event event) {
			Executions.schedule(_desktop, new ScheduleListener(_que), event);
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
		private boolean isIdle() {
			return _que.isIdle();
		}
		private void close() {
			_que.close();
			_desktop.removeListener(_cleanup);
			_desktop.removeListener(_service);
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
	private static class ScheduleListener implements EventListener, java.io.Serializable {
		private final DesktopEventQueue _que;
		private ScheduleListener(DesktopEventQueue queue) {
			_que = queue;
		}
		public void onEvent(Event event) {
			if (!_que.isClose()) //just in case
				_que.publish(event);
		}
	}
	private class EQService implements AuService, java.io.Serializable {
		public boolean service(AuRequest request, boolean everError) {
			if (_closed)
				close(request.getDesktop());
			return false;
		}
	}
	private class EQCleanup implements DesktopCleanup, java.io.Serializable {
		public void cleanup(Desktop desktop) throws Exception {
			final DesktopInfo di;
			synchronized (_dtInfos) {
				di = _dtInfos.remove(desktop);
			}
			if (di != null)
				di.close();
		}
	}
}
