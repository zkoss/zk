/* AbstractWebApp.java

	Purpose:

	Description:

	History:
		Wed Mar 15 17:28:15     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Files;
import org.zkoss.util.Utils;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.SimpleSessionCache;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.SessionCache;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A skeletal implementation of {@link WebApp}.
 *
 * @author tomyeh
 */
public abstract class AbstractWebApp implements WebApp, WebAppCtrl {
	private static final Logger log = LoggerFactory.getLogger(AbstractWebApp.class);

	private String _appnm;
	private Configuration _config;
	private UiEngine _engine;
	private DesktopCacheProvider _provider;
	private UiFactory _factory;
	private FailoverManager _failover;
	private IdGenerator _idgen;
	private SessionCache _sesscache;
	private AuDecoder _audec;

	private static String _build;

	/** Constructor.
	 *
	 * <p>Note: after constructed, it is not initialized completely.
	 * For example, {@link #getConfiguration} returns null.
	 *
	 * <p>WebManager will initialize it later such as initializing
	 * a {@link Configuration} instance by loading zk.xml,
	 * and then calling {@link #init}.
	 */
	protected AbstractWebApp() {
	}

	public String getAppName() {
		return _appnm != null ? _appnm : "ZK";
	}

	public void setAppName(String name) {
		_appnm = name != null ? name : "";
	}

	public final String getVersion() {
		return org.zkoss.zk.Version.RELEASE;
	}

	public String getBuild() {
		return getBuildStamp();
	}

	public static final String getBuildStamp() {
		return _build == null ? loadBuild() : _build;
	}

	public int getSubversion(int portion) {
		return Utils.getSubversion(getVersion(), portion);
	}

	public final Configuration getConfiguration() {
		return _config;
	}

	public Object getAttribute(String name, boolean recurse) {
		return getAttribute(name);
	}

	public boolean hasAttribute(String name, boolean recurse) {
		return hasAttribute(name);
	}

	public Object setAttribute(String name, Object value, boolean recurse) {
		return setAttribute(name, value);
	}

	public Object removeAttribute(String name, boolean recurse) {
		return removeAttribute(name);
	}

