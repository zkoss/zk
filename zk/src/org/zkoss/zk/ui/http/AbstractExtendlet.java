/* AbstractExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  8 12:17:03     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
	static final Log log = Log.lookup(WpdExtendlet.class);

	ExtendletContext _webctx;
	/** DSP interpretation cache. */
	ResourceCache _cache;
	/** The provider. */
	private ThreadLocal _provider = new ThreadLocal();
	private Boolean _debugJS;

	//Public Utilities
	/** Sets whether to generate JS files that is easy to debug. */
	public void setDebugJS(boolean debugJS) {
		_debugJS = Boolean.valueOf(debugJS);
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

	//Package Utilities
	Provider getProvider() {
		return (Provider)_provider.get();
	}
	void setProvider(Provider provider) {
		_provider.set(provider);
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
	/*package*/ static Method getMethod(Element el) {
		final String clsnm = IDOMs.getRequiredAttributeValue(el, "class");
		final String mtdnm = IDOMs.getRequiredAttributeValue(el, "name");
		final Class cls;
		try {
			cls = Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			log.error("Class not found: "+clsnm+", "+el.getLocator());
			return null; //to report as many errors as possible
		}

		try {
			final Method mtd = cls.getMethod(mtdnm, new Class[0]);
			if ((mtd.getModifiers() & Modifier.STATIC) == 0) {
				log.error("Not a static method: "+mtd);
				return null;
			}
			return mtd;
		} catch (NoSuchMethodException ex) {
			log.error("Method not found in "+clsnm+": "+mtdnm+" "+el.getLocator());
			return null;
		}
	}
	/** Invokes a static method.*/
	/*package*/ String invoke(Method mtd) {
		final Provider provider = getProvider();
		final Class[] argTypes = mtd.getParameterTypes();
		final Object[] args = new Object[argTypes.length];
		if (provider != null)
			for (int j = 0; j < args.length; ++j)
				if (ServletRequest.class.isAssignableFrom(argTypes[j]))
					args[j] = provider.request;
				else if (ServletResponse.class.isAssignableFrom(argTypes[j]))
					args[j] = provider.response;
				else if (ServletContext.class.isAssignableFrom(argTypes[j]))
					args[j] = getServletContext();
		try {
			Object o = mtd.invoke(null, args);
			return o instanceof String ? (String)o: "";
		} catch (Throwable ex) { //log and eat ex
			log.error("Unable to invoke "+mtd, ex);
			return "";
		}
	}

	//Extendlet
	public boolean getFeature(int feature) {
		return feature == ALLOW_DIRECT_INCLUDE;
	}

	//utility class
	/*package*/ class Provider { //don't use private since WpdContent needs it
		/*package*/ final HttpServletRequest request;
		/*package*/ final HttpServletResponse response;

		/*package*/ Provider(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
		}

		/*package*/
		InputStream getResourceAsStream(String path, boolean locate)
		throws IOException, ServletException {
			if (locate)
				path = Servlets.locate(_webctx.getServletContext(),
					this.request, path, _webctx.getLocator());

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
	}
	/*package*/ class FileProvider extends Provider {
		private String _parent;
		/*package*/ FileProvider(File file, boolean debugJS) {
			super(null, null);
			_parent = file.getParent();
		}
		InputStream getResourceAsStream(String path, boolean locate)
		throws IOException {
			if (isDebugJS()) {
				final int j = path.lastIndexOf('.');
				if (j >= 0)
					path = 	path.substring(0, j) + ".src" + path.substring(j);
			}
			final File file = new File(_parent, path);
			return locate ? new FileInputStream(Files.locate(file.getPath())):
				new FileInputStream(file);
		}
	}
}
