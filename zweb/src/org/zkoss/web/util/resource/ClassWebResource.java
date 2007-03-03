/* ClassWebResource.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 20 16:49:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.util.Map;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.Locators;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.Loader;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.dsp.Interpreter;
import org.zkoss.web.servlet.dsp.Interpretation;
import org.zkoss.web.servlet.dsp.ServletDSPContext;

/**
 * Used to access resouces located in class path and under /web.
 * It doesn't work alone. Rather, it is a helper for servlet, such as
 * {@link ClassWebServlet}.
 *
 * <p>Typical use:
 * <ol>
 * <li>Declares a member in the servlet to
 * serve the request for the resource located in class path.
 * <li>Invoke {@link #getInstance} to init the member when
 * Servlet.init() is called.
 * <li>Calling {@link #doGet} when a request is receive.
 * </ol>
 *
 * <p>Note: if a file is named with name-*.ext, it locates the proper file
 * based on the current Locale (org.zkoss.util.prefs.App.getCurrentLocale).
 *
 * @author tomyeh
 */
public class ClassWebResource {
	private static final Log log = Log.lookup(ClassWebResource.class);

	private final ServletContext _ctx;
	private final String _mappingURI;
	private final ClassWebContext _cwc;
	private final ResourceCache _dspCache;

	/** The prefix of path of web resources ("/web"). */
	public static final String PATH_PREFIX = "/web";

