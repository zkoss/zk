/* ProgressCallbacks.java

{{IS_NOTE
	$Id: ProgressCallbacks.java,v 1.16 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Mar  8 15:13:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.progress;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import com.potix.mesg.MCommon;
import com.potix.lang.Threads;
import com.potix.lang.Exceptions;
import com.potix.lang.Expectable;
import com.potix.lang.SystemException;
import com.potix.lang.OperationException;
import com.potix.util.logging.Log;
import com.potix.util.prefs.Apps;

/**
 * Utilities to handle {@link ProgressCallback}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.16 $ $Date: 2006/02/27 03:42:06 $
 */
public class ProgressCallbacks {
	private static final Log log = Log.lookup(ProgressCallbacks.class);

	private static final ThreadLocal _cbInfo = new ThreadLocal();
	private static final CallbackThread _cbthd = new CallbackThread();

	/** Invokes the progress if any (returning from {@link #get}).
	 * To improve the performance, the second progress is ignored if it
	 * happens in 1 seconds.
	 *
	 * @exception OperationException if this thread shall stop processing
	 */
	public static final void onProgress() throws OperationException {
		final Info info = (Info)_cbInfo.get();
		if (info != null)
			info.onProgress(true);
	}
	/** Invokes the progress, if any, to show an message.
	 * It is optinal. If not implemented, simply does nothing.
	 */
	public static final void onMessage(String msg) throws OperationException {
		final Info info = (Info)_cbInfo.get();
		if (info != null)
			info.onMessage(msg);
	}

	/** Sets the progress callback for the current thread.
	 * It will not inherit to child threads, so it's caller's job if
	 * it is demanded.
	 */
	public static final void set(ProgressCallback pgcb) {
		final Info old = (Info)_cbInfo.get();
		if (old != null)
			old.cease();
		_cbInfo.set(pgcb != null ? new Info(pgcb): null);
	}
	/** Returns the progress callback for the current thread.
	 * In most case, you could simply invoke {@link #onProgress} without
	 * knowing whether {@link #set} was called.
	 */
	public static final ProgressCallback get() {
		final Info info = (Info)_cbInfo.get();
		return info != null ? info.getCallback(): null;
	}
	/** Adds a cancelable task to the progress callback, such that, when
	 * the callback detects aborting, it could cancel the task.
	 *
	 * <p>If the task is ever canceled (i.e., {@link CancelListener#onCancel} is called),
	 * it is removed from the list (as if {@link #removeCancelListener} is called).
	 * In other words, the progress callback at most calls {@link CancelListener#onCancel}
	 * once (unless it is added again).
	 *
	 * <p>If no {@link ProgressCallback} is ever set for the current thread,
	 * the listener is ignored.
	 *
	 * @return true if the listener is added successfully;
	 * false if the listener has been added before, or no current progress
	 * callback.
	 */
	public static final boolean addCancelListener(CancelListener listener) {
		final Info info = (Info)_cbInfo.get();
		return info != null && info.addCancelListener(listener);
	}
	/** Removes the cancelable task registed by {@link #addCancelListener}.
	 * @return false if the listener is not added before.
	 */
	public static final boolean removeCancelListener(CancelListener listener) {
		final Info info = (Info)_cbInfo.get();
		return info != null && info.removeCancelListener(listener);
	}

	private static class Info {
		private final ProgressCallback _callback;
		private final Set _cancelListeners = new HashSet(3);
		/** Next time to callback on progress. */
		private long _pgcbTime;
		/** When to invoke Apps.keepAlive(). */
		private byte _kaCount;
		private Info(ProgressCallback callback) {
			_callback = callback;
			_pgcbTime = System.currentTimeMillis() + 100;
			_cbthd.enqueue(this);
		}
		private void cease() {
			_cbthd.dequeue(this);
		}
		private void cancel() {
			synchronized (_cancelListeners) {
				for (Iterator it = _cancelListeners.iterator(); it.hasNext();) {
					try {
						((CancelListener)it.next()).onCancel();
					} catch (Throwable ex) {
						log.warning("Failed to call back onCancel", ex);
					}
					it.remove();
				}
			}
		}
		private ProgressCallback getCallback() {
			return _callback;
		}
		synchronized private void onProgress(boolean keepAlive)
		throws OperationException {
			final long now = System.currentTimeMillis();
			if (now >= _pgcbTime) {
				try {
					_pgcbTime = now + 2000; //2 sec
					_callback.onProgress();
				} catch (Throwable ex) {
					cancel();
					if (ex instanceof Expectable) {
						if (ex instanceof RuntimeException) {
							throw (RuntimeException)ex;
						} else if (ex instanceof Error) {
							throw (Error)ex;
						}
					}
					throw new OperationException(ex);
						//Not SystemException.Aide.wrap since it is predictable
				}

				//Note: we have to keep it alive because a report might
				//exceed the timeout.
				if (++_kaCount > 60 && keepAlive) { //2 min
					_kaCount = 0;
					Apps.keepAlive();
				}
			}
		}
		private void onMessage(String msg) throws OperationException {
			try {
				_callback.onMessage(msg);
			} catch (Throwable ex) {
				cancel();
				if (ex instanceof Expectable) {
					if (ex instanceof RuntimeException) {
						throw (RuntimeException)ex;
					} else if (ex instanceof Error) {
						throw (Error)ex;
					}
				}
				throw new OperationException(ex);
					//Not SystemException.Aide.wrap since it is predictable
			}
		}
		private boolean addCancelListener(CancelListener listener) {
			synchronized (_cancelListeners) { //required because CallbackThread calls onProgress
				return _cancelListeners.add(listener);
			}
		}
		/** Removes the cancelable task registed by {@link #addCancelListener}.
		 */
		private boolean removeCancelListener(CancelListener listener) {
			synchronized (_cancelListeners) { //required because CallbackThread calls onProgress
				return _cancelListeners.remove(listener);
			}
		}
		protected void finalized() {
			cease();
		}
	}

	/** Generates callback when the main thread is doing a long task
	 * such as executing a slow SQL.
	 * <p>There is single thread to handle this periodic callback, while
	 * there might be many ProgressCallback available.
	 * Thus, when a progress callback is assigned, enqueue() must be called.
	 * On the other hand, when it is released, dequeue() must be called.
	 */
	private static class CallbackThread extends Thread {
		private final Set _infos = new HashSet();
		private volatile boolean _ceased = false;
		private CallbackThread() {
			Threads.setPriority(this, Thread.MIN_PRIORITY);
			Threads.setDaemon(this, true);
			start();
		}
		private void enqueue(Info info) {
			if (info == null)
				throw new NullPointerException();
			synchronized (_infos) {
				_infos.add(info);
				_infos.notify();
			}
		}
		private void dequeue(Info info) {
			synchronized (_infos) {
				_infos.remove(info);
			}
		}
		private void cease() {
			_ceased = true;
		}
		public void run() {
			try {
				while (!_ceased) {
					synchronized (_infos) {
						if (_infos.isEmpty())
							_infos.wait();

						for (Iterator it = _infos.iterator(); it.hasNext();) {
							try {
								((Info)it.next()).onProgress(false);
							} catch (Throwable ex) {
								log.info(Exceptions.getMessage(ex));
								it.remove(); //no longer alive
							}
						}
					}
					sleep(2000); //2 seconds
				}
				System.out.println("Progress callback thread stopped");
			} catch (InterruptedException ex) {
				System.out.println("Progress callback thread is interrupted");
				//don't use log because LogService might be stopped, too
			}
		}
	}
}
