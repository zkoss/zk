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
import org.zkoss.util.CollectionsX;
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
 * The default implementation of the desktop-scoped event queue ({@link EventQueue}).
 * @author tomyeh
 * @since 5.0.0
 */
public class DesktopEventQueue<T extends Event> implements EventQueue<T>, java.io.Serializable {
	/*package*/ static final Log log = Log.lookup(DesktopEventQueue.class);

	private static final String ON_QUEUE = "onQueue";

	/** A dummy target for handling onQueue. */
	private final Component _dummyTarget = new AbstractComponent();
	private final List<ListenerInfo<T>> _listenerInfos = new LinkedList<ListenerInfo<T>>();
	private int _nAsync;
	private boolean _serverPushEnabled;
	private boolean _closed;

	public DesktopEventQueue() {
		_dummyTarget.addEventListener(ON_QUEUE, new QueueListener());
	}

	/** Returns if there is listener being registered.
	 */
	public boolean isIdle() {
		return _listenerInfos.isEmpty();
	}

	//EventQueue//
	public void publish(T event) {
		if (event == null)
			throw new IllegalArgumentException();
		if (Executions.getCurrent() == null) {
			currentThread().postEvent(event);
		} else {
			Events.postEvent(ON_QUEUE, _dummyTarget, event);
		}
	}
	@SuppressWarnings("unchecked")
	private static final <W extends Event> AsyncListenerThread<W> currentThread() {
		final Thread thd = Thread.currentThread();
		if (!(thd instanceof AsyncListenerThread))
			throw new IllegalStateException("publish() can be called only in an event listener");
		return (AsyncListenerThread<W>)thd;
	}
	public void subscribe(EventListener<T> listener) {
		subscribe(listener, null, false);
	}
	public void subscribe(EventListener<T> listener, EventListener<T> callback) {
		subscribe(listener, callback, true);
	}
	public void subscribe(EventListener<T> listener, boolean async) {
		subscribe(listener, null, async);
	}
	private void
	subscribe(EventListener<T> listener, EventListener<T> callback, boolean async) {
		if (async && _nAsync++ == 0) {
			final Execution exec = Executions.getCurrent();
			if (exec == null)
				throw new IllegalStateException("Execution required");
			_serverPushEnabled = !exec.getDesktop().enableServerPush(true);
		}
		_listenerInfos.add(new ListenerInfo<T>(listener, callback, async));
	}
	public boolean unsubscribe(EventListener<T> listener) {
		if (listener != null)
			for (Iterator<ListenerInfo<T>> it = _listenerInfos.iterator(); it.hasNext();) {
				final ListenerInfo<T> inf = it.next();
				if (listener.equals(inf.listener)) {
					it.remove();
					if (inf.async && --_nAsync == 0 && _serverPushEnabled)
						Executions.getCurrent().getDesktop().enableServerPush(false);
					return true;
				}
			}
		return false;
	}
	public boolean isSubscribed(EventListener<T> listener) {
		if (listener != null)
			for (ListenerInfo<T> li: _listenerInfos)
				if (listener.equals(li.listener))
					return true;
		return false;
	}
	public void close() {
		_closed = true;
		_listenerInfos.clear();
		if (_serverPushEnabled) {
			try {
				Executions.getCurrent().getDesktop().enableServerPush(false);
			} catch (Throwable ex) {
				log.warningBriefly("Ingored: unable to stop server push", ex);
			}
		}
	}
	public boolean isClose() {
		return _closed;
	}
	private class QueueListener implements EventListener<Event>, java.io.Serializable {
		public void onEvent(Event event) throws Exception {
			@SuppressWarnings("unchecked")
			T evt = (T)event.getData();
			for (Iterator<ListenerInfo<T>> it = CollectionsX.comodifiableIterator(_listenerInfos);
			it.hasNext();) {
				final ListenerInfo<T> inf = it.next();
				if (inf.async)
					new AsyncListenerThread<T>(DesktopEventQueue.this, inf, evt)
						.start();
				else
					inf.listener.onEvent(evt);
			}
		}
	}

	/** Info of a listener */
	private static class ListenerInfo<T extends Event> implements java.io.Serializable {
		/*package*/ final EventListener<T> listener;
		/*package*/ final EventListener<T> callback; //used only if async
		/*package*/ final boolean async;
		private ListenerInfo(EventListener<T> listener,
		EventListener<T> callback, boolean async) {
			if (listener == null)
				throw new IllegalArgumentException();
			this.listener = listener;
			this.callback = callback;
			this.async = async;
		}
	}
	/** Unlike ServerPushEventQueue, we cannot use Executions.schedule, and
	 * we have to use a thread and activate/deactivate, since asynchronous listener
	 * might take too long to execute (that is what it is used for).
	 */
	private static class AsyncListenerThread<T extends Event> extends Thread {
		private static final Log log = DesktopEventQueue.log;
	
		/*package*/ final Desktop _desktop;
		private final EventQueue<T> _que;
		private final ListenerInfo<T> _inf;
		private final T _event;
		private List<T> _pendingEvents;
		private AsyncListenerThread(EventQueue<T> que, ListenerInfo<T> inf, T event) {
			_desktop = Executions.getCurrent().getDesktop();
			_que = que;
			_inf = inf;
			_event = event;
			Threads.setDaemon(this, true);
		}
		/*package*/ void postEvent(T event) {
			if (_pendingEvents == null)
				_pendingEvents = new LinkedList<T>();
			_pendingEvents.add(event);
		}
		public void run() {
			try {
				_inf.listener.onEvent(_event);
	
				if (_inf.callback != null || _pendingEvents != null) {
					try {
						Executions.activate(_desktop);
						try {
							if (_pendingEvents != null)
								for (T evt: _pendingEvents)
									_que.publish(evt);

							if (_inf.callback != null)
								_inf.callback.onEvent(_event);
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
}
