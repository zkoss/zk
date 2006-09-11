/* EventProcessingThread.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 11:24:00     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.lang.reflect.Method;

import com.potix.lang.D;
import com.potix.lang.Threads;
import com.potix.lang.Exceptions;
import com.potix.util.Locales;
import com.potix.util.TimeZones;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.util.Namespace;
import com.potix.zk.ui.util.Namespaces;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.sys.SessionsCtrl;
import com.potix.zk.ui.sys.ExecutionCtrl;
import com.potix.zk.ui.sys.ExecutionsCtrl;
import com.potix.zk.ui.sys.ComponentCtrl;

/** Thread to handle events.
 * We need to handle events in a separate thread, because it might
 * suspend (by calling {@link com.potix.zk.ui.sys.UiEngine#wait}), such as waiting
 * a modal dialog to complete.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class EventProcessingThread extends Thread {
	private static final Log log = Log.lookup(EventProcessingThread.class);

	/** Part of the command: component to handle the event. */
	private Component _comp;
	/** Part of the command: event to process. */
	private Event _event;
	/** The desktop that the component belongs to. */
	private Desktop _desktop;
	/** Part of the command: a list of EventThreadInit instances. */
	private List _evtThdInits;
	/** Part of the command: locale. */
	private Locale _locale;
	/** Part of the command: time zone. */
	private TimeZone _timeZone;
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
	private boolean _ceased;
	/** Whether not to show message when stopping. */
	private boolean _silent;
	/** Whether it is suspended. */
	private transient boolean _suspended;

	public EventProcessingThread() {
		if (log.debugable()) log.debug("Starting an event processing thread");
		Threads.setDaemon(this, true);
		start();
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

	/** Returns whether it is ceased.
	 */
	public boolean isCeased() {
		return _ceased;
	}
	/** Returns whether this thread is idle, i.e., not processing any event.
	 */
	synchronized public boolean isIdle() {
		return _event == null;
	}
	/** Returns the event being processed by this thread, or null if idle.
	 */
	public final Event getEvent() {
		return _event;
	}
	/** Returns the component being processed by this thread, or null if idle.
	 */
	public final Component getComponent() {
		return _comp;
	}

	/** Stops the thread. Called only by {@link com.potix.zk.ui.sys.UiEngine}
	 * when it is stopping.
	 */
	public void cease() {
		synchronized (_evtmutex) {
			_ceased = true;
			_evtmutex.notifyAll();
		}
		if (_suspmutex != null) {
			synchronized (_suspmutex) {
				_suspmutex.notifyAll();
			}
		}
	}
	/** Stops the thread silently. Called by {@link com.potix.zk.ui.sys.UiEngine}
	 * to stop abnormal
	 * page.
	 */
	public void ceaseSilently() {
		_silent = true;
		cease();
	}
	/** Suspends the current thread and Waits until {@link #doResume}
	 * is called.
	 *
	 * <p>Note:
	 * <ul>
	 * <li>It is used internally only for implementing {@link com.potix.zk.ui.sys.UiEngine}
	 * <li>Caller must invoke {@link Configuration#invokeEventThreadSuspends}
	 * before calling this method. (Reason: UiEngine might have to store some info
	 * after {!link Configuration#invokeEventThreadSuspends} is called.
	 * <li>The current thread must be {@link EventProcessingThread}.
	 * <li>It is a static method.
	 * </ul>
	 */
	public static void doSuspend(Object mutex) throws InterruptedException {
		((EventProcessingThread)Thread.currentThread()).doSuspend0(mutex);
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

				if (!_ceased) _suspmutex.wait();
			}
		} finally {
			_suspmutex = null;
			_suspended = false; //just in case (such as _ceased)
		}

		_desktop.getWebApp().getConfiguration()
			.invokeEventThreadResumes(_comp, _event, false);

		if (_ceased) throw new InterruptedException("Ceased");
		setup();
	}
	/** Resumes this thread and returns only if the execution (being suspended
	 * by {@link #doSuspend}) completes.
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

		//First, copy since _comp and _event since event thread clean up them
		//when completed
		final Component comp = _comp;
		final Event event = _event;
		try {
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
				if (!_ceased && !isIdle() && !_suspended)
					_evtmutex.wait();
			}
		} finally {
			//_evtThdCleanups is null if //1) no listener;
			//2) the event thread is suspended again (handled by another doResume)
			if (_evtThdCleanups != null)
				invokeEventThreadCompletes(comp, event);
		}

		checkError();
		return isIdle();
	}

	/** Ask this event thread to process the specified event.
	 *
	 * <p>Used internally to implement {@link com.potix.zk.ui.sys.UiEngine}.
	 * Application developers
	 * shall use {@link com.potix.zk.ui.event.Events#sendEvent} instead.
	 *
	 * @return whether the event has been processed completely or just be suspended.
	 * Recycle it only if true is returned.
	 */
	public boolean processEvent(Component comp, Event event) {
		if (Thread.currentThread() instanceof EventProcessingThread)
			throw new IllegalStateException("processEvent cannot be called in an event thread");
		if (comp == null || event == null)
			throw new IllegalArgumentException("null");
		if (_ceased)
			throw new InternalError("The event thread has beeing stopped");
		if (_comp != null)
			throw new InternalError("reentering processEvent not allowed");

		_desktop = comp.getDesktop();
		if (_desktop == null)
			throw new InternalError("Not belonging to any desktop? "+comp);

		_comp = comp;
		_event = event;
		_locale = Locales.getCurrent();
		_timeZone = TimeZones.getCurrent();
		_ex = null;

		_evtThdInits = _desktop.getWebApp()
			.getConfiguration().newEventThreadInits(comp, event);
		try {
			synchronized (_evtmutex) {
				_evtmutex.notify(); //ask the event thread to handle it
				if (!_ceased) _evtmutex.wait();
					//wait until the event thread to complete or suspended
			}
		} catch (InterruptedException ex) {
			throw new UiException(ex);
		} finally {
			//_evtThdCleanups is null if //1) no listener;
			//2) the event thread is suspended (then handled by doResume).
			if (_evtThdCleanups != null)
				invokeEventThreadCompletes(comp, event);
		}

		checkError(); //check any error occurs
		return isIdle();
	}
	private void invokeEventThreadCompletes(Component comp, Event event)
	throws UiException {
		final List errs = _ex != null ? null: new LinkedList();

		_desktop.getWebApp().getConfiguration()
			.invokeEventThreadCompletes(_evtThdCleanups, comp, event, errs);
		_evtThdCleanups = null;

		if (errs != null && !errs.isEmpty())
			throw UiException.Aide.wrap((Throwable)errs.get(0));
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
			while (!_ceased) {
				final boolean evtAvail = !isIdle();
				if (evtAvail) {
					Configuration config = _desktop.getWebApp().getConfiguration();
					boolean cleaned = false;
					++_nBusyThd;
					try {
						if (D.ON && log.finerable()) log.finer("Processing event: "+_event);

						
						config.invokeEventThreadInits(_evtThdInits, _comp, _event);
						_evtThdInits = null;

						Locales.setThreadLocal(_locale);
						TimeZones.setThreadLocal(_timeZone);
						setup();
						process0();
					} catch (Throwable ex) {
						_ex = ex;
						cleaned = true;
						_evtThdCleanups =
							config.newEventThreadCleanups(_comp, _event, ex, null);
					} finally {
						--_nBusyThd;

						if (!cleaned) {
							final List errs = new LinkedList();
							_evtThdCleanups =
								config.newEventThreadCleanups(_comp, _event, null, errs);
							if (_ex == null && !errs.isEmpty())
								_ex = (Throwable)errs.get(0);
									//propogate back the first exception
						}

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
					if (!_ceased) _evtmutex.wait();
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
						+"\n"+Exceptions.getFirstStackTrace(ex));
			} else {	
				System.out.println("The event processing thread interrupted: "+Exceptions.getMessage(ex));
				//Don't use log because it might be stopped
			}
		} finally {
			--_nThd;
		}
	}

	/** Sends the specified component and event and processes the event
	 * synchronously. Used to implements {@link com.potix.zk.ui.event.Events#sendEvent}.
	 */
	public void sendEvent(final Component comp, Event event)
	throws Exception {
		if (D.ON && log.finerable()) log.finer("Process sent event: "+event);
		if (event == null || comp == null)
			throw new IllegalArgumentException("Both comp and event must be specified");
		if (!(Thread.currentThread() instanceof EventProcessingThread))
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
		final Namespace ns = Namespaces.beforeInterpret(null, _comp);
		ns.backupVariable("event", false);
		ns.setVariable("event", _event, true);
		try {
			process1(ns);
		} finally {
			Namespaces.afterInterpret(ns);
		}
	}
	private void process1(Namespace ns) throws Exception {
		final Page page = _comp.getPage();
		final String evtnm = _event.getName();

		final String script =
			((ComponentCtrl)_comp).getMillieu().getEventHandler(_comp, evtnm);
		if (script != null) {
			page.interpret(script, ns);
			if (!_event.isPropagatable())
				return; //done
		}

		for (Iterator it = _comp.getListenerIterator(evtnm); it.hasNext();) {
			((EventListener)it.next()).onEvent(_event);
			if (!_event.isPropagatable())
				return; //done
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
