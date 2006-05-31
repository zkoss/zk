/* Configuration.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Mar 26 16:06:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import com.potix.util.logging.Log;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.EventThreadInit;
import com.potix.zk.ui.event.EventThreadCleanup;
import com.potix.zk.ui.event.EventThreadSuspend;
import com.potix.zk.ui.event.EventThreadResume;
import com.potix.zk.ui.util.SessionInit;
import com.potix.zk.ui.util.SessionCleanup;

/**
 * The ZK configuration.
 *
 * <p>To retrieve the current configuration, use
 * {@link com.potix.zk.ui.WebApp#getConfiguration}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Configuration {
	private static final Log log = Log.lookup(Configuration.class);

	private final List
		_evtInits = new LinkedList(), _evtCleans = new LinkedList(),
		_evtSusps = new LinkedList(), _evtResus = new LinkedList(),
		_sessInits = new LinkedList(), _sessCleans = new LinkedList(),
		_dtInits = new LinkedList(), _dtCleans = new LinkedList();
	private Monitor _monitor;
	private String _timeoutUrl;

	/** Adds a listener class.
	 */
	public void addListener(Class klass) throws Exception {
		if (Monitor.class.isAssignableFrom(klass)) {
			if (_monitor != null)
				throw new UiException("Monitor listener can be assigned only once");
			_monitor = (Monitor)klass.newInstance();
		}

		if (EventThreadInit.class.isAssignableFrom(klass)) {
			synchronized (_evtInits) {
				_evtInits.add(klass);
			}
		}
		if (EventThreadCleanup.class.isAssignableFrom(klass)) {
			synchronized (_evtCleans) {
				_evtCleans.add(klass);
			}
		}
		if (EventThreadSuspend.class.isAssignableFrom(klass)) {
			synchronized (_evtSusps) {
				_evtSusps.add(klass);
			}
		}
		if (EventThreadResume.class.isAssignableFrom(klass)) {
			synchronized (_evtResus) {
				_evtResus.add(klass);
			}
		}

		if (SessionInit.class.isAssignableFrom(klass)) {
			synchronized (_sessInits) {
				_sessInits.add(klass);
			}
		}
		if (SessionCleanup.class.isAssignableFrom(klass)) {
			synchronized (_sessCleans) {
				_sessCleans.add(klass);
			}
		}

		if (DesktopInit.class.isAssignableFrom(klass)) {
			synchronized (_dtInits) {
				_dtInits.add(klass);
			}
		}
		if (DesktopCleanup.class.isAssignableFrom(klass)) {
			synchronized (_dtCleans) {
				_dtCleans.add(klass);
			}
		}
	}
	/** Removes a listener class.
	 */
	public void removeListener(Class klass) {
		synchronized (_evtInits) {
			_evtInits.remove(klass);
		}
		synchronized (_evtCleans) {
			_evtCleans.remove(klass);
		}
		synchronized (_evtSusps) {
			_evtSusps.remove(klass);
		}
		synchronized (_evtResus) {
			_evtResus.remove(klass);
		}

		synchronized (_sessInits) {
			_sessInits.remove(klass);
		}
		synchronized (_sessCleans) {
			_sessCleans.remove(klass);
		}
		synchronized (_dtInits) {
			_dtInits.remove(klass);
		}
		synchronized (_dtCleans) {
			_dtCleans.remove(klass);
		}
	}

	/** Contructs a list of {@link EventThreadInit} instances for
	 * each relevant listener registered by {@link #addListener}.
	 *
	 * <p>It is called by ZK Engine before starting an event processing
	 * thread.
	 *
	 * @exception UiException to prevent a thread from being processed
	 */
	public List newEventThreadInits(Component comp, Event evt)
	throws UiException {
		if (_evtInits.isEmpty()) return Collections.EMPTY_LIST;
			//it is OK to test LinkedList.isEmpty without synchronized

		final List inits = new LinkedList();
		synchronized (_evtInits) {
			for (Iterator it = _evtInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					final EventThreadInit init =
						(EventThreadInit)klass.newInstance();
					init.prepare(comp, evt);
					inits.add(init);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the event being processed
				}
			}
		}
		return inits;
	}
	/** Invokes {@link EventThreadInit#init} for each instance returned
	 * by {@link #newEventThreadInits}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param inits a list of {@link EventThreadInit} instances returned from
	 * {@link #newEventThreadInits}.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 */
	public void invokeEventThreadInits(List inits, Component comp, Event evt) {
		if (inits.isEmpty()) return;

		for (Iterator it = inits.iterator(); it.hasNext();) {
			final EventThreadInit fn = (EventThreadInit)it.next();
			try {
				fn.init(comp, evt);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+fn, ex);
			}
		}
	}
	/** Invokes {@link EventThreadCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link EventThreadCleanup} is constructed first,
	 * and then invoke {@link EventThreadCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 */
	public void invokeEventThreadCleanups(Component comp, Event evt) {
		if (_evtCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_evtCleans) {
			for (Iterator it = _evtCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((EventThreadCleanup)klass.newInstance())
						.cleanup(comp, evt);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}

	/** Invokes {@link EventThreadSuspend#beforeSuspend} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link EventThreadSuspend} is constructed first,
	 * and then invoke {@link EventThreadSuspend#beforeSuspend}.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param obj which object that {@link com.potix.zk.ui.Executions#wait}
	 * is called with.
	 * @exception UiException to prevent a thread from suspending
	 */
	public void invokeEventThreadSuspends(Component comp, Event evt, Object obj)
	throws UiException {
		if (_evtSusps.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_evtSusps) {
			for (Iterator it = _evtSusps.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((EventThreadSuspend)klass.newInstance())
						.beforeSuspend(comp, evt, obj);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the thread to suspend
				}
			}
		}
	}
	/** Invokes {@link EventThreadResume#afterResume} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link EventThreadResume} is constructed first,
	 * and then invoke {@link EventThreadResume#afterResume}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param aborted whether it is caused by aborting. If false, it is
	 * resumed normally.
	 */
	public
	void invokeEventThreadResumes(Component comp, Event evt, boolean aborted) {
		if (_evtResus.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_evtResus) {
			for (Iterator it = _evtResus.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((EventThreadResume)klass.newInstance())
						.afterResume(comp, evt, aborted);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}

	/** Invokes {@link SessionInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link SessionInit} is constructed first,
	 * and then invoke {@link SessionInit#init}.
	 *
	 * @param sess the session that is created
	 * @exception UiException to prevent a session from being created
	 */
	public void invokeSessionInits(Session sess)
	throws UiException {
		if (_sessInits.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_sessInits) {
			for (Iterator it = _sessInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((SessionInit)klass.newInstance()).init(sess);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the creation of a session
				}
			}
		}
	}
	/** Invokes {@link SessionCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link SessionCleanup} is constructed first,
	 * and then invoke {@link SessionCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param sess the session that is being destroyed
	 */
	public void invokeSessionCleanups(Session sess) {
		if (_sessCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_sessCleans) {
			for (Iterator it = _sessCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((SessionCleanup)klass.newInstance()).cleanup(sess);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}

	/** Invokes {@link DesktopInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link DesktopInit} is constructed first,
	 * and then invoke {@link DesktopInit#init}.
	 *
	 * @param desktop the desktop that is created
	 * @exception UiException to prevent a desktop from being created
	 */
	public void invokeDesktopInits(Desktop desktop)
	throws UiException {
		if (_dtInits.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_dtInits) {
			for (Iterator it = _dtInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((DesktopInit)klass.newInstance()).init(desktop);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the creation of a session
				}
			}
		}
	}
	/** Invokes {@link DesktopCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link DesktopCleanup} is constructed first,
	 * and then invoke {@link DesktopCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param desktop the desktop that is being destroyed
	 */
	public void invokeDesktopCleanups(Desktop desktop) {
		if (_dtCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_dtCleans) {
			for (Iterator it = _dtCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((DesktopCleanup)klass.newInstance()).cleanup(desktop);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}

	/** Sets the URL that is used when the session timeout or
	 * desktop is no longer available.
	 *
	 * @param url the URL used if timeout, or null to show an error message
	 * at the client only. If empty, it works as reloading the same URL again.
	 */
	public void setTimeoutURL(String url) {
		_timeoutUrl = url;
	}
	/** Sets the URL that is used when the session timeout or
	 * desktop is no longer available, or null.
	 *
	 * <p>Default: null.
	 *
	 * <p>If null is returned, an message is shown up at the client.
	 * If empty, it works as reloading the same URL again.
	 * If non null, the browser will be redirected to the returned URL.
	 */
	public String getTimeoutURL() {
		return _timeoutUrl;
	}

	/** Returns the monitor for this application, or null if not set.
	 */
	public Monitor getMonitor() {
		return _monitor;
	}
	/** Sets the monitor for this application, or null to disable it.
	 *
	 * <p>In addition to call this method, you could specify a monitor
	 * in web.xml.
	 */
	public void setMonitor(Monitor monitor) {
		_monitor = monitor;
	}
}
