/* SimpleWebApp.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 27 09:27:03     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.zkoss.util.CollectionsX;
import org.zkoss.web.servlet.xel.AttributesMap;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.impl.AbstractWebApp;
import org.zkoss.zk.ui.impl.ScopeListeners;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A servlet-based Web application.
 *
 * @author tomyeh
 */
public class SimpleWebApp extends AbstractWebApp {
	private ServletContext _ctx;
	private final ScopeListeners _scopeListeners = new ScopeListeners(this);

	public SimpleWebApp() {
	}

	//super//
	public void init(Object context, Configuration config) {
		if (context == null)
			throw new IllegalArgumentException("context");
		_ctx = (ServletContext) context;
		super.init(context, config);
	}

	private final Map<String, Object> _attrs = new AttributesMap() {
		@SuppressWarnings("unchecked")
		protected Enumeration<String> getKeys() {
			return _ctx.getAttributeNames();
		}

		protected Object getValue(String key) {
			return _ctx.getAttribute(key);
		}

		protected void setValue(String key, Object val) {
			_ctx.setAttribute(key, val);
		}

		protected void removeValue(String key) {
			_ctx.removeAttribute(key);
		}
	};

	public Object getAttribute(String name) {
		return _ctx.getAttribute(name);
	}

	public boolean hasAttribute(String name) {
		return getAttribute(name) != null; //Servlet limitation
	}

	public Object setAttribute(String name, Object value) {
		Object old = _ctx.getAttribute(name);
		_ctx.setAttribute(name, value);
		return old;
	}

	public Object removeAttribute(String name) {
		Object old = _ctx.getAttribute(name);
		_ctx.removeAttribute(name);
		return old;
	}

	public Map<String, Object> getAttributes() {
		return _attrs;
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _scopeListeners.addScopeListener(listener);
	}

	public boolean removeScopeListener(ScopeListener listener) {
		return _scopeListeners.removeScopeListener(listener);
	}

	/** Returns all scope listeners.
	 */
	/*package*/ ScopeListeners getScopeListeners() {
		return _scopeListeners;
	}

	public String getUpdateURI() {
		return getUpdateURI(true);
	}

	public String getUpdateURI(boolean encode) {
		final String uri = getWebManager().getUpdateURI();
		return encode ? Executions.getCurrent().encodeURL(uri) : uri;
	}

	public String getResourceURI() {
		return getResourceURI(true);
	}

	public String getResourceURI(boolean encode) {
		final String uri = getWebManager().getResourceURI();
		return encode ? Executions.getCurrent().encodeURL(uri) : uri;
	}

	private WebManager getWebManager() {
		return WebManager.getWebManager(this);
	}

	public WebApp getWebApp(String uripath) {
		final ServletContext another = _ctx.getContext(uripath);
		if (another != null) {
			final WebManager webman = WebManager.getWebManagerIfAny(another);
			if (webman != null)
				return webman.getWebApp();
		}
		return null;
	}

	public String getDirectory() {
		return null;
	}

	public URL getResource(String path) {
		if (path.startsWith("~./"))
			return getWebManager().getClassWebResource().getResource(path.substring(2));
		try {
			return _ctx.getResource(path);
		} catch (MalformedURLException ex) {
			throw new UiException("Failed to retrieve " + path, ex);
		}
	}

	public InputStream getResourceAsStream(String path) {
		if (path.startsWith("~./"))
			return getWebManager().getClassWebResource().getResourceAsStream(path.substring(2));
		return _ctx.getResourceAsStream(path);
	}

	public String getInitParameter(String name) {
		return _ctx.getInitParameter(name);
	}

	public Iterable<String> getInitParameterNames() {
		return new Iterable<String>() {
			@SuppressWarnings("unchecked")
			public Iterator<String> iterator() {
				return new CollectionsX.EnumerationIterator<String>(_ctx.getInitParameterNames());
			}
		};
	}

	public String getRealPath(String path) {
		return _ctx.getRealPath(path);
	}

	public String getMimeType(String file) {
		return _ctx.getMimeType(file);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourcePaths(String path) {
		return _ctx.getResourcePaths(path);
	}

	/** @deprecated As of release 6.0.0, replaced with {@link #getServletContext}.
	 */
	public Object getNativeContext() {
		return _ctx;
	}

	public ServletContext getServletContext() {
		return _ctx;
	}

	public void log(String msg) {
		_ctx.log(msg);
	}

	public void log(String msg, Throwable ex) {
		_ctx.log(msg, ex);
	}
}
