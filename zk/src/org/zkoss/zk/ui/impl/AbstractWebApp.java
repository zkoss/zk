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

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.metainfo.DefinitionLoaders;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.impl.SessionDesktopCacheProvider;
import org.zkoss.zk.ui.impl.UiEngineImpl;

/**
 * A skeletal implementation of {@link WebApp}.
 *
 * @author tomyeh
 */
abstract public class AbstractWebApp implements WebApp, WebAppCtrl {
	private String _appnm = "ZK";
	private Configuration _config;
	private UiEngine _engine;
	private DesktopCacheProvider _provider;
	private UiFactory _factory;
	private FailoverManager _failover;
	private IdGenerator _idgen;

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
		return "2.4.2-FL";
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

		DefinitionLoaders.setZkVersion(getVersion());

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

		_engine.start(this);
		_provider.start(this);
		_factory.start(this);

		_config.invokeWebAppInits();
	}
	public void destroy() {
		_config.invokeWebAppCleanups();

		_config.detroyRichlets();

		getUiFactory().stop(this);
		getDesktopCacheProvider().stop(this);
		getUiEngine().stop(this);

		//we don't reset _config since WebApp cannot be re-inited after stop
	}

	public final UiEngine getUiEngine() {
		return _engine;
	}
	public DesktopCache getDesktopCache(Session sess) {
		return _provider.getDesktopCache(sess);
	}
	public DesktopCacheProvider getDesktopCacheProvider() {
		return _provider;
	}
	public UiFactory getUiFactory() {
		return _factory;
	}
	public FailoverManager getFailoverManager() {
		return _failover;
	}
	public IdGenerator getIdGenerator() {
		return _idgen;
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
}
