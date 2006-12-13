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
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.DesktopCache;

/**
 * A skeletal implementation of {@link WebApp}.
 *
 * @author tomyeh
 */
abstract public class AbstractWebApp implements WebApp, WebAppCtrl {
	private String _appnm = "ZK";
	private final Configuration _config;
	private UiEngine _engine;
	private DesktopCacheProvider _provider;
	private UiFactory _factory;

	/** Constructor.
	 *
	 * <p>Note: after constructed, it is not initialized completely.
	 * Rather, WebManager will initialize it later such as initializing
	 * {@link #getConfiguration} by loading zk.xml and calling {@link #init}.
	 */
	protected AbstractWebApp() {
		_config = new Configuration(this);
	}

	public String getAppName() {
		return _appnm;
	}
	public void setAppName(String name) {
		_appnm = name != null ? name: "";
	}

	public final String getVersion() {
		return "2.2.0";
	}

	public final Configuration getConfiguration() {
		return _config;
	}

	//WebAppCtrl//
	public void init(UiEngine engine, DesktopCacheProvider provider,
	UiFactory factory) {
		if (engine == null || provider == null || factory == null)
			throw new IllegalArgumentException("null");

		_engine = engine;
		_provider = provider;
		_factory = factory;
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
