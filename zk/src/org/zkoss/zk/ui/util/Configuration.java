/* Configuration.java

	Purpose:
		
	Description:
		
	History:
		Sun Mar 26 16:06:56     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.util.FastReadArray;
import org.zkoss.util.WaitLock;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadInit;
import org.zkoss.zk.ui.event.EventThreadResume;
import org.zkoss.zk.ui.event.EventThreadSuspend;
import org.zkoss.zk.ui.impl.EventInterceptors;
import org.zkoss.zk.ui.impl.MultiComposer;
import org.zkoss.zk.ui.impl.RichletConfigImpl;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.PropertiesRenderer;
import org.zkoss.zk.ui.sys.SEORenderer;
import org.zkoss.zk.ui.sys.SessionCache;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.WebAppFactory;

/**
 * The ZK configuration.
 *
 * <p>To retrieve the current configuration, use
 * {@link org.zkoss.zk.ui.WebApp#getConfiguration}.
 *
 * <p>Note: A {@link Configuration} instance can be assigned to at most one
 * {@link WebApp} instance.
 *
 * @author tomyeh
 */
public class Configuration {
	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	private static final String PROP_EXPRESS_FACTORY
		= "org.zkoss.xel.ExpressionFactory.class";

	private WebApp _wapp;
	/** List of classes. */
	private final FastReadArray<Class<?>>
		_evtInits = new FastReadArray<Class<?>>(Class.class),
		_evtCleans = new FastReadArray<Class<?>>(Class.class),
		_evtSusps = new FastReadArray<Class<?>>(Class.class),
		_evtResus = new FastReadArray<Class<?>>(Class.class),
		_appInits = new FastReadArray<Class<?>>(Class.class),
		_appCleans = new FastReadArray<Class<?>>(Class.class),
		_sessInits = new FastReadArray<Class<?>>(Class.class),
		_sessCleans = new FastReadArray<Class<?>>(Class.class),
		_dtInits = new FastReadArray<Class<?>>(Class.class),
		_dtCleans = new FastReadArray<Class<?>>(Class.class),
		_execInits = new FastReadArray<Class<?>>(Class.class),
		_execCleans = new FastReadArray<Class<?>>(Class.class),
		_composers = new FastReadArray<Class<?>>(Class.class),
		_initiators = new FastReadArray<Class<?>>(Class.class),
		_seoRends = new FastReadArray<Class<?>>(Class.class),
		_resolvers = new FastReadArray<Class<?>>(Class.class);
		//since it is called frequently, we use array to avoid synchronization
	/** List of objects. */
	private final FastReadArray<URIInterceptor>
		_uriIntcps = new FastReadArray<URIInterceptor>(URIInterceptor.class);
	private final FastReadArray<RequestInterceptor>
		_reqIntcps = new FastReadArray<RequestInterceptor>(RequestInterceptor.class);
	private final FastReadArray<UiLifeCycle>
		_uiCycles = new FastReadArray<UiLifeCycle>(UiLifeCycle.class);
	private final FastReadArray<PropertiesRenderer>
		_propRends = new FastReadArray<PropertiesRenderer>(PropertiesRenderer.class);
	private final FastReadArray<String>
		_labellocs = new FastReadArray<String>(String.class);
	private final Map<String, String> _prefs  = Collections.synchronizedMap(new HashMap<String,String>());
	/** Map(String name, [Class richlet, Map params] or Richilet richlet). */
	private final Map<String, Object> _richlets = new HashMap<String, Object>();
	/** Map(String path, [String name, boolean wildcard]). */
	private final Map<String, Object[]> _richletmaps = new HashMap<String, Object[]>();
	/** Map(String deviceType, List(ErrorPage)). */
	private final Map<String, List<ErrorPage>> _errpgs = new HashMap<String, List<ErrorPage>>(4);
	/** Map(String deviceType+connType, Map(errorCode, uri)) */
	private final Map<String, Map<Integer,String>> _errURIs = new HashMap<String, Map<Integer,String>>();
	/** Map(String deviceType, TimeoutURIInfo ti) */
	private final Map<String, TimeoutURIInfo> _timeoutURIs = Collections.synchronizedMap(new HashMap<String, TimeoutURIInfo>());
	private Monitor _monitor;
	private PerformanceMeter _pfmeter;
	private ExecutionMonitor _execmon;
	private DesktopRecycle _dtRecycle;
	private final FastReadArray<String> _themeURIs = new FastReadArray<String>(String.class);
	private ThemeProvider _themeProvider;
	/** A set of disabled theme URIs. */
	private Set<String> _disThemeURIs;
	/** A list of client packages. */
	private final FastReadArray<String> _clientpkgs = new FastReadArray<String>(String.class);
	private Class<?> _wappcls, _wappftycls, _uiengcls, _dcpcls, _uiftycls,
		_failmancls, _idgencls, _sesscachecls, _audeccls;
	private int _dtTimeout = 3600, _sessDktMax = 15, _sessReqMax = 5,
		_sessPushMax = -1,
		_sessTimeout = 0, _sparThdMax = 100, _suspThdMax = -1,
		_maxUploadSize = 5120, _fileSizeThreshold, _maxProcTime = 3000,
		_promptDelay = 900, _tooltipDelay = 800, _autoResendTimeout = 200;
	private String _charsetResp = "UTF-8", _charsetUpload = "UTF-8";
	private CharsetFinder _charsetFinderUpload;
	/** The event interceptors. */
	private final EventInterceptors _eis = new EventInterceptors();
	private int _evtTimeWarn = 600; //sec
	/** A map of attributes. */
	private final Map<String, Object> _attrs = Collections.synchronizedMap(new HashMap<String, Object>());
	/** whether to use the event processing thread. */
	private boolean _evtThdEnabled; //disabled by default since ZK 5
	/** whether zscript is enabled. */
	private boolean _zscriptEnabled = true;
	/** keep-across-visits. */
	private boolean _keepDesktop;
	/** Whether to keep the session alive when receiving onTimer.
	 */
	private boolean _timerKeepAlive;
	/** Whether to debug JavaScript. */
	private boolean _debugJS;
	/** Whether the ZK application is crawlable. */
	private boolean _crawlable;
	/** Whether to use the same UUID sequence. */
	private boolean _repeatUuid;
	// ZK-1671: ThemeProvider defined in metainfo/zk/zk.xml from jar file doesn't work
	//		Depends on the jar file loading order, user-defined theme provider may be 
	//		overridden by StandardThemeProvider
	private boolean _customThemeProvider = false;
	private boolean _customThemeRegistry = false;
	private boolean _customThemeResolver = false;

	/** Constructor.
	 */
	public Configuration() {
	}

	/** Returns the Web application that this configuration belongs to,
	 * or null if it is not associated yet.
	 */
	public WebApp getWebApp() {
		return _wapp;
	}
	/** Associates it with a web application.
	 */
	public void setWebApp(WebApp wapp) {
		_wapp = wapp;
	}

