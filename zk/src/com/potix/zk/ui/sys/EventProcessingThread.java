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
package com.potix.zk.ui.sys;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Locale;
import java.lang.reflect.Method;

import com.potix.lang.D;
import com.potix.lang.Threads;
import com.potix.lang.Exceptions;
import com.potix.util.prefs.Apps;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.metainfo.ComponentDefinition;
import com.potix.zk.ui.metainfo.InstanceDefinition;

/** Thread to handle events.
 * We need to handle events in a separate thread, because it might
 * suspend (by calling {@link UiEngine#wait}), such as waiting
 * a modal dialog to complete.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.22 $ $Date: 2006/05/29 04:28:09 $
 */
public class EventProcessingThread extends Thread {
	private static final Log log = Log.lookup(EventProcessingThread.class);

	/** Part of the command: component to handle the event. */
	private Component _comp;
	/** Part of the command: event to process. */
	private Event _event;
	/** Part of the command: a list of EventThreadInit instances. */
	private List _evtThdInits;
	/** Part of the command: locale. */
	private Locale _locale;
	/** Result of the command. */
	private Throwable _ex;

	private static int _nThd, _nBusyThd;

	/** The mutex use to notify an event is ready for processing, or
	 * has been processed.
	 */
	private final Object _evtmutex = new Integer(0);
	/** The mutex use to suspend an event processing. */
	private final Object _suspmutex = new Integer(2);
	private boolean _ceased;
	/** Whether not to show message when stopping. */
	private boolean _silent;

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

	/** Stops the thread. Called only by UiEngine when it is stopping.
	 */
	public void cease() {
		synchronized (_evtmutex) {
			_ceased = true;
			_evtmutex.notifyAll();
		}
		synchronized (_suspmutex) {
			_suspmutex.notifyAll();
		}
	}
	/** Stops the thread silently. Called by UiEngine to stop abnormal
	 * page.
	 */
	public void ceaseSilently() {
		_silent = true;
		cease();
	}
	/** Suspends the current thread and Waits until {@link #doResume}
	 * is called.
	 * <p>Note:
	 * <ul>
	 * <li>The current thread must be {@link EventProcessingThread}.
	 * <li>It is a static method.
	 * </ul>
	 */
	public static void doSuspend() throws InterruptedException {
		((EventProcessingThread)Thread.currentThread()).doSuspend0();
	}
	private void doSuspend0() throws InterruptedException {
		if (D.ON && log.finerable()) log.finer("Suspend event processing; "+_event);
		if (isIdle())
			throw new InternalError("Called without processing event?");

		//we have to lock _suspmutex first. Otherwise, after _evtmutex.notify
		//doResume might be called and _suspmutex.notify before wait
		//----
		//NOTE: potential dead lock if doSuspend and doResume are called
		//at the same time. However, it shall not happen because we can only
		//resume suspended processing.
		synchronized (_suspmutex) {
			synchronized (_evtmutex) {
				_evtmutex.notify(); //let the main thread continue
			}
			if (!_ceased) _suspmutex.wait();
		}

		_comp.getDesktop().getWebApp().getConfiguration()
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

		//we have to lock _evtmutex first. Otherwise, after _suspmutex.notify
		//evtthd might complete or suspend again before wait
		synchronized (_evtmutex) {
			synchronized (_suspmutex) {
				_suspmutex.notify(); //wake the suspended event thread
			}
			if (!_ceased) _evtmutex.wait();
				//wait until the event thread completes or suspend again
		}

		checkError();
		return isIdle();
	}

	/** Returns the desktop, or null if it is not processing an event.
	 */
	synchronized public Desktop getDesktop() {
		return _comp != null ? _comp.getDesktop(): null;
	}
	/** Ask this event thread to process the specified event.
	 *
	 * <p>Note: it cannot be called from another event thread.
	 * Reason: to prevent deadlock.
	 *
	 * @return whether the event has been processed completely or just be suspended.
	 * Recycle it only if true is returned.
	 */
	public
	boolean processEvent(Component comp, Event event, List evtThdInits) {
		if (Thread.currentThread() instanceof EventProcessingThread)
			throw new IllegalStateException("processEvent cannot be called in an event thread");
		if (_ceased)
			throw new InternalError("The event thread has beeing stopped");

		request(comp, event, evtThdInits);

		try {
			synchronized (_evtmutex) {
				_evtmutex.notify(); //ask the event thread to handle it
				if (!_ceased) _evtmutex.wait();
					//wait until the event thread to complete or suspended
			}
		} catch (InterruptedException ex) {
			throw new UiException(ex);
		}

		checkError(); //check any error occurs
		return isIdle();
	}
	/** AuRequest the event processing thread to process an event.
	 */
	synchronized private
	void request(Component comp, Event event, List evtThdInits) {
		if (_comp != null)
			throw new InternalError("reentering processEvent not allowed");
		if (comp == null || event == null)
			throw new NullPointerException();
		_comp = comp;
		_event = event;
		_evtThdInits = evtThdInits;
		_locale = Apps.getCurrentLocale();
		_ex = null;
	}
	/** Setup for execution. */
	synchronized private void setup() {
		final Desktop desktop = _comp.getDesktop();
		SessionsCtrl.setCurrent(desktop.getSession());
		final Execution exec = desktop.getExecution();
		ExecutionsCtrl.setCurrent(exec);
		((ExecutionCtrl)exec).setCurrentPage(_comp.getPage());
			//Note: _com.getPage might return null because this method
			//is also called when resumed.
	}
	/** Cleanup for executionl. */
	synchronized private void cleanup() {
		_comp.getDesktop().getWebApp()
			.getConfiguration().invokeEventThreadCleanups(_comp, _event);
		_comp = null;
		_event = null;
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
					++_nBusyThd;
					try {
						if (D.ON && log.finerable()) log.finer("Processing event: "+_event);

						_comp.getDesktop().getWebApp().getConfiguration()
							.invokeEventThreadInits(_evtThdInits, _comp, _event);
						_evtThdInits = null;

						Apps.setThreadLocale(_locale);
						setup();
						process0();
					} catch (Throwable ex) {
						_ex = ex;
					} finally {
						--_nBusyThd;
						cleanup();
						ExecutionsCtrl.setCurrent(null);
						SessionsCtrl.setCurrent(null);
						Apps.setThreadLocale(_locale = null);
//						if (D.ON && log.finerable()) log.finer("Real processing is done; "+_event);
					}
				}

				synchronized (_evtmutex) {
					if (evtAvail)
						_evtmutex.notify();
						//wake the main thread OR the resuming thread
					if (!_ceased) _evtmutex.wait();
						//wait the main thread to issue request
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
		if (_comp.getDesktop() != comp.getDesktop())
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

		final Page page = _comp.getPage();
		final String evtnm = _event.getName();

		final ComponentDefinition compdef = _comp.getDefinition();
		if (compdef instanceof InstanceDefinition) {
			final String script =
				((InstanceDefinition)compdef).getEventHandler(_comp, evtnm);
			if (script != null) {
				page.setVariable("event", _event);

				try {
					page.interpret(_comp, script);
				} finally {
					page.setVariable("event", null);
				}
				if (!_event.isPropagatable())
					return; //done
			}
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
