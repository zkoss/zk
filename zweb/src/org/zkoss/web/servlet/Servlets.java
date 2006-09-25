/* Servlets.java

{{IS_NOTE
	Purpose:
	Description:
	History:
	90/12/10 22:24:28, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.D;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Checksums;
import org.zkoss.util.CacheMap;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.Locators;
import org.zkoss.util.resource.PropertyBundle;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.math.Calcs;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ExtendedWebContext;
import org.zkoss.web.util.resource.ServletContextLocator;

/**
 * The servlet relevant utilities.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @see org.zkoss.web.servlet.http.Https
 * @see org.zkoss.web.servlet.Charsets
 */
public class Servlets {
	private static final Log log = Log.lookup(Servlets.class);

	/** Whether EL is supported. */
	private static Boolean _elSupported = null;
	/** Utilities; no instantiation required. */
	protected Servlets() {}

	/**
	 * Gets a set of the parameters of a request.
	 * Only parameters listed in the paramNames argument are returned.
	 *
	 * <p>The value of the result map is String[] instances.
	 * In other word, ServletRequest.getParameterValues is used.
	 *
	 * @param request the request to get the parameters
	 * @param paramNames the parameter's names to get
	 * @return the map; never null
	 * @exception NullPointerException if or request and paramNames is null
	 */
	public static final Map
	getParameters(ServletRequest request, String[] paramNames) {
		final Map map = new HashMap();
		for (int j = 0; j < paramNames.length; ++j) {
			String[] vals = request.getParameterValues(paramNames[j]);
			if (vals != null && vals.length > 0)
				map.put(paramNames[j],
					vals.length == 1 ? (Object)vals[0]: (Object)vals);
		}
		return map;
	}

	/** Returns whether a URL starts with xxx://, mailto:, about:,
	 * javascript:
	 */
	public static final boolean isUniversalURL(String uri) {
		if (uri == null || uri.length() == 0) return false;

		final char cc = uri.charAt(0);
		return cc >= 'a' && cc <= 'z'
			&& (uri.indexOf("://") > 0 || uri.startsWith("mailto:")
			|| uri.startsWith("javascript:") || uri.startsWith("about:"));
	}

