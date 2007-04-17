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
		return "2.3.1-FL";
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
		final UiEngine engine;
		if (cls == null) {
			engine = new UiEngineImpl();
		} else {
			try {
				engine = (UiEngine)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getDesktopCacheProviderClass();
		final DesktopCacheProvider provider;
		if (cls == null) {
			provider = new SessionDesktopCacheProvider();
		} else {
			try {
				provider = (DesktopCacheProvider)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		cls = _config.getUiFactoryClass();
		final UiFactory factory;
		if (cls == null) {
			factory = new SimpleUiFactory();
		} else {
			try {
				factory = (UiFactory)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}

		_engine = engine;
		_provider = provider;
		_factory = factory;

		engine.start(this);
		provider.start(this);
		factory.start(this);

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
