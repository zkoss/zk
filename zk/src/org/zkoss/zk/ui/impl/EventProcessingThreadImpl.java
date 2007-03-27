/* EventProcessingThreadImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 11:24:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.lang.reflect.Method;

import org.zkoss.lang.D;
import org.zkoss.lang.Threads;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.sys.EventProcessingThread;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;

/** Thread to handle events.
 * We need to handle events in a separate thread, because it might
 * suspend (by calling {@link org.zkoss.zk.ui.sys.UiEngine#wait}), such as waiting
 * a modal dialog to complete.
 * 
 * @author tomyeh
 */
public class EventProcessingThreadImpl extends Thread
implements EventProcessingThread {
	private static final Log log = Log.lookup(EventProcessingThreadImpl.class);

	/** Part of the command: component to handle the event. */
	private Component _comp;
	/** Part of the command: event to process. */
	private Event _event;
	/** The desktop that the component belongs to. */
	private Desktop _desktop;
	/** Part of the command: locale. */
	private Locale _locale;
	/** Part of the command: time zone. */
	private TimeZone _timeZone;
	/** Part of the result: a list of EventThreadInit instances. */
	private List _evtThdInits;
	/** Part of the result: a list of EventThreadResume instances. */
	private List _evtThdResumes;
	/** Part of the result. a list of EventThreadSuspend instances. */
	private List _evtThdSuspends;
	/** Part of the result: a list of EventThreadCleanup instances. */
	private List _evtThdCleanups;
	/** Result of the result. */
	private Throwable _ex;

	private static int _nThd, _nBusyThd;

	/** The mutex use to notify an event is ready for processing, or
	 * has been processed.
	 */
	private final Object _evtmutex = new Object();
	/** The mutex use to suspend an event processing. */
	private Object _suspmutex;
	/** If null, it means not ceased yet.
	 * If not null, it means it is ceased and it is a text describing the cause.
	 */
	private String _ceased;
	/** Whether not to show message when stopping. */
	private boolean _silent;
	/** Whether it is suspended. */
	private transient boolean _suspended;

	public EventProcessingThreadImpl() {
		if (log.debugable()) log.debug("Starting an event processing thread");
		Threads.setDaemon(this, true);
		start();
	}

	//EventProcessingThread//
	public boolean isCeased() {
		return _ceased != null;
	}
	public boolean isSuspended() {
		return _suspended;
	}
	synchronized public boolean isIdle() {
		return _event == null;
	}
	public final Event getEvent() {
		return _event;
	}
	public final Component getComponent() {
		return _comp;
	}

	//extra utilities//
	/** Stops the thread. Called only by {@link org.zkoss.zk.ui.sys.UiEngine}
	 * when it is stopping.
	 * <p>Application developers shall use {@link org.zkoss.zk.ui.sys.DesktopCtrl#ceaseSuspendedThread}
	 * instead.
	 *
	 * @param cause a human readable text describing the cause.
	 * If null, an empty string is assumed.
	 */
	public void cease(String cause) {
		synchronized (_evtmutex) {
			_ceased = cause != null ? cause: "";
			_evtmutex.notifyAll();
		}
		if (_suspmutex != null) {
			synchronized (_suspmutex) {
				_suspmutex.notifyAll();
			}
		}
	}
	/** Stops the thread silently. Called by {@link org.zkoss.zk.ui.sys.UiEngine}
	 * to stop abnormally.
	 */
	public void ceaseSilently(String cause) {
		_silent = true;
		cease(cause);
	}

	/** Returns the number of event threads.
	 */
	public static final int getThreadNumber() {
		return _nThd;
	}
	/** Returns the number of event threads in processing.
	 */
	public static final int getThreadNumberInProcessing() {
		return _nBusyThd;
	}

	/** Suspends the current thread and Waits until {@link #doResume}
	 * is called.
	 *
	 * <p>Note:
	 * <ul>
	 * <li>It is used internally only for implementing {@link org.zkoss.zk.ui.sys.UiEngine}
	 * Don't call it directly.
	 * <li>Caller must invoke {@link #newEventThreadSuspends}
	 * before calling this method. (Reason: UiEngine might have to store some info
	 * after {@link #newEventThreadSuspends} is called.
	 * <li>The current thread must be {@link EventProcessingThreadImpl}.
	 * <li>It is a static method.
	 * </ul>
	 */
	public static void doSuspend(Object mutex) throws InterruptedException {
		((EventProcessingThreadImpl)Thread.currentThread()).doSuspend0(mutex);
	}
	private void doSuspend0(Object mutex) throws InterruptedException {
		if (log.finerable()) log.finer("Suspend event processing; "+_event);
		if (mutex == null)
			throw new IllegalArgumentException("null mutex");
		if (isIdle())
			throw new InternalError("Called without processing event?");
		if (_suspmutex != null)
			throw new InternalError("Suspend twice?");

		//Spec: locking mutex is optional for app developers
		//so we have to lock it first
		_suspmutex = mutex;
		try {
			synchronized (_suspmutex) {
				_suspended = true;

				//let the main thread continue
				synchronized (_evtmutex) {
					_evtmutex.notify();
				}

				if (_ceased == null) _suspmutex.wait();
			}
		} finally {
			_suspmutex = null;
			_suspended = false; //just in case (such as _ceased)
		}

		if (_ceased != null)
			throw new InterruptedException(_ceased);

		setup();

		if (_evtThdResumes != null && !_evtThdResumes.isEmpty()) {
			_desktop.getWebApp().getConfiguration()
				.invokeEventThreadResumes(_evtThdResumes, _comp, _event, null);
				//FUTURE: how to propogate errors to the client
		}
		_evtThdResumes = null;
	}
	/** Resumes this thread and returns only if the execution (being suspended
	 * by {@link #doSuspend}) completes.
	 *
	 * <p>It executes in the main thread (i.e., the servlet thread).
	 *
	 * @return whether the event has been processed completely or just be suspended
	 */
	public boolean doResume() throws InterruptedException {
		if (this.equals(Thread.currentThread()))
			throw new IllegalStateException("A thread cannot resume itself");
		if (D.ON && log.finerable()) log.finer("Resume event processing; "+_event);
		if (isIdle())
			throw new InternalError("Called without processing event?");
		if (_suspmutex == null)
			throw new InternalError("Resume non-suspended thread?");

		//Copy first since event thread clean up them, when completed
		final Configuration config = _desktop.getWebApp().getConfiguration();
		final Component comp = _comp;
		final Event event = _event;
		try {
			_evtThdResumes = config.newEventThreadResumes(comp, event);

			//Spec: locking mutex is optional for app developers
			//so we have to lock it first
			synchronized (_suspmutex) {
				_suspended = false;
				_suspmutex.notify(); //wake the suspended event thread
			}

			//wait until the event thread completes or suspends again
			//If complete: isIdle() is true
			//If suspend again: _suspended is true
			synchronized (_evtmutex) {
				if (_ceased == null && !isIdle() && !_suspended)
					_evtmutex.wait();
			}
		} finally {
			//_evtThdCleanups is null if //1) no listener;
			//2) the event thread is suspended again (handled by another doResume)
			invokeEventThreadCompletes(config, comp, event);
		}

		checkError();
		return isIdle();
	}

	/** Ask this event thread to process the specified event.
	 *
	 * <p>Used internally to implement {@link org.zkoss.zk.ui.sys.UiEngine}.
	 * Application developers
	 * shall use {@link org.zkoss.zk.ui.event.Events#sendEvent} instead.
	 *
	 * @return whether the event has been processed completely or just be suspended.
	 * Recycle it only if true is returned.
	 */
	public boolean processEvent(Component comp, Event event) {
		if (Thread.currentThread() instanceof EventProcessingThreadImpl)
			throw new IllegalStateException("processEvent cannot be called in an event thread");
		if (comp == null || event == null)
			throw new IllegalArgumentException("null");
		if (_ceased != null)
			throw new InternalError("The event thread has beeing stopped. Cause: "+_ceased);
		if (_comp != null)
			throw new InternalError("reentering processEvent not allowed");

		_desktop = comp.getDesktop();
		if (_desktop == null)
			throw new InternalError("Not belonging to any desktop? "+comp);

		_comp = comp;
		_locale = Locales.getCurrent();
		_timeZone = TimeZones.getCurrent();
		_ex = null;

		final Configuration config = _desktop.getWebApp().getConfiguration();
		_evtThdInits = config.newEventThreadInits(comp, event);
		try {
			synchronized (_evtmutex) {
				_event = event;
					//Bug 1577842: don't let event thread start (and end) too early
				_evtmutex.notify(); //ask the event thread to handle it
				if (_ceased == null) {
					_evtmutex.wait();
						//wait until the event thread to complete or suspended

					if (_suspended) {
						config.invokeEventThreadSuspends(_evtThdSuspends, comp, event);
						_evtThdSuspends = null;
					}
				}
			}
		} catch (InterruptedException ex) {
			throw new UiException(ex);
		} finally {
			//_evtThdCleanups is null if //1) no listener;
			//2) the event thread is suspended (then handled by doResume).
			invokeEventThreadCompletes(config, comp, event);
		}

		checkError(); //check any error occurs
		return isIdle();
	}
	/** Invokes {@link Configuration#newEventThreadSuspends}.
	 * The caller must execute in the event processing thread.
	 * It is called only for implementing {@link org.zkoss.zk.ui.sys.UiEngine}.
	 * Don't call it directly.
	 */
	public void newEventThreadSuspends(Object mutex) {
		if (_comp == null) throw new IllegalStateException();
		_evtThdSuspends = _desktop.getWebApp().getConfiguration()
			.newEventThreadSuspends(_comp, _event, mutex);
			//it might throw an exception, so process it before updating
			//_suspended
	}

	private void invokeEventThreadCompletes(Configuration config,
	Component comp, Event event) throws UiException {
		if (_evtThdCleanups != null && !_evtThdCleanups.isEmpty()) {
			final List errs = _ex != null ? null: new LinkedList();

			config.invokeEventThreadCompletes(_evtThdCleanups, comp, event, errs);

			if (errs != null && !errs.isEmpty())
				throw UiException.Aide.wrap((Throwable)errs.get(0));
		}
		_evtThdCleanups = null;
	}
	/** Setup for execution. */
	synchronized private void setup() {
		SessionsCtrl.setCurrent(_desktop.getSession());
		final Execution exec = _desktop.getExecution();
		ExecutionsCtrl.setCurrent(exec);
		((ExecutionCtrl)exec).setCurrentPage(_comp.getPage());
			//Note: _com.getPage might return null because this method
			//is also called when resumed.
	}
	/** Cleanup for executionl. */
	synchronized private void cleanup() {
		_comp = null;
		_event = null;
		_desktop = null;
	}
	private void checkError() {
		if (_ex != null) { //failed to process
			if (log.debugable()) log.realCause(_ex);
			final Throwable ex = _ex;
			_ex = null;
			throw UiException.Aide.wrap(ex);
		}
	}

	//-- Thread --//
	public void run() {
		++_nThd;
		try {
			while (_ceased == null) {
				final boolean evtAvail = !isIdle();
				if (evtAvail) {
					Configuration config = _desktop.getWebApp().getConfiguration();
					boolean cleaned = false;
					++_nBusyThd;
					try {
						if (D.ON && log.finerable()) log.finer("Processing event: "+_event);

						Locales.setThreadLocal(_locale);
						TimeZones.setThreadLocal(_timeZone);
						setup();

						config.invokeEventThreadInits(_evtThdInits, _comp, _event);
						_evtThdInits = null;

						process0();
					} catch (Throwable ex) {
						cleaned = true;
						newEventThreadCleanups(config, ex);
					} finally {
						--_nBusyThd;

						if (!cleaned) newEventThreadCleanups(config, _ex);

						cleanup();
						ExecutionsCtrl.setCurrent(null);
						SessionsCtrl.setCurrent(null);
						Locales.setThreadLocal(_locale = null);
						TimeZones.setThreadLocal(_timeZone = null);
						if (D.ON && log.finerable()) log.finer("Real processing is done; "+_event);
					}
				}

				synchronized (_evtmutex) {
					if (evtAvail)
						_evtmutex.notify();
						//wake the main thread OR the resuming thread
					if (_ceased == null)
						_evtmutex.wait();
						//wait the main thread to issue another request
				}
			}

			if (_silent) {
				if (log.debugable()) log.debug("The event processing thread stops");
			} else {
				System.out.println("The event processing thread stops");
				//Don't use log because it might be stopped
			}
		} catch (InterruptedException ex) {
			if (_silent) {
				if (log.debugable())
					log.debug("The event processing thread interrupted: "+Exceptions.getMessage(ex)
						+"\n"+Exceptions.getBriefStackTrace(ex));
			} else {	
				System.out.println("The event processing thread interrupted: "+Exceptions.getMessage(ex));
				//Don't use log because it might be stopped
			}
		} finally {
			--_nThd;
		}
	}
	/** Invokes {@link Configuration#newEventThreadCleanups}.
	 */
	private void newEventThreadCleanups(Configuration config, Throwable ex) {
		final List errs = new LinkedList();
		if (ex != null) errs.add(ex);
		_evtThdCleanups =
			config.newEventThreadCleanups(_comp, _event, errs);
		_ex = errs.isEmpty() ? null: (Throwable)errs.get(0);
			//propogate back the first exception
	}

	/** Sends the specified component and event and processes the event
	 * synchronously. Used to implements {@link org.zkoss.zk.ui.event.Events#sendEvent}.
	 */
	public void sendEvent(final Component comp, Event event)
	throws Exception {
		if (D.ON && log.finerable()) log.finer("Process sent event: "+event);
		if (event == null || comp == null)
			throw new IllegalArgumentException("Both comp and event must be specified");
		if (!(Thread.currentThread() instanceof EventProcessingThreadImpl))
			throw new IllegalStateException("Only callable when processing an event");
		if (_desktop != comp.getDesktop())
			throw new IllegalStateException("Must in the same desktop");
		final Component oldComp = _comp;
		final Event oldEvent = _event;
		try {
			_comp = comp;
			_event = event;
			setup();
			process0();
		} finally {
			_comp = oldComp;
			_event = oldEvent;
			setup();
		}
	}
	/** Processes the component and event.
	 */
	private void process0() throws Exception {
		if (_comp == null || _event == null)
			throw new IllegalStateException("comp and event must be initialized");

		//Bug 1506712: event listeners might be zscript, so we have to
		//keep built-in variables as long as possible
		final HashMap backup = new HashMap();
		final Namespace ns = Namespaces.beforeInterpret(backup, _comp);
		Namespaces.pushCurrent(ns);
			//we have to push since process1 might invoke methods from zscript class
		try {
			Namespaces.backupVariable(backup, ns, "event");
			ns.setVariable("event", _event, true);
			process1(ns);
		} finally {
			Namespaces.popCurrent();
			Namespaces.afterInterpret(backup, ns);
		}
	}
	private void process1(Namespace ns) throws Exception {
		final Page page = _comp.getPage();
		final String evtnm = _event.getName();

		for (Iterator it = _comp.getListenerIterator(evtnm); it.hasNext();) {
			final Object el = it.next();
			if (el instanceof Express) {
				((EventListener)el).onEvent(_event);
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

		for (Iterator it = _comp.getListenerIterator(evtnm); it.hasNext();) {
			final Object el = it.next();
			if (!(el instanceof Express)) {
				((EventListener)el).onEvent(_event);
				if (!_event.isPropagatable())
					return; //done
			}
		}

		final Method mtd = ExecutionsCtrl
			.getEventMethod(_comp.getClass(), evtnm);
		if (mtd != null) {
			if (D.ON && log.finerable())
				log.finer("Method for event="+evtnm+" comp="+_comp+" method="+mtd);

			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(_comp, null);
			else
				mtd.invoke(_comp, new Object[] {_event});
			if (!_event.isPropagatable())
				return; //done
		}

		for (Iterator it = page.getListenerIterator(evtnm); it.hasNext();) {
			((EventListener)it.next()).onEvent(_event);
			if (!_event.isPropagatable())
				return; //done
		}
	}

	//-- Object --//
	public String toString() {
		return "[Event processing thread: event="+_event+", ceased="+_ceased+']';
	}
}
