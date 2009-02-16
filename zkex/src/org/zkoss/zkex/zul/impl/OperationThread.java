/* OperationThread.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 29, 2007 9:21:36 AM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkex.zul.impl;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;

/**
 * This class is for model sharer developer only, you rarely need to use this class.<br/>
 * 
 * OperationThread has only one instance in each desktop, it store it-self in the desktop by setAttribute.
 * It create and monitor the {@link OperationQueue}, if there are any operation in queue, it will consume it. <br/>
 * 
 * @author Dennis.Chen
 * @since 3.0.0
 */
public class OperationThread extends Thread {

	private static final String DESKTOP_KEY = "zkex:opthread";

	private boolean _running = true;

	private OperationQueue _queue;

	private Desktop _desktop;

	private long _activateTimeout = 10000;
	
	private long _waitTimeout = 10000;

	private int _maxFailCount = 4;

	private static final Log log = Log.lookup(OperationThread.class);

	/* package */OperationThread(Desktop desktop) {
		_desktop = desktop;
		_queue = new OperationQueue();
		this.setName("OPThread-" + desktop.getId());
	}

	/* package */OperationQueue getQueue() {
		return _queue;
	}

	
	/**
	 * Get the {@link OperationQueue} of {@linkplain OperationThread},
	 * It is check is there any {@linkplain OperationThread} exist in desktop.
	 * If no, create one ,start it and store in desktop, then return thread's operation queue.
	 * If yes, return operation queue directly.  
	 * 
	 * There is only one {@linkplain OperationThread} in each desktop.
	 * @param desktop the associated desktop
	 * @return a queue which associate to desktop
	 */
	public static OperationQueue getQueue(Desktop desktop) {
		if (desktop == null)
			throw new NullPointerException("desktop is null");
		synchronized(desktop) {
			if (!desktop.isAlive()) {
				throw new IllegalStateException("desktop not alive:" + desktop);
			}
			OperationThread t = (OperationThread) desktop.getAttribute(DESKTOP_KEY);
			if (t == null) {
				t = new OperationThread(desktop);
				if(D.ON && log.debugable()){
					log.debug("staring a Operation Thread for desktop:"+desktop+",name="+t.getName());
				}
				desktop.setAttribute(DESKTOP_KEY, t);
				t.start();
				
			}
			return t.getQueue();
		}
	}

	/**
	 * Terminate a {@linkplain OperationThread} which is stored in desktop and clear it.
	 * 
	 * @param desktop the associated desktop
	 */
	public static void destroyWith(Desktop desktop) {
		if (desktop == null){
			throw new NullPointerException("desktop is null");
		}
		if(D.ON && log.debugable()){
			log.debug("destory a Operation Thread for desktop:"+desktop);
		}
		synchronized(desktop) {
			if (desktop.isAlive()) {// destroy desktop if it still alive.
				OperationThread t = (OperationThread) desktop
						.getAttribute(DESKTOP_KEY);
				desktop.removeAttribute(DESKTOP_KEY);
				if (t != null && t.isRunning()) {
					t.terminate();
				}
				
			}
		}
	}

	/**
	 * Is this thread still running
	 * @return true is thread still running
	 */
	public boolean isRunning() {
		return _running;
	}

	/**
	 * Terminate this thread. thread will be stopped after last run trip.
	 */
	public void terminate() {
		_running = false;
		synchronized(_queue) {
			_queue.notifyAll();
		}
	}

	public void run() {
		try {
			int failCount = 0;
			while (_running) {
				if(!_desktop.isAlive()){
					throw new DesktopUnavailableException(
							"Desktop is not alive"+_desktop);
				}
				if (_queue.hasElement()) {
					boolean r = false;
					try {
						r = Executions.activate(_desktop, _activateTimeout);
					} catch (InterruptedException e) {
					}
					if (!r) {
						failCount++;
						if (failCount >= _maxFailCount) {
							throw new DesktopUnavailableException(
									"Fail to Activate Desktop:" + _desktop
											+ " after " + failCount + " times");
						}
						continue;
					}
					failCount = 0;

					// since we have activated desktop,
					// we execute all operation in queue
					Operation op = null;
					try {
						while (_queue.hasElement()) {
							op = _queue.next();
							op.execute(_desktop);
						}
					} catch (Exception x) {
						// if exception, notify fail.
						if (op != null) {
							op.failToExecute(_desktop);
						}
						throw x;
					} finally {
						Executions.deactivate(_desktop);
					}
				}
				try {
					// if queue is still empty, and still running then we wait.
					// queue will be notify when someone put operation to queue
					// or terminate is invoked check OperationQueue#put
					synchronized(_queue) {
						if (_running && !_queue.hasElement()) {
							//wait just a while, 
							_queue.wait(_waitTimeout);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		} catch (DesktopUnavailableException x) {
			// send fail to all operation that it can't be execute.
			if(D.ON && log.debugable()){
				log.debug("Desktop not available:" + x.getMessage());
			}
			while (_queue.hasElement()) {
				Operation op = _queue.next();
				op.failToExecute(_desktop);
			}
		} catch (Exception x) {
			log.warning(x);
			while (_queue.hasElement()) {
				Operation op = _queue.next();
				op.failToExecute(_desktop);
			}
		} finally {
			_running = false;
			OperationThread.destroyWith(_desktop);
			//notify all listener thread is destroyed.
			_queue.fireQueueUnavailable(_desktop);
			_queue.clearListener();
			_desktop = null;
			_queue = null;
			if(D.ON && log.debugable()){
				log.debug("end of a Operation Thread, name="+this.getName());
			}
		}
	}
}