	//-- resource locator --//
	/** Locates a page based on the specified Locale. It never returns null.
	 *
	 * <p>If an URI contains "*", it will be replaced with a proper Locale.
	 * For example, if the current Locale is zh_TW and the resource is
	 * named "ab*.cd", then it searches "ab_zh_TW.cd", "ab_zh.cd" and
	 * then "ab.cd", until any of them is found.
	 *
	 * <blockquote>Note: "*" must be right before ".", or the last character.
	 * For example, "ab*.cd" and "ab*" are both correct, while
	 * "ab*cd" and "ab*\/cd" are ignored.</blockquote>
	 *
	 * <p>If an URI contains two "*", the first "*" will be replaced with
	 * a browser code and the second with a proper locale.
	 * The browser code depends on what browser
	 * the user are used to visit the web site.
	 * Currently, the code for Internet Explorer is "ie", Safari is "saf",
	 * and all others are "moz".
	 * Thus, in the above example, if the resource is named "ab**.cd"
	 * and Firefox is used, then it searches "abmoz_zh_TW.cd", "abmoz_zh.cd"
	 * and then "abmoz.cd", until any of them is found.
	 *
	 * <p>Note: it assumes the path as name_lang_cn_var.ext where
	 * ".ext" is optional. Example, my_zh_tw.html.jsp.
	 *
	 * <p>Note: unlike {@link Encodes#encodeURL(ServletContext, ServletRequest, ServletResponse, String)},
	 * it always locates the Locale, without handling "*".
	 *
	 * @param ctx the servlet context to locate pages
	 * @param pgpath the page path excluding servlet name. It is OK to have
	 * the query string. It might contain "*" for current browser code and Locale.
	 * @param locator the locator used to locate resource. If null, ctx
	 * is assumed.
	 * @return pgpath if the original one matches; others if locale matches;
	 * never null
	 */
	public static final String locate(ServletContext ctx,
	ServletRequest request, String pgpath, Locator locator)
	throws ServletException {
		if (isUniversalURL(pgpath))
			return pgpath;

		final int jquest = pgpath.indexOf('?');
		final int f = pgpath.indexOf('*');
		if (f < 0 || (jquest >= 0 && f > jquest)) return pgpath;
			//optimize the case that no "*" at all

		final String qstr;
		if (jquest >= 0) {
			qstr = pgpath.substring(jquest);
			pgpath = pgpath.substring(0, jquest);
		} else {
			qstr = null;
		}

		//by browser?
		int l = pgpath.lastIndexOf('*');
		if (l > f) { //two '*'
			final String bc = Servlets.isExplorer(request) ? "ie":
				Servlets.isSafari(request) ? "saf": "moz";
			l += bc.length() - 1;
			pgpath = pgpath.substring(0, f) + bc + pgpath.substring(f + 1);
		}

		//by locale?
		if (l < pgpath.length() - 1
		&& (pgpath.charAt(l + 1) != '.' || pgpath.indexOf('/', l + 1) >= 0))
			return qstr != null ? pgpath + qstr: pgpath; //not by locale

		pgpath = pgpath.substring(0, l) + pgpath.substring(l + 1); //remove "*"

		final String PGPATH_CACHE = "s_pgpath_cache";
		Map map = (Map)ctx.getAttribute(PGPATH_CACHE);
		if (map == null) {
			map = Collections.synchronizedMap( //7 min
				new CacheMap().setMaxSize(128).setLifetime(7*60*1000));
			ctx.setAttribute(PGPATH_CACHE, map);
		}

		final Locale locale = Locales.getCurrent();
		final URIIndex index = new URIIndex(pgpath, locale);

		String uri = (String)map.get(index);
		if (uri == null) {
			final Locators.URLLocation loc =
				Locators.locate(pgpath, locale,
					locator != null ? locator: new ServletContextLocator(ctx));
			uri = loc != null ? loc.file: pgpath;
			map.put(index, uri);
		}

		return qstr != null ? uri + qstr: uri;
	}
	private static class URIIndex {
		private final String _uri;
		private final Locale _locale;
		private URIIndex(String uri, Locale locale) {
			if (uri == null || locale == null)
				throw new IllegalArgumentException("null");
			_uri = uri;
			_locale = locale;
		}
		public int hashCode() {
			return _uri.hashCode();
		}
		public boolean equals(Object o) {
			//To speed up, don't check whether o is the right class
			final URIIndex idx = (URIIndex)o;
			return _uri.equals(idx._uri) && _locale.equals(idx._locale);
		}
	}
	/** Locates a page based on the current Locale.
	 *
	 * @param ctx the page context to locate pages
	 * @param pgpath the page path excluding servlet name
	 * @return pgpath if the original one matches; others if appending protocol
	 * and/or locale matches; never null
	 */
	public static final
	String locate(PageContext ctx, String pgpath)
	throws ServletException {
		return locate(ctx.getServletContext(), ctx.getRequest(),
			pgpath, null);
	}

