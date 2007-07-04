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
import java.util.HashMap;
import java.net.URL;
import java.io.InputStream;
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
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.Locators;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;

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
 * <li>Calling {@link #service} when a request is receive.
 * </ol>
 *
 * @author tomyeh
 */
public class ClassWebResource {
	private static final Log log = Log.lookup(ClassWebResource.class);

	private final ServletContext _ctx;
	private final String _mappingURI;
	private final ClassWebContext _cwc;
	/** An array of extensions that have to be compressed (with gzip). */
	private String[] _compressExts;
	/** Map(String ext, Resourcelet). */
	private final Map _reslets = new HashMap(5);

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
		addResourcelet("dsp", new DSPResourcelet());
	}
	/** Process the request.
	 * @since 2.4.1
	 */
	public void service(HttpServletRequest request,
	HttpServletResponse response)
	throws ServletException, IOException {
		final Object old = Charsets.setup(request, response, "UTF-8");
		try {
			final String pi = Https.getThisPathInfo(request);
//			if (D.ON && log.debugable()) log.debug("Path info: "+pi);
			if (pi != null)
				web(request, response, pi.substring(PATH_PREFIX.length()));
		} finally {
			Charsets.cleanup(request, old);
		}
	}

	/** Returns the resource processor of the specified extension, or null
	 * if not associated yet.
	 *
	 * @param ext the extension, e.g, "js" and "css".
	 * @return the resource processor, or null if not associated yet.
	 * @since 2.4.1
	 */
	public Resourcelet getResourcelet(String ext) {
		if (ext == null)
			return null;

		ext = ext.toLowerCase();
		synchronized (_reslets) {
			return (Resourcelet)_reslets.get(ext);
		}
	}
	/** Adds a resource processor ({@link Resourcelet}) to process
	 * the resource of the specified extension.
	 *
	 * @param ext the extension, e.g, "js" and "css".
	 * @param reslet the resouce processor
	 * @return the previous resource processor, or null if not associated
	 * before.
	 * @since 2.4.1
	 */
	public Resourcelet addResourcelet(String ext, Resourcelet reslet) {
		if (ext == null || reslet == null)
			throw new IllegalArgumentException("null");

		reslet.init(_cwc);

		ext = ext.toLowerCase();
		synchronized (_reslets) {
			return (Resourcelet)_reslets.put(ext, reslet);
		}
	}
	/** Removes the resource processor for the specified extension.
	 *
	 * @param ext the extension, e.g, "js" and "css".
	 * @return the previous resource processor, or null if no resource
	 * processor was associated with the specified extension.
	 * @since 2.4.1
	 */
	public Resourcelet removeResourcelet(String ext) {
		if (ext == null)
			return null;

		ext = ext.toLowerCase();
		synchronized (_reslets) {
			return (Resourcelet)_reslets.remove(ext);
		}
	}

	/** Sets the extension that shall be compressed if the browser
	 * supports the compression encoding (accept-encoding).
	 *
	 * <p>Default: null (no compression at all).
	 *
	 * @param exts an array of extensions, e.g., {"js", "css"}.
	 * If null or zero-length, it means no compression at all.
	 *@since 2.4.1
	 */
	public void setCompress(String[] exts) {
		_compressExts = exts != null && exts.length > 0 ? exts: null;
	}
	/**Returns  the extension that shall be compressed if the browser
	 * supports the compression encoding (accept-encoding).
	 *
	 * <p>Default: null (no compression at all).
	 *@since 2.4.1
	 */
	public String[] getCompress() {
		return _compressExts;
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

		final String ext = Servlets.getExtension(pi);
		if (ext != null) {
			//Invoke the resource processor (Resourcelet)
			final Resourcelet reslet = getResourcelet(ext);
			if (reslet != null) {
				reslet.service(request, response, pi, jsextra);
				return;
			}

			if (!Servlets.isIncluded(request)) {				
				final String ctype = ContentTypes.getContentType(ext);
				if (ctype != null)
					response.setContentType(ctype);
//				if (D.ON && log.debugable()) log.debug("Content type: "+ctype+" for "+pi);
			}
		}

		byte[] extra = jsextra != null ? jsextra.getBytes("UTF-8"): null;
		pi = Servlets.locate(_ctx, request, pi, _cwc.getLocator());
		final InputStream is = getResourceAsStream(pi);
		byte[] data;
		if (is == null) {
			if ("js".equals(ext)) {
				//Don't sendError. Reason: 1) IE waits and no onerror fired
				//2) better to debug (user will tell us what went wrong)
				data = ("(zk.error?zk.error:alert)('"+pi+" not found');").getBytes();
					//FUTURE: zweb shall not depend on zk
			} else {
				if (Servlets.isIncluded(request)) log.error("Resource not found: "+pi);
				response.sendError(response.SC_NOT_FOUND, pi);
				return;
			}
		} else {
			//Note: don't compress images
			data = shallCompress(request, ext) ?
				Https.gzip(request, response, is, extra): null;
			if (data != null) extra = null; //extra is compressed and output
			else data = Files.readAll(is);
				//since what is embedded in the jar is not big, so load completely
		}

		int len = data.length;
		if (extra != null) len += extra.length;
		response.setContentLength(len);

		final ServletOutputStream out = response.getOutputStream();
		out.write(data);
		if (extra != null) out.write(extra);
		out.flush();
	}
	private boolean shallCompress(ServletRequest request, String ext) {
		if (ext != null && _compressExts != null
		&& !Servlets.isIncluded(request))
			for (int j = 0; j < _compressExts.length; ++j)
				if (ext.equals(_compressExts[j]))
					return true;
		return false;
	}

	/**
	 * An implementation of ExtendedWebContext to load resources from
	 * the class path rooted at /web.
	 */
	private class ClassWebContext implements ExtendedWebContext {
		private final Locator _locator = new Locator() {
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

		/** Returns the associated class web resource. */
		public ClassWebResource getClassWebResource() {
			return ClassWebResource.this;
		}

		//-- ExtendedWebContext --//
		public ServletContext getServletContext() {
			return _ctx;
		}
		public Locator getLocator() {
			return _locator;
		}
		public boolean shallCompress(ServletRequest request, String ext) {
			return ClassWebResource.this.shallCompress(request, ext);
		}
	
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
//			if (D.ON && log.debugable()) log.debug("getRequestDispatcher: "+uri);
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
