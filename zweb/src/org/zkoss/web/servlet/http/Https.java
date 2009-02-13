/* Https.java

{{IS_NOTE
	Purpose: 
	Description: 
	History:
	2001/11/29 13:53:05, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.http;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import org.zkoss.lang.D;
import org.zkoss.lang.SystemException;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Files;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ExtendletContext;

/**
 * The Servlet-related utilities.
 *
 * @author tomyeh
 */
public class Https extends Servlets {
	private static final Log log = Log.lookup(Https.class);

	/** Compresses the content into an byte array, or null
	 * if the browser doesn't support the compression (accept-encoding).
	 *
	 * @param content1 the first part of the content to compress; null to ignore.
	 * If you have multiple input streams, use java.io.SequenceInputStream
	 * to concatenate them
	 * @param content2 the second part of the content to compress; null to ignore.
	 * @return the compressed result in an byte array,
	 * null if the browser doesn't support the compression.
	 * @since 2.4.1
	 */
	public static final byte[] gzip(HttpServletRequest request,
	HttpServletResponse response, InputStream content1,
	byte[] content2) throws IOException {
		//We check Content-Encoding first to avoid compressing twice
		String ae = request.getHeader("accept-encoding");
		if (ae != null && !response.containsHeader("Content-Encoding")) {
			if (ae.indexOf("gzip") >= 0) {
				response.addHeader("Content-Encoding", "gzip");
				final ByteArrayOutputStream boas = new ByteArrayOutputStream(8192);
				final GZIPOutputStream gzs = new GZIPOutputStream(boas);
				if (content1 != null) Files.copy(gzs, content1);
				if (content2 != null) gzs.write(content2);
				gzs.finish();
				return boas.toByteArray();
//			} else if (ae.indexOf("deflate") >= 0) {
//Refer to http://www.gzip.org/zlib/zlib_faq.html#faq38
//It is not a good idea to zlib (i.e., deflate)
			}
		}
		return null;
	}

	/**
	 * Gets the complete server name, including protocol, server, and ports.
	 * Example, http://mysite.com:8080
	 */
	public static final String getCompleteServerName(HttpServletRequest hreq) {
		final StringBuffer sb = hreq.getRequestURL();
		final String ctx = hreq.getContextPath();
		final int j = sb.indexOf(ctx);
		if (j < 0)
			throw new SystemException("Unknown request: url="+sb+", ctx="+ctx);
		return sb.delete(j, sb.length()).toString();
	}

	/**
	 * Gets the complete context path, including protocol, server, ports, and 
	 * context.
	 * Example, http://mysite.com:8080/we
	 */
	public static final String getCompleteContext(HttpServletRequest hreq) {
		final StringBuffer sb = hreq.getRequestURL();
		final String ctx = hreq.getContextPath();
		final int j = sb.indexOf(ctx);
		if (j < 0)
			throw new SystemException("Unknown request: url="+sb+", ctx="+ctx);
		return sb.delete(j + ctx.length(), sb.length()).toString();
	}

