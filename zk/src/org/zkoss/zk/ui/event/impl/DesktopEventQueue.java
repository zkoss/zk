/* DesktopEventQueue.java

	Purpose:
		
	Description:
		
	History:
		Fri May  2 17:44:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.lang.Threads;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;

/**
 * The default implementation of the desktop-level event queue ({@link EventQueue}).
 * @author tomyeh
 * @since 5.0.0
 */
public class DesktopEventQueue implements EventQueue {
	/*package*/ static final Log log = Log.lookup(DesktopEventQueue.class);

	private final Component _dummy = new AbstractComponent();
	private final List _listeners = new LinkedList();
	private int _nAsync;
	private boolean _serverPushEnabled;

	public DesktopEventQueue() {
		_dummy.addEventListener("onQueue", new EventListener() {
			public void onEvent(Event event) throws Exception {
				//Note: we cannot use the same algorithm as EventProcessor.process0() because
				//listeners can be registered multiple times to the event queue
				final Event evt = (Event)event.getData();
				final List listenerCalled = new LinkedList();
				for (Iterator it = _listeners.iterator();;) {
					final ListenerInfo inf;
					try {
						if (!it.hasNext())
							return; //done
						inf = (ListenerInfo)it.next();
					} catch (java.util.ConcurrentModificationException ex) {
						break;
					}

					listenerCalled.add(inf);
					invoke(inf, evt);
				}

				//make a copy and iterate again
				for (Iterator it = new LinkedList(_listeners).iterator(); it.hasNext();) {
					final ListenerInfo inf = (ListenerInfo)it.next();
					if (!listenerCalled.remove(inf) && _listeners.contains(inf))
						invoke(inf, evt);
				}
			}
		});
	}

	/** A working thread is created to handle it if it is an asynchronous
	 * listener.
	 * @param async whether it is an asynchronous event listener.
	 */
	private void invoke(ListenerInfo inf, Event event)
	throws Exception {
		if (inf.async) {
			new AsyncListenerThread(this, inf, event).start();
		} else {
			inf.listener.onEvent(event);
		}
	}

	/** Returns if there is listener being registered.
	 */
	public boolean isIdle() {
		return _listeners.isEmpty();
	}

	//EventQueue//
	public void publish(Event event) {
		if (event == null)
			throw new IllegalArgumentException();
		if (Executions.getCurrent() == null) {
			final Thread thd = Thread.currentThread();
			if (!(thd instanceof AsyncListenerThread))
				throw new IllegalStateException("publish() can be called only in an event listener");

			((AsyncListenerThread)thd).postEvent(event);
		} else {
			Events.postEvent("onQueue", _dummy, event);
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
	private void
	subscribe(EventListener listener, EventListener callback, boolean async) {
		if (async && _nAsync++ == 0) {
			final Execution exec = Executions.getCurrent();
			if (exec == null)
				throw new IllegalStateException("Execution required");
			_serverPushEnabled = !exec.getDesktop().enableServerPush(true);
		}
		_listeners.add(new ListenerInfo(listener, callback, async));
	}
	public boolean unsubscribe(EventListener listener) {
		if (listener != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();) {
				final ListenerInfo inf = (ListenerInfo)it.next();
				if (listener.equals(inf.listener)) {
					it.remove();
					if (inf.async && --_nAsync == 0 && _serverPushEnabled)
						Executions.getCurrent().getDesktop().enableServerPush(false);
					return true;
				}
			}
		return false;
	}
	public boolean isSubscribed(EventListener listener) {
		if (listener != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				if (listener.equals(((ListenerInfo)it.next()).listener))
					return true;
		return false;
	}
	public void close() {
		_listeners.clear();

		if (_serverPushEnabled) {
			try {
				Executions.getCurrent().getDesktop().enableServerPush(false);
			} catch (Throwable ex) {
				log.warningBriefly("Ingored: unable to stop server push", ex);
			}
		}
	}
}
/** Info of a listener */
/*package*/ class ListenerInfo {
	/*package*/ final EventListener listener;
	/*package*/ final EventListener callback; //used only if async
	/*package*/ final boolean async;
	/*package*/ ListenerInfo(EventListener listener,
	EventListener callback, boolean async) {
		if (listener == null)
			throw new IllegalArgumentException();
		this.listener = listener;
		this.callback = callback;
		this.async = async;
	}
}
/*package*/ class AsyncListenerThread extends Thread {
	private static final Log log = DesktopEventQueue.log;

	/*package*/ final Desktop _desktop;
	private final EventQueue _que;
	private final ListenerInfo _inf;
	private final Event _event;
	private List _pendingEvents;
	/*package*/ AsyncListenerThread(EventQueue que, ListenerInfo inf, Event event) {
		_desktop = Executions.getCurrent().getDesktop();
		_que = que;
		_inf = inf;
		_event = event;
		Threads.setDaemon(this, true);
	}
	/*package*/ void postEvent(Event event) {
		if (_pendingEvents == null)
			_pendingEvents = new LinkedList();
		_pendingEvents.add(event);
	}
	public void run() {
		try {
			_inf.listener.onEvent(_event);

			if (_inf.callback != null || _pendingEvents != null) {
				try {
					Executions.activate(_desktop);
					try {
						for (Iterator it = _pendingEvents.iterator(); it.hasNext();)
							_que.publish((Event)it.next());
						if (_inf.callback != null)
							_inf.callback.onEvent(null);
					} finally {
						Executions.deactivate(_desktop);
					}
				} catch (Throwable ex) {
					log.realCauseBriefly(ex);
				}
			}
		} catch (DesktopUnavailableException ex) {
			//ignore
		} catch (Throwable ex) {
			log.realCauseBriefly(ex);
			throw UiException.Aide.wrap(ex);
		}
	}
}
