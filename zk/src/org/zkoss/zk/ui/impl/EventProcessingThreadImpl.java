/* EventProcessingThreadImpl.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 11:24:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import org.zkoss.lang.Threads;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.EventProcessingThread;

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

	/** The processor. */
	private EventProcessor _proc;
	/** Part of the command: locale. */
	private Locale _locale;
	/** Part of the command: time zone. */
	private TimeZone _timeZone;
	/** Part of the result: a list of EventThreadInit instances. */
	private List _evtThdInits;
	/** Part of the result: a list of EventThreadCleanup instances. */
	private List _evtThdCleanups;
	/** Part of the result: a list of EventThreadResume instances. */
	private List _evtThdResumes;
	/** Part of the result. a list of EventThreadSuspend instances. */
	private List _evtThdSuspends;
	/** Result of the result. */
	private Throwable _ex;
	/** Whether the execution is activated. */
	private boolean _acted;

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
//		if (log.debugable()) log.debug("Starting an event processing thread");
		Threads.setDaemon(this, true);
		start();
	}

	//EventProcessingThread//
	public boolean isCeased() {
		return _ceased != null || !isAlive();
	}
	public boolean isSuspended() {
		return _suspended;
	}
	synchronized public boolean isIdle() {
		return _proc == null;
	}
	public final Event getEvent() {
		return _proc.getEvent();
	}
	public final Component getComponent() {
		return _proc.getComponent();
	}
	public void sendEvent(final Component comp, Event event)
	throws Exception {
//		if (log.finerable()) log.finer("Process sent event: "+event);
		if (event == null || comp == null)
			throw new IllegalArgumentException("Both comp and event must be specified");
		if (!(Thread.currentThread() instanceof EventProcessingThreadImpl))
			throw new IllegalStateException("Only callable when processing an event");

		final EventProcessor oldproc = _proc;
		_proc = new EventProcessor(_proc.getDesktop(), comp, event);
		try {
			setup();
			process0();
		} finally {
			_proc = oldproc;
			if (_ceased != null)
				throw new InterruptedException(_ceased);
				//Bug 2819521: cease() resumes suspend threads, which shall stop
			setup();
		}
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
//		if (log.finerable()) log.finer("Suspend event processing; "+_proc);
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

				//Bug 1814298: need to call Execution.onDeactivate
				Execution exec = getExecution();
				if (exec != null) {
					_acted = false;
					try {
						((ExecutionCtrl)exec).onDeactivate();
					} catch (Throwable ex) {
						log.warningBriefly("Ignored deactivate failure", ex);
					}
				}

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

		//being resumed
		setup();
		Execution exec = getExecution();
		if (exec != null) {
			((ExecutionCtrl)exec).onActivate();
			_acted = true;
		}

		final List resumes = _evtThdResumes;
		_evtThdResumes = null;
		if (resumes != null && !resumes.isEmpty()) {
			_proc.getDesktop().getWebApp().getConfiguration()
				.invokeEventThreadResumes(
					resumes, getComponent(), getEvent());
				//FUTURE: how to propogate errors to the client
		}
	}
	private Execution getExecution() {
		Execution exec = _proc.getDesktop().getExecution();
		return exec != null ? exec: Executions.getCurrent();
			//just in case that the execution is dead first
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
//		if (log.finerable()) log.finer("Resume event processing; "+_proc);
		if (isIdle())
			throw new InternalError("Called without processing event?");
		if (_suspmutex == null)
			throw new InternalError("Resume non-suspended thread?");

		//Copy first since event thread clean up them, when completed
		final Configuration config =
			_proc.getDesktop().getWebApp().getConfiguration();
		final Component comp = getComponent();
		final Event event = getEvent();
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
	public boolean processEvent(Desktop desktop, Component comp, Event event) {
		if (Thread.currentThread() instanceof EventProcessingThreadImpl)
			throw new IllegalStateException("processEvent cannot be called in an event thread");
		if (_ceased != null)
			throw new InternalError("The event thread has beeing stopped. Cause: "+_ceased);
		if (_proc != null)
			throw new InternalError("reentering processEvent not allowed");

		_locale = Locales.getCurrent();
		_timeZone = TimeZones.getCurrent();
		_ex = null;

		final EventProcessor proc = new EventProcessor(desktop, comp, event);
			//it also check the correctness of desktop/comp/event
		final Configuration config = desktop.getWebApp().getConfiguration();
		_evtThdInits = config.newEventThreadInits(comp, event);
		try {
			long evtTimeWarn = config.getEventTimeWarning();
			long begt = 0;
			if (evtTimeWarn > 0) {
				begt = System.currentTimeMillis();
				evtTimeWarn *= 1000;
			}
			for (;;) {
				synchronized (_evtmutex) {
					_proc = proc; //Bug 1577842: don't let event thread start (and end) too early

					_evtmutex.notify(); //ask the event thread to handle it
					if (_ceased == null) {
						if (evtTimeWarn > 0)
							_evtmutex.wait(evtTimeWarn);
						else
							_evtmutex.wait();
							//wait until the event thread to complete or suspended

						if (_suspended) {
							config.invokeEventThreadSuspends(_evtThdSuspends, comp, event);
							_evtThdSuspends = null;
							break;
						}
						if (_proc == null || _ceased != null)
							break;
						if (!isAlive())
							throw new UiException("The event processing thread was aborted");

						log.warning("The event processing takes more than "+
							((System.currentTimeMillis()-begt)/1000)+" seconds: "+proc);
					}
				}
			}
		} catch (InterruptedException ex) {
			throw new UiException(ex);
		} finally {
			//Note: newEventThreadCleanups was called in run(), i.e.,
			//in the event thread

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
		if (_proc == null)
			throw new IllegalStateException();

		_evtThdSuspends = _proc.getDesktop().getWebApp().getConfiguration()
			.newEventThreadSuspends(getComponent(), getEvent(), mutex);
			//it might throw an exception, so process it before updating
			//_suspended
	}

	private void invokeEventThreadCompletes(Configuration config,
	Component comp, Event event) throws UiException {
		final List errs = new LinkedList();
		if (_ex != null) errs.add(_ex);

		if (_evtThdCleanups != null && !_evtThdCleanups.isEmpty())
			config.invokeEventThreadCompletes(_evtThdCleanups, comp, event, errs, _ceased != null);

		_evtThdCleanups = null;
		_ex = errs.isEmpty() ? null: (Throwable)errs.get(0);
	}
	/** Setup for execution. */
	synchronized private void setup() {
		_proc.setup();
	}
	/** Cleanup for execution. */
	synchronized private void cleanup() {
		_proc.cleanup();
		_proc = null;
	}
	private void checkError() {
		if (_ex != null) { //failed to process
//			if (log.debugable()) log.realCause(_ex);
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
				if (!isIdle()) {
					final Configuration config =
						_proc.getDesktop().getWebApp().getConfiguration();
					boolean cleaned = false;
					++_nBusyThd;
					Execution exec = null;
					try {
//						if (log.finerable()) log.finer("Processing event: "+_proc);

						Locales.setThreadLocal(_locale);
						TimeZones.setThreadLocal(_timeZone);

						setup();
						exec = getExecution();
						if (exec != null) {
							((ExecutionCtrl)exec).onActivate();
							_acted = true;
						}

						final boolean b = config.invokeEventThreadInits(
							_evtThdInits, getComponent(), getEvent());
						_evtThdInits = null;

						if (b) process0();
					} catch (Throwable ex) {
						cleaned = true;
						newEventThreadCleanups(config, ex);
							//ex will be assigned to _ex if newEventThreadCleanups not 'eat' it
					} finally {
						--_nBusyThd;

						if (!cleaned)
							newEventThreadCleanups(config, _ex);

//						if (log.finerable()) log.finer("Real processing is done: "+_proc);
						if (exec != null && _acted) { //_acted is false if suspended is killed
							_acted = false;
							try {
								((ExecutionCtrl)exec).onDeactivate();
							} catch (Throwable ex) {
								log.warningBriefly("Ignored deactivate failure", ex);
							}
						}
						cleanup();

						Locales.setThreadLocal(_locale = null);
						TimeZones.setThreadLocal(_timeZone = null);

						if (_ex != null && _ceased != null)
							_ex = null; //avoid anoying message (Bug 2819521)
					}
				}

				synchronized (_evtmutex) {
					_evtmutex.notify();
						//wake the main thread OR the resuming thread

					if (_ceased == null)
						_evtmutex.wait();
						//wait the main thread to issue another request
				}
			}
//			System.out.println(Thread.currentThread()+" stopped: "+_ceased);
		} catch (Throwable ex) {
			if (_ceased == null)
				_ceased = Exceptions.getMessage(ex);

			if (Exceptions.findCause(ex, InterruptedException.class) == null)
				throw UiException.Aide.wrap(ex);
//			System.out.println(Thread.currentThread()+" interrupted silently: "+_ceased);
		} finally {
			--_nThd;
			synchronized (_evtmutex) { //just in case
				final boolean abnormal = _ceased == null;
				if (abnormal) _ceased = "Unknow reason";
				_evtmutex.notify();
//				if (abnormal) System.out.println(Thread.currentThread()+" stopped with unknown cause");
			}
		}
	}
	/** Invokes {@link Configuration#newEventThreadCleanups}.
	 */
	private void newEventThreadCleanups(Configuration config, Throwable ex) {
		final List errs = new LinkedList();
		if (ex != null) errs.add(ex);
		_evtThdCleanups =
			config.newEventThreadCleanups(getComponent(), getEvent(), errs, _ceased != null);
		_ex = errs.isEmpty() ? null: (Throwable)errs.get(0);
			//propogate back the first exception
	}

	/** Processes the component and event.
	 */
	private void process0() throws Exception {
		if (_proc == null)
			throw new IllegalStateException("Not initialized");
		_proc.process();
	}

	//-- Object --//
	public String toString() {
		return "[" +getName()+": "+_proc+", ceased="+_ceased+']';
	}
}