	/** Returns whether the client is a robot (such as Web crawlers).
	 *
	 * <p>Because there are too many robots, it returns true if the user-agent
	 * is not recognized.
	 */
	public static final boolean isRobot(ServletRequest req) {
		String agt = req instanceof HttpServletRequest ?
			((HttpServletRequest)req).getHeader("user-agent"): null;
		if (agt == null)
			return false;

		agt = agt.toLowerCase();
		return agt.indexOf("msie") < 0 && agt.indexOf("opera") < 0
			&& agt.indexOf("gecko/") < 0 && agt.indexOf("safari") < 0;
	}
	/** Returns whether the browser is Internet Explorer.
	 * If true, it also implies {@link #isExplorer7} is true.
	 */
	public static final boolean isExplorer(ServletRequest req) {
		String agt = req instanceof HttpServletRequest ?
			((HttpServletRequest)req).getHeader("user-agent"): null;
		if (agt == null)
			return false;

		agt = agt.toLowerCase();
		return agt.indexOf("msie") >= 0 && agt.indexOf("opera") < 0;
	}
	/** Returns whether the browser is Explorer 7 or later.
	 */
	public static final boolean isExplorer7(ServletRequest req) {
		String agt = req instanceof HttpServletRequest ?
			((HttpServletRequest)req).getHeader("user-agent"): null;
		if (agt == null)
			return false;

		agt = agt.toLowerCase();
		return agt.indexOf("msie 7") >= 0;
	}
	/** Returns whether the browser is Gecko based, such as Mozilla, Firefox and Camino
	 */
	public static final boolean isGecko(ServletRequest req) {
		String agt = req instanceof HttpServletRequest ?
			((HttpServletRequest)req).getHeader("user-agent"): null;
		if (agt == null)
			return false;

		agt = agt.toLowerCase();
		return agt.indexOf("gecko/") >= 0 && agt.indexOf("safari") < 0;
	}
	/** Returns whether the browser is Safari.
	 */
	public static final boolean isSafari(ServletRequest req) {
		String agt = req instanceof HttpServletRequest ?
			((HttpServletRequest)req).getHeader("user-agent"): null;
		if (agt == null)
			return false;

		agt = agt.toLowerCase();
		return agt.indexOf("safari") >= 0;
	}

	//-- interface to access profile --//
	/** The profile's filename, i.e., "/web.profile". */
	public static final String PROFILE_FILENAME = "/web.profile";

	/** Gets the profile value of the given key in the given file.
	 *
	 * <p>This implementation caches the result, so the performance shall
	 * be good enough.
	 */
	public static final String
	getProfile(ServletContext ctx, String flnm, String key, String defVal)
	throws IOException {
		final String PROFILE_CACHE = "s_profile_cache";
		Map map = (Map)ctx.getAttribute(PROFILE_CACHE);
		if (map == null) {
			map = Collections.synchronizedMap(new CacheMap().setMaxSize(16));
			ctx.setAttribute(PROFILE_CACHE, map);
		}

		Properties props = (Properties)map.get(flnm);
		if (props == null) {
			final InputStream is = ctx.getResourceAsStream(flnm);
			if (is == null) {
				if (!flnm.startsWith("/"))
					throw new IOException("The filename must starts with '/': "+flnm);
				if (D.ON)
					log.debug(MCommon.FILE_NOT_FOUND, flnm);//not bother user
				return defVal;
			}

			props = new Properties();
			props.load(is);
			map.put(flnm, props);
		}
		return props.getProperty(key, defVal);
	}
	/** Returns the profile value in integer.
	 */
	public static final int
	getProfile(ServletContext ctx, String flnm, String key, int defVal)
	throws IOException {
		String val = getProfile(ctx, flnm, key, null);
		return val != null ? Calcs.intValueOf(val): defVal;
	}

	/** Handles the exception and generates three variables stored
	 * int the request's attribute: zk_class, zk_message, zk_throwable and zk_stack_trace.
	 *
	 * <p>It is designed to be used with the jsp pages that handle exceptions.
	 */
	public static void handle(ServletRequest request,
	ServletResponse response, Throwable ex) {
		//Jetty doesn't assign exception if NullPointerException is found
		if(ex == null)
			ex = (Throwable)
				request.getAttribute(Attributes.ERROR_EXCEPTION);

		String exClass = "SEVERE ERROR: unable to locate the exception";
		String exMsg = "", exStackTrace = "";
		Throwable exCause = null;
		if(ex != null){
			//save exMsg, the exception message
			exMsg = Exceptions.getMessage(ex);

			//save exClass, the exception class
			exClass = ex.getClass().getName();

			//prepare exCause, the real cause
			exCause = Exceptions.getRealCause(ex);

			//generate stack trace
			StringWriter sw = new StringWriter(4096);
			exCause.printStackTrace(new PrintWriter(sw));
			exStackTrace = sw.toString();
		}
		request.setAttribute("zk_class", exClass);
		request.setAttribute("zk_throwable", exCause);
		request.setAttribute("zk_message", exMsg);
		request.setAttribute("zk_stack_trace", exStackTrace);
	}

