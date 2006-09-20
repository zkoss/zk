/* AbstractWebApp.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 15 17:28:15     2006, Created by tomyeh@potix.com
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
abstract public class AbstractWebApp implements WebApp, WebAppCtrl {
	private String _appnm = "ZK";
	private final Configuration _config;
	private final UiEngine _engine;
	private final DesktopCacheProvider _provider;
	private final UiFactory _factory;

	protected AbstractWebApp(Configuration config, UiEngine engine,
	DesktopCacheProvider provider, UiFactory factory) {
		if (config == null || engine == null || provider == null
		|| factory == null)
			throw new IllegalArgumentException("null");
		_config = config;
		_engine = engine;
		_provider = provider;
		_factory = factory;
	}

	public String getAppName() {
		return _appnm;
	}
	public void setAppName(String name) {
		_appnm = name != null ? name: "";
	}

	public final String getVersion() {
		return "2.1.2 Freshly";
	}

	public final Configuration getConfiguration() {
		return _config;
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
