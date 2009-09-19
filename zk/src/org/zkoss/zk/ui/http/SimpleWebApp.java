/* SimpleWebApp.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 27 09:27:03     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.zkoss.util.CollectionsX;
import org.zkoss.web.servlet.xel.AttributesMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.impl.AbstractWebApp;
import org.zkoss.zk.ui.impl.ScopeListeners;

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
		_ctx = (ServletContext)context;

		super.init(context, config);
	}

	private final Map _attrs = new AttributesMap() {
		protected Enumeration getKeys() {
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
	public Map getAttributes() {
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
		final String uri = WebManager.getWebManager(this).getUpdateURI();
		return encode ? Executions.getCurrent().encodeURL(uri): uri;
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
		try {
			return _ctx.getResource(path);
		} catch (MalformedURLException ex) {
			throw new UiException("Failed to retrieve "+path, ex);
		}
	}
	public InputStream getResourceAsStream(String path) {
		return _ctx.getResourceAsStream(path);
	}

	public String getInitParameter(String name) {
		return _ctx.getInitParameter(name);
	}
	public Iterator getInitParameterNames() {
		return new CollectionsX.EnumerationIterator(
			_ctx.getInitParameterNames());
	}
	public String getRealPath(String path) {
		return _ctx.getRealPath(path);
	}
	public String getMimeType(String file) {
		return _ctx.getMimeType(file);
	}
	public Set getResourcePaths(String path) {
		return _ctx.getResourcePaths(path);
	}
	public Object getNativeContext() {
		return _ctx;
	}
}
