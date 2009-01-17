/* AbstractWebApp.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 15 17:28:15     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.InputStream;

import org.zkoss.util.Utils;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Files;

import org.zkoss.zk.Version;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.metainfo.DefinitionLoaders;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.http.SimpleSessionCache;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.SessionCache;
import org.zkoss.zk.ui.impl.SessionDesktopCacheProvider;
import org.zkoss.zk.ui.impl.UiEngineImpl;

/**
 * A skeletal implementation of {@link WebApp}.
 *
 * @author tomyeh
 */
abstract public class AbstractWebApp implements WebApp, WebAppCtrl {
	private static final Log log = Log.lookup(AbstractWebApp.class);

	private String _appnm = "ZK", _build;
	private Configuration _config;
	private UiEngine _engine;
	private DesktopCacheProvider _provider;
	private UiFactory _factory;
	private FailoverManager _failover;
	private IdGenerator _idgen;
	private SessionCache _sesscache;

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
		return _appnm;
	}
	public void setAppName(String name) {
		_appnm = name != null ? name: "";
	}

	public final String getVersion() {
		return Version.RELEASE;
	}
	public final String getBuild() {
		if (_build == null)
			loadBuild();
		return _build;
	}
	public int getSubversion(int portion) {
		return Utils.getSubversion(getVersion(), portion);
	}

	public final Configuration getConfiguration() {
		return _config;
	}

	//WebAppCtrl//
	public void init(Object context, Configuration config) {
		if (_config != null)
			throw new IllegalStateException("Cannot be initialized twice");
		if (config == null)
			throw new IllegalArgumentException("null");
		final WebApp oldwapp = config.getWebApp();
		if (oldwapp != null && oldwapp != this)
			throw new IllegalArgumentException("config already belongs to other Web app, "+oldwapp);

		_config = config;
		_config.setWebApp(this);

		Class cls = _config.getUiEngineClass();
		if (cls == null) {
			_engine = new UiEngineImpl();
		} else {
			try {
				_engine = (UiEngine)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getDesktopCacheProviderClass();
		if (cls == null) {
			_provider = new SessionDesktopCacheProvider();
		} else {
			try {
				_provider = (DesktopCacheProvider)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getUiFactoryClass();
		if (cls == null) {
			_factory = new SimpleUiFactory();
		} else {
			try {
				_factory = (UiFactory)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getFailoverManagerClass();
		if (cls != null) {
			try {
				_failover = (FailoverManager)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}
		cls = _config.getIdGeneratorClass();
		if (cls != null) {
			try {
				_idgen = (IdGenerator)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getSessionCacheClass();
		if (cls == null) {
			_sesscache = new SimpleSessionCache();
		} else {
			try {
				_sesscache = (SessionCache)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		_engine.start(this);
		_provider.start(this);
		_factory.start(this);
		if (_failover != null) {
			try {
				_failover.start(this);
			} catch (AbstractMethodError ex) { //backward compatible
			}
		}
		_sesscache.init(this);

		_config.invokeWebAppInits();
	}
	public void destroy() {
		_config.invokeWebAppCleanups();

		_config.detroyRichlets();

		try {
			_sesscache.destroy(this);
		} catch (AbstractMethodError ex) { //backward compatible
		}
		_factory.stop(this);
		_provider.stop(this);
		_engine.stop(this);
		if (_failover != null) {
			try {
				_failover.stop(this);
			} catch (AbstractMethodError ex) { //backward compatible
			}
			_failover = null;
		}
		_factory = null;
		_provider = null;
		_engine = null;
		_sesscache = null;

		//we don't reset _config since WebApp cannot be re-inited after stop
	}

	public final UiEngine getUiEngine() {
		return _engine;
	}
	public void setUiEngine(UiEngine engine) {
		if (engine == null) throw new IllegalArgumentException();
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
		if (provider == null) throw new IllegalArgumentException();
		_provider.stop(this);
		_provider = provider;
		_provider.start(this);
	}
	public UiFactory getUiFactory() {
		return _factory;
	}
	public void setUiFactory(UiFactory factory) {
		if (factory == null) throw new IllegalArgumentException();
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
		if (cache == null) throw new IllegalArgumentException();
		_sesscache.destroy(this);
		_sesscache = cache;
		_sesscache.init(this);
	}		

	/** Invokes {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#sessionWillPassivate}.
	 */
	public void sessionWillPassivate(Session sess) {
		_provider.sessionWillPassivate(sess);
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
			log.warning("Failed to cleanup session", ex);
		}

		try {
			getSessionCache().remove(sess);
		} catch (Throwable ex) {
			log.warning("Failed to cleanup session", ex);
		}

		try {
			((SessionCtrl)sess).onDestroyed();
				//after called, sess.getNativeSession() is null!
		} catch (Throwable ex) {
			log.warning("Failed to cleanup session", ex);
		}
	}

	/** Loads the build identifier. */
	synchronized private void loadBuild() {
		if (_build == null) {
			final String FILE = "/metainfo/zk/build";
			InputStream is = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(FILE);
			if (is == null) {
				is = AbstractWebApp.class.getResourceAsStream(FILE);
				if (is == null) {
					_build = "";
					return; //done;
				}
			}
			try {
				_build = new String(Files.readAll(is)).trim();
			} catch (Exception ex) {
				_build = "error";
			} finally {
				try {is.close();} catch (Throwable ex) {}
			}
		}
	}
}
