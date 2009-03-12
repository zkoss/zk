/* EventProcessor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  8 14:10:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;

/**
 * A utility class that simplify the implementation of
 * {@link org.zkoss.zk.ui.sys.EventProcessingThread}.
 *
 * @author tomyeh
 */
public class EventProcessor {
//	private static final Log log = Log.lookup(EventProcessor.class);

	/** The desktop that the component belongs to. */
	private final Desktop _desktop;
	/** Part of the command: component to handle the event. */
	private final Component _comp;
	/** Part of the command: event to process. */
	private Event _event;
	/** Whether it is in processing an event.
	 * It is used only the event processing thread is disabled.
	 */
	private static ThreadLocal _inEvt;

	/** Returns whether the current thread is an event listener.
	 */
	public static final boolean inEventListener() {
		return (Thread.currentThread() instanceof EventProcessingThread)
			|| (_inEvt != null && _inEvt.get() != null); //used if event thread is disabled
	}
	/** Sets whether the current thread is an event listener.
	 * It needs to be called only if the event processing thread is
	 * disabled.
	 *
	 * <p>It is used only internally.
	 */
	/*package*/ static final void inEventListener(boolean in) {
		if (in) {
			if (_inEvt == null)
				_inEvt = new ThreadLocal();
			_inEvt.set(Boolean.TRUE);
		} else {
			if (_inEvt != null)
				_inEvt.set(null);
		}
	}

	/**
	 * @param comp the component. Its desktop must be either null
	 * or the same as desktop.
	 */
	public EventProcessor(Desktop desktop, Component comp, Event event) {
		if (desktop == null || comp == null || event == null)
			throw new IllegalArgumentException("null");

		final Desktop dt = comp.getDesktop();
		if (dt != null && desktop != dt)
			throw new IllegalStateException("Process events for another desktop? "+comp);

		_desktop = desktop;
		_comp = comp;
		_event = event;
	}

	/** Returns the desktop.
	 */
	public final Desktop getDesktop() {
		return _desktop;
	}
	/** Returns the event.
	 */
	public final Event getEvent() {
		return _event;
	}
	/** Returns the component.
	 */
	public final Component getComponent() {
		return _comp;
	}

	/** Process the event.
	 * Note: it doesn't invoke EventThreadInit and EventThreadCleanup.
	 *
	 * <p>This method is to implement
	 * {@link org.zkoss.zk.ui.sys.EventProcessingThread}.
	 * See also {@link org.zkoss.zk.ui.util.Configuration#isEventThreadEnabled}.
	 */
	public void process() throws Exception {
		//Bug 1506712: event listeners might be zscript, so we have to
		//keep built-in variables as long as possible
		final HashMap backup = new HashMap();
		final Namespace ns = Namespaces.beforeInterpret(backup, _comp, true);
			//we have to push since process0 might invoke methods from zscript class
		try {
			Namespaces.setImplicit(backup, ns, "event", _event);

			_event = ((DesktopCtrl)_desktop).beforeProcessEvent(_event);
			if (_event != null) {
				Namespaces.setImplicit(null, ns, "event", _event); //_event might change
				process0(ns);
				((DesktopCtrl)_desktop).afterProcessEvent(_event);
			}
		} finally {
			Namespaces.afterInterpret(backup, ns, true);
		}
	}
	private void process0(Namespace ns) throws Exception {
		final Page page = getPage();
		final String evtnm = _event.getName();

		final Set listenerCalled = new HashSet();
			//OK to use Set since the same listener cannot be added twice
		boolean retry = false;
		for (Iterator it = _comp.getListenerIterator(evtnm);;) {
			final EventListener el = nextListener(it);
			if (el == null) {
				break; //done

			} else if (el == RETRY) {
				retry = true;
				it = _comp.getListenerIterator(evtnm);

			} else if ((el instanceof Express)
			&& (!retry || !listenerCalled.contains(el))) {
				listenerCalled.add(el);

				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}

		final ZScript zscript = ((ComponentCtrl)_comp).getEventHandler(evtnm);
		if (zscript != null) {
			page.interpret(
				zscript.getLanguage(), zscript.getContent(page, _comp), ns);
			if (!_event.isPropagatable())
				return; //done
		}

		retry = false;
		listenerCalled.clear();
		for (Iterator it = _comp.getListenerIterator(evtnm);;) {
			final EventListener el = nextListener(it);
			if (el == null) {
				break; //done

			} else if (el == RETRY) {
				retry = true;
				it = _comp.getListenerIterator(evtnm);

			} else if (!(el instanceof Express)
			&& (!retry || !listenerCalled.contains(el))) {
				listenerCalled.add(el);

				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}

		final Method mtd =
			ComponentsCtrl.getEventMethod(_comp.getClass(), evtnm);
		if (mtd != null) {
//			if (log.finerable()) log.finer("Method for event="+evtnm+" comp="+_comp+" method="+mtd);

			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(_comp, null);
			else
				mtd.invoke(_comp, new Object[] {_event});
			if (!_event.isPropagatable())
				return; //done
		}

		retry = false;
		listenerCalled.clear();
		for (Iterator it = page.getListenerIterator(evtnm);;) {
			final EventListener el = nextListener(it);
			if (el == null) {
				break; //done

			} else if (el == RETRY) {
				retry = true;
				it = page.getListenerIterator(evtnm);

			} else if (!retry || !listenerCalled.contains(el)) {
				listenerCalled.add(el);

				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}
	}
	private static EventListener nextListener(Iterator it) {
		try {
			return it.hasNext() ? (EventListener)it.next(): null;
		} catch (java.util.ConcurrentModificationException ex) {
			return RETRY;
		}
	}
	/** Represents {@link #nextListener} encounter co-modification error. */
	private static final EventListener RETRY = new EventListener() {
		public void onEvent(Event event) {
		}
	};

	/** Setup this processor before processing the event by calling
	 * {@link #process}.
	 *
	 * <p>Note: it doesn't invoke {@link ExecutionCtrl#onActivate}
	 */
	public void setup() {
		SessionsCtrl.setCurrent(_desktop.getSession());
		final Execution exec = _desktop.getExecution();
		ExecutionsCtrl.setCurrent(exec);
		((ExecutionCtrl)exec).setCurrentPage(getPage());
	}
	/** Cleanup this process after processing the event by calling
	 * {@link #process}.
	 *
	 * <p>Note: Don't call this method if the event process executes
	 * in the same thread.
	 */
	public void cleanup() {
		ExecutionsCtrl.setCurrent(null);
		SessionsCtrl.setCurrent(null);
	}

	private Page getPage() {
		final Page page = _comp.getPage();
		if (page != null)
			return page;

		final Iterator it = _desktop.getPages().iterator();
		return it.hasNext() ? (Page)it.next(): null;
	}

	//Object//
	public String toString() {
		return "[comp: "+_comp+", event: "+_event+']';
	}
}
