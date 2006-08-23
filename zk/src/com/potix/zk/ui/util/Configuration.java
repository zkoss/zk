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
import com.potix.zk.ui.WebApp;
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
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.sys.DesktopCacheProvider;
import com.potix.zk.ui.sys.LocaleProvider;
import com.potix.zk.ui.sys.TimeZoneProvider;
import com.potix.zk.ui.sys.UiFactory;

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
		_appInits = new LinkedList(), _appCleans = new LinkedList(),
		_sessInits = new LinkedList(), _sessCleans = new LinkedList(),
		_dtInits = new LinkedList(), _dtCleans = new LinkedList();
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

		if (ApplicationInit.class.isAssignableFrom(klass)) {
			synchronized (_appInits) {
				_appInits.add(klass);
			}
		}
		if (ApplicationCleanup.class.isAssignableFrom(klass)) {
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

	/** Invokes {@link ApplicationInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link ApplicationInit} is constructed first,
	 * and then invoke {@link ApplicationInit#init}.
	 *
	 * <p>Unlike {@link #invokeApplicationInits}, it doesn't throw any exceptions.
	 * Rather, it only logs them.
	 *
	 * @param wapp the Web application that is created
	 */
	public void invokeApplicationInits(WebApp wapp)
	throws UiException {
		if (_appInits.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_appInits) {
			for (Iterator it = _appInits.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((ApplicationInit)klass.newInstance()).init(wapp);
				} catch (Throwable ex) {
					log.error("Failed to invoke "+klass, ex);
				}
			}
		}
	}
	/** Invokes {@link ApplicationCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>An instance of {@link ApplicationCleanup} is constructed first,
	 * and then invoke {@link ApplicationCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param wapp the Web application that is being destroyed
	 */
	public void invokeApplicationCleanups(WebApp wapp) {
		if (_appCleans.isEmpty()) return;
			//it is OK to test LinkedList.isEmpty without synchronized

		synchronized (_appCleans) {
			for (Iterator it = _appCleans.iterator(); it.hasNext();) {
				final Class klass = (Class)it.next();
				try {
					((ApplicationCleanup)klass.newInstance()).cleanup(wapp);
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

	/** Returns the charset used by {@link com.potix.zk.ui.http.DHtmlLayoutServlet},
	 * or null to use the container's default.
	 * <p>Default: UTF-8.
	 */
	public String getCharset() {
		return _charset;
	}
	/** Sets the charset used by {@link com.potix.zk.ui.http.DHtmlLayoutServlet}.
	 *
	 * @param charset the charset to use. If null or empty, the container's default
	 * is used.
	 */
	public void setCharset(String charset) {
		_charset = charset != null && charset.length() > 0 ? charset: null;
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
