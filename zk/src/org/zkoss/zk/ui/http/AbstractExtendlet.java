/* AbstractExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  8 12:17:03     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Classes;
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletLoader;

import org.zkoss.zk.ui.WebApp;

/**
 * Skeletal implementation for {@link WpdExtendlet} and {@link WcsExtendlet}.
 *
 * @author tomyeh
 * @since 5.0.0
 */
/*package*/ abstract class AbstractExtendlet implements Extendlet {
	static final Log log = Log.lookup(AbstractExtendlet.class);

	ExtendletContext _webctx;
	/** DSP interpretation cache. */
	ResourceCache _cache;
	private Boolean _debugJS;

	//Public Utilities
	/** Sets whether to generate JS files that is easy to debug. */
	public void setDebugJS(boolean debugJS) {
		_debugJS = Boolean.valueOf(debugJS);
		if (_cache != null)
			_cache.clear();
	}
	/** Returns whether to generate JS files that is easy to debug. */
	public boolean isDebugJS() {
		if (_debugJS == null) {
			final WebApp wapp = getWebApp();
			if (wapp == null) return true; //zk lighter
			_debugJS = Boolean.valueOf(wapp.getConfiguration().isDebugJS());
		}
		return _debugJS.booleanValue();
	}

	WebApp getWebApp() {
		return _webctx != null ? WebManager.getWebManager(_webctx.getServletContext()).getWebApp(): null;
	}
	ServletContext getServletContext() {
		return _webctx != null ? _webctx.getServletContext(): null;
	}
	void init(ExtendletConfig config, ExtendletLoader loader) {
		_webctx = config.getExtendletContext();
		_cache = new ResourceCache(loader, 16);
		_cache.setMaxSize(1024);
		_cache.setLifetime(60*60*1000); //1hr
		final int checkPeriod = loader.getCheckPeriod();
		_cache.setCheckPeriod(checkPeriod >= 0 ? checkPeriod: 60*60*1000); //1hr
	}

	/** Returns the static method defined in an element, or null if failed. */
	/*package*/ static MethodInfo getMethodInfo(Element el) {
		final String clsnm = IDOMs.getRequiredAttributeValue(el, "class");
		final String sig = IDOMs.getRequiredAttributeValue(el, "signature");
		final Class cls;
		try {
			cls = Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			log.error("Class not found: "+clsnm+", "+el.getLocator());
			return null; //to report as many errors as possible
		}

		try {
			final Method mtd = Classes.getMethodBySignature(cls, sig, null);
			if ((mtd.getModifiers() & Modifier.STATIC) == 0) {
				log.error("Not a static method: "+mtd);
				return null;
			}

			final Object[] args = new Object[mtd.getParameterTypes().length];
			for (int j = 0; j < args.length; ++j)
				args[j] = el.getAttributeValue("arg" + j);
				
			return new MethodInfo(mtd, args);
		} catch (ClassNotFoundException ex) {
			log.realCauseBriefly("Unable to load class when resolving "+sig+" "+el.getLocator(), ex);
		} catch (NoSuchMethodException ex) {
			log.error("Method not found in "+clsnm+": "+sig+" "+el.getLocator());
		}
		return null;
	}
	/** Invokes a static method.*/
	/*package*/ String invoke(RequestContext reqctx, MethodInfo mi) {
		final Class[] argTypes = mi.method.getParameterTypes();
		final Object[] args = mi.arguments;
		if (reqctx != null) {
			for (int j = 0; j < args.length; ++j)
				if (ServletRequest.class.isAssignableFrom(argTypes[j]))
					args[j] = reqctx.request;
				else if (ServletResponse.class.isAssignableFrom(argTypes[j]))
					args[j] = reqctx.response;
				else if (ServletContext.class.isAssignableFrom(argTypes[j]))
					args[j] = getServletContext();
		}
		try {
			Object o = mi.method.invoke(null, args);
			return o instanceof String ? (String)o: "";
		} catch (Throwable ex) { //log and eat ex
			log.error("Unable to invoke "+mi.method, ex);
			return "";
		}
	}

	//Extendlet
	public boolean getFeature(int feature) {
		return feature == ALLOW_DIRECT_INCLUDE;
	}

	private InputStream getResourceAsStream(
	HttpServletRequest request, String path, boolean locate)
	throws IOException, ServletException {
		if (locate)
			path = Servlets.locate(_webctx.getServletContext(),
				request, path, _webctx.getLocator());

		if (_cache.getCheckPeriod() >= 0) {
			//Due to Web server might cache the result, we use URL if possible
			try {
				URL url = _webctx.getResource(path);
				if (url != null)
					return url.openStream();
			} catch (Throwable ex) {
				log.warningBriefly("Unable to read from URL: "+path, ex);
			}
		}

		//Note: _webctx will handle the renaming for debugJS (.src.js)
		return _webctx.getResourceAsStream(path);
	}
	private URL getResource(String path) throws IOException {
		return _webctx.getResource(path);
	}

	//utility class
	/*package*/ static class MethodInfo {
		final Method method;
		final Object[] arguments;
		MethodInfo(Method method, Object[] arguments) {
			this.method = method;
			this.arguments = arguments;
		}
	}

	/** A resource context.
	 */
	/*package*/ static class RequestContext { //don't use private since WpdContent needs it
		private AbstractExtendlet _extlet;
		/*package*/ final HttpServletRequest request;
		/*package*/ final HttpServletResponse response;

		/*package*/ RequestContext(AbstractExtendlet extlet,
		HttpServletRequest request, HttpServletResponse response) {
			_extlet = extlet;
			this.request = request;
			this.response = response;
		}
		InputStream getResourceAsStream(String path, boolean locate)
		throws IOException, ServletException {
			return _extlet.getResourceAsStream(this.request, path, locate);
		}
		URL getResource(String path) throws IOException {
			return _extlet.getResource(path);
		}
	}
}