	/** Returns the URL of the resource of the specified URI by searching
	 * the class path (with {@link #PATH_PREFIX}).
	 */
	public static URL getResource(String uri) {
		return Locators.getDefault().getResource(PATH_PREFIX + uri);
	}
	/** Returns the resource in a stream of the specified URI by searching
	 * the class path (with {@link #PATH_PREFIX}).
	 */
	public static InputStream getResourceAsStream(String uri) {
		return Locators.getDefault().getResourceAsStream(PATH_PREFIX + uri);
	}
	/** Returns the instance (singlton in the whole app) for
	 * handling resources located in class path.
	 */
	public static final
	ClassWebResource getInstance(ServletContext ctx, String mappingURI) {
		synchronized (ctx) {
			final ClassWebContext cwc =
				(ClassWebContext)Servlets.getExtendedWebContext(ctx, ".");
			if (cwc != null)
				return cwc.getClassWebResource();

			final ClassWebResource cwr = new ClassWebResource(ctx, mappingURI);
			Servlets.addExtendedWebContext(ctx, ".", cwr._cwc);
			return cwr;
		}
	}
	/** Constructor. */
	private ClassWebResource(ServletContext ctx, String mappingURI) {
		if (!mappingURI.startsWith("/") || mappingURI.endsWith("/"))
			throw new IllegalArgumentException("mappingURI must start with /, but not ends with /");
		if (ctx == null)
			throw new IllegalArgumentException("null ctx");

		_ctx = ctx;
		_mappingURI = mappingURI;
		_cwc = new ClassWebContext();
		_dspCache = new ResourceCache(new DSPLoader(_cwc), 131);
		_dspCache.setMaxSize(1000).setLifetime(60*60*1000); //1hr
		_dspCache.setCheckPeriod(60*60*1000); //1hr
	}
	/** Process the request. */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final Object old = Charsets.setup(request, response, "UTF-8");
		try {
			final String pi = Https.getThisPathInfo(request);
			//if (D.ON && log.debugable()) log.debug("Path info: "+pi);
			if (pi != null)
				web(request, response, pi.substring(PATH_PREFIX.length()));
		} finally {
			Charsets.cleanup(request, old);
		}
	}
	//-- Work with ClassWebContext --//
	/** Works with {@link ClassWebContext} to
	 * load resources from class path (thru this servlet).
	 */
	private void web(HttpServletRequest request,
	HttpServletResponse response, String pi)
	throws ServletException, IOException {
		//A trick used to enforce browser to load new version JavaScript
		//How it work: client engine prefix URI with /_zver123, where
		//123 is the build version that changes once reload is required
		//Then, the server eliminate such prefix before locating resource
		final String ZVER = "/_zver";
		if (pi.startsWith(ZVER)) {
			final int j = pi.indexOf('/', ZVER.length());
			if (j >= 0) pi = pi.substring(j);
			else log.warning("Unknown path info: "+pi);
		}

		//Notify the browser by calling back the code specified with /_zcb
		String jsextra = null;
		final String ZCB = "/_zcb"; //denote callback is required
		if (pi.startsWith(ZCB)) {
			final int j = pi.indexOf('/', ZCB.length());
			if (j >= 0) {
				jsextra = pi.substring(ZCB.length(), j);
				pi = pi.substring(j);
			} else {
				jsextra = pi.substring(ZCB.length());
				log.warning("Unknown path info: "+pi);
			}

			final int len = jsextra.length();
			if (len == 0) jsextra = null;
			else {
				final char cc = jsextra.charAt(len - 1);
				if (cc != ';') {
					if (cc != ')') jsextra += "()";
					jsextra += ';';
				}
			}
		}

		final String ext = getExtension(pi);
		if (ext != null) {
			if ("dsp".equals(ext)) {
				final Interpretation cnt =
					(Interpretation)_dspCache.get(pi);
				if (cnt == null) {
					if (Servlets.isIncluded(request)) log.error("Resource not found: "+pi);
						//It might be eaten, so log the error
					response.sendError(response.SC_NOT_FOUND, pi+" not found.");
					return;
				}
				cnt.interpret(new ServletDSPContext(
					_ctx, request, response, _cwc.getLocator()));
				if (jsextra != null) response.getWriter().write(jsextra);
				return; //done
			}

			if (!Servlets.isIncluded(request)) {				
				final String ctype = ContentTypes.getContentType(ext);
				if (ctype != null)
					response.setContentType(ctype);
				if (D.ON && log.debugable()) log.debug("Content type: "+ctype+" for "+pi);
			}
		}

		pi = Servlets.locate(_ctx, request, pi, _cwc.getLocator());
		final InputStream is = getResourceAsStream(pi);
		final byte[] data;
		if (is == null) {
			if ("js".equals(ext) || "css".equals(ext)) {
				//1. Don't sendError for them. Otherwise FF won't invoke onload
				//2. always log because browser usually don't show error message for them
				log.warning("Resource not found: "+pi);
				data = new byte[0];
			} else {
				if (Servlets.isIncluded(request)) log.error("Resource not found: "+pi);
				response.sendError(response.SC_NOT_FOUND, pi+" not found.");
				return;
			}
		} else {
			data = Files.readAll(is);
			//since what is embedded in the jar is not big, so load at once
		}

		int len = data.length;
		final byte[] extra = jsextra != null ? jsextra.getBytes("UTF-8"): null;
		if (extra != null) len += extra.length;
		response.setContentLength(len);

		final ServletOutputStream out = response.getOutputStream();
		out.write(data);
		if (extra != null) out.write(extra);
		out.flush();
	}
	/** Returns the file extension of the specified path info. */
	private static final String getExtension(String pi) {
		final int j = pi.lastIndexOf('.');
		if (j < 0 || pi.indexOf('/', j + 1) >= 0)
			return null;
		final String ext = pi.substring(j + 1);
		final int k = ext.indexOf(';');
		return k >= 0 ? ext.substring(0, k).toLowerCase(): ext.toLowerCase();
	}
	private static class DSPLoader implements Loader {
		private final ClassWebContext _cwc;
		private DSPLoader(ClassWebContext cwc) {
			_cwc = cwc;
		}

		//-- super --//
		public boolean shallCheck(Object src, long expiredMillis) {
			return expiredMillis > 0;
		}
		/** Returns the last modified time.
		 */
		public long getLastModified(Object src) {
			return 1; //any value (because it is packed in jar)
		}
		public Object load(Object src) throws Exception {
			if (D.ON && log.debugable()) log.debug("Parse "+src);
			final String path = (String)src;
			final InputStream is = getResourceAsStream(path);
			if (is == null)
				return null;
			try {
				return parse0(is, Interpreter.getContentType(path));
			} catch (Exception ex) {
				if (log.debugable())
					log.realCauseBriefly("Failed to parse "+path, ex);
				else
					log.error("Failed to parse "+path
					+"\nCause: "+ex.getClass().getName()+" "+Exceptions.getMessage(ex)
					+"\n"+Exceptions.getBriefStackTrace(ex));
				return null; //as non-existent
			}
		}
		private Object parse0(InputStream is, String ctype) throws Exception {
			if (is == null) return null;

			final String content =
				Files.readAll(new InputStreamReader(is, "UTF-8"))
				.toString();
			return new Interpreter()
				.parse(content, ctype, null, _cwc.getLocator());
		}
	}

	/**
	 * An implementation of ExtendedWebContext to load resources from
	 * the class path rooted at /web.
	 */
	private class ClassWebContext implements ExtendedWebContext {
		/** Returns the locator for this {@link ClassWebContext}. */
		public Locator getLocator() {
			return new Locator() {
				public String getDirectory() {
					return null;
				}
				public URL getResource(String name) {
					return ClassWebResource.getResource(name);
				}
				public InputStream getResourceAsStream(String name) {
					return ClassWebResource.getResourceAsStream(name);
				}
			};
		}
	
		/** Returns the associated class web resource. */
		public ClassWebResource getClassWebResource() {
			return ClassWebResource.this;
		}

		//-- ExtendedWebContext --//
		public String encodeURL(ServletRequest request,
		ServletResponse response, String uri)
		throws ServletException, UnsupportedEncodingException {
			uri = Servlets.locate(_ctx, request, uri, getLocator()); //resolves "*"
			uri = _mappingURI + PATH_PREFIX + uri; //prefix with mapping

			//prefix context path
			if (request instanceof HttpServletRequest) {
				String ctxpath = ((HttpServletRequest)request).getContextPath();
				final int ctxlen = ctxpath.length();
				if (ctxlen > 0) {
					final char cc = ctxpath.charAt(0);
					if (cc != '/') ctxpath = '/' + ctxpath;
						//Work around a bug for Pluto's RenderRequest (1.0.1)
					else if (ctxlen == 1) ctxpath = ""; // "/" =>  ""
						//Work around liferay's issue: Upload 1627928 (not verified)
				}
				uri = ctxpath + uri;
			}

			int j = uri.indexOf('?');
			if (j < 0) {
				uri = Encodes.encodeURI(uri);
			} else {
				uri = Encodes.encodeURI(uri.substring(0, j))
					+ uri.substring(j);
			}
			//encode
			if (response instanceof HttpServletResponse)
				uri = ((HttpServletResponse)response).encodeURL(uri);
			return uri;
		}
		public String encodeRedirectURL(HttpServletRequest request,
		HttpServletResponse response, String uri, Map params, int mode) {
			return Https.encodeRedirectURL(_ctx, request, response,
				_mappingURI + PATH_PREFIX + uri, params, mode);
		}
		public RequestDispatcher getRequestDispatcher(String uri) {
			if (D.ON && log.debugable()) log.debug("getRequestDispatcher: "+uri);
			return _ctx.getRequestDispatcher(_mappingURI + PATH_PREFIX + uri);
		}
		public URL getResource(String uri) {
			return ClassWebResource.getResource(uri);
		}
		public InputStream getResourceAsStream(String uri) {
			return ClassWebResource.getResourceAsStream(uri);
		}
	}
}
