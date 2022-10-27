/* DummyWebApp.java

	Purpose:
		
	Description:
		
	History:
		10:15 AM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.mock;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.SessionCache;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Mock Dummy {@link WebApp} and {@link WebAppCtrl}
 * @author jumperchen
 */
public class DummyWebApp implements WebApp, WebAppCtrl {
	private UiEngine _engine;
	private Configuration _config;
	private IdGenerator _idGenerator;

	public DummyWebApp() {
		this._engine = new DummyUiEngine();
		this._config = new Configuration();
		this._idGenerator = new IdGenerator() {
			// same as AbstractComponent.ANONYMOUS_ID
			private static final String prefix = "z__i";
			private int count = 1;

			public String nextComponentUuid(Desktop desktop, Component comp,
					ComponentInfo compInfo) {
				return prefix + Integer.toString(count++, 36);
			}

			public String nextPageUuid(Page page) {
				return prefix + Integer.toString(count++, 36);
			}

			public String nextDesktopId(Desktop desktop) {
				return prefix + Integer.toString(count++, 36);
			}
		};
	}

	public String getAppName() {
		return null;
	}

	public void setAppName(String s) {

	}

	public String getVersion() {
		return null;
	}

	public String getBuild() {
		return null;
	}

	public int getSubversion(int i) {
		return 0;
	}

	public Object getAttribute(String s) {
		return null;
	}

	public Object getAttribute(String s, boolean b) {
		return null;
	}

	public boolean hasAttribute(String s) {
		return false;
	}

	public boolean hasAttribute(String s, boolean b) {
		return false;
	}

	public Object setAttribute(String s, Object o) {
		return null;
	}

	public Object setAttribute(String s, Object o, boolean b) {
		return null;
	}

	public Object removeAttribute(String s) {
		return null;
	}

	public Object removeAttribute(String s, boolean b) {
		return null;
	}

	public boolean addScopeListener(ScopeListener scopeListener) {
		return false;
	}

	public boolean removeScopeListener(ScopeListener scopeListener) {
		return false;
	}

	public Map<String, Object> getAttributes() {
		return null;
	}

	public WebApp getWebApp(String s) {
		return null;
	}

	public String getDirectory() {
		return null;
	}

	public URL getResource(String s) {
		return null;
	}

	public InputStream getResourceAsStream(String s) {
		return null;
	}

	public String getRealPath(String s) {
		return null;
	}

	public String getMimeType(String s) {
		return null;
	}

	public Set<String> getResourcePaths(String s) {
		return null;
	}

	public String getInitParameter(String s) {
		return null;
	}

	public Iterable<String> getInitParameterNames() {
		return null;
	}

	public String getUpdateURI() {
		return null;
	}

	public String getUpdateURI(boolean b) {
		return null;
	}

	public Configuration getConfiguration() {
		return _config;
	}

	public Object getNativeContext() {
		return null;
	}

	public ServletContext getServletContext() {
		return null;
	}

	public void log(String s) {

	}

	public void log(String s, Throwable throwable) {

	}

	public void init(Object o, Configuration configuration) {

	}

	public void destroy() {

	}

	public UiEngine getUiEngine() {
		return _engine;
	}

	public void setUiEngine(UiEngine uiEngine) {
		_engine = uiEngine;
	}

	public DesktopCache getDesktopCache(Session session) {
		return null;
	}

	public DesktopCacheProvider getDesktopCacheProvider() {
		return null;
	}

	public void setDesktopCacheProvider(
			DesktopCacheProvider desktopCacheProvider) {

	}

	public UiFactory getUiFactory() {
		return null;
	}

	public void setUiFactory(UiFactory uiFactory) {

	}

	public FailoverManager getFailoverManager() {
		return null;
	}

	public void setFailoverManager(FailoverManager failoverManager) {

	}

	public IdGenerator getIdGenerator() {
		return _idGenerator;
	}

	public void setIdGenerator(IdGenerator idGenerator) {

	}

	public SessionCache getSessionCache() {
		return null;
	}

	public void setSessionCache(SessionCache sessionCache) {

	}

	public AuDecoder getAuDecoder() {
		return null;
	}

	public void setAuDecoder(AuDecoder auDecoder) {

	}

	public void sessionWillPassivate(Session session) {

	}

	public void sessionDidActivate(Session session) {

	}

	public void sessionDestroyed(Session session) {

	}
}
