/* EventProcessor.java

	Purpose:
		
	Description:
		
	History:
		Tue May  8 14:10:54     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * A utility class that simplify the implementation of
 * {@link org.zkoss.zk.ui.sys.EventProcessingThread}.
 *
 * @author tomyeh
 */
public class EventProcessor {
	private static final Log log = Log.lookup(EventProcessor.class);

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
		final Scope scope = Scopes.beforeInterpret(_comp);
			//we have to push since process0 might invoke methods from zscript class
		try {
			Scopes.setImplicit("event", _event);

			_event = ((DesktopCtrl)_desktop).beforeProcessEvent(_event);
			if (_event != null) {
				Scopes.setImplicit("event", _event); //_event might change
				process0(scope);
				((DesktopCtrl)_desktop).afterProcessEvent(_event);
			}
		} finally {
			final Execution exec = _desktop.getExecution();
			if (exec != null) //just in case
				((ExecutionCtrl)exec).setExecutionInfo(null);
			Scopes.afterInterpret();
		}
	}
	private void process0(Scope scope) throws Exception {
		final Page page = getPage();
		if (page == null || !page.isAlive()) {
			String msg = (page == null ? "No page is available in "+_desktop: "Page "+page+" was destroyed");
			if (_desktop.isAlive())
				msg += " (but desktop is alive)";
			else
				msg += " because desktop was destroyed.\n"
				+"It is usually caused by invalidating the native session directly. "
				+"If it is required, please set Attributes.RENEW_NATIVE_SESSION first.";
			log.warning(msg);
		}

		final ExecInfo execinf;
		((ExecutionCtrl)_desktop.getExecution())
			.setExecutionInfo(execinf = new ExecInfo(_event));
		final String evtnm = _event.getName();
		for (Iterator it = _comp.getListenerIterator(evtnm); it.hasNext();) {
		//Note: CollectionsX.comodifiableIterator is used so OK to iterate
			final EventListener el = (EventListener)it.next();
			execinf.update(null, el, null);
			if (el instanceof Express) {
				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}
		
		if (page != null && _comp.getDesktop() != null) {
			final ZScript zscript = ((ComponentCtrl)_comp).getEventHandler(evtnm);
			execinf.update(null, null, zscript);
			if (zscript != null) {
				page.interpret(
						zscript.getLanguage(), zscript.getContent(page, _comp), scope);
				if (!_event.isPropagatable())
					return; //done
			}
		}

		for (Iterator it = _comp.getListenerIterator(evtnm); it.hasNext();) {
		//Note: CollectionsX.comodifiableIterator is used so OK to iterate
			final EventListener el = (EventListener)it.next();
			execinf.update(null, el, null);
			if (!(el instanceof Express)) {
				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}

		final Method mtd =
			ComponentsCtrl.getEventMethod(_comp.getClass(), evtnm);
		if (mtd != null) {
//			if (log.finerable()) log.finer("Method for event="+evtnm+" comp="+_comp+" method="+mtd);
			execinf.update(mtd, null, null);

			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(_comp, null);
			else
				mtd.invoke(_comp, new Object[] {_event});
			if (!_event.isPropagatable())
				return; //done
		}

		if (page != null)
			for (Iterator it = page.getListenerIterator(evtnm); it.hasNext();) {
			//Note: CollectionsX.comodifiableIterator is used so OK to iterate
				final EventListener el = (EventListener)it.next();
				execinf.update(null, el, null);
				el.onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
	}

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
		SessionsCtrl.setCurrent((Session)null);
	}

	private Page getPage() {
		final Page page = _comp.getPage();
		if (page != null)
			return page;

		return _desktop.getFirstPage();
	}

	//Object//
	public String toString() {
		return "[comp: "+_comp+", event: "+_event+']';
	}
}
/*package*/ class ExecInfo implements org.zkoss.zk.ui.sys.ExecutionInfo {
	private final Thread _thread;
	private final Event _event;
	private Method _method;
	private EventListener _listener;
	private ZScript _zscript;

	/*package*/ ExecInfo(Event event) {
		_thread = Thread.currentThread();
		_event = event;
	}
	public Thread getThread() {
		return _thread;
	}
	public Event getEvent() {
		return _event;
	}
	public Method getEventMethod() {
		return _method;
	}
	public EventListener getEventListener() {
		return _listener;
	}
	public ZScript getEventZScript() {
		return _zscript;
	}
	/** @deprecated As of release 5.0.8, replaced with {@link #getEventZScript}
	 */
	public ZScript getEventZscript() {
		return getEventZScript();
	}
	public void update(Method mtd, EventListener ln, ZScript zs) {
		_method = mtd;
		_listener = ln;
		_zscript = zs;
	}
}