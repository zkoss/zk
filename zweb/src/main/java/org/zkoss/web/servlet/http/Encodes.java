/* Encodes.java

	Purpose:

	Description:

	History:
		Fri Jun 21 14:13:50  2002, Created by tomyeh

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.http;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ExtendletContext;

/**
 * Encoding utilities for servlets.
 *
 * @author tomyeh
 * @see Https
 */
public class Encodes {
	private static final Logger log = LoggerFactory.getLogger(Encodes.class);

	protected Encodes() {
	} //prevent from instantiation

	/** Encodes a string to HTTP URI compliance by use of
	 * {@link Charsets#getURICharset}.
	 *
	 * <p>Besides two-byte characters, it also encodes any character found
	 * in unsafes.
	 *
	 * @param unsafes the set of characters that must be encoded; never null.
	 * It must be sorted.
	 */
	private static final String encodeURI0(String s, char[] unsafes) throws UnsupportedEncodingException {
		if (s == null)
			return null;

		final String charset = Charsets.getURICharset();
		final byte[] in = s.getBytes(charset);
		final byte[] out = new byte[in.length * 3]; //at most: %xx
		int j = 0, k = 0;
		for (; j < in.length; ++j) {
			//Though it is ok to use '+' for ' ', Jetty has problem to
			//handle space between Chinese characters.
			final char cc = (char) (((int) in[j]) & 0xff);
			if (cc >= 0x80 || cc <= ' ' || Arrays.binarySearch(unsafes, cc) >= 0) {
				out[k++] = (byte) '%';
				String cvt = Integer.toHexString(cc);
				if (cvt.length() == 1) {
					out[k++] = (byte) '0';
					out[k++] = (byte) cvt.charAt(0);
				} else {
					out[k++] = (byte) cvt.charAt(0);
					out[k++] = (byte) cvt.charAt(1);
				}
			} else {
				out[k++] = in[j];
			}
		}
		return j == k ? s : new String(out, 0, k, charset);
	}

	/** unsafe character when that are used in url's location. */
	private static final char[] URI_UNSAFE;
	/** unsafe character when that are used in url's query. */
	private static final char[] URI_COMP_UNSAFE;

	static {
		URI_UNSAFE = "`%^{}[]\\\"<>|".toCharArray();
		Arrays.sort(URI_UNSAFE);

		URI_COMP_UNSAFE = "`%^{}[]\\\"<>|$&,/:;=?#".toCharArray();
		Arrays.sort(URI_COMP_UNSAFE);
	}

	/** Does the HTTP encoding for the URI location.
	 * For example, '%' is translated to '%25'.
	 *
	 * <p>Since {@link #encodeURL(ServletContext, ServletRequest, ServletResponse, String)}
	 * will invoke this method automatically, you rarely need this method.
	 *
	 * @param s the string to encode; null is OK
	 * @return the encoded string or null if s is null
	 * @see #encodeURIComponent
	 */
	public static final String encodeURI(String s) throws UnsupportedEncodingException {
		return encodeURI0(s, URI_UNSAFE);
	}

	/** Does the HTTP encoding for an URI query parameter.
	 * For example, '/' is translated to '%2F'.
	 * Both name and value must be encoded separately. Example,
	 * <code>encodeURIComponent(name) + '=' + encodeURIComponent(value)</code>.
	 *
	 * <p>Since {@link #encodeURL(ServletContext, ServletRequest, ServletResponse, String)}
	 * will <i>not</i> invoke this method automatically, you'd better
	 * to encode each query parameter by this method or
	 * {@link #addToQueryString(StringBuffer,Map)}.
	 *
	 * @param s the string to encode; null is OK
	 * @return the encoded string or null if s is null
	 * @see #addToQueryString(StringBuffer,String,Object)
	 * @see #encodeURI
	 */
	public static final String encodeURIComponent(String s) throws UnsupportedEncodingException {
		return encodeURI0(s, URI_COMP_UNSAFE);
	}