	/** Gets the value of the specified cookie, or null if not found.
	 * @param name the cookie's name
	 */
	public static final
	String getCookieValue(HttpServletRequest request, String name) {
		final Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int j = cookies.length; --j >= 0;) {
				if (cookies[j].getName().equals(name))
					return cookies[j].getValue();
			}
		}
		return null;
	}

	/**
	 * Returns the servlet uri of the request.
	 * A servlet uri is getServletPath() + getPathInfo().
	 * In other words, a servlet uri is a request uri without the context path.
	 * <p>However, HttpServletRequest.getRequestURI returns in encoded format,
	 * while this method returns in decode format (i.e., %nn is converted).
	 */
	public static final String getServletURI(HttpServletRequest request) {
		final String sp = request.getServletPath();
		final String pi = request.getPathInfo();
		if (pi == null || pi.length() == 0)
			return sp;
		if (sp.length() == 0)
			return pi;
		return sp + pi;
	}

	/**
	 * Gets the context path of this page.
	 * Unlike getContextPath, it detects whether the current page is included.
	 *
	 * @return "/" if request is not a http request
	 */
	public static final String getThisContextPath(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.INCLUDE_CONTEXT_PATH);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getContextPath(): "";
	}
	/**
	 * Gets the servlet path of this page.
	 * Unlike getServletPath, it detects whether the current page is included.
	 *
	 * @return "/" if request is not a http request
	 */
	public static final String getThisServletPath(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.INCLUDE_SERVLET_PATH);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getServletPath(): "/";
	}
	/**
	 * Gets the request URI of this page.
	 * Unlike getRequestURI, it detects whether the current page is included.
	 *
	 * @return "/" if request is not a http request
	 */
	public static final String getThisRequestURI(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.INCLUDE_REQUEST_URI);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getRequestURI(): "/";
	}
	/**
	 * Gets the query string of this page.
	 * Unlike getQueryString, it detects whether the current page is included.
	 *
	 * @return null if request is not a http request
	 */
	public static final String getThisQueryString(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.INCLUDE_QUERY_STRING);
		return path != null || isIncluded(request)
			|| !(request instanceof HttpServletRequest) ? path: //null is valid even included
				((HttpServletRequest)request).getQueryString();
	}
	/**
	 * Gets the path info of this page.
	 * Unlike getPathInfo, it detects whether the current page is included.
	 *
	 * @return null if request is not a http request
	 */
	public static final String getThisPathInfo(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.INCLUDE_PATH_INFO);
		return path != null || isIncluded(request)
			|| !(request instanceof HttpServletRequest) ? path: //null is valid even included
				((HttpServletRequest)request).getPathInfo();
	}

	/**
	 * Gets the original context path regardless of being forwarded or not.
	 * Unlike getContextPath, it won't be affected by forwarding.
	 */
	public static final String getOriginContextPath(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.FORWARD_CONTEXT_PATH);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getContextPath(): "";
	}
	/**
	 * Gets the original servlet path regardless of being forwarded or not.
	 * Unlike getServletPath, it won't be affected by forwarding.
	 */
	public static final String getOriginServletPath(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.FORWARD_SERVLET_PATH);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getServletPath(): "/";
	}
	/**
	 * Gets the request URI regardless of being forwarded or not.
	 * Unlike HttpServletRequest.getRequestURI,
	 * it won't be affected by forwarding.
	 */
	public static final String getOriginRequestURI(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.FORWARD_REQUEST_URI);
		return path != null ? path:
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getRequestURI(): "/";
	}
	/**
	 * Gets the path info regardless of being forwarded or not.
	 * Unlike getPathInfo, it won't be affected by forwarding.
	 */
	public static final String getOriginPathInfo(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.FORWARD_QUERY_STRING);
		return path != null ? path:
			isForwarded(request) ? null: //null is valid even included
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getQueryString(): null;
	}
	/**
	 * Gets the query string regardless of being forwarded or not.
	 * Unlike getQueryString, it won't be affected by forwarding.
	 */
	public static final String getOriginQueryString(ServletRequest request) {
		String path = (String)request.getAttribute(Attributes.FORWARD_PATH_INFO);
		return path != null ? path:
			isForwarded(request) ? null: //null is valid even included
			request instanceof HttpServletRequest ?
				((HttpServletRequest)request).getPathInfo(): null;
	}

	/** Returns the servlet path + path info + query string.
	 * Because the path info is decoded, the return string can be considered
	 * as decoded. On the other hand {@link #getOriginFullRequest} is in
	 * the encoded form.
	 * @see #getOriginFullRequest
	 */
	public static final String getOriginFullServlet(ServletRequest request) {
		final String qstr = getOriginQueryString(request);
		final String pi = getOriginPathInfo(request);
		if (qstr == null && pi == null)
			return getOriginServletPath(request);

		final StringBuffer sb =
			new StringBuffer(80).append(getOriginServletPath(request));
		if (pi != null) sb.append(pi);
		if (qstr != null) sb.append('?').append(qstr);
		return sb.toString();
	}
	/** Returns the request uri + query string.
	 * Unlik {@link #getOriginFullServlet}, this is in the encoded form
	 * (e.g., %nn still exists, if any).
	 * Note: request uri = context path + servlet path + path info.
	 */
	public static final String getOriginFullRequest(ServletRequest request) {
		final String qstr = getOriginQueryString(request);
		return qstr != null ? getOriginRequestURI(request) + '?' + qstr:
			getOriginRequestURI(request);
	}

	/**
	 * Redirects to another URL by prefixing the context path and
	 * encoding with encodeRedirectURL.
	 *
	 * <p>It encodes the URI automatically (encodeRedirectURL).
	 * Parameters are encoded by
	 * {@link Encodes#setToQueryString(StringBuffer,Map)}.
	 *
	 * <p>Like {@link Encodes#encodeURL}, the servlet context is
	 * prefixed if uri starts with "/". In other words, to redirect other
	 * application, the complete URL must be used, e.g., http://host/other.
	 *
	 * <p>Also, HttpServletResponse.encodeRedirectURL is called automatically.
	 *
	 * @param request the request; used only if params is not null
	 * @param response the response
	 * @param uri the redirect uri (not encoded; not including context-path),
	 * or null to denote {@link #getOriginFullServlet}
	 * It is OK to relevant (without leading '/').
	 * If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params the attributes that will be set when the redirection
	 * is back; null to ignore; format: (String, Object)
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final void sendRedirect(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	String uri, Map params, int mode)
	throws IOException, ServletException {
		final String encodedUrl =
			encodeRedirectURL(ctx, request, response, uri, params, mode);
		if (D.ON && log.debugable()) log.debug("redirect to " + encodedUrl);
		response.sendRedirect(encodedUrl);
	}
	/** Encodes an URL such that it can be used with HttpServletResponse.sendRedirect.
	 */
	public static final String encodeRedirectURL(ServletContext ctx, 
	HttpServletRequest request, HttpServletResponse response,
	String uri, Map params, int mode) {
		if (uri == null) {
			uri = request.getContextPath() + getOriginFullServlet(request);
		} else {
			final int len = uri.length();
			if (len ==0 || uri.charAt(0) == '/') {
				uri = request.getContextPath() + uri;
			} else if (uri.charAt(0) == '~') {
				final int j = uri.indexOf('/', 1);
				final String ctxroot =
					j >= 0 ? "/" + uri.substring(1, j): "/" + uri.substring(1);
				final ExtendletContext extctx =
					Servlets.getExtendletContext(ctx, ctxroot.substring(1));
				if (extctx != null) {
					uri = j >= 0 ? uri.substring(j): "/";
					return extctx.encodeRedirectURL(
						request, response, uri, params, mode);
				} else {
					uri = len >= 2 && uri.charAt(1) == '/' ?
						uri.substring(1): '/' + uri.substring(1);
				}
			}
		}

		return response.encodeRedirectURL(generateURI(uri, params, mode));		
	}

	/**
	 * Converts a date string to a Date instance.
	 * The format of the giving date string must be complaint
	 * to HTTP proptocol.
	 *
	 * @exception ParseException if the string is not valid
	 */
	public static final Date toDate(String sdate) throws ParseException {
		for (int j = 0;;) {
			try {
				synchronized (_dateFmts[j]) {
					return _dateFmts[j].parse(sdate);
				}
			} catch (ParseException ex) {
				if (++j == _dateFmts.length)
					throw ex;
			}
		}
	}
	/**
	 * Converts a data to a string complaint to HTTP protocol.
	 */
	public static final String toString(Date date) {
		synchronized (_dateFmts[0]) {
			return _dateFmts[0].format(date);
		}
	}
	private static final SimpleDateFormat _dateFmts[] = {
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
	};
}