	//WebAppCtrl//
	public void init(Object context, Configuration config) {
		if (_config != null)
			throw new IllegalStateException("Cannot be initialized twice");
		if (config == null)
			throw new IllegalArgumentException("null");
		final WebApp oldwapp = config.getWebApp();
		if (oldwapp != null && oldwapp != this)
			throw new IllegalArgumentException("config already belongs to other Web app, " + oldwapp);

		_config = config;
		if (_appnm == null)
			_appnm = _config.getPreference("org.zkoss.zk.ui.WebApp.name", "ZK");
		_config.setWebApp(this);

		Class cls = _config.getUiEngineClass();
		if (cls == null) {
			_engine = new UiEngineImpl();
		} else {
			try {
				_engine = (UiEngine) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		cls = _config.getDesktopCacheProviderClass();
		if (cls == null) {
			_provider = new SessionDesktopCacheProvider();
		} else {
			try {
				_provider = (DesktopCacheProvider) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		cls = _config.getUiFactoryClass();
		if (cls == null) {
			_factory = new SimpleUiFactory();
		} else {
			try {
				_factory = (UiFactory) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		cls = _config.getFailoverManagerClass();
		if (cls != null) {
			try {
				_failover = (FailoverManager) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}
		cls = _config.getIdGeneratorClass();
		if (cls != null) {
			try {
				_idgen = (IdGenerator) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		cls = _config.getSessionCacheClass();
		if (cls == null) {
			_sesscache = new SimpleSessionCache();
		} else {
			try {
				_sesscache = (SessionCache) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		cls = _config.getAuDecoderClass();
		if (cls != null) {
			try {
				_audec = (AuDecoder) cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}
		_engine.start(this);
		_provider.start(this);
		_factory.start(this);
		if (_failover != null)
			_failover.start(this);
		_sesscache.init(this);

		_config.invokeWebAppInits();
	}

	public void destroy() {
		try {
			_config.invokeWebAppCleanups();
		} catch (Throwable ex) {
			log.warn("Failed to invoke webapp cleanups", ex);
		}
		try {
			_config.detroyRichlets();
		} catch (Throwable ex) {
			log.warn("Failed to destroy richlets", ex);
		}

		try {
			_sesscache.destroy(this);
		} catch (NoClassDefFoundError ex) { //Bug 3046360
		} catch (Throwable ex) {
			log.warn("Failed to destroy session cache", ex);
		}

		try {
			_factory.stop(this);
		} catch (Throwable ex) {
			log.warn("Failed to stop factory", ex);
		}
		try {
			_provider.stop(this);
		} catch (Throwable ex) {
			log.warn("Failed to stop provider", ex);
		}
		try {
			_engine.stop(this);
		} catch (Throwable ex) {
			log.warn("Failed to stop engine", ex);
		}
		if (_failover != null) {
			try {
				_failover.stop(this);
			} catch (NoClassDefFoundError ex) { //Bug 3046360
			} catch (Throwable ex) {
				log.warn("Failed to stop failover", ex);
			}
			_failover = null;
		}
		_factory = null;
		//		_provider = null;
		//Provider might be stopped before sessionDidActivate is called (Tomcat 5.5.2)
		_engine = null;
		_sesscache = null;

		try {
			org.zkoss.util.Cleanups.cleanup();
		} catch (NoClassDefFoundError ex) { //Bug 3046360
		} catch (Throwable ex) {
			log.warn("Failed to cleanup", ex);
		}

		//we don't reset _config since WebApp cannot be re-inited after stop
	}

	public final UiEngine getUiEngine() {
		return _engine;
	}

	public void setUiEngine(UiEngine engine) {
		if (engine == null)
			throw new IllegalArgumentException();
		_engine.stop(this);
		_engine = engine;
		_engine.start(this);
	}

	public DesktopCache getDesktopCache(Session sess) {
		return _provider.getDesktopCache(sess);
	}

	public DesktopCacheProvider getDesktopCacheProvider() {
		return _provider;
	}

	public void setDesktopCacheProvider(DesktopCacheProvider provider) {
		if (provider == null)
			throw new IllegalArgumentException();
		_provider.stop(this);
		_provider = provider;
		_provider.start(this);
	}

	public UiFactory getUiFactory() {
		return _factory;
	}

	public void setUiFactory(UiFactory factory) {
		if (factory == null)
			throw new IllegalArgumentException();
		_factory.stop(this);
		_factory = factory;
		_factory.start(this);
	}

	public FailoverManager getFailoverManager() {
		return _failover;
	}

	public void setFailoverManager(FailoverManager failover) {
		if (_failover != null)
			_failover.stop(this);
		_failover = failover;
		if (_failover != null)
			_failover.start(this);
	}

	public IdGenerator getIdGenerator() {
		return _idgen;
	}

	public void setIdGenerator(IdGenerator idgen) {
		_idgen = idgen;
	}

	public SessionCache getSessionCache() {
		return _sesscache;
	}

	public void setSessionCache(SessionCache cache) {
		if (cache == null)
			throw new IllegalArgumentException();
		_sesscache.destroy(this);
		_sesscache = cache;
		_sesscache.init(this);
	}

	public AuDecoder getAuDecoder() {
		return _audec;
	}

	public void setAuDecoder(AuDecoder audec) {
		_audec = audec;
	}

	/** Invokes {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#sessionWillPassivate}.
	 */
	public void sessionWillPassivate(Session sess) {
		if (_provider != null)
			_provider.sessionWillPassivate(sess);
		//Provider might be stopped before sessionDidActivate is called (Tomcat 5.5.2)
	}

	/** Invokes {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#sessionDidActivate}.
	 */
	public void sessionDidActivate(Session sess) {
		_provider.sessionDidActivate(sess);
	}

	public void sessionDestroyed(Session sess) {
		try {
			getDesktopCacheProvider().sessionDestroyed(sess);
		} catch (Throwable ex) {
			log.warn("Failed to cleanup session", ex);
		}

		try {
			getSessionCache().remove(sess);
		} catch (Throwable ex) {
			log.warn("Failed to cleanup session", ex);
		}

		try {
			((SessionCtrl) sess).onDestroyed();
			//after called, sess.getNativeSession() is null!
		} catch (Throwable ex) {
			log.warn("Failed to cleanup session", ex);
		}
	}

	/** Loads the build identifier.
	 * This method is used only Internally
	 */
	public static synchronized String loadBuild() {
		if (_build == null) {
			final String FILE = "/metainfo/zk/build";
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream is = null;
			try {
				Enumeration<URL> en = cl.getResources(FILE.substring(1)); // should not start with "/"
				while (en.hasMoreElements()) {
					URL url = en.nextElement();
					String path = url.getPath();
					if (path != null && path.matches(".*/zk(-\\d[^/]*?)?\\.jar\\!?" + FILE + "$")) { //the filename of jar might change
						is = url.openStream();
						break;
					}
				}
			} catch (IOException e) {
				//do nothing
			}
			if (is == null) {
				is = AbstractWebApp.class.getResourceAsStream(FILE);
				if (is == null)
					return _build = ""; //done;
			}
			try {
				_build = new String(Files.readAll(is)).trim();
			} catch (Exception ex) {
				_build = "error";
			} finally {
				try {
					is.close();
				} catch (Throwable ignored) {
				}
			}
		}
		return _build;
	}
}