	/**
	/** Appends a map of parameters (name=value) to a query string.
	 * It returns the query string preceding with '?' if any parameter exists,
	 * or an empty string if no parameter at all. Thus, you could do
	 *
	 * <p><code>request.getRequestDispatcher(<br>
	 * addToQueryStirng(new StringBuffer(uri), params).toString());</code>
	 *
	 * <p>Since RequestDispatcher.include and forward do not allow wrapping
	 * the request and response -- see spec and the implementation of both
	 * Jetty and Catalina, we have to use this method to pass the parameters.
	 *
	 * @param params a map of parameters; format: (String, Object) or
	 * (String, Object[]); null is OK
	 */
	public static final StringBuffer addToQueryString(StringBuffer sb, Map params) throws UnsupportedEncodingException {
		if (params != null) {
			for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry) it.next();
				addToQueryString(sb, (String) me.getKey(), me.getValue());
			}
		}
		return sb;
	}

	/** Appends a parameter (name=value) to a query string.
	 * This method automatically detects whether other query is already
	 * appended. If so, &amp; is used instead of ?.
	 *
	 * <p>The query string might contain servlet path and other parts.
	 * This method starts the searching from the first '?'.
	 * If the query string doesn't contain '?', it is assumed to be a string
	 * without query's name/value pairs.
	 *
	 * @param value the value. If it is null, only name is appended.
	 * If it is an array of objects, multiple pairs of name=value[j] will
	 * be appended.
	 */
	public static final StringBuffer addToQueryString(StringBuffer sb, String name, Object value)
			throws UnsupportedEncodingException {
		if (value instanceof Object[]) {
			final Object[] vals = (Object[]) value;
			if (vals.length == 0) {
				value = null; //only append name
			} else {
				for (int j = 0; j < vals.length; ++j)
					addToQueryString(sb, name, vals[j]);
				return sb; //done
			}
		}

		sb.append(next(sb, '?', 0) >= sb.length() ? '?' : '&');
		sb.append(encodeURIComponent(name)).append('=');
		//NOTE: jetty with jboss3.0.6 ignore parameters without '=',
		//so we always append '=' even value is null
		if (value != null)
			sb.append(encodeURIComponent(Objects.toString(value)));

		return sb;
	}

	/** Returns the first occurrence starting from j, or sb.length().
	 */
	private static final int next(StringBuffer sb, char cc, int j) {
		for (final int len = sb.length(); j < len; ++j)
			if (sb.charAt(j) == cc)
				break;
		return j;
	}

	/**
	 * Sets the parameter (name=value) to a query string.
	 * If the name already exists in the query string, it will be removed first.
	 * If your name has appeared in the string, it will replace
	 * your new value to the query string.
	 * Otherwise, it will append the name/value to the new string.
	 *
	 * <p>The query string might contain servlet path and other parts.
	 * This method starts the searching from the first '?'.
	 * If the query string doesn't contain '?', it is assumed to be a string
	 * without query's name/value pairs.
	 *
	 * @param str The query string like xxx?xxx=xxx&amp;xxx=xxx or null.
	 * @param name The get parameter name.
	 * @param value The value associated with the get parameter name.
	 * @return The new or result query string with your name/value.
	 * @see #addToQueryString
	 */
	public static final String setToQueryString(String str, String name, Object value)
			throws UnsupportedEncodingException {
		final StringBuffer sb = new StringBuffer();
		if (str != null)
			sb.append(str);
		return setToQueryString(sb, name, value).toString();
	}

	/**
	 * Sets the parameter (name=value) to a query string.
	 * If the name already exists in the query string, it will be
	 * removed first.
	 * @see #addToQueryString
	 */
	public static final StringBuffer setToQueryString(StringBuffer sb, String name, Object value)
			throws UnsupportedEncodingException {
		removeFromQueryString(sb, name);
		return addToQueryString(sb, name, value);
	}

	/**
	 * Sets a map of parameters (name=value) to a query string.
	 * If the name already exists in the query string, it will be removed first.
	 * @see #addToQueryString
	 */
	public static final String setToQueryString(String str, Map params) throws UnsupportedEncodingException {
		return setToQueryString(new StringBuffer(str), params).toString();
	}

	/**
	 * Sets a map of parameters (name=value) to a query string.
	 * If the name already exists in the query string, it will be removed first.
	 * @see #addToQueryString
	 */
	public static final StringBuffer setToQueryString(StringBuffer sb, Map params) throws UnsupportedEncodingException {
		if (params != null) {
			for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry) it.next();
				setToQueryString(sb, (String) me.getKey(), me.getValue());
			}
		}
		return sb;
	}

	/** Tests whether a parameter exists in the query string.
	 */
	public static final boolean containsQuery(String str, String name) {
		int j = str.indexOf(name);
		if (j <= 0)
			return false;

		char cc = str.charAt(j - 1);
		if (cc != '?' && cc != '&')
			return false;

		j += name.length();
		if (j >= str.length())
			return true;
		cc = str.charAt(j);
		return cc == '=' || cc == '&';
	}

	/** Remove all name/value pairs of the specified name from a string.
	 *
	 * <p>The query string might contain servlet path and other parts.
	 * This method starts the searching from the last '?'.
	 * If the query string doesn't contain '?', it is assumed to be a string
	 * without query's name/value pairs.
	 * @see #addToQueryString
	 */
	public static final String removeFromQueryString(String str, String name) throws UnsupportedEncodingException {
		if (str == null)
			return null;

		int j = str.indexOf('?');
		if (j < 0)
			return str;

		final StringBuffer sb = new StringBuffer(str);
		removeFromQueryString(sb, name);
		return sb.length() == str.length() ? str : sb.toString();
	}

	/** Remove all name/value pairs of the specified name from a string.
	 * @see #addToQueryString
	 */
	public static final StringBuffer removeFromQueryString(StringBuffer sb, String name)
			throws UnsupportedEncodingException {
		name = encodeURIComponent(name);

		if (name == null || name.isEmpty())
			return sb;

		int j = sb.indexOf("?");
		if (j < 0)
			return sb; //no '?'

		j = sb.indexOf(name, j + 1);
		if (j < 0)
			return sb; //no name

		final int len = name.length();
		do {
			//1. make sure left is & or ?
			int k = j + len;
			char cc = sb.charAt(j - 1);
			if (cc != '&' && cc != '?') {
				j = k;
				continue;
			}

			//2. make sure right is = or & or end-of-string
			if (k < sb.length()) {
				cc = sb.charAt(k);
				if (cc != '=' && cc != '&') {
					j = k;
					continue;
				}
			}

			//3. remove it until next & or end-of-string
			k = next(sb, '&', k);
			if (k < sb.length())
				sb.delete(j, k + 1); //also remove '&'
			else
				sb.delete(j - 1, k); //also preceding '?' or '&'
		} while ((j = sb.indexOf(name, j)) > 0);
		return sb;
	}

	/** Encodes an URL.
	 * It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details.
	 *
	 * <p>In additions, if uri starts with "/", the context path, e.g.,
	 * /zkdemo, is prefixed.
	 * In other words, "/ab/cd" means it is relevant to the servlet
	 * context path (say, "/zkdemo").
	 *
	 * <p>If uri starts with "~abc/", it means it is relevant to a foreign
	 * Web context called /abc. And, it will be converted to "/abc/" first
	 * (without prefix request.getContextPath()).
	 *
	 * <p>Finally, the uri is encoded by HttpServletResponse.encodeURL.
	 *
	 * <p>This method invokes {@link #encodeURI} for any characters
	 * before '?'. However, it does NOT encode any character after '?'. Thus,
	 * you might have to invoke
	 * {@link #encodeURIComponent} or {@link #addToQueryString(StringBuffer,Map)}
	 * to encode the query parameters.
	 *
	 * <h3>The URL Prefix and Encoder</h3>
	 *
	 * <p>In a sophisticated environment, e.g.,
	 * <a href="http://en.wikipedia.org/wiki/Reverse_proxy">Reverse Proxy</a>,
	 * the encoded URL might have to be prefixed with some special prefix.
	 * To do that, you can implement {@link URLEncoder}, and then
	 * specify the class with the library property called
	 * <code>org.zkoss.web.servlet.http.URLEncoder</code>.
	 * When {@link #encodeURL} encodes an URL, it will invoke
	 * {@link URLEncoder#encodeURL} such you can do customized encoding,
	 * such as insert a special prefix.
	 *
	 * @param request the request; never null
	 * @param response the response; never null
	 * @param uri it must be null, empty or starts with "/". It might contain
	 * "*" for current browser code and Locale.
	 * @param ctx the servlet context; used only if "*" is contained in uri
	 * @exception IndexOutOfBoundException if uri is empty
	 * @see org.zkoss.web.servlet.Servlets#locate
	 * @see org.zkoss.web.servlet.Servlets#generateURI
	 */
	public static final String encodeURL(ServletContext ctx, ServletRequest request, ServletResponse response,
			String uri) throws ServletException {
		try {
			return urlEncoder().encodeURL(ctx, request, response, uri, _urlenc0);
		} catch (Exception ex) {
			log.error("", ex);
			throw new ServletException("Unable to encode " + uri, ex);
		}
	}

	private static URLEncoder urlEncoder() {
		if (_urlenc == null) {
			final String cls = Library.getProperty("org.zkoss.web.servlet.http.URLEncoder");
			if (cls != null && cls.length() > 0) {
				try {
					_urlenc = (URLEncoder) Classes.newInstanceByThread(cls);
				} catch (Throwable ex) {
					throw SystemException.Aide.wrap(ex, "Unable to instantiate " + cls);
				}
			} else {
				_urlenc = _urlenc0;
			}
		}
		return _urlenc;
	}

	private static URLEncoder _urlenc;
	private static final URLEncoder _urlenc0 = new URLEncoder() {
		public String encodeURL(ServletContext ctx, ServletRequest request, ServletResponse response, String uri,
				URLEncoder defaultEncoder) throws Exception {
			return encodeURL0(ctx, request, response, uri);
		}
	};

	private static final String encodeURL0(ServletContext ctx, ServletRequest request, ServletResponse response,
			String uri) throws Exception {
		if (uri == null || uri.length() == 0)
			return uri; //keep as it is

		boolean ctxpathSpecified = false;
		if (uri.charAt(0) != '/') { //NOT relative to context path
			if (Servlets.isUniversalURL(uri))
				return uri; //nothing to do

			if (uri.charAt(0) == '~') { //foreign context
				final String ctxroot;
				if (uri.length() == 1) {
					ctxroot = uri = "/";
				} else if (uri.charAt(1) == '/') {
					ctxroot = "/";
					uri = uri.substring(1);
				} else {
					uri = '/' + uri.substring(1);
					final int j = uri.indexOf('/', 1);
					ctxroot = j >= 0 ? uri.substring(0, j) : uri;
				}

				final ExtendletContext extctx = Servlets.getExtendletContext(ctx, ctxroot.substring(1));
				if (extctx != null) {
					final int j = uri.indexOf('/', 1);
					return extctx.encodeURL(request, response, j >= 0 ? uri.substring(j) : "/");
				}

				final ServletContext newctx = ctx.getContext(ctxroot);
				if (newctx != null) {
					ctx = newctx;
				} else if (log.isDebugEnabled()) {
					log.debug("Context not found: {}", ctxroot);
				}
				ctxpathSpecified = true;
			} else if (Https.isIncluded(request) || Https.isForwarded(request)) {
				//if relative URI and being included/forwarded,
				//converts to absolute
				String pgpath = Https.getThisServletPath(request);
				if (pgpath != null) {
					int j = pgpath.lastIndexOf('/');
					if (j >= 0) {
						uri = pgpath.substring(0, j + 1) + uri;
					} else {
						log.warn("The current page doesn't contain '/':{}",
								pgpath);
					}
				}
			}
		}

		//locate by locale and browser if necessary
		uri = Servlets.locate(ctx, request, uri, null);

		//prefix context path
		if (!ctxpathSpecified && uri.charAt(0) == '/'
		//ZK-3131: do not prefix context path to relative protocol urls that starts with //
				&& !(uri.length() > 1 && uri.charAt(1) == '/') && (request instanceof HttpServletRequest)) {
			//Work around with a bug when we wrap Pluto's RenderRequest (1.0.1)
			String ctxpath = Https.getThisContextPath(request);
			if (ctxpath.length() > 0 && ctxpath.charAt(0) != '/')
				ctxpath = '/' + ctxpath;

			//Some Web server's ctxpath is "/"
			final int last = ctxpath.length() - 1;
			if (last >= 0 && ctxpath.charAt(last) == '/')
				ctxpath = ctxpath.substring(0, last);

			uri = ctxpath + uri;
		}

		int j = uri.indexOf('?');
		if (j < 0) {
			uri = encodeURI(uri);
		} else {
			uri = encodeURI(uri.substring(0, j)) + uri.substring(j);
		}
		//encode
		if (response instanceof HttpServletResponse)
			uri = ((HttpServletResponse) response).encodeURL(uri);
		return uri;
	}

	/** The URL encoder used to encode URL by including the session ID,
	 * and servlet context path.
	 * It is a plugin that {@link Encodes#encodeURL} will call
	 * to encode the URL.
	 *
	 * <p>In a sophisticated environment, e.g.,
	 * <a href="http://en.wikipedia.org/wiki/Reverse_proxy">Reverse Proxy</a>,
	 * the encoded URL might have to be prefixed with some special prefix.
	 * To do that, you can implement {@link URLEncoder}, and then
	 * specify the class with the library property called
	 * <code>org.zkoss.web.servlet.http.URLEncoder</code>.
	 * When {@link #encodeURL} encodes an URL, it will invoke
	 * {@link URLEncoder#encodeURL} such you can do customized encoding,
	 * such as insert a special prefix.
	 *
	 * @since 3.0.1
	 * @see Encodes#encodeURL
	 */
	public static interface URLEncoder {
		/** Encodes the specified URL by including the session ID and
		 * the servlet context path, if necessary.
		 *
		 * <p>Notice that url might contain "~" and other special
		 * characters that the Web server won't support.
		 * The implementation might invoke back the default encoding
		 * by use of the defaultEcoder parameter.
		 *
		 * @param url the URL to encode. It shall not include
		 * the servlet context path.
		 * @param defaultEncoder the default encoder (never null).
		 * @since 5.0.0
		 */
		public String encodeURL(ServletContext ctx, ServletRequest request, ServletResponse response, String url,
				URLEncoder defaultEncoder) throws Exception;
	}
}
