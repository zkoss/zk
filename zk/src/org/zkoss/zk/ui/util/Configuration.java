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
package org.zkoss.zk.ui.util;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadSuspend;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.zk.ui.util.SessionInit;
import org.zkoss.zk.ui.util.SessionCleanup;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.LocaleProvider;
import org.zkoss.zk.ui.sys.TimeZoneProvider;
import org.zkoss.zk.ui.sys.UiFactory;

/**
 * The ZK configuration.
 *
 * <p>To retrieve the current configuration, use
 * {@link org.zkoss.zk.ui.WebApp#getConfiguration}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Configuration {
	private static final Log log = Log.lookup(Configuration.class);

	private final List
		_evtInits = new LinkedList(), _evtCleans = new LinkedList(),
		_evtSusps = new LinkedList(), _evtResus = new LinkedList(),
		_appInits = new LinkedList(), _appCleans = new LinkedList(),
		_sessInits = new LinkedList(), _sessCleans = new LinkedList(),
		_dtInits = new LinkedList(), _dtCleans = new LinkedList(),
		_execInits = new LinkedList(), _execCleans = new LinkedList();
	private final Map _prefs  = Collections.synchronizedMap(new HashMap());
	/** List(ErrorPage). */
	private final List _errpgs = new LinkedList();
	private Monitor _monitor;
	private String _timeoutUri;
	private String _themeUri;
	private Class _uiengcls, _dcpcls, _uiftycls, _tzpcls, _lpcls;
	private Integer _dtTimeout, _dtMax, _sessTimeout, _evtThdMax;
	private Integer _maxUploadSize = new Integer(5120);
	private String _charset = "UTF-8";

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

		if (WebAppInit.class.isAssignableFrom(klass)) {
			synchronized (_appInits) {
				_appInits.add(klass);
			}
		}
		if (WebAppCleanup.class.isAssignableFrom(klass)) {
			synchronized (_appCleans) {
				_appCleans.add(klass);
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

		if (ExecutionInit.class.isAssignableFrom(klass)) {
			synchronized (_execInits) {
				_execInits.add(klass);
			}
		}
		if (ExecutionCleanup.class.isAssignableFrom(klass)) {
			synchronized (_execCleans) {
				_execCleans.add(klass);
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

		synchronized (_appInits) {
			_appInits.remove(klass);
		}
		synchronized (_appCleans) {
			_appCleans.remove(klass);
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
		synchronized (_execInits) {
			_execInits.remove(klass);
		}
		synchronized (_execCleans) {
			_execCleans.remove(klass);
		}
	}

	/** Contructs a list of {@link EventThreadInit} instances and invokes
	 * {@link EventThreadInit#prepare} for
	 * each relevant listener registered by {@link #addListener}.
	 *
	 * <p>It is called by UiEngine before starting an event processing
	 * thread.
	 *
	 * @exception UiException to prevent a thread from being processed
	 * if {@link EventThreadInit#prepare} throws an exception
	 * @return a list of {@link EventThreadInit} instances that are
	 * constructed in this method (and their {@link EventThreadInit#prepare}
	 * are called successfully).
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
	 * @param inits a list of {@link EventThreadInit} instances returned from
	 * {@link #newEventThreadInits}, or null if no instance at all.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @exception UiException to prevent a thread from being processed
	 * if {@link EventThreadInit#prepare} throws an exception
	 */
	public void invokeEventThreadInits(List inits, Component comp, Event evt) 
	throws UiException {
		if (inits == null || inits.isEmpty()) return;

		for (Iterator it = inits.iterator(); it.hasNext();) {
			final EventThreadInit fn = (EventThreadInit)it.next();
			try {
				fn.init(comp, evt);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the event being processed
			}
		}
	}
	/** Invokes {@link EventThreadCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link EventThreadCleanup} is constructed first,
	 * and then invoke {@link EventThreadCleanup#cleanup}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param ex the exception being thrown (and not handled) during
	 * the processing of the event, or null it is executed successfully.
	 * @param errs used to hold the exceptions that are thrown by
	 * {@link EventThreadCleanup#cleanup}.
	 * If null, all exceptions are ignored (but logged)
	 * @return a list of {@link EventThreadCleanup} instances that is
	 * constructed in this method (and their {@link EventThreadCleanup#cleanup}
	 * are called successfully -- without throwing any exception).
	 */
	public List newEventThreadCleanups(Component comp, Event evt,
	Throwable ex, List errs) {
		if (_evtCleans.isEmpty()) return Collections.EMPTY_LIST;
			//it is OK to test LinkedList.isEmpty without synchronized

		final List cleanups = new LinkedList();
		synchronized (_evtCleans) {
			for (Iterator it = _evtCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					final EventThreadCleanup cleanup =
						(EventThreadCleanup)klass.newInstance();
					cleanup.cleanup(comp, evt, ex);
					cleanups.add(cleanup);
				} catch (Throwable t) {
					if (errs != null) errs.add(t);
					log.error("Failed to invoke "+klass, t);
				}
			}
		}
		return cleanups;
	}
	/** Invoke {@link EventThreadCleanup#complete} for each instance returned by
	 * {@link #newEventThreadCleanups}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param cleanups a list of {@link EventThreadCleanup} instances returned from
	 * {@link #newEventThreadCleanups}, or null if no instance at all.
	 * @param errs used to hold the exceptions that are thrown by
	 * {@link EventThreadCleanup#complete}.
	 * If null, all exceptions are ignored (but logged).
	 */
	public void invokeEventThreadCompletes(List cleanups, Component comp, Event evt,
	List errs) {
		if (cleanups == null || cleanups.isEmpty()) return;

		for (Iterator it = cleanups.iterator(); it.hasNext();) {
			final EventThreadCleanup fn = (EventThreadCleanup)it.next();
			try {
				fn.complete(comp, evt);
			} catch (Throwable ex) {
				if (errs != null) errs.add(ex);
				log.error("Failed to invoke "+fn, ex);
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
	 * @param obj which object that {@link Executions#wait}
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

	/** Contructs a list of {@link EventThreadResume} instances and invokes
	 * {@link EventThreadResume#beforeResume} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>It is called by UiEngine when resuming a suspended event thread.
	 * Notice: it executes in the main thread (i.e., the servlet thread).
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @exception UiException to prevent a thread from being resumed
	 * if {@link EventThreadResume#beforeResume} throws an exception
	 * @return a list of {@link EventThreadResume} instances that are constructed
	 * in this method (and their {@link EventThreadResume#beforeResume}
	 * are called successfully).
	 */
	public List newEventThreadResumes(Component comp, Event evt)
	throws UiException {
		if (_evtResus.isEmpty()) return Collections.EMPTY_LIST;
			//it is OK to test LinkedList.isEmpty without synchronized

		final List resumes = new LinkedList();
		synchronized (_evtResus) {
			for (Iterator it = _evtResus.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					final EventThreadResume resume =
						(EventThreadResume)klass.newInstance();
					resume.beforeResume(comp, evt);
					resumes.add(resume);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the event being resumed
				}
			}
		}
		return resumes;
	}
	/** Invokes {@link EventThreadResume#afterResume} for each instance returned
	 * by {@link #newEventThreadResumes}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
s	 * @param resumes a list of {@link EventThreadResume} instances returned from
	 * {@link #newEventThreadResumes}, or null if no instance at all.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param errs used to hold the exceptions that are thrown by
	 * {@link EventThreadResume#afterResume}.
	 * If null, all exceptions are ignored (but logged)
	 */
	public void invokeEventThreadResumes(List resumes, Component comp, Event evt,
	List errs) {
		if (resumes == null || resumes.isEmpty()) return;

		for (Iterator it = resumes.iterator(); it.hasNext();) {
			final EventThreadResume fn = (EventThreadResume)it.next();
			try {
				fn.afterResume(comp, evt);
			} catch (Throwable ex) {
				if (errs != null) errs.add(ex);
				log.error("Failed to invoke "+fn+" after resumed", ex);
			}
		}
	}
	/** Invokes {@link EventThreadResume#abortResume} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link EventThreadResume} is constructed first,
	 * and then invoke {@link EventThreadResume#abortResume}.
	 *
	 * <p>It never throws an exception but logging.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 */
	public void invokeEventThreadResumeAborts(Component comp, Event evt) {
		if (_evtResus.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_evtResus) {
			for (Iterator it = _evtResus.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((EventThreadResume)klass.newInstance())
						.abortResume(comp, evt);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass+" for aborting", ex);
				}
			}
		}
	}

	/** Invokes {@link WebAppInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link WebAppInit} is constructed first,
	 * and then invoke {@link WebAppInit#init}.
	 *
	 * <p>Unlike {@link #invokeWebAppInits}, it doesn't throw any exceptions.
	 * Rather, it only logs them.
	 *
	 * @param wapp the Web application that is created
	 */
	public void invokeWebAppInits(WebApp wapp)
	throws UiException {
		if (_appInits.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_appInits) {
			for (Iterator it = _appInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((WebAppInit)klass.newInstance()).init(wapp);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}
	/** Invokes {@link WebAppCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link WebAppCleanup} is constructed first,
	 * and then invoke {@link WebAppCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param wapp the Web application that is being destroyed
	 */
	public void invokeWebAppCleanups(WebApp wapp) {
		if (_appCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_appCleans) {
			for (Iterator it = _appCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((WebAppCleanup)klass.newInstance()).cleanup(wapp);
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

	/** Invokes {@link ExecutionInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link ExecutionInit} is constructed first,
	 * and then invoke {@link ExecutionInit#init}.
	 *
	 * @param exec the execution that is created
	 * @param parent the previous execution, or null if no previous at all
	 * @exception UiException to prevent an execution from being created
	 */
	public void invokeExecutionInits(Execution exec, Execution parent)
	throws UiException {
		if (_execInits.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_execInits) {
			for (Iterator it = _execInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((ExecutionInit)klass.newInstance()).init(exec, parent);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
					//Don't intercept; to prevent the creation of a session
				}
			}
		}
	}
	/** Invokes {@link ExecutionCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link ExecutionCleanup} is constructed first,
	 * and then invoke {@link ExecutionCleanup#cleanup}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param exec the execution that is being destroyed
	 * @param parent the previous execution, or null if no previous at all
	 * @param ex the first exception being thrown (and not handled) during the
	 * execution, or null it is executed successfully.
	 * @param errs used to hold the exceptions that are thrown by
	 * {@link ExecutionCleanup#cleanup}.
	 * If null, all exceptions are ignored (but logged)
	 */
	public void invokeExecutionCleanups(Execution exec, Execution parent,
	Throwable ex, List errs) {
		if (_execCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_execCleans) {
			for (Iterator it = _execCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((ExecutionCleanup)klass.newInstance()).cleanup(exec, parent, ex);
				} catch (Throwable t) {
					if (errs != null) errs.add(t);
					log.error("Failed to invoke "+klass, t);
				}
			}
		}
	}

	/** Sets the URI of CSS that will be generated for each ZUML desktop.
	 */
	public void setThemeURI(String uri) {
		_themeUri = uri;
	}
	/** Returns the URI of CSS that will be generated for each ZUML desktop.
	 * <p>Default: null
	 */
	public String getThemeURI() {
		return _themeUri;
	}

	/** Sets the URI that is used when the session timeout or
	 * desktop is no longer available.
	 *
	 * @param uri the URI used if timeout, or null to show an error message
	 * at the client only. If empty, it works as reloading the same URI again.
	 */
	public void setTimeoutURI(String uri) {
		_timeoutUri = uri;
	}
	/** Sets the URI that is used when the session timeout or
	 * desktop is no longer available, or null.
	 *
	 * <p>Default: null.
	 *
	 * <p>If null is returned, an message is shown up at the client.
	 * If empty, it works as reloading the same URI again.
	 * If non null, the browser will be redirected to the returned URI.
	 */
	public String getTimeoutURI() {
		return _timeoutUri;
	}

	/** Sets the class for implementing {@link LocaleProvider}, or null to
	 * use the default.
	 */
	public void setLocaleProviderClass(Class cls) {
		if (cls != null && !LocaleProvider.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("LocaleProvider not implemented: "+cls);
		_lpcls = cls;
	}
	/** Returns the class for implementing {@link LocaleProvider}, or null for default.
	 */
	public Class getLocaleProviderClass() {
		return _lpcls;
	}
	/** Sets the class for implementing {@link TimeZoneProvider}, or null to
	 * use the default.
	 */
	public void setTimeZoneProviderClass(Class cls) {
		if (cls != null && !TimeZoneProvider.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("TimeZoneProvider not implemented: "+cls);
		_tzpcls = cls;
	}
	/** Returns the class for implementing {@link TimeZoneProvider}, or null for default.
	 */
	public Class getTimeZoneProviderClass() {
		return _tzpcls;
	}

	/** Sets the class for implementing {@link UiEngine}, or null to
	 * use the default.
	 */
	public void setUiEngineClass(Class cls) {
		if (cls != null && !UiEngine.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("UiEngine not implemented: "+cls);
		_uiengcls = cls;
	}
	/** Returns the class for implementing {@link UiEngine}, or null for default.
	 */
	public Class getUiEngineClass() {
		return _uiengcls;
	}

	/** Sets the class for implementing {@link DesktopCacheProvider}, or null to
	 * use the default.
	 */
	public void setDesktopCacheProviderClass(Class cls) {
		if (cls != null && !DesktopCacheProvider.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("DesktopCacheProvider not implemented: "+cls);
		_dcpcls = cls;
	}
	/** Returns the class for implementing the UI engine, or null for default.
	 */
	public Class getDesktopCacheProviderClass() {
		return _dcpcls;
	}

	/** Sets the class for implementing {@link UiFactory}, or null to
	 * use the default.
	 */
	public void setUiFactoryClass(Class cls) {
		if (cls != null && !UiFactory.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("UiFactory not implemented: "+cls);
		_uiftycls = cls;
	}
	/** Returns the class for implementing the UI engine, or null for default.
	 */
	public Class getUiFactoryClass() {
		return _uiftycls;
	}

	/** Specifies the maximal allowed upload size, in kilobytes.
	 * <p>Default: 5120.
	 * @param sz the maximal allowed upload size. If null, there is no
	 * limitation.
	 */
	public void setMaxUploadSize(Integer sz) {
		_maxUploadSize = sz;
	}
	/** Returns the maximal allowed upload size, in kilobytes, or null
	 * if no limiatation.
	 */
	public Integer getMaxUploadSize() {
		return _maxUploadSize;
	}

	/** Specifies the time, in seconds, between client requests
	 * before ZK will invalidate the desktop, or null for default (1 hour).
	 */
	public void setDesktopMaxInactiveInterval(Integer secs) {
		_dtTimeout = secs;
	}
	/** Returns the time, in seconds, between client requests
	 * before ZK will invalidate the desktop, or null for default.
	 */
	public Integer getDesktopMaxInactiveInterval() {
		return _dtTimeout;
	}

	/**  Specifies the time, in seconds, between client requests
	 * before ZK will invalidate the session, or null for default.
	 */
	public void setSessionMaxInactiveInterval(Integer secs) {
		_sessTimeout = secs;
	}
	/** Returns the time, in seconds, between client requests
	 * before ZK will invalidate the session, or null for default.
	 */
	public Integer getSessionMaxInactiveInterval() {
		return _sessTimeout;
	}

	/** Specifies the maximal allowed number of desktop
	 * per session, or null for default (10).
	 */
	public void setMaxDesktops(Integer max) {
		_dtMax = max;
	}
	/** Returns the maximal allowed number of desktop
	 * per session, or null for default (10).
	 */
	public Integer getMaxDesktops() {
		return _dtMax;
	}

	/** Specifies the maximal allowed number of event processing threads
	 * per Web application, or null for default (100).
	 */
	public void setMaxEventThreads(Integer max) {
		_evtThdMax = max;
	}
	/** Returns the maximal allowed number of event processing threads
	 * per Web application, or null for default (100).
	 */
	public Integer getMaxEventThreads() {
		return _evtThdMax;
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

	/** Returns the charset used by {@link org.zkoss.zk.ui.http.DHtmlLayoutServlet},
	 * or null to use the container's default.
	 * <p>Default: UTF-8.
	 */
	public String getCharset() {
		return _charset;
	}
	/** Sets the charset used by {@link org.zkoss.zk.ui.http.DHtmlLayoutServlet}.
	 *
	 * @param charset the charset to use. If null or empty, the container's default
	 * is used.
	 */
	public void setCharset(String charset) {
		_charset = charset != null && charset.length() > 0 ? charset: null;
	}

	/** Returns the value of the preference defined in zk.xml, or by
	 * {@link #setPreference}.
	 *
	 * <p>Preference is application specific. You can specify whatever you want
	 * as you specifying context-param for a Web application.
	 *
	 * @param defaultValue the default value that is used if the specified
	 * preference is not found.
	 */
	public String getPreference(String name, String defaultValue) {
		final String value = (String)_prefs.get(name);
		return value != null ? value: defaultValue;
	}
	/** Sets the value of the preference.
	 */
	public void setPreference(String name, String value) {
		if (name == null || value == null)
			throw new IllegalArgumentException("null");
		_prefs.put(name, value);
	}
	/** Returns a set of all preference names.
	 */
	public Set getPreferenceNames() {
		return _prefs.keySet();
	}

	/** Adds an error page.
	 *
	 * @param type what type of errors the error page is associated with.
	 * @param location where is the error page.
	 */
	public void addErrorPage(Class type, String location) {
		if (!Throwable.class.isAssignableFrom(type))
			throw new IllegalArgumentException("Throwable or derived is required: "+type);
		if (location == null)
			throw new IllegalArgumentException("location required");
		synchronized (_errpgs) {
			_errpgs.add(new ErrorPage(type, location));
		}
	}
	/** Returns the error page that matches the specified error, or null if not found.
	 */
	public String getErrorPage(Throwable error) {
		if (!_errpgs.isEmpty()) {
			synchronized (_errpgs) {
				for (Iterator it = _errpgs.iterator(); it.hasNext();) {
					final ErrorPage errpg = (ErrorPage)it.next();
					if (errpg.type.isInstance(error))
						return errpg.location;
				}
			}
		}
		return null;
	}
	private static class ErrorPage {
		private final Class type;
		private final String location;
		private ErrorPage(Class type, String location) {
			this.type = type;
			this.location = location;
		}
	};
}