	/**
	 * Tests whether this page is included by another page.
	 */
	public static final boolean isIncluded(ServletRequest request) {
		return request.getAttribute(Attributes.INCLUDE_CONTEXT_PATH) != null;
	}
	/**
	 * Tests whether this page is forwarded by another page.
	 */
	public static final boolean isForwarded(ServletRequest request) {
		return request.getAttribute(Attributes.FORWARD_CONTEXT_PATH) != null;
	}
	/**
	 * Forward to the specified URI.
	 * It enhances RequestDispatcher in the following ways.
	 *
	 * <ul>
	 *  <li>It handles "~ctx"" where ctx is the context path of the
	 *  foreign context. It is called foriegn URI.</li>
	 *  <li>It detects whether the page calling this method
	 * is included by another servlet/page. If so, it uses
	 * RequestDispatcher.include() instead of RequestDispatcher.forward().</li>
	 *  <li>The forwarded page could accept additional parameters --
	 * acutually converting parameters to a query string
	 * and appending it to uri.</li>
	 * <li>In additions, it does HTTP encoding, i.e., converts '+' and other
	 * characters to comply HTTP.</li>
	 * </ul>
	 *
	 * <p>NOTE: don't include query parameters in uri.
	 *
	 * @param ctx the servlet context. If null, uri cannot be foreign URI.
	 * It is ignored if URI is relevant (neither starts with '/' nor '~').
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * If it could be any context path recognized by the Web container or
	 * any name registered with {@link #addExtendedWebContext}.
	 * @param params the parameter map; null to ignore
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final
	void forward(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws IOException, ServletException {
		if (D.ON && log.debugable()) log.debug("Forwarding "+uri);

		//include or foward depending whether this page is included or not
		if (isIncluded(request)) {
			include(ctx, request, response, uri, params, mode);
			return;
		}

		final RequestDispatcher disp =
			getRequestDispatcher(ctx, request, uri, params, mode);
		if (disp == null)
			throw new ServletException("No dispatcher available to forward to "+uri);

		if (mode == PASS_THRU_ATTR && params != null && !params.isEmpty()) {
			final Map old = setPassThruAttr(request, params);
			try {
				disp.forward(request, response);
			} catch (ClassCastException ex) {
				//Tom M. Yeh, 2006/09/21: Bug 1548478
				//Cause: http://issues.apache.org/bugzilla/show_bug.cgi?id=39417
				//
				//Bug or limitation of Catalina: not accepting HttpServletRequest
				//othere than the original one or wrapper of original one
				//
				//Real Cause: org.apache.catalina.core.ApplicationDispatcher
				//call unwrapRequest() twice, and then causes ClassCastException
				//
				//Resolution: since it is the almost last statement, it is safe
				//to ignore this exception
				if (!(request instanceof org.zkoss.web.portlet.RenderHttpServletRequest))
					throw ex; //not the case described above
			} finally {
				restorePassThruAttr(request, old);
			}
		} else {
			disp.forward(request, response);
		}
	}
	/** A shortcut of forward(request, response, uri, null, 0).
	 */
	public static final 
	void forward(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri) throws IOException, ServletException {
		forward(ctx, request, response, uri, null, 0);
	}
	/**
	 * Includes the resource at the specified URI.
	 * It enhances RequestDispatcher to allow the inclusion with
	 * a parameter map -- acutually converting parameters to a query string
	 * and appending it to uri.
	 *
	 * <p>NOTE: don't include query parameters in uri.
	 *
	 * @param ctx the servlet context. If null, uri cannot be foreign URI.
	 * It is ignored if URI is relevant (neither starts with '/' nor '~').
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params the parameter map; null to ignore
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final
	void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws IOException, ServletException {
		if (D.ON && log.debugable()) log.debug("Including "+uri+" at "+ctx);

		//20050606: Tom Yeh
		//We have to set this special attribute for jetty
		//Otherwise, if including a page crossing context might not return
		//the same session
		request.setAttribute("org.mortbay.jetty.servlet.Dispatcher.shared_session", Boolean.TRUE);

		final RequestDispatcher disp =
			getRequestDispatcher(ctx, request, uri, params, mode);
		if (disp == null)
			throw new ServletException("No dispatcher available to include "+uri);

		if (mode == PASS_THRU_ATTR && params != null && !params.isEmpty()) {
			final Map old = setPassThruAttr(request, params);
			try {
				disp.include(request, response);
			} finally {
				restorePassThruAttr(request, old);
			}
		} else {
			disp.include(request, response);
		}
	}
	/** A shortcut of include(request, response, uri, null, 0).
	 */
	public static final 
	void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri) throws IOException, ServletException {
		include(ctx, request, response, uri, null, 0);
	}
	/** Sets the arg attribute to pass parameters thru request's attribute.
	 */
	private static final
	Map setPassThruAttr(ServletRequest request, Map params) {
		final Map old = (Map)request.getAttribute(Attributes.ARG);
		request.setAttribute(Attributes.ARG, params);
		return old;
	}
	/** Restores what has been done by {@link #setPassThruAttr}.
	 */
	private static final
	void restorePassThruAttr(ServletRequest request, Map old) {
		if (old != null)
			request.setAttribute(Attributes.ARG, old);
		else
			request.removeAttribute(Attributes.ARG);
	}
	/** Returns the request dispatch of the specified URI.
	 *
	 * @param ctx the servlet context. If null, uri cannot be foreign URI.
	 * It is ignored if uri is relevant (neither starts with '/' nor '~').
	 * @param request the request. If null, uri cannot be relevant.
	 * It is used only if uri is relevant.
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params the parameter map; null to ignore
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final RequestDispatcher
	getRequestDispatcher(ServletContext ctx, ServletRequest request,
	String uri, Map params, int mode)
	throws ServletException {
		final char cc = uri.length() > 0 ? uri.charAt(0): (char)0;
		if (ctx == null || (cc != '/' && cc != '~')) {//... or relevant
			if (request == null)
				throw new IllegalArgumentException(
					ctx == null ?
						"Servlet context and request cannot be both null":
						"Request is required to use revalant URI: "+uri);
			if (cc == '~')
				throw new IllegalArgumentException("Servlet context is required to use foreign URI: "+uri);
			uri = generateURI(uri, params, mode);
			return request.getRequestDispatcher(uri);
		}

		//NO NEED to encodeURL since it is forward/include
		return new ParsedURI(ctx, uri).getRequestDispatcher(params, mode);
	}
	/** Returns the resource of the specified uri.
	 * Unlike ServletContext.getResource, it handles "~" like
	 * {@link #getRequestDispatcher} did.
	 */
	public static final URL getResource(ServletContext ctx, String uri)
	throws MalformedURLException {
		return new ParsedURI(ctx, uri).getResource();
	}
	/** Returns the resource stream of the specified uri.
	 * Unlike ServletContext.getResource, it handles "~" like
	 * {@link #getRequestDispatcher} did.
	 */
	public static final InputStream getResourceAsStream(
	ServletContext ctx, String uri) {
		return new ParsedURI(ctx, uri).getResourceAsStream();
	}
	/** Used to resolve "~" in URI. */
	private static class ParsedURI {
		private ServletContext _svlctx;
		private ExtendedWebContext _extctx;
		private String _uri;

		private ParsedURI(final ServletContext ctx, final String uri) {
			if (uri != null && uri.startsWith("~")) { //refer to foreign context
				final int j = uri.indexOf('/', 1);
				final String ctxroot;
				if (j >= 0) {
					ctxroot = "/" + uri.substring(1, j);
					_uri = uri.substring(j);
				} else {
					ctxroot = "/" + uri.substring(1);
					_uri = "/";
				}

				_extctx = getExtendedWebContext(ctx, ctxroot.substring(1));
				if (_extctx == null) {
					_svlctx = ctx.getContext(ctxroot);
					if (_svlctx == null) 
						throw new SystemException("Context not found or not visible to "+ctx+": "+ctxroot);
				}
			} else {
				_svlctx = ctx;
				_uri = uri;
			}
		}
		private RequestDispatcher getRequestDispatcher(Map params, int mode) {
			if (_extctx == null && _svlctx == null) //not found
				return null;

			final String uri = generateURI(_uri, params, mode);
			return _svlctx != null ? _svlctx.getRequestDispatcher(uri):
				_extctx.getRequestDispatcher(uri);
		}
		private URL getResource() throws MalformedURLException {
			return _svlctx != null ? _svlctx.getResource(_uri):
				_extctx != null ? _extctx.getResource(_uri): null;
		}
		private InputStream getResourceAsStream() {
			return _svlctx != null ? _svlctx.getResourceAsStream(_uri):
				_extctx != null ? _extctx.getResourceAsStream(_uri): null;
		}
	}

	/** Whether to overwrite uri if both uri and params contain the same
	 * parameter.
	 * Used by {@link #generateURI}
	 */
	public static final int OVERWRITE_URI = 0;
	/** Whether to ignore params if both uri and params contain the same
	 * parameter.
	 * Used by {@link #generateURI}
	 */
	public static final int IGNORE_PARAM = 1;
	/** Whether to append params if both uri and params contain the same
	 * parameter. In other words, they both appear as the final query string.
	 * Used by {@link #generateURI}
	 */
	public static final int APPEND_PARAM = 2;
	/** Whether the specified parameters shall be passed thru the request
	 * attribute called arg.
	 */
	public static final int PASS_THRU_ATTR = 3;
	/** Generates URI by appending the parameters.
	 * Note: it doesn't support the ~xxx/ format.
	 *
	 * @param params the parameters to apend to the query string
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * {@link #APPEND_PARAM} and {@link #PASS_THRU_ATTR}.
	 * It defines how to handle if both uri and params contains the same
	 * parameter.
	 * mode is used only if both uri contains query string and params is
	 * not empty.
	 * @see Encodes#encodeURL(ServletContext, ServletRequest, ServletResponse, String)
	 */
	public static final String generateURI(String uri, Map params, int mode) {
		if (uri.startsWith("~"))
			throw new IllegalArgumentException("~ctx not supported here: "+uri);

		final int j = uri.indexOf('?');
		String qstr = null;
		if (j >= 0) {
			qstr = uri.substring(j);
			uri = uri.substring(0, j);
		}

		//if (D.ON && uri.indexOf('%') >= 0)
		//	log.warning(new IllegalStateException("You might encode URL twice: "+uri));
		//might too annoying

		try {
			uri = Encodes.encodeURI(uri);
			final boolean noQstr = qstr == null;
			final boolean noParams =
				mode == PASS_THRU_ATTR || params == null || params.isEmpty();
			if (noQstr && noParams)
				return uri;

			if (noQstr != noParams)
				mode = APPEND_PARAM;

			final StringBuffer sb = new StringBuffer(80).append(uri);
			if (qstr != null) sb.append(qstr);

			switch (mode) {
			case IGNORE_PARAM:
				//removing params that is conflict
				for (final Iterator it = params.entrySet().iterator();
				it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					final String nm = (String)me.getKey();
					if (Encodes.containsQuery(qstr, nm))
						it.remove();
				}
				//flow thru
			case OVERWRITE_URI:
				return Encodes.setToQueryString(sb, params).toString();
			case APPEND_PARAM:
				return Encodes.addToQueryString(sb, params).toString();
			default:
				throw new IllegalArgumentException("Unknown mode: "+mode);
			}
		} catch (UnsupportedEncodingException ex) {
			throw new SystemException(ex);
		}
	}

	/** A list of context root paths (e.g., "/abc"). */
	private static List _ctxroots;
	/** Returns a list of context paths (e.g., "/abc") that this application
	 * has. This implementation parse application.xml. For war that doesn't
	 * contain application.xml might have to override this method and
	 * parse another file to know what context being loaded.
	 */
	public static final List getContextPaths() {
		if (_ctxroots != null)
			return _ctxroots;

		try {
			synchronized (Servlets.class) {
				return _ctxroots = myGetContextPaths();
			}
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
	private static final List myGetContextPaths() throws Exception {
		final String APP_XML = "/META-INF/application.xml";
		final List ctxroots = new LinkedList();
		final URL xmlURL = Locators.getDefault().getResource(APP_XML);
		if (xmlURL == null)
			throw new SystemException("File not found: "+APP_XML);

		if (log.debugable()) log.debug("Parsing "+APP_XML);
		final Element root =
			new SAXBuilder(false,false,true).build(xmlURL).getRootElement();

		for (Iterator it = root.getElements("module").iterator();
		it.hasNext();) {
			final Element e = (Element)it.next();
			final String ctxroot = (String)e.getContent("web/context-root");
			if (ctxroot == null) {
				//if (D.ON && log.finerable()) log.finer("Skip non-web: "+e.getContent("java"));
				continue;
			}

			ctxroots.add(ctxroot.startsWith("/") ? ctxroot: "/" + ctxroot);
		}

		log.info("Context found: "+ctxroots);
		return new ArrayList(ctxroots);
	}

	/** Returns a token to represent a limit-time offer.
	 * It is mainly used as an parameter value (mostlycalled zk_lto), and then
	 * you could verify whether it is expired by {@link #isOfferExpired}.
	 */
	public static final String getLimitTimeOffer() {
		final String lto = Long.toHexString(System.currentTimeMillis());
		return lto + Checksums.getChecksum(lto, "");
	}
	/** Returns whether a token returned by getLimitTimeOffer expired.
	 * @param timeout how long the office shall expire, unit: seconds.
	 */
	public static final boolean isOfferExpired(String lto, int timeout) {
		final int len = lto != null ? lto.length(): 0;
		if (len <= 1)
			return true;

		final char cksm = lto.charAt(len - 1);
		lto = lto.substring(0, len - 1);
		if (cksm != Checksums.getChecksum(lto, ""))
			return true;

		try {
			return Long.parseLong(lto, 16) + timeout*1000L
				< System.currentTimeMillis();
		} catch (NumberFormatException ex) {
			return true;
		}
	}

	/** Adds an extended context.
	 * @return the previous extended context, if any, associated with
	 * the specified name.
	 */
	public static final
	ExtendedWebContext addExtendedWebContext(ServletContext ctx,
	String name, ExtendedWebContext extctx) {
		if (name == null || extctx == null)
			throw new IllegalArgumentException("null");
		return (ExtendedWebContext)getExtWebCtxs(ctx).put(name, extctx);
	}
	/** Removes an extended context of the specified name.
	 */
	public static final
	ExtendedWebContext removeExtendedWebContext(ServletContext ctx, String name) {
		return (ExtendedWebContext)getExtWebCtxs(ctx).remove(name);
	}
	/** Returns the extended context of the specified name.
	 */
	public static final
	ExtendedWebContext getExtendedWebContext(ServletContext ctx, String name) {
		return (ExtendedWebContext)getExtWebCtxs(ctx).get(name);
	}
	private static final Map getExtWebCtxs(ServletContext ctx) {
		synchronized (Servlets.class) { //don't use ctx because it might be a proxy (in portlet)
			final String attr = "javax.zkoss.web.servlets.ExtendedWebContexts";
				//such that it could be shared among portlets
			Map ctxs = (Map)ctx.getAttribute(attr);
			if (ctxs == null)
				ctx.setAttribute(attr, ctxs = Collections.synchronizedMap(new HashMap(2)));
			return ctxs;
		}
	}

	/** Returns whether EL is supported by the servlet container.
	 */
	public static final boolean isELSupported() {
		if (_elSupported == null) {
			try {
				if (javax.servlet.jsp.el.ExpressionEvaluator.class.getName() != null)
					_elSupported = Boolean.TRUE;
			} catch (Throwable ex) {
				_elSupported = Boolean.FALSE;
			}
		}
		return _elSupported.booleanValue();
	}
}