	/** Adds a listener class.
	 *
	 * <p>Notice that there is only one listener allowed for the following classes:
	 * {@link Monitor}, {@link PerformanceMeter}, and {@link DesktopRecycle}.
	 * On the other hand, any number listeners are allowed for other classes.
	 *
	 * <p>Notice that if the listener implements {@link Composer}, it can also
	 * implement {@link org.zkoss.zk.ui.util.ComposerExt} and/or {@link org.zkoss.zk.ui.util.FullComposer} to have
	 * more detailed control. However, ComposerExt and FullComposer are meaningless
	 * to richlets. In additions, an independent
	 * composer is instantiated for each page so there is synchronization required.
	 *
	 * <p>By default, a listener is instantiated when required, and dropped
	 * after invoked. In other words, a new instance will be instantiated in
	 * the next invocation. It means you don't have to worry the threading,
	 * <p>However, for better performance, the following listeners will be instantiated
	 * in {@link #addListener}, and then used repeatedly. It means it has
	 * to be thread safe. These listeners include
	 * {@link URIInterceptor}, {@link RequestInterceptor},
	 * {@link EventInterceptor}, {@link UiLifeCycle},
	 * and {@link PropertiesRenderer}.
	 *
	 * @param klass the listener class must implement at least one of
	 * {@link Monitor}, {@link PerformanceMeter}, {@link EventThreadInit},
	 * {@link EventThreadCleanup}, {@link EventThreadSuspend},
	 * {@link EventThreadResume}, {@link WebAppInit}, {@link WebAppCleanup},
	 * {@link SessionInit}, {@link SessionCleanup}, {@link DesktopInit},
	 * {@link DesktopCleanup}, {@link ExecutionInit}, {@link ExecutionCleanup},
	 * {@link Composer}, {@link Initiator} (since 5.0.7), {@link SEORenderer} (since 5.0.7),
	 * {@link PropertiesRenderer} (since 5.0.7),
	 * {@link VariableResolver},
	 * {@link URIInterceptor}, {@link RequestInterceptor},
	 * {@link UiLifeCycle}, {@link DesktopRecycle},
	 * and/or {@link EventInterceptor} interfaces.
	 * @see Desktop#addListener
	 */
	public void addListener(Class<?> klass) throws Exception {
		boolean added = false;
		Object listener = null;

		if (Monitor.class.isAssignableFrom(klass)) {
			if (_monitor != null)
				throw new UiException(onlyOnce(Monitor.class));
			_monitor = (Monitor)(listener = getInstance(klass, listener));
			added = true;
		}
		if (PerformanceMeter.class.isAssignableFrom(klass)) {
			if (_pfmeter != null)
				throw new UiException(onlyOnce(PerformanceMeter.class));
			_pfmeter = (PerformanceMeter)(listener = getInstance(klass, listener));
			added = true;
		}
		if (ExecutionMonitor.class.isAssignableFrom(klass)) {
			if (_execmon != null)
				throw new UiException(onlyOnce(ExecutionMonitor.class));
			_execmon = (ExecutionMonitor)(listener = getInstance(klass, listener));
			added = true;
		}
		if (DesktopRecycle.class.isAssignableFrom(klass)) {
			if (_dtRecycle != null)
				throw new UiException(onlyOnce(DesktopRecycle.class));
			_dtRecycle = (DesktopRecycle)(listener = getInstance(klass, listener));
			added = true;
		}

		if (EventThreadInit.class.isAssignableFrom(klass)) {
			_evtInits.add(klass);
			added = true;
		}
		if (EventThreadCleanup.class.isAssignableFrom(klass)) {
			_evtCleans.add(klass);
			added = true;
		}
		if (EventThreadSuspend.class.isAssignableFrom(klass)) {
			_evtSusps.add(klass);
			added = true;
		}
		if (EventThreadResume.class.isAssignableFrom(klass)) {
			_evtResus.add(klass);
			added = true;
		}

		if (WebAppInit.class.isAssignableFrom(klass)) {
			_appInits.add(klass);
			added = true;
		}
		if (WebAppCleanup.class.isAssignableFrom(klass)) {
			_appCleans.add(klass);
			added = true;
		}

		if (SessionInit.class.isAssignableFrom(klass)) {
			_sessInits.add(klass);
			added = true;
		}
		if (SessionCleanup.class.isAssignableFrom(klass)) {
			_sessCleans.add(klass);
			added = true;
		}

		if (DesktopInit.class.isAssignableFrom(klass)) {
			_dtInits.add(klass);
			added = true;
		}
		if (DesktopCleanup.class.isAssignableFrom(klass)) {
			_dtCleans.add(klass);
			added = true;
		}

		if (ExecutionInit.class.isAssignableFrom(klass)) {
			_execInits.add(klass);
			added = true;
		}
		if (ExecutionCleanup.class.isAssignableFrom(klass)) {
			_execCleans.add(klass);
			added = true;
		}
		if (Composer.class.isAssignableFrom(klass)) {
			_composers.add(klass); //not instance
			added = true;
		}
		if (Initiator.class.isAssignableFrom(klass)) {
			_initiators.add(klass); //not instance
			added = true;
		}
		if (SEORenderer.class.isAssignableFrom(klass)) {
			_seoRends.add(klass);
			added = true;
		}
		if (VariableResolver.class.isAssignableFrom(klass)) {
			_resolvers.add(klass); //not instance
			added = true;
		}

		//for better performance, the following listeners are instantiated
		//here and shared in the whole application

		if (URIInterceptor.class.isAssignableFrom(klass)) {
			try {
				_uriIntcps.add((URIInterceptor)
					(listener = getInstance(klass, listener)));
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+klass, ex);
			}
			added = true;
		}
		if (RequestInterceptor.class.isAssignableFrom(klass)) {
			try {
				_reqIntcps.add((RequestInterceptor)
					(listener = getInstance(klass, listener)));
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+klass, ex);
			}
			added = true;
		}
		if (EventInterceptor.class.isAssignableFrom(klass)) {
			try {
				_eis.addEventInterceptor((EventInterceptor)
					(listener = getInstance(klass, listener)));
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+klass, ex);
			}
			added = true;
		}
		if (UiLifeCycle.class.isAssignableFrom(klass)) {
			try {
				_uiCycles.add((UiLifeCycle)
					(listener = getInstance(klass, listener)));
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+klass, ex);
			}
			added = true;
		}
		if (PropertiesRenderer.class.isAssignableFrom(klass)) {
			try {
				_propRends.add((PropertiesRenderer)
					(listener = getInstance(klass, listener)));
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+klass, ex);
			}
			added = true;
		}

		if (!added)
			throw new UiException("Unknown listener: "+klass);
	}
	private static final String onlyOnce(Class<?> cls) {
		return cls + " can be assigned only once";
	}
	private static Object getInstance(Class<?> klass, Object listener)
	throws Exception {
		return listener != null ? listener: klass.newInstance();
	}
	/** Removes a listener class.
	 * @see Desktop#removeListener
	 */
	public void removeListener(final Class<?> klass) {
		if (_monitor != null && _monitor.getClass().equals(klass))
			_monitor = null;
		if (_pfmeter != null && _pfmeter.getClass().equals(klass))
			_pfmeter = null;
		if (_execmon != null && _execmon.getClass().equals(klass))
			_execmon = null;
		if (_dtRecycle != null && _dtRecycle.getClass().equals(klass))
			_dtRecycle = null;

		_evtInits.remove(klass);
		_evtCleans.remove(klass);
		_evtSusps.remove(klass);
		_evtResus.remove(klass);

		_appInits.remove(klass);
		_appCleans.remove(klass);

		_sessInits.remove(klass);
		_sessCleans.remove(klass);

		_dtInits.remove(klass);
		_dtCleans.remove(klass);

		_execInits.remove(klass);
		_execCleans.remove(klass);

		_composers.remove(klass);
		_initiators.remove(klass);
		_seoRends.remove(klass);
		_resolvers.remove(klass);

		final SameClass sc = new SameClass(klass);
		_uriIntcps.removeBy(sc, true);
		_reqIntcps.removeBy(sc, true);
		_uiCycles.removeBy(sc, true);
		_propRends.removeBy(sc, true);

		_eis.removeEventInterceptor(klass);
	}

	/** Constructs a list of {@link EventThreadInit} instances and invokes
	 * {@link EventThreadInit#prepare} for
	 * each relevant listener registered by {@link #addListener}.
	 *
	 * <p>Used only internally (by {@link UiEngine} before starting an event
	 * processing thread).
	 *
	 * @exception UiException to prevent a thread from being processed
	 * if {@link EventThreadInit#prepare} throws an exception
	 * @return a list of {@link EventThreadInit} instances that are
	 * constructed in this method (and their {@link EventThreadInit#prepare}
	 * are called successfully), or null.
	 */
	public List<EventThreadInit> newEventThreadInits(Component comp, Event evt)
	throws UiException {
		final Class<?>[] ary = _evtInits.toArray();
		if (ary.length == 0) return null;

		final List<EventThreadInit> inits = new LinkedList<EventThreadInit>();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
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
		return inits;
	}
	/** Invokes {@link EventThreadInit#init} for each instance returned
	 * by {@link #newEventThreadInits}.
	 *
 	 * <p>Used only internally.
	 *
	 * @param inits a list of {@link EventThreadInit} instances returned from
	 * {@link #newEventThreadInits}, or null if no instance at all.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @exception UiException to prevent a thread from being processed
	 * if {@link EventThreadInit#init} throws an exception
	 * @return false if you want to ignore the event, i.e., not to proceed
	 * any event processing for the specified event (evt).
	 */
	public boolean invokeEventThreadInits(List<EventThreadInit> inits, Component comp, Event evt) 
	throws UiException {
		if (inits == null || inits.isEmpty()) return true; //not to ignore

		for (EventThreadInit fn: inits) {
			try {
				if (!fn.init(comp, evt))
					return false; //ignore the event
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the event being processed
			}
		}
		return true;
	}
	/** Invokes {@link EventThreadCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link EventThreadCleanup} is constructed first,
	 * and then invoke {@link EventThreadCleanup#cleanup}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param errs a list of exceptions (java.lang.Throwable) if any exception
	 * occurred before this method is called, or null if no exception at all.
	 * Note: you can manipulate the list directly to add or clean up exceptions.
	 * For example, if exceptions are fixed correctly, you can call errs.clear()
	 * such that no error message will be displayed at the client.
	 * @param silent whether not to log the exception
	 * @return a list of {@link EventThreadCleanup}, or null
	 * @since 3.6.3
	 */
	public List<EventThreadCleanup> newEventThreadCleanups(Component comp, Event evt, List<Throwable> errs, boolean silent) {
		final Class<?>[] ary = _evtCleans.toArray();
		if (ary.length == 0) return null;

		final List<EventThreadCleanup> cleanups = new LinkedList<EventThreadCleanup>();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				final EventThreadCleanup cleanup =
					(EventThreadCleanup)klass.newInstance();
				cleanup.cleanup(comp, evt, errs);
				cleanups.add(cleanup);
			} catch (Throwable t) {
				if (errs != null) errs.add(t);
				if (!silent)
					log.error("Failed to invoke "+klass, t);
			}
		}
		return cleanups.isEmpty() ? null: cleanups;
	}

	/** Invoke {@link EventThreadCleanup#complete} for each instance returned by
	 * {@link #newEventThreadCleanups}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param cleanups a list of {@link EventThreadCleanup} instances returned from
	 * {@link #newEventThreadCleanups}, or null if no instance at all.
	 * @param errs used to hold the exceptions that are thrown by
	 * {@link EventThreadCleanup#complete}.
	 * If null, all exceptions are ignored (but logged).
	 * @param silent whether not to log the exception
	 * @since 3.6.3
	 */
	public void invokeEventThreadCompletes(List<EventThreadCleanup> cleanups, Component comp, Event evt,
	List<Throwable> errs, boolean silent) {
		if (cleanups == null || cleanups.isEmpty()) return;

		for (EventThreadCleanup fn: cleanups) {
			try {
				fn.complete(comp, evt);
			} catch (Throwable ex) {
				if (errs != null) errs.add(ex);
				if (!silent)
					log.error("Failed to invoke "+fn, ex);
			}
		}
	}

	/** Constructs a list of {@link EventThreadSuspend} instances and invokes
	 * {@link EventThreadSuspend#beforeSuspend} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
 	 *
	 * <p>Note: caller shall execute in the event processing thread.
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @param obj which object that {@link Executions#wait}
	 * is called with.
	 * @exception UiException to prevent a thread from suspending
	 * @return a list of {@link EventThreadSuspend}, or null
	 */
	public List<EventThreadSuspend> newEventThreadSuspends(Component comp, Event evt, Object obj) {
		final Class<?>[] ary = _evtSusps.toArray();
		if (ary.length == 0) return null;

		final List<EventThreadSuspend> suspends = new LinkedList<EventThreadSuspend>();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				final EventThreadSuspend suspend =
					(EventThreadSuspend)klass.newInstance();
				suspend.beforeSuspend(comp, evt, obj);
				suspends.add(suspend);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the event being suspended
			}
		}
		return suspends;
	}
	/** Invokes {@link EventThreadSuspend#afterSuspend} for each relevant
	 * listener registered by {@link #addListener}.
	 * Unlike {@link #invokeEventThreadSuspends}, caller shall execute in
	 * the main thread (a.k.a., servlet thread).
	 *
 	 * <p>Used only internally.
	 *
	 * <p>Unlike {@link #invokeEventThreadSuspends}, exceptions are logged
	 * and ignored.
	 *
	 * @param suspends a list of {@link EventThreadSuspend} instances returned
	 * from {@link #newEventThreadSuspends}, or null if no instance at all.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 */
	public void invokeEventThreadSuspends(List<EventThreadSuspend> suspends, Component comp, Event evt)
	throws UiException {
		if (suspends == null || suspends.isEmpty()) return;

		for (EventThreadSuspend fn: suspends) {
			try {
				fn.afterSuspend(comp, evt);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+fn+" after suspended", ex);
			}
		}
	}

	/** Constructs a list of {@link EventThreadResume} instances and invokes
	 * {@link EventThreadResume#beforeResume} for each relevant
	 * listener registered by {@link #addListener}.
	 *
	 * <p>Used only internally (by {@link UiEngine} when resuming a suspended event
	 * thread).
	 * Notice: it executes in the main thread (i.e., the servlet thread).
	 *
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * @exception UiException to prevent a thread from being resumed
	 * if {@link EventThreadResume#beforeResume} throws an exception
	 * @return a list of {@link EventThreadResume} instances that are constructed
	 * in this method (and their {@link EventThreadResume#beforeResume}
	 * are called successfully), or null.
	 */
	public List<EventThreadResume> newEventThreadResumes(Component comp, Event evt)
	throws UiException {
		final Class<?>[] ary = _evtResus.toArray();
		if (ary.length == 0) return null;

		final List<EventThreadResume> resumes = new LinkedList<EventThreadResume>();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
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
		return resumes;
	}
	/** Invokes {@link EventThreadResume#afterResume} for each instance returned
	 * by {@link #newEventThreadResumes}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param resumes a list of {@link EventThreadResume} instances returned from
	 * {@link #newEventThreadResumes}, or null if no instance at all.
	 * @param comp the component which the event is targeting
	 * @param evt the event to process
	 * If null, all exceptions are ignored (but logged)
	 */
	public void invokeEventThreadResumes(List<EventThreadResume> resumes, Component comp, Event evt)
	throws UiException {
		if (resumes == null || resumes.isEmpty()) return;

		for (EventThreadResume fn: resumes) {
			try {
				fn.afterResume(comp, evt);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	/** Invokes {@link EventThreadResume#abortResume} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
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
		final Class<?>[] ary = _evtResus.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				((EventThreadResume)klass.newInstance())
					.abortResume(comp, evt);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+klass+" for aborting", ex);
			}
		}
	}

	/** Invokes {@link WebAppInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link WebAppInit} is constructed first,
	 * and then invoke {@link WebAppInit#init}.
	 *
	 * <p>Unlike {@link #invokeWebAppInits}, it doesn't throw any exceptions.
	 * Rather, it only logs them.
	 */
	public void invokeWebAppInits() throws UiException {
		final Class<?>[] ary = _appInits.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				((WebAppInit)klass.newInstance()).init(_wapp);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+klass, ex);
			}
		}
	}
	/** Invokes {@link WebAppCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link WebAppCleanup} is constructed first,
	 * and then invoke {@link WebAppCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 */
	public void invokeWebAppCleanups() {
		final Class<?>[] ary = _appCleans.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				((WebAppCleanup)klass.newInstance()).cleanup(_wapp);
			} catch (NoClassDefFoundError ex) { //Bug 3046360
			} catch (Throwable ex) {
				log.error("Ignored: failed to invoke "+klass, ex);
			}
		}
	}

	/** Invokes {@link SessionInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link SessionInit} is constructed first,
	 * and then invoke {@link SessionInit#init}.
	 *
	 * @param sess the session that is created
	 * @param request the original request. If HTTP, it is
	 * javax.servlet.http.HttlServletRequest.
	 * @exception UiException to prevent a session from being created
	 * @since 3.0.1
	 */
	public void invokeSessionInits(Session sess, Object request)
	throws UiException {
		final Class<?>[] ary = _sessInits.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				final SessionInit fn = (SessionInit)klass.newInstance();
				fn.init(sess, request);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the creation of a session
			}
		}
	}
	/** Invokes {@link SessionCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link SessionCleanup} is constructed first,
	 * and then invoke {@link SessionCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param sess the session that is being destroyed
	 */
	public void invokeSessionCleanups(Session sess) {
		final Class<?>[] ary = _sessCleans.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				((SessionCleanup)klass.newInstance()).cleanup(sess);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+klass, ex);
			}
		}
	}

	/** Invokes {@link DesktopInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link DesktopInit} is constructed first,
	 * and then invoke {@link DesktopInit#init}.
	 *
	 * @param desktop the desktop that is created
	 * @param request the original request. If HTTP, it is
	 * javax.servlet.http.HttlServletRequest.
	 * @exception UiException to prevent a desktop from being created
	 * @since 3.0.1
	 */
	public void invokeDesktopInits(Desktop desktop, Object request)
	throws UiException {
		final Class<?>[] ary = _dtInits.toArray();
		for (int j = 0; j < ary.length; ++j) {
			final Class<?> klass = ary[j];
			try {
				final DesktopInit fn = (DesktopInit)klass.newInstance();
				fn.init(desktop, request);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the creation of a session
			}
		}
	}
	/** Invokes {@link DesktopCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link DesktopCleanup} is constructed first,
	 * and then invoke {@link DesktopCleanup#cleanup}.
	 *
	 * <p>It never throws an exception.
	 *
	 * @param desktop the desktop that is being destroyed
	 */
	public void invokeDesktopCleanups(Desktop desktop) {
		final Class<?>[] ary = _dtCleans.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				((DesktopCleanup)ary[j].newInstance()).cleanup(desktop);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}

	/** Invokes {@link ExecutionInit#init} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
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
		final Class<?>[] ary = _execInits.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				((ExecutionInit)ary[j].newInstance()).init(exec, parent);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
				//Don't intercept; to prevent the creation of a session
			}
		}
	}
	/** Invokes {@link ExecutionCleanup#cleanup} for each relevant
	 * listener registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>An instance of {@link ExecutionCleanup} is constructed first,
	 * and then invoke {@link ExecutionCleanup#cleanup}.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param exec the execution that is being destroyed
	 * @param parent the previous execution, or null if no previous at all
	 * @param errs a list of exceptions (java.lang.Throwable) if any exception
	 * occurred before this method is called, or null if no exception at all.
	 * Note: you can manipulate the list directly to add or clean up exceptions.
	 * For example, if exceptions are fixed correctly, you can call errs.clear()
	 * such that no error message will be displayed at the client.
	 */
	public void invokeExecutionCleanups(Execution exec, Execution parent, List<Throwable> errs) {
		final Class<?>[] ary = _execCleans.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				((ExecutionCleanup)ary[j].newInstance())
					.cleanup(exec, parent, errs);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
				if (errs != null) errs.add(ex);
			}
		}
	}

	/** Invokes {@link URIInterceptor#request} for each relevant listener
	 * registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>If any of them throws an exception, the exception is propagated to
	 * the caller.
	 *
	 * @exception UiException if it is rejected by the interceptor.
	 * Use {@link UiException#getCause} to retrieve the cause.
	 */
	public void invokeURIInterceptors(String uri) {
		URIInterceptor[] ary = _uriIntcps.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].request(uri);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	/** Invokes {@link RequestInterceptor#request} for each relevant listener
	 * registered by {@link #addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>If any of them throws an exception, the exception is propagated to
	 * the caller.
	 *
	 * @exception UiException if it is rejected by the interceptor.
	 * Use {@link UiException#getCause} to retrieve the cause.
	 */
	public void invokeRequestInterceptors(Session sess, Object request,
	Object response) {
		RequestInterceptor[] ary = _reqIntcps.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].request(sess, request, response);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}

	/** Adds the location of a properties file for i18n labels.
	 * <p>Default: none (/WEB-INF/zk-label.properties is assumed).
	 * <p>Notice that this method has no effect after the web server has been
	 * started. Thus, it is suggested to use the label-location element in zk.xml instead.
	 * @since 5.0.7
	 */
	public void addLabelLocation(String location) {
		if (location == null || location.length() == 0)
			throw new IllegalArgumentException();
		_labellocs.add(location);
	}
	/** Returns an array of the locations of properties files registered
	 * by {@link #addLabelLocation}.
	 * @since 5.0.7
	 */
	public String[] getLabelLocations() {
		return _labellocs.toArray();
	}

	/** Returns the system-level composer or null if none is registered.
	 * To register a system-level composer, use {@link #addListener}.
	 * <p>Notice that any number of composers can be registered,
	 * and a single composer is returned to represent them all.
	 * @since 5.0.1
	 */
	public Composer<?> getComposer(Page page) throws Exception {
		return MultiComposer.getComposer(page, _composers.toArray());
	}

	/** Returns a readonly list of the system-level initiators.
	 * It is empty if none is registered.
	 * To register a system-level initiator, use {@link #addListener}.
	 * @since 5.0.7
	 */
	public Initiator[] getInitiators() {
		final Class<?>[] initclses = _initiators.toArray();
		if (initclses.length == 0)
			return new Initiator[0];

		final List<Initiator> inits = new LinkedList<Initiator>();
		for (int j = 0; j < initclses.length; ++j) {
			try {
				inits.add((Initiator)initclses[j].newInstance());
			} catch (Throwable ex) {
				log.error("Failed to instantiate " + initclses[j]);
			}
		}
		return inits.toArray(new Initiator[inits.size()]);
	}
	/** Returns a readonly list of the system-level SEO renderer.
	 * It is empty if none is registered.
	 * To register a system-level SEO renderers, use {@link #addListener}.
	 * <p>Notice that, once registered, an instance is instantiated before
	 * invoking {@link SEORenderer#render}.
	 * @since 5.0.7
	 */
	public SEORenderer[] getSEORenderers() {
		final Class<?>[] sdclses = _seoRends.toArray();
		if (sdclses.length == 0)
			return new SEORenderer[0];

		final List<SEORenderer> sds = new LinkedList<SEORenderer>();
		for (int j = 0; j < sdclses.length; ++j) {
			try {
				sds.add((SEORenderer)sdclses[j].newInstance());
			} catch (Throwable ex) {
				log.error("Failed to instantiate " + sdclses[j]);
			}
		}
		return sds.toArray(new SEORenderer[sds.size()]);
	}
	
	/** Initializes the given page with the variable resolvers registered
	 * by {@link #addListener}.
	 * It must be called before accessing a page (actually in {@link org.zkoss.zk.ui.sys.PageCtrl#preInit}).
	 * @since 5.0.4
	 */
	public void init(Page page) {
		final Class<?>[] classes = _resolvers.toArray();
		for (int j = 0; j < classes.length; ++j) {
			try {
				page.addVariableResolver((VariableResolver)classes[j].newInstance());
			} catch (Throwable ex) {
				log.error("Failed to instantiate "+classes[j], ex);
			}
		}
	}

	/** Returns a readonly list of the system-level properties renders.
	 * It is empty if none is registered.
	 * To register a system-level properties renders, use {@link #addListener}.
	 * <p>Notice that, once registered, it is instantiated immediately,
	 * and the same instance is shared for rendering the properties of every component.
	 * @since 5.0.7
	 */
	public PropertiesRenderer[] getPropertiesRenderers() {
		return _propRends.toArray();
	}
	/** Invokes {@link UiLifeCycle#afterComponentAttached}
	 * when a component is attached to a page.
	 * @since 3.0.6
	 */
	public void afterComponentAttached(Component comp, Page page) {
		final UiLifeCycle[] ary = _uiCycles.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].afterComponentAttached(comp, page);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}
	/** Invokes {@link UiLifeCycle#afterComponentDetached}
	 * when a component is detached from a page.
	 * @since 3.0.6
	 */
	public void afterComponentDetached(Component comp, Page prevpage) {
		final UiLifeCycle[] ary = _uiCycles.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].afterComponentDetached(comp, prevpage);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}
	/** Invokes {@link UiLifeCycle#afterComponentMoved}
	 * when a component is moved (a.k.a., page changed).
	 * @since 3.0.6
	 */
	public void afterComponentMoved(Component parent, Component child, Component prevparent) {
		final UiLifeCycle[] ary = _uiCycles.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].afterComponentMoved(parent, child, prevparent);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}
	/** Invokes {@link UiLifeCycle#afterPageAttached}
	 * when a compnent's parent is changed.
	 * @since 3.0.6
	 */
	public void afterPageAttached(Page page, Desktop desktop) {
		final UiLifeCycle[] ary = _uiCycles.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].afterPageAttached(page, desktop);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}
	/** Invokes {@link UiLifeCycle#afterPageDetached}
	 * when a compnent's parent is changed.
	 * @since 3.0.6
	 */
	public void afterPageDetached(Page page, Desktop prevdesktop) {
		final UiLifeCycle[] ary = _uiCycles.toArray();
		for (int j = 0; j < ary.length; ++j) {
			try {
				ary[j].afterPageDetached(page, prevdesktop);
			} catch (Throwable ex) {
				log.error("Failed to invoke "+ary[j], ex);
			}
		}
	}

	/** Adds an CSS resource that will be generated for each ZUML desktop.
	 *
	 * <p>Note: if {@link ThemeProvider} is specified ({@link #setThemeProvider}),
	 * the final theme URIs generated depend on {@link ThemeProvider#getThemeURIs}.
	 */
	public void addThemeURI(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("empty");
		_themeURIs.add(uri);
	}
	/** Returns a readonly list of the URI of the CSS resources that will be
	 * generated for each ZUML desktop (never null).
	 *
	 * <p>Default: an array with zero length.
	 */
	public String[] getThemeURIs() {
		return _themeURIs.toArray();
	}
	/** Specifies what theme URI to be disabled.
	 *
	 * <p>Note: if {@link ThemeProvider} is used ({@link #setThemeProvider}),
	 * the URIs of the theme depend on {@link ThemeProvider#getThemeURIs}.
	 *
	 * @param uri the theme URI to disable
	 * @since 3.0.0
	 */
	public void addDisabledThemeURI(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException();

		synchronized (this) {
			if (_disThemeURIs == null)
				_disThemeURIs = Collections.synchronizedSet(new HashSet<String>(4));
		}
		_disThemeURIs.add(uri);
	}
	/** Returns a set of the theme URIs that are disabled (never null).
	 *
	 * @since 3.0.0
	 * @see #addDisabledThemeURI
	 */
	public Set<String> getDisabledThemeURIs() {
		if (_disThemeURIs != null)
			return _disThemeURIs;
		return Collections.emptySet();
	}

	/** Returns the theme provider for the current execution,
	 * or null if not available.
	 *
	 * <p>Default: null.
	 *
	 * <p>Note: if specified, the final theme URIs is decided by
	 * the provider. The URIs specified in {@link #getThemeURIs} are
	 * passed to provider, and it has no effect if the provider decides
	 * to ignore them.
	 * @since 3.0.0
	 * @see #getThemeURIs
	 * @see #getDisabledThemeURIs
	 */
	public ThemeProvider getThemeProvider() {
		return _themeProvider;
	}
	/** Sets the theme provider for the current execution,
	 * or null if not available.
	 *
	 * @param provider the theme provide. If null, the default theme URIs
	 * will be used.
	 * @see #getThemeProvider
	 * @since 3.0.0
	 */
	public void setThemeProvider(ThemeProvider provider) {
		_themeProvider = provider;
	}

	/** Sets the class used to handle UI loading and updates, or null to
	 * use the default.
	 * It must implement {@link UiEngine}.
	 */
	public void setUiEngineClass(Class<?> cls) {
		if (cls != null && !UiEngine.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("UiEngine not implemented: "+cls);
		_uiengcls = cls;
	}
	/** Returns the class used to handle UI loading and updates,
	 * or null if default is used.
	 * It must implement {@link UiEngine}.
	 */
	public Class<?> getUiEngineClass() {
		return _uiengcls;
	}

	/** Sets the class used to represent this Web application,
	 * or null to use the default.
	 * It must implement {@link WebApp} and {@link WebAppCtrl}
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 */
	public void setWebAppClass(Class<?> cls) {
		if (cls != null && (!WebApp.class.isAssignableFrom(cls)
		|| !WebAppCtrl.class.isAssignableFrom(cls)))
			throw new IllegalArgumentException("WebApp or WebAppCtrl not implemented: "+cls);
		_wappcls = cls;
	}
	/** Returns the class used to represent this Web application,
	 * or null if default is used.
	 * It must implement {@link WebApp} and {@link WebAppCtrl}
	 */
	public Class<?> getWebAppClass() {
		return _wappcls;
	}

	/** Sets the class used to instantiate an instance representing this Web application,
	 * or null to use the default.
	 *
	 * <p>Note: {@link #setWebAppClass} has the higher priority if not null.
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 * @param cls the class that implements {@link WebAppFactory}.
	 * @since 6.0.0
	 */
	public void setWebAppFactoryClass(Class<?> cls) {
		if (cls != null && !WebAppFactory.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("WebAppFactory not implemented: "+cls);
		_wappftycls = cls;
	}
	/** Returns the class used to instantiate an instance representing this Web application,
	 * or null if default is used.
	 * It must implement {@link WebAppFactory}.
	 * <p>Note: {@link #getWebAppClass} has the higher priority if not null.
	 * @since 6.0.0
	 */
	public Class<?> getWebAppFactoryClass() {
		return _wappftycls;
	}

	/** Sets the class used to provide the desktop cache, or null to
	 * use the default.
	 * It must implement {@link DesktopCacheProvider}.
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 */
	public void setDesktopCacheProviderClass(Class<?> cls) {
		if (cls != null && !DesktopCacheProvider.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("DesktopCacheProvider not implemented: "+cls);
		_dcpcls = cls;
	}
	/** Returns the class used to provide the desktop cache, or null
	 * if default is used.
	 * It must implement {@link DesktopCacheProvider}.
	 */
	public Class<?> getDesktopCacheProviderClass() {
		return _dcpcls;
	}

	/** Sets the class used to instantiate desktops, pages and components, or
	 * null to use the default.
	 * It must implement {@link UiFactory},
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 */
	public void setUiFactoryClass(Class<?> cls) {
		if (cls != null && !UiFactory.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("UiFactory not implemented: "+cls);
		_uiftycls = cls;
	}
	/** Returns the class used to instantiate desktops, pages and components,
	 * or null if default is used.
	 * It must implement {@link UiFactory},
	 */
	public Class<?> getUiFactoryClass() {
		return _uiftycls;
	}

	/** Sets the class used to handle the failover mechanism, or null if
	 * no custom failover mechanism.
	 * It must implement {@link FailoverManager}.
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 */
	public void setFailoverManagerClass(Class<?> cls) {
		if (cls != null && !FailoverManager.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("FailoverManager not implemented: "+cls);
		_failmancls = cls;
	}
	/** Returns the class used to handle the failover mechanism,
	 * or null if no custom failover mechanism.
	 * It must implement {@link FailoverManager}.
	 */
	public Class<?> getFailoverManagerClass() {
		return _failmancls;
	}

	/** Sets the class that is used to generate UUID/ID of desktop,
	 * page and components, or null to use the default.
	 * It must implement {@link IdGenerator}.
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 * @since 2.4.1
	 */
	public void setIdGeneratorClass(Class<?> cls) {
		if (cls != null && !IdGenerator.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("IdGenerator not implemented: "+cls);
		_idgencls = cls;
	}
	/** Returns the class used to generate UUID/ID for desktop,
	 * page and components, or null if the default shall be used.
	 * It must implement {@link IdGenerator}
	 * @since 2.4.1
	 */
	public Class<?> getIdGeneratorClass() {
		return _idgencls;
	}

	/** Sets the class that is used to store ZK sessions,
	 * or null to use the default.
	 * It must implement {@link SessionCache}.
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 * @since 3.0.5
	 */
	public void setSessionCacheClass(Class<?> cls) {
		if (cls != null && !SessionCache.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("SessionCache not implemented: "+cls);
		_sesscachecls = cls;
	}
	/** Returns the class used to store ZK sessions, or null
	 * if the default shall be used.
	 * It must implement {@link SessionCache}.
	 * @since 3.0.5
	 */
	public Class<?> getSessionCacheClass() {
		return _sesscachecls;
	}

	/** Sets the class that is used to decode AU requests,
	 * or null to use the default.
	 * It must implement {@link AuDecoder}.
	 *
	 * <p>Note: you have to set the class before {@link WebApp} is created.
	 * Otherwise, it won't have any effect.
	 * @since 5.0.4
	 */
	public void setAuDecoderClass(Class<?> cls) {
		if (cls != null && !AuDecoder.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("AuDecoder not implemented: "+cls);
		_audeccls = cls;
	}
	/** Returns the class used to decode AU requests, or null
	 * if the default shall be used.
	 * It must implement {@link AuDecoder}.
	 * @since 5.0.4
	 */
	public Class<?> getAuDecoderClass() {
		return _audeccls;
	}

	/** Specifies the maximal allowed time to process events, in milliseconds.
	 * ZK will keep processing the requests until all requests are processed,
	 * or the maximal allowed time expires.
	 *
	 * <p>Default: 3000.
	 *
	 * <p>Note: since 3.0.0, this setting has no effect on AU requests.
	 * It controls only the requests from the client-polling server push.
	 *
	 * @param time the maximal allowed time to process events.
	 * It must be positive.
	 */
	public void setMaxProcessTime(int time) {
		_maxProcTime = time;
	}
	/** Returns the maximal allowed time to process events, in milliseconds.
	 * It is always positive.
	 */
	public int getMaxProcessTime() {
		return _maxProcTime;
	}

	/** Specifies the maximal allowed upload size, in kilobytes.
	 *
	 * <p>Default: 5120.
	 *
	 * @param sz the maximal allowed upload size.
	 * A negative value indicates there is no limit.
	 */
	public void setMaxUploadSize(int sz) {
		_maxUploadSize = sz;
	}
	/** Returns the maximal allowed upload size, in kilobytes, or 
	 * a negative value if no limit.
	 */
	public int getMaxUploadSize() {
		return _maxUploadSize;
	}
	/** Specifies the threshold at which a temporary file is created as a 
	 * buffer, in kilobytes.
	 *
	 * <p>Default: 128.
	 *
	 * @param sz the file size threshold
	 *  A negative value implies default setting.
	 * @since 5.0.8
	 */
	public void setFileSizeThreshold(int sz) {
		_fileSizeThreshold = sz;
	}
	/** Returns the threshold at which a temporary file is created as a 
	 * buffer, in kilobytes, or a negative value which implies default setting.
	 * @since 5.0.8
	 */
	public int getFileSizeThreshold() {
		return _fileSizeThreshold;
	}
	/** Returns the charset used to encode the uploaded text file
	 * (never null).
	 *
	 * <p>Default: UTF-8.
	 * @see #getUploadCharsetFinder
	 */
	public String getUploadCharset() {
		return _charsetUpload;
	}
	/** Sets the charset used to encode the upload text file.
	 *
	 * <p>Note: {@link #setUploadCharsetFinder} has the higher priority.
	 *
	 * @param charset the charset to use.
	 * If null or empty, UTF-8 is assumed.
	 * @see #setUploadCharsetFinder
	 */
	public void setUploadCharset(String charset) {
		_charsetUpload = charset != null && charset.length() > 0 ? charset: "UTF-8";
	}
	/** Returns the finder that is used to decide the character set
	 * for the uploaded text file(s), or null if not available.
	 *
	 * <p>Default: null
	 * @since 3.0.0
	 * @see #getUploadCharset
	 */
	public CharsetFinder getUploadCharsetFinder() {
		return _charsetFinderUpload;
	}
	/** Sets the finder that is used to decide the character set
	 * for the uploaded text file(s), or null if not available.
	 *
	 * <p>It has the higher priority than {@link #setUploadCharset}.
	 * In other words, {@link #getUploadCharset} is used only if
	 * this method returns null or {@link CharsetFinder#getCharset}
	 * returns null.
	 *
	 * @since 3.0.0
	 * @see #setUploadCharset
	 */
	public void setUploadCharsetFinder(CharsetFinder finder) {
		_charsetFinderUpload = finder;
	}

	/** Specifies the time, in seconds, between client requests
	 * before ZK will invalidate the desktop.
	 *
	 * <p>Default: 3600 (1 hour).
	 *
	 * <p>A negative value indicates the desktop should never timeout.
	 */
	public void setDesktopMaxInactiveInterval(int secs) {
		_dtTimeout = secs;
	}
	/** Returns the time, in seconds, between client requests
	 * before ZK will invalidate the desktop.
	 *
	 * <p>Notice that this timeout is used only if JVM starts
	 * GC when the memory is running low.
	 *
	 * <p>A negative value indicates the desktop should never timeout.
	 */
	public int getDesktopMaxInactiveInterval() {
		return _dtTimeout;
	}

	/** Specifies the time, in milliseconds, before ZK Client Engine shows
	 * a dialog to prompt users that the request is in processing.
	 *
	 * <p>Default: 900
	 */
	public void setProcessingPromptDelay(int minisecs) {
		_promptDelay = minisecs;
	}
	/** Returns the time, in milliseconds, before ZK Client Engine shows
	 * a dialog to prompt users that the request is in processing.
	 */
	public int getProcessingPromptDelay() {
		return _promptDelay;
	}
	/**
	 * Specifies the time, in milliseconds, to filter out consecutive
	 * click events.
	 * If two click events (also onOK and onCancel) come too close, the
	 * second one will be removed to avoid the denial-of-service attack.
	 *
	 * <p>If you prefer not to filter out any of them, specify a non-positive
	 * value.
	 *
	 * <p>Default: 0
	 *
	 * @param minisecs the delay to filtering the second click event
	 * if it happens shorter than the second value.
	 * If a non-positive value is specified, no click event is ignored.
	 * @deprecated As of release 5.0.0, please use {@link org.zkoss.zul.Button#setAutodisable} instead.
	 * @since 3.6.0
	 */
	public void setClickFilterDelay(int minisecs) {
	}
	/** Returns the time, in milliseconds, to filter out consecutive
	 * click events.
	 * @deprecated As of release 5.0.0, please use {@link org.zkoss.zul.Button#setAutodisable} instead.
	 * @since 3.6.0
	 */
	public int getClickFilterDelay() {
		return 0;
	}
	/** Specifies the time, in milliseconds, before ZK Client Engine shows
	 * the tooltip when a user moves the mouse over particular UI components.
	 *
	 * <p>Default: 800
	 */
	public void setTooltipDelay(int minisecs) {
		_tooltipDelay = minisecs;
	}
	/** Returns the time, in milliseconds, before ZK Client Engine shows
	 * the tooltip when a user moves the mouse over particular UI components.
	 */
	public int getTooltipDelay() {
		return _tooltipDelay;
	}
	/** Specifies the timeout, in milliseconds, to re-send the AU request when
	 * the server's service unavailable or timeout.
	 * <p>Default: 200
	 * @since 6.5.2
	 */
	public void setAutoResendTimeout(int minisecs) {
		_autoResendTimeout = minisecs;
	}
	/** Returns the timeout, in milliseconds, for re-sending the AU request when
	 * the server's service unavailable or timeout.
	 * @since 6.5.2
	 * <p>Default: 200
	 */
	public int getAutoResendTimeout() {
		return _autoResendTimeout;
	}
	/** @deprecated As of release 6.0.0, it is removed without replacement, since
	 * it is rarely applicable and over complicated.
	 * Specifies the time, in milliseconds, before ZK Client Engine re-sends
	 * the request to the server.
	 *
	 * <p>Default: -1 (i.e., disabled).
	 * However, if ZK 5.0.3 EE or prior, the default is 9000.
	 *
	 * <p>There are many reasons an Ajax request is not received by
	 * the server. With the resending mechanism, ZK ensures the reliable
	 * connection between the client and the server.
	 *
	 * <p>See also <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1839246&group_id=152762&atid=785191">Bug 1839246</a>
	 *
	 * @since 3.0.1
	 *
	 * @param minisecs the timeout in milliseconds.
	 * Since 3.0.3, you can specify a non-positive number to disable the resend.
	 */
	public void setResendDelay(int minisecs) {
	}
	/** @deprecated As of release 6.0.0, it is removed without replacement, since
	 * it is rarely applicable and over complicated.
	 * Returns the time, in milliseconds, before ZK Client Engine re-sends
	 * the request to the server.
	 * @since 3.0.1
	 */
	public int getResendDelay() {
		return -1;
	}

	/** Returns whether this Web application can be crawled by search engines.
	 * Notice that there is some performance loss for huge web pages.
	 * <p>Default: false.
	 * @since 5.0.0
	 */
	public boolean isCrawlable() {
		return _crawlable;
	}
	/** Sets whether this Web application is crawlable.
	 * Make a Web application that allows search engines to crawl the application.
	 * Notice that there is some performance loss for huge web pages. (EE only)
	 * @since 5.0.0
	 */
	public void setCrawlable(boolean crawlable) {
		if (crawlable && !WebApps.getFeature("ee")) {
			log.warn("The crawlable setting is for EE edtion only!");
			return;
		}
		_crawlable = crawlable;
	}

	/** Returns the timeout URI for this device.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * <p>Default: null (to shown an error message).
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @since 3.6.3
	 */
	public URIInfo getTimeoutURI(String deviceType) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo inf = _timeoutURIs.get(deviceType);
		return inf != null && inf.uri != null ? inf: null;
	}
	/** Sets the timeout URI.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @param timeoutURI the timeout URI. If empty, it means to reload
	 * the same page. If null, an error message is shown instead of
	 * redirecting to another page.
	 * @param type how to handle the timeout URI. It is one of
	 * {@link URIInfo#SEND_REDIRECT} or {@link URIInfo#POPUP}.
	 * However, it supports only {@link URIInfo#SEND_REDIRECT} currently.
	 * @return the previous timeout URI, or null if not available.
	 * @since 3.6.3
	 */
	public URIInfo setTimeoutURI(String deviceType, String timeoutURI, int type) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo newi = new TimeoutURIInfo(timeoutURI, type);
		TimeoutURIInfo oldi = _timeoutURIs.put(deviceType, newi);
		if (oldi != null) newi.auto = oldi.auto;
		return oldi != null && oldi.uri != null ? oldi: null;
	}

	/** Returns the timeout message for this device, or null if the default
	 * message is preferred.
	 * It is used only if {@link #getTimeoutURI} returns null.
	 * @since 5.0.5
	 * @see #setTimeoutMessage
	 */
	public String getTimeoutMessage(String deviceType) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo inf = _timeoutURIs.get(deviceType);
		return inf != null ? inf.message: null;
	}
	/** Sets the timeout message for this device, or null if the default
	 * message is preferred.
	 * It is used only if {@link #getTimeoutURI} returns null.
	 * <p>To specify an I18N label, prefix the key with <code>label:</code>.
	 * To specify the JavaScript code, prefix the code with <code>script:</code>.
	 * Refer to <a href="http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_session-config_Element#The_timeout-message_Element">ZK Configuration Reference</a>
	 * for more information.
	 * @return the previous message, if any
	 * @since 5.0.5
	 */
	public String setTimeoutMessage(String deviceType, String message) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo inf = _timeoutURIs.get(deviceType);
		if (inf != null) {
			String old = inf.message;
			inf.message = message;
			return old;
		}

		inf = new TimeoutURIInfo();
		inf.message = message;
		_timeoutURIs.put(deviceType, inf);
		return null;
	}

	/** Returns whether to automatically trigger the timeout at the client.
	 * Refer to {@link #setAutomaticTimeout} for details.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @see #setAutomaticTimeout
	 * @see #getTimeoutURI
	 * @since 3.6.3
	 */
	public boolean isAutomaticTimeout(String deviceType) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo inf = _timeoutURIs.get(deviceType);
		return inf != null && inf.auto;
	}
	/** Sets whether to automatically trigger the timeout at the client.
	 *
	 * <p>Default: false. It means this page is redirected to the timeout URI
	 * when the use takes some action after timeout. In other words,
	 * nothing happens if the user does nothing.
	 * If it is set to true, it is redirected as soon as the timeout URI,
	 * no matter the user takes any action.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @see #setTimeoutURI
	 * @since 3.6.3
	 */
	public boolean setAutomaticTimeout(String deviceType, boolean auto) {
		if (deviceType == null) deviceType = "ajax";

		TimeoutURIInfo inf = _timeoutURIs.get(deviceType);
		if (inf != null) {
			boolean old = inf.auto;
			inf.auto = auto;
			return old;
		}

		inf = new TimeoutURIInfo();
		inf.auto = auto;
		_timeoutURIs.put(deviceType, inf);
		return false;
	}

	/** Sets the URI to redirect to, when ZK Client Engine receives
	 * an error.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @param errCode the error code.
	 * @param uri the URI to redirect to. It cannot be null.
	 * If empty, the client will reload the same page again.
	 * If null, it is the same as {@link #removeClientErrorReload}
	 * @param connType the connection type: au or server-push.
	 * If null, "au" is assumed.
	 * @return the previous URI associated with the specified error code
	 * @since 3.6.3
	 */
	public String setClientErrorReload(String deviceType, int errCode, String uri,
	String connType) {
		if (uri == null)
			return removeClientErrorReload(deviceType, errCode, connType);

		final String index = deviceConn2Str(deviceType, connType);
		synchronized (_errURIs) {
			Map<Integer, String> map = _errURIs.get(index);
			if (map == null)
				_errURIs.put(index, map = new LinkedHashMap<Integer, String>());
			return map.put(errCode, uri);
		}
	}
	/** Removes the URI to redirect to, when ZK Client Engine receives
	 * an error.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @param errCode the error code.
	 * @param connType the connection type: au or server-push.
	 * If null, "au" is assumed.
	 * @return the previous URI associated with the specified error code
	 * @since 3.6.3
	 */
	public String removeClientErrorReload(String deviceType, int errCode,
	String connType) {
		final String index = deviceConn2Str(deviceType, connType);
		synchronized (_errURIs) {
			Map<Integer, String> map = _errURIs.get(index);
			return map != null ? map.remove(errCode): null;
		}
	}
	/** Returns the URI that is associated with the specified error code,
	 * or null if no URI is associated.
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @param errCode the error code.
	 * @param connType the connection type: au or server-push.
	 * If null, "au" is assumed.
	 * @since 3.6.3
	 */
	public String getClientErrorReload(String deviceType, int errCode,
	String connType) {
		final String index = deviceConn2Str(deviceType, connType);
		synchronized (_errURIs) {
			Map<Integer, String> map = _errURIs.get(index);
			return map != null ? map.get(errCode): null;
		}
	}
	/** Returns an array of pairs of the error code and URI info of
	 * the specified device and connection (never null).
	 *
	 * <p>Default: none (none since 3.6.0, while
	 * older version: 302, 401 and 403 are associated with an empty URI).
	 *
	 * @param deviceType the device type: ajax or mil.
	 * If null, ajax is assumed.
	 * @param connType the connection type: au or server-push.
	 * If null, "au" is assumed.
	 * @return an array of pairs (two-element arrays) of the error code and URI info.
	 * In other words, each element of the returned array is a pair of
	 * Integer and {@link URIInfo}.
	 * For example, [[410, new URIInfo("/login.zul")], [310, new URIInfo("/login2.zul")]].
	 * @since 3.6.3
	 */
	public Object[][] getClientErrorReloads(String deviceType, String connType) {
		final String index = deviceConn2Str(deviceType, connType);
		synchronized (_errURIs) {
			final Map<Integer, String> map = _errURIs.get(index);
			if (map != null) {
				Object[][] infs = new Object[map.size()][2];
				int j = 0;
				for (Map.Entry<Integer, String> me: map.entrySet()) {
					infs[j][0] = me.getKey();
					infs[j++][1] = new URIInfo(me.getValue());
				}
				return infs;
			}
		}
		return new Object[0][0];
	}
	private static final String deviceConn2Str(String deviceType, String connType) {
		if (deviceType == null) deviceType = "ajax";
		return connType != null && "server-push".equals(connType) ?
			"s:" + deviceType: deviceType;
	}

	/**  Specifies the time, in seconds, between client requests
	 * before ZK will invalidate the session.
	 *
	 * <p>Default: 0 (means the system default).
	 *
	 * @see #setTimerKeepAlive
	 * @see Session#setMaxInactiveInterval
	 */
	public void setSessionMaxInactiveInterval(int secs) {
		_sessTimeout = secs;
	}
	/** Returns the time, in seconds, between client requests
	 * before ZK will invalidate the session.
	 *
	 * <p>Default: 0 (means the system default).
	 *
	 * <p>A negative value indicates that there is no limit.
	 * Zero means to use the system default (usually defined in web.xml).
	 *
	 * @see #isTimerKeepAlive
	 * @see Session#getMaxInactiveInterval
	 */
	public int getSessionMaxInactiveInterval() {
		return _sessTimeout;
	}

	/** Specifies the maximal allowed number of desktop
	 * per session.
	 *
	 * <p>Default: 15.
	 *
	 * <p>A negative value indicates there is no limit.
	 * @since 3.0.1
	 */
	public void setSessionMaxDesktops(int max) {
		_sessDktMax = max;
	}
	/** Returns the maximal allowed number of desktop per session.
	 *
	 * <p>A negative value indicates there is no limit.
	 * @since 3.0.1
	 */
	public int getSessionMaxDesktops() {
		return _sessDktMax;
	}
	/** Specifies the maximal allowed number of concurrent requests
	 * per session.
	 *
	 * <p>Default: 5.
	 *
	 * <p>A negative value indicates there is no limit, but it is
	 * not recommended due to the possibility of the DoS attacks.
	 * @since 3.0.1
	 */
	public void setSessionMaxRequests(int max) {
		_sessReqMax = max;
	}
	/** Returns the maximal allowed number of concurrent requests
	 * per session.
	 *
	 * <p>A negative value indicates there is no limit, but it is
	 * not recommended due to the possibility of the DoS attacks.
	 * @since 3.0.1
	 */
	public int getSessionMaxRequests() {
		return _sessReqMax;
	}

	/** Specifies the maximal allowed number of concurrent server-pushes
	 * per session.
	 *
	 * <p>Default: -1 (no limitation).
	 *
	 * @param max the maximal allowed number.
	 * A negative value indicates there is no limit.
	 * @since 5.0.0
	 */
	public void setSessionMaxPushes(int max) {
		_sessPushMax = max;
	}
	/** Returns the maximal allowed number of concurrent server-pushes
	 * per session.
	 */
	public int getSessionMaxPushes() {
		return _sessPushMax;
	}

	/** Specifies the maximal allowed number of the spare pool for
	 * queuing the event processing threads (per Web application).
	 *
	 * <p>Default: 100.
	 *
	 * <p>A negative value indicates there is no limit.
	 *
	 * <p>ZK uses a thread pool to keep the idle event processing threads.
	 * It speeds up the service of an event by reusing the thread queued
	 * in this pool.
	 *
	 * @see #setMaxSuspendedThreads
	 * @see #isEventThreadEnabled
	 */
	public void setMaxSpareThreads(int max) {
		_sparThdMax = max;
	}
	/** Returns the maximal allowed number of the spare pool for
	 * queuing event processing threads (per Web application).
	 * @see #isEventThreadEnabled
	 */
	public int getMaxSpareThreads() {
		return _sparThdMax;
	}

	/** Specifies the maximal allowed number of suspended event
	 * processing threads (per Web application).
	 *
	 * <p>Default: -1 (no limit).
	 *
	 * <p>A negative value indicates there is no limit.
	 *
	 * <p>It is ignored if the use of the event processing thread
	 * is disable ({@link #isEventThreadEnabled}.
	 */
	public void setMaxSuspendedThreads(int max) {
		_suspThdMax = max;
	}
	/** Returns the maximal allowed number of suspended event
	 * processing threads (per Web application).
	 *
	 * <p>It is ignored if the use of the event processing thread
	 * is disable ({@link #isEventThreadEnabled}.
	 * @see #isEventThreadEnabled
	 */
	public int getMaxSuspendedThreads() {
		return _suspThdMax;
	}
	/** Sets whether to use the event processing thread.
	 *
	 * <p>Default: false (disabled).
	 *
	 * @exception IllegalStateException if there is suspended thread
	 * and use is false.
	 */
	public void enableEventThread(boolean enable) {
		if (!enable && _wapp != null) {
			final UiEngine engine = ((WebAppCtrl)_wapp).getUiEngine();
			if (engine != null) {
				if (engine.hasSuspendedThread())
					throw new IllegalStateException("Unable to disable due to suspended threads");
			}
		}
		if (enable)
			log.warn("Enable event thread has deprecated!");
		_evtThdEnabled = enable;
	}
	/** Returns whether to use the event processing thread.
	 * <p>Default: false (disabled).
	 */
	public boolean isEventThreadEnabled() {
		return _evtThdEnabled;
	}

	/** Sets whether zscript is allowed.
	 * <p>Default: true (enabled).
	 * @since 6.0.0
	 */
	public void enableZScript(boolean enable) {
		_zscriptEnabled = enable;
	}
	/** Returns whether zscript is allowed.
	 * <p>Default: true (enabled).
	 * @since 6.0.0
	 */
	public boolean isZScriptEnabled() {
		return _zscriptEnabled;
	}

	/** Returns the monitor for this application, or null if not set.
	 */
	public Monitor getMonitor() {
		return _monitor;
	}
	/** Sets the monitor for this application, or null to disable it.
	 *
	 * <p>Default: null.
	 *
	 * <p>There is at most one monitor for each Web application.
	 * The previous monitor will be replaced when this method is called.
	 *
	 * <p>In addition to call this method, you could specify a monitor
	 * in zk.xml
	 *
	 * @param monitor the performance meter. If null, the meter function
	 * is disabled.
	 * @return the previous monitor, or null if not available.
	 */
	public Monitor setMonitor(Monitor monitor) {
		final Monitor old = _monitor;
		_monitor = monitor;
		return old;
	}

	/** Returns the performance meter for this application, or null if not set.
	 * @since 3.0.0
	 */
	public PerformanceMeter getPerformanceMeter() {
		return _pfmeter;
	}
	/** Sets the performance meter for this application, or null to disable it.
	 *
	 * <p>Default: null.
	 *
	 * <p>There is at most one performance meter for each Web application.
	 * The previous meter will be replaced when this method is called.
	 *
	 * <p>In addition to call this method, you could specify
	 * a performance meter in zk.xml
	 *
	 * @param meter the performance meter. If null, the meter function
	 * is disabled.
	 * @return the previous performance meter, or null if not available.
	 * @since 3.0.0
	 */
	public PerformanceMeter setPerformanceMeter(PerformanceMeter meter) {
		final PerformanceMeter old = _pfmeter;
		_pfmeter = meter;
		return old;
	}

	/** Returns the execution monitor for this application, or null if not set.
	 * @since 6.0.0
	 */
	public ExecutionMonitor getExecutionMonitor() {
		return _execmon;
	}
	/** Sets the execution monitor for this application, or null to disable it.
	 *
	 * <p>Default: null.
	 *
	 * <p>There is at most one execution monitor for each Web application.
	 * The previous meter will be replaced when this method is called.
	 *
	 * <p>In addition to call this method, you could specify
	 * an execution monitor in zk.xml
	 *
	 * @param monitor the execution monitor. If null, the monitor function
	 * is disabled.
	 * @return the previous execution monitor, or null if not available.
	 * @since 6.0.0
	 */
	public ExecutionMonitor setExecutionMonitor(ExecutionMonitor monitor) {
		final ExecutionMonitor old = _execmon;
		_execmon = monitor;
		return old;
	}

	/** Returns the desktop recycle for this application, or null if not set.
	 * @since 5.0.0
	 */
	public DesktopRecycle getDesktopRecycle() {
		return _dtRecycle;
	}
	/** Sets the desktop recycler for this application, or null to disable it.
	 *
	 * <p>Default: null.
	 *
	 * <p>There is at most one desktop recycle for each Web application.
	 * The previous instance will be replaced when this method is called.
	 *
	 * <p>In addition to call this method, you could specify
	 * a desktop recycle in zk.xml
	 *
	 * @param dtRecycle the desktop recycle. If null, the recycle function
	 * is disabled.
	 * @return the previous desktop recycle, or null if not available.
	 * @since 5.0.0
	 */
	public DesktopRecycle setDesktopRecycle(DesktopRecycle dtRecycle) {
		final DesktopRecycle old = _dtRecycle;
		_dtRecycle = dtRecycle;
		return old;
	}

	/** Returns the charset used to generate the HTTP response
	 * or null to use the container's default.
	 * It is currently used by {@link org.zkoss.zk.ui.http.DHtmlLayoutServlet},
	 *
	 * <p>Default: UTF-8.
	 */
	public String getResponseCharset() {
		return _charsetResp;
	}
	/** Sets the charset used to generate HTTP response.
	 * It is currently used by {@link org.zkoss.zk.ui.http.DHtmlLayoutServlet},
	 *
	 * @param charset the charset to use. If null or empty, the container's default
	 * is used.
	 */
	public void setResponseCharset(String charset) {
		_charsetResp = charset != null && charset.length() > 0 ? charset: null;
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
		final String value = _prefs.get(name);
		return value != null ? value: defaultValue;
	}
	/** Sets the value of the preference.
	 */
	public void setPreference(String name, String value) {
		if (name == null || value == null)
			throw new IllegalArgumentException("null");
		_prefs.put(name, value);
	}
	/** Returns a readonly set of all preference names.
	 */
	public Set<String> getPreferenceNames() {
		return _prefs.keySet();
	}

	/** Adds the definition of a richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old richlet will be replaced.
	 *
	 * @param name the richlet name
	 * @param params the initial parameters, or null if no initial parameter at all.
	 * Once called, the caller cannot access <code>params</code> any more.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 */
	public Object addRichlet(String name, Class<?> richletClass, Map<String, String> params) {
		if (!Richlet.class.isAssignableFrom(richletClass))
			throw new IllegalArgumentException("A richlet class, "+richletClass+", must implement "+Richlet.class.getName());

		return addRichlet0(name, richletClass, params);
	}
	/** Adds the definition of a richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old servlet will be replaced.
	 *
	 * @param name the richlet name
	 * @param richletClassName the class name. The class will be loaded
	 * only when the richlet is loaded.
	 * @param params the initial parameters, or null if no initial parameter at all.
	 * Once called, the caller cannot access <code>params</code> any more.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 */
	public Object addRichlet(String name, String richletClassName, Map<String, String> params) {
		if (richletClassName == null || richletClassName.length() == 0)
			throw new IllegalArgumentException("richletClassName is required");

		return addRichlet0(name, richletClassName, params);
	}
	/** Adds the richlet.
	 *
	 * <p>If there was a richlet associated with the same name, the
	 * the old one will be replaced.
	 *
	 * @param name the richlet name
	 * @param richlet the richlet implemetation.
	 * @return the previous richlet class or class-name with the specified name,
	 * or null if no previous richlet.
	 * @since 7.0.2
	 */
	public Object addRichlet(String name, Richlet richlet) {
		if (richlet == null)
			throw new IllegalArgumentException("richlet instance is required");

		return addRichlet0(name, richlet, null);
	}
	private Object addRichlet0(String name, Object richletClass, Map<String, String> params) {
		Object o;
		
		for (;;) {
			// remove previous richlet if it exists
			o = removeRichlet0(name);
			
			synchronized (_richlets) {
				// add new richlet definition only if map does not contain record
				// with same name
				if (!(_richlets.containsKey(name))) {
					if (richletClass instanceof Richlet) {
						_richlets.put(name, richletClass);
					} else {
						_richlets.put(name, new Object[] {richletClass, params});
					}
					break;
				}
			}
		}

		return o;
	}
	
	/**
	 * Removes the richlet and associated richlet mappings.
	 * 
	 * @param name the richlet name
	 * @return the removed richlet class or class-name with the specified name,
	 * or null if the richlet is not found.
	 * @since 7.0.2
	 */
	public Object removeRichlet(String name) {
		// remove richlet
		final Object o = removeRichlet0(name);
		
		// remove associated richlet mappings
		removeRichletMapping(name);
		
		return o;
	}
	
	/**
	 * Removes the richlet.
	 * 
	 * @param name the richlet name
	 * @return the removed richlet class or class-name with the specified name,
	 * or null if the richlet is not found.
	 * @since 7.0.2
	 */
	private Object removeRichlet0(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name is required");
		}
		Object o;

		for (;;) {
			// remove richlet
			synchronized (_richlets) {
				o = _richlets.remove(name);
			}
			
			// verify it sth instancing richlet at the moment
			if (o instanceof WaitLock) {
				WaitLock lock = (WaitLock) o;
				if (!lock.waitUntilUnlock(300 * 1000)) { //5 minute
					String msg = new StringBuilder("Unable to remove richlet ").
							append(name).
							append("\nCause: conflict too long.").
							toString();
					final PotentialDeadLockException ex =
						new PotentialDeadLockException(msg);
					log.warn(msg, ex); //very rare, possibly a bug
					throw ex;
				}
			} else {
				break;
			}
		}
		
		if (o == null) {
			return null;
		}
		if (o instanceof Richlet) {
			// destroy object if it is richlet
			destroy((Richlet) o);
			return o.getClass();
		}
		return ((Object[])o)[0];
	}
	/** Adds a richlet mapping.
	 *
	 * @param name the name of the richlet.
	 * @param path the URL pattern. It must start with '/' and may end
	 * with '/*'.
	 * @exception UiException if the richlet is not defined yet.
	 * See {@link #addRichlet}.
	 * @since 2.4.0
	 */
	public void addRichletMapping(String name, String path) {
		//Note: "/" is the same as ""
		if (path == null || path.length() == 0 || "/".equals(path))
			path = "";
		else if (path.charAt(0) != '/')
			throw new IllegalArgumentException("path must start with '/', not "+path);

		final boolean wildcard = path.endsWith("/*");
		if (wildcard) //wildcard
			path = path.substring(0, path.length() - 2);
				//note it might be empty
		
		//richlet mapping cannot be added if richlet is not defined,
		//so check if richlet with same name exists and then
		//add richlet mapping
		synchronized (_richlets) {
			if (!_richlets.containsKey(name))
				throw new UiException("Richlet not defined: "+name);
		
			synchronized (_richletmaps) {
				_richletmaps.put(
					path, new Object[] {name, Boolean.valueOf(wildcard)});
			}
		}
	}
	
	/**
	 * Removes all richlet mappings for the specified richlet.
	 * 
	 * @param name the richlet name
	 */
	private void removeRichletMapping(String name) {
		// remove richlet mapping
		synchronized (_richletmaps) {
			Iterator<Map.Entry<String, Object[]>> iter = 
					_richletmaps.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object[]> entry = iter.next();
				String richletName = (String) entry.getValue()[0];
				if (richletName.equals(name)) {
					iter.remove();
				}
			}
		}
	}
	
	private static void destroy(Richlet richlet) {
		try {
			richlet.destroy();
		} catch (Throwable ex) {
			log.error("Unable to destroy "+richlet);
		}
	}
	/** Returns an instance of richlet of the specified name, or null
	 * if not found.
	 */
	@SuppressWarnings("unchecked")
	public Richlet getRichlet(String name) {
		WaitLock lock = null;
		final Object[] info;
		for (;;) {
			synchronized (_richlets) {
				Object o = _richlets.get(name);
				if (o == null || (o instanceof Richlet)) { //not found or loaded
					return (Richlet)o;
				} else if (o instanceof WaitLock) { //loading by another thread
					lock = (WaitLock)o;
				} else {
					info = (Object[])o;

					//going to load in this thread
					_richlets.put(name, lock = new WaitLock());
					break; //then, load it
				}
			} //sync(_richlets)

			if (!lock.waitUntilUnlock(300*1000)) { //5 minute
				final PotentialDeadLockException ex =
					new PotentialDeadLockException(
					"Unable to load richlet "+name+"\nCause: conflict too long.");
				log.warn("", ex); //very rare, possibly a bug
				throw ex;
			}
		} //for (;;)

		//load it
		try {
			if (info[0] instanceof String) {
				try {
					info[0] = Classes.forNameByThread((String)info[0]);
				} catch (Throwable ex) {
					throw new UiException("Failed to load "+info[0]);
				}
			}

			final Object o = ((Class<?>)info[0]).newInstance();
			if (!(o instanceof Richlet))
				throw new UiException(Richlet.class+" must be implemented by "+info[0]);

			final Richlet richlet = (Richlet)o;
			richlet.init(newRichletConfig((Map<String, String>)info[1]));

			synchronized (_richlets) {
				_richlets.put(name, richlet);
			}
			return richlet;
		} catch (Throwable ex) {
			synchronized (_richlets) {
				_richlets.put(name, info); //remove lock and restore info
			}
			throw UiException.Aide.wrap(ex, "Unable to instantiate "+info[0]);
		} finally {
			lock.unlock();
		}
	}
	private RichletConfig newRichletConfig(Map<String, String> params) {
		return new RichletConfigImpl(_wapp, params);
	}

	/** Returns an instance of richlet for the specified path, or
	 * null if not found.
	 */
	public Richlet getRichletByPath(String path) {
		if (path == null || path.length() == 0 || "/".equals(path))
			path = "";
		else if (path.charAt(0) != '/')
			path = '/' + path;

		final int len = path.length();
		for (int j = len;;) {
			final Richlet richlet =
				getRichletByPath0(path.substring(0, j), j != len);
			if (richlet != null || j == 0)
				return richlet;
			j = path.lastIndexOf('/', j - 1); //j must not -1
		}
	}
	private Richlet getRichletByPath0(String path, boolean wildcardOnly) {
		final Object[] info;
		synchronized (_richletmaps) {
			info = _richletmaps.get(path);
		}
		return info != null &&
			(!wildcardOnly || ((Boolean)info[1]).booleanValue()) ?
				getRichlet((String)info[0]): null;
	}
	/** Destroyes all richlets.
	 */
	public void detroyRichlets() {
		synchronized (_richlets) {
			for (Iterator<Object> it = _richlets.values().iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof Richlet)
					destroy((Richlet)o);
			}
			_richlets.clear();
		}
	}

	/** Specifies whether to keep the desktops across visits.
	 * If false, the desktops are removed when an user reloads an URL
	 * or browses to another URL.
	 *
	 * <p>Default: false.
	 */
	public void setKeepDesktopAcrossVisits(boolean keep) {
		_keepDesktop = keep;
	}
	/** Returns whether to keep the desktops across visits.
	 * If false, the desktops are removed when an user reloads an URL
	 * or browses to another URL.
	 */
	public boolean isKeepDesktopAcrossVisits() {
		return _keepDesktop;
	}

	/** Specifies whether to keep the session alive,
	 * when receiving the onTimer event.
	 *
	 * <p>Default: false.
	 *
	 * <p>A session is expired (and then invalidated), if it didn't receive
	 * any client request in the specified timeout interval
	 * ({@link #getSessionMaxInactiveInterval}).
	 * By setting this option to true, the session timeout will be reset
	 * when onTimer is received (just like any other event).
	 *
	 * <p>Note: if true and the timer is shorter than
	 * the session timeout ({@link #getSessionMaxInactiveInterval}),
	 * the session is never expired.
	 *
	 * @param alive whether to keep the session alive when receiving
	 * onTimer
	 * @since 3.0.0
	 */
	public void setTimerKeepAlive(boolean alive) {
		_timerKeepAlive = alive;
	}
	/** Returns whether to keep the session alive,
	 * when receiving the onTimer event.
	 * In other words, it returns whether to reset the session timeout
	 * counter when receiving onTimer, just like any other events.
	 *
	 * @since 3.0.0
	 */
	public boolean isTimerKeepAlive() {
		return _timerKeepAlive;
	}

	/** Returns whether to debug JavaScript files.
	 * If true, it means the original (i.e., uncompressed) JavaScript files
	 * shall be loaded instead of the compressed JavaScript files.
	 *
	 * @since 3.0.4
	 * @see #setDebugJS
	 */
	public boolean isDebugJS() {
		return _debugJS;
	}
	/**Sets whether to debug JavaScript files.
	 *
	 * <p>Default: false.
	 *
	 * <p>If true is specified, it will try to load the original
	 * Java (i.e., uncompressed) file instead of the compressed one.
	 * For example, if {@link org.zkoss.web.util.resource.ClassWebResource#service} is called to load abc.js,
	 * and {@link #isDebugJS}, then {@link org.zkoss.web.util.resource.ClassWebResource#service} will try
	 * to load abc.org.js first. If not found, it load ab.js insted.
	 *
	 * <p>If {@link #isDebugJS} is false (default),
	 * abc.js is always loaded.
	 *
	 * <p>Prior to 5.0.3, the setting won't affect JavaScript files that have been
	 * loaded. That is, the reboot is required.
	 *
	 * @param debug whether to debug JavaScript files.
	 * If true, the original JavaScript files shall be
	 * loaded instead of the compressed files.
	 * @since 3.0.4
	 */
	public void setDebugJS(boolean debug) {
		_debugJS = debug;
		if (_wapp != null)
			org.zkoss.zk.ui.http.Utils.updateDebugJS(_wapp, debug);
	}

	/** Returns whether to use the same UUID sequence for desktops after
	 * rebooting.
	 * <p>Default: false.
	 * <p>Note: if the custom ID generator (org.zkoss.zk.ui.util.IdGenerator)
	 * is used, this option is meaningless.
	 * @since 5.0.0
	 */
	public boolean isRepeatUuid() {
		return _repeatUuid;
	}
	/** Sets whether to use the same UUID sequence for desktops after
	 * rebooting.
	 * @since 5.0.0
	 */
	public void setRepeatUuid(boolean repeat) {
		_repeatUuid = repeat;
	}

	/** Sets the implementation of the expression factory that shall
	 * be used by the whole system.
	 *
	 * <p>Default: null -- it means the org.zkoss.xel.el.ELFactory class
	 * (it requires zcommons-el.jar).
	 *
	 * <p>Note: you can only specify an implementation that is compatible
	 * with JSP EL here, since ZK's builtin pages depend on it.
	 * However, you can use any factory you like in an individual page,
	 * as long as all expressions in the page follow the syntax of
	 * the evaluator you are using.
	 *
	 * @param expfcls the implemtation class, or null to use the default.
	 * Note: expfcls must implement {@link ExpressionFactory}.
	 * @since 3.0.0
	 */
	public void setExpressionFactoryClass(Class<? extends ExpressionFactory> expfcls) {
		Expressions.setExpressionFactoryClass(expfcls);
	}
	/** Returns the implementation of the expression factory that
	 * is used by the whole system, or null if the sytem default is used.
	 *
	 * @see #setExpressionFactoryClass
	 * @since 3.0.0
	 */
	public Class<? extends ExpressionFactory> getExpressionFactoryClass() {
		return Expressions.getExpressionFactoryClass();
	}

	/** Invokes {@link EventInterceptor#beforeSendEvent}
	 * registered by {@link #addListener} with a class implementing
	 * {@link EventInterceptor}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public Event beforeSendEvent(Event event) {
		return _eis.beforeSendEvent(event);
	}
	/** Invokes {@link EventInterceptor#beforePostEvent}
	 * registered by {@link #addListener} with a class implementing
	 * {@link EventInterceptor}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public Event beforePostEvent(Event event) {
		return _eis.beforePostEvent(event);
	}
	/** Invokes {@link EventInterceptor#beforeProcessEvent}
	 * registered by {@link #addListener} with a class implementing
	 * {@link EventInterceptor}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public Event beforeProcessEvent(Event event) {
		return _eis.beforeProcessEvent(event);
	}
	/** Invokes {@link EventInterceptor#afterProcessEvent}
	 * registered by {@link #addListener} with a class implementing
	 * {@link EventInterceptor}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public void afterProcessEvent(Event event) {
		_eis.afterProcessEvent(event);
	}

	/** Returns a map of application-specific attributes.
	 * @since 5.0.0
	 */
	public Map<String, Object> getAttributes() {
		return _attrs;
	}
	/** Returns the value of an application-specific attribute, or
	 * null if not found.
	 * @since 5.0.0
	 */
	public Object getAttribute(String name) {
		return _attrs.get(name);
	}
	/** Returns the value of an application-specific attribute.
	 * @param value the value of the attribute. If null, it means removal,
	 * i.e., {@link #removeAttribute}.
	 * @return the previous value, or null if no such value.
	 * @since 5.0.0
	 */
	public Object setAttribute(String name, Object value) {
		return value != null ? _attrs.put(name, value): removeAttribute(name);
	}
	/** Removes the value of an application-specific attribute.
	 * @return the previous value, or null if no such value.
	 * @since 5.0.0
	 */
	public Object removeAttribute(String name) {
		return _attrs.remove(name);
	}

	/** Adds a client (JavaScript) pacakge that is provided by this server.
	 * <p>Default: none.
	 * <p>If no package is defined (default), ZK Client Engine assumes
	 * all packages coming from the server generating the HTML page.
	 *
	 * <p>However, it might not be true if you want to load some client
	 * codes from different server (such as Ajax-asService).
	 * Therefore, you have to invoke this method to add the client packages
	 * if this server is going to provide JavaScript codes for other servers.
	 * @since 5.0.0
	 */
	public void addClientPackage(String pkg) {
		if (pkg == null || pkg.length() == 0)
			throw new IllegalArgumentException("empty");
		_clientpkgs.add(pkg);
	}
	/** Returns a readonly list of the names of the client pages
	 * that are provided by this server
	 *
	 * @since 5.0.0
	 */
	public String[] getClientPackages() {
		return _clientpkgs.toArray();
	}

	/** Returns the time, in seconds, to show a warning message
	 * if an event has been processinged longer than it.
	 * <p>Default: 600
	 * @since 3.6.3
	 */
	public int getEventTimeWarning() {
		return _evtTimeWarn;
	}
	/** Set the time, in seconds, to show a warning message
	 * if an event has been processinged longer than it.
	 * @param secs the number of seconds.
	 * If a non-positive number is specified, no warning message at all.
	 * @since 3.6.3
	 */
	public void setEventTimeWarning(int secs) {
		_evtTimeWarn = secs;
	}
	/** Adds an error page.
	 *
	 * @param deviceType the device type: ajax or mil
	 * @param type what type of errors the error page is associated with.
	 * @param location where is the error page.
	 * @return the previous location of the same error, or null if not
	 * defined yet.
	 * @since 2.4.1
	 */
	public String addErrorPage(String deviceType, Class<?> type, String location) {
		if (!Throwable.class.isAssignableFrom(type))
			throw new IllegalArgumentException("Throwable or derived is required: "+type);
		if (location == null || deviceType == null)
			throw new IllegalArgumentException();

		List<ErrorPage> l;
		synchronized (_errpgs) {
			l = _errpgs.get(deviceType);
			if (l == null)
				_errpgs.put(deviceType, l = new LinkedList<ErrorPage>());
		}

		String previous = null;
		synchronized (l) {
			//remove the previous definition
			for (Iterator<ErrorPage> it = l.iterator(); it.hasNext();) {
				final ErrorPage errpg = it.next();
				if (errpg.type.equals(type)) {
					previous = errpg.location;
					it.remove();
					break;
				}
			}
			l.add(new ErrorPage(type, location));
		}
		return previous;
	}
	/** Returns the error page that matches the specified error, or null if not found.
	 *
	 * @param deviceType the device type: ajax or mil
	 * @param error the exception being thrown
	 * @since 2.4.1
	 */
	public String getErrorPage(String deviceType, Throwable error) {
		if (!_errpgs.isEmpty()) {
			final List<ErrorPage> l;
			synchronized (_errpgs) {
				l = _errpgs.get(deviceType);
			}
			if (l != null) {
				synchronized (l) {
					for (Iterator<ErrorPage> it = l.iterator(); it.hasNext();) {
						final ErrorPage errpg = it.next();
						if (errpg.type.isInstance(error))
							return errpg.location;
					}
				}
			}
		}
		return null;
	}
	private static class ErrorPage {
		private final Class<?> type;
		private final String location;
		private ErrorPage(Class<?> type, String location) {
			this.type = type;
			this.location = location;
		}
	}
	/** Used with {@link FastReadArray} to check if an object is
	 * the same class as specified.
	 */
	private static class SameClass implements Comparable<Object> {
		private final Class<?> _klass;
		private SameClass(Class<?> klass) {
			_klass = klass;
		}
		public int compareTo(Object o) {
			return o.getClass().equals(_klass) ? 0: 1;
		}

		//Object//
		public String toString() {
			return Objects.toString(_klass);
		}
		public boolean equals(Object o) {
			if (this == o) return true;
			return Objects.equals(_klass, o instanceof SameClass ? ((SameClass)o)._klass: o);
		}
		public int hashCode() {
			return Objects.hashCode(_klass);
		}
	}
	private static class TimeoutURIInfo extends URIInfo {
		private String message;
		private boolean auto;
		private TimeoutURIInfo() {
			super(null);
		}
		private TimeoutURIInfo(String uri, int type) {
			super(uri, type);
		}
	}

	/**
	 * Returns whether to use custom {@link ThemeProvider}. If true, it means
	 * the default ThemeProvider shall be ignored. It is set to true if declare
	 * custom ThemeProvider in zk.xml
	 * <p>
	 * Default: false
	 * @since 7.0.0
	 */
	public boolean isCustomThemeProvider() {
		return _customThemeProvider;
	}
	/**
	 * Sets whether to use custom {@link ThemeProvider}.
	 * <p>Default: false.
	 * <p>It is set to true if declare custom ThemeProvider in zk.xml
	 * <p>Note: internal use only
	 * @param customThemeProvider whether to use custom {@link ThemeProvider}.
	 * @since 7.0.0
	 */
	public void setCustomThemeProvider(boolean customThemeProvider) {
		_customThemeProvider = customThemeProvider;
	}
	/**
	 * Returns whether to use custom {@link ThemeRegistry}. If true, it means
	 * the default ThemeRegistry shall be ignored. It is set to true if declare
	 * custom ThemeRegistry in zk.xml
	 * <p>Default: false
	 * @since 7.0.0
	 */
	public boolean isCustomThemeRegistry() {
		return _customThemeRegistry;
	}
	/**
	 * Sets whether to use custom {@link ThemeRegistry}.
	 * <p>Default: false.
	 * <p>It is set to true if declare custom ThemeRegistry in zk.xml.
	 * <p>Note: internal use only
	 * @param customThemeRegistry whether to use custom {@link ThemeRegistry}.
	 * @since 7.0.0
	 */
	public void setCustomThemeRegistry(boolean customThemeRegistry) {
		_customThemeRegistry = customThemeRegistry;
	}
	/**
	 * Returns whether to use custom {@link ThemeResolver}. If true, it means
	 * the default ThemeResolver shall be ignored. It is set to true if declare
	 * custom ThemeResolver in zk.xml
	 * <p>Default: false
	 * @since 7.0.0
	 */
	public boolean isCustomThemeResolver() {
		return _customThemeResolver;
	}
	/**
	 * Sets whether to use custom {@link ThemeResolver}.
	 * <p>Default: false.
	 * <p>It is set to true if declare custom ThemeResolver in zk.xml.
	 * <p>Note: internal use only
	 * @param customThemeResolver whether to use custom {@link ThemeResolver}.
	 * @since 7.0.0
	 */
	public void setCustomThemeResolver(boolean customThemeResolver) {
		_customThemeResolver = customThemeResolver;
	}
}
