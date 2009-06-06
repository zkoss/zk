/* SimpleWebApp.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb 27 09:27:03     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.impl.AbstractWebApp;

/**
 * A servlet-based Web application.
 *
 * @author tomyeh
 */
public class SimpleWebApp extends AbstractWebApp {
	private ServletContext _ctx;
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
	public void setAttribute(String name, Object value) {
		_ctx.setAttribute(name, value);
	}
	public void removeAttribute(String name) {
		_ctx.removeAttribute(name);
	}
	public Map getAttributes() {
		return _attrs;
	}

	public String getUpdateURI() {
		return WebManager.getWebManager(this).getUpdateURI();
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
