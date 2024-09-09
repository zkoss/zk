/* Https.java

	Purpose:
	Description:
	History:
	2001/11/29 13:53:05, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Files;
import org.zkoss.io.RepeatableInputStream;
import org.zkoss.io.RepeatableReader;
import org.zkoss.lang.Strings;
import org.zkoss.util.media.Media;
import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ExtendletContext;

/**
 * The Servlet-related utilities.
 *
 * @author tomyeh
 */
public class Https extends Servlets {
	private static final Logger log = LoggerFactory.getLogger(Https.class);

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
	public static final byte[] gzip(HttpServletRequest request, HttpServletResponse response, InputStream content1,
			byte[] content2) throws IOException {
		//We check Content-Encoding first to avoid compressing twice
		String ae = request.getHeader("accept-encoding");
		if (ae != null && !response.containsHeader("Content-Encoding")) {
			if (ae.indexOf("gzip") >= 0) {
				response.addHeader("Content-Encoding", "gzip");
				final ByteArrayOutputStream boas = new ByteArrayOutputStream(8192);
				final GZIPOutputStream gzs = new GZIPOutputStream(boas);
				if (content1 != null)
					Files.copy(gzs, content1);
				if (content2 != null)
					gzs.write(content2);
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
		final String uri = hreq.getRequestURI();
		return sb.substring(0, sb.length() - uri.length());
	}

	/**
	 * Gets the complete context path, including protocol, server, ports, and
	 * context.
	 * Example, http://mysite.com:8080/we
	 */
	public static final String getCompleteContext(HttpServletRequest hreq) {
		final StringBuffer sb = hreq.getRequestURL();
		final String uri = hreq.getRequestURI();
		final String ctx = hreq.getContextPath();
		return sb.substring(0, sb.length() - uri.length() + ctx.length());
	}

	/** Gets the value of the specified cookie, or null if not found.
	 * @param name the cookie's name
	 */
	public static final String getCookieValue(HttpServletRequest request, String name) {
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
	 * @return "" if request is not a http request
	 */
	public static final String getThisContextPath(ServletRequest request) {
		String asyncPath = (String) request.getAttribute(Attributes.ASYNC_CONTEXT_PATH);
		if (asyncPath != null)
			return asyncPath;
		String path = (String) request.getAttribute(Attributes.INCLUDE_CONTEXT_PATH);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getContextPath() : "";
	}

	/**
	 * Gets the servlet path of this page.
	 * Unlike getServletPath, it detects whether the current page is included.
	 *
	 * @return "/" if request is not a http request
	 */
	public static final String getThisServletPath(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.INCLUDE_SERVLET_PATH);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getServletPath() : "/";
	}

	/**
	 * Gets the request URI of this page.
	 * Unlike getRequestURI, it detects whether the current page is included.
	 *
	 * @return "/" if request is not a http request
	 */
	public static final String getThisRequestURI(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.INCLUDE_REQUEST_URI);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURI() : "/";
	}

	/**
	 * Gets the query string of this page.
	 * Unlike getQueryString, it detects whether the current page is included.
	 *
	 * @return null if request is not a http request
	 */
	public static final String getThisQueryString(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.INCLUDE_QUERY_STRING);
		return path != null || isIncluded(request) || !(request instanceof HttpServletRequest) ? path
				: //null is valid even included
				((HttpServletRequest) request).getQueryString();
	}

	/**
	 * Gets the path info of this page.
	 * Unlike getPathInfo, it detects whether the current page is included.
	 *
	 * @return null if request is not a http request
	 */
	public static final String getThisPathInfo(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.INCLUDE_PATH_INFO);
		return path != null || isIncluded(request) || !(request instanceof HttpServletRequest) ? path
				: //null is valid even included
				((HttpServletRequest) request).getPathInfo();
	}

	/**
	 * Gets the original context path regardless of being forwarded or not.
	 * Unlike getContextPath, it won't be affected by forwarding.
	 */
	public static final String getOriginContextPath(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.FORWARD_CONTEXT_PATH);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getContextPath() : "";
	}

	/**
	 * Gets the original servlet path regardless of being forwarded or not.
	 * Unlike getServletPath, it won't be affected by forwarding.
	 */
	public static final String getOriginServletPath(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.FORWARD_SERVLET_PATH);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getServletPath() : "/";
	}

	/**
	 * Gets the request URI regardless of being forwarded or not.
	 * Unlike HttpServletRequest.getRequestURI,
	 * it won't be affected by forwarding.
	 */
	public static final String getOriginRequestURI(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.FORWARD_REQUEST_URI);
		return path != null ? path
				: request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURI() : "/";
	}

	/**
	 * Gets the path info regardless of being forwarded or not.
	 * Unlike getPathInfo, it won't be affected by forwarding.
	 */
	public static final String getOriginPathInfo(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.FORWARD_PATH_INFO);
		return path != null ? path : isForwarded(request) ? null
				: //null is valid even included
				request instanceof HttpServletRequest ? ((HttpServletRequest) request).getPathInfo() : null;
	}

	/**
	 * Gets the query string regardless of being forwarded or not.
	 * Unlike getQueryString, it won't be affected by forwarding.
	 */
	public static final String getOriginQueryString(ServletRequest request) {
		String path = (String) request.getAttribute(Attributes.FORWARD_QUERY_STRING);
		return path != null ? path : isForwarded(request) ? null
				: //null is valid even included
				request instanceof HttpServletRequest ? ((HttpServletRequest) request).getQueryString() : null;
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

		final StringBuffer sb = new StringBuffer(80).append(getOriginServletPath(request));
		if (pi != null)
			sb.append(pi);
		if (qstr != null)
			sb.append('?').append(qstr);
		return sb.toString();
	}

	/** Returns the request uri + query string.
	 * Unlike {@link #getOriginFullServlet}, this is in the encoded form
	 * (e.g., %nn still exists, if any).
	 * Note: request uri = context path + servlet path + path info.
	 */
	public static final String getOriginFullRequest(ServletRequest request) {
		final String qstr = getOriginQueryString(request);
		return qstr != null ? getOriginRequestURI(request) + '?' + qstr : getOriginRequestURI(request);
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
	 * <br/>Notice that, since 3.6.3, <code>uri</code> could contain
	 * '*' (to denote locale and browser). Refer to {@link #locate}.
	 * @param params the attributes that will be set when the redirection
	 * is back; null to ignore; format: (String, Object)
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * and {@link #APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 */
	public static final void sendRedirect(ServletContext ctx, HttpServletRequest request, HttpServletResponse response,
			String uri, Map params, int mode) throws IOException, ServletException {
		uri = locate(ctx, request, uri, null);
		final String encodedUrl = encodeRedirectURL(ctx, request, response, uri, params, mode);
		//if (log.isDebugEnabled()) log.debug("redirect to " + encodedUrl);
		response.sendRedirect(encodedUrl);
	}

	/** Encodes an URL such that it can be used with HttpServletResponse.sendRedirect.
	 */
	public static final String encodeRedirectURL(ServletContext ctx, HttpServletRequest request,
			HttpServletResponse response, String uri, Map params, int mode) {
		if (uri == null) {
			uri = request.getContextPath() + getOriginFullServlet(request);
		} else {
			final int len = uri.length();
			if (len == 0 || uri.charAt(0) == '/') {
				uri = request.getContextPath() + uri;
			} else if (uri.charAt(0) == '~') {
				final int j = uri.indexOf('/', 1);
				final String ctxroot = j >= 0 ? "/" + uri.substring(1, j) : "/" + uri.substring(1);
				final ExtendletContext extctx = Servlets.getExtendletContext(ctx, ctxroot.substring(1));
				if (extctx != null) {
					uri = j >= 0 ? uri.substring(j) : "/";
					return extctx.encodeRedirectURL(request, response, uri, params, mode);
				} else {
					uri = len >= 2 && uri.charAt(1) == '/' ? uri.substring(1) : '/' + uri.substring(1);
				}
			}
		}

		return response.encodeRedirectURL(generateURI(uri, params, mode));
	}

	/**
	 * Converts a date string to a Date instance.
	 * The format of the giving date string must be complaint
	 * to HTTP protocol.
	 *
	 * @exception ParseException if the string is not valid
	 */
	public static final Date toDate(String sdate) throws ParseException {
		ParseException ex = null;
		for (String df : _dfs) {
			try {
				return new SimpleDateFormat(df, Locale.US).parse(sdate);
			} catch (ParseException t) {
				if (ex == null)
					ex = t;
			}
		}
		throw ex;
	}

	private static final String PATH_REGEX = "^(/[-\\w:@&?=+,.!/~*'%$_;\\(\\)]*)?$";
	private static final Pattern PATH_PATTERN = Pattern.compile(PATH_REGEX);

	/**
	 * Returns the normalized path, or null if it is invalid.
	 * It is invalid if it is null, or starts with "/" and contains "..".
	 *
	 * @since 10.0.0
	 */
	public static String normalizePath(String path) {
		if (path == null) {
			return null;
		}
		if ("/".equals(path)) {
			return path;
		}

		List<String> parts = new ArrayList<>();
		String[] segments = path.split("/");

		for (String segment : segments) {
			if (segment.equals("..")) {
				if (!parts.isEmpty()) {
					parts.remove(parts.size() - 1); // Go up one directory
				} else {
					// Path is trying to go above the root, which might be a security issue
					return null;
				}
			} else if (!segment.equals(".") && !segment.isEmpty()) {
				parts.add(segment); // Add non-empty, non-current directory segments
			}
		}

		String result =  String.join("/", parts);
		if (path.startsWith("/")) {
			result = "/" + result;
		}
		if (path.endsWith("/")) {
			result = result + "/";
		}
		return result;
	}

	/**
	 * Returns whether the specified path is valid.
	 * It is valid if it is null, or starts with "/" and doesn't contain "..".
	 *
	 * @since 10.0.0
	 */
	public static boolean isValidPath(String path) {
		if (path == null)
			return false;
		path = normalizePath(path);

		if (path == null || !PATH_PATTERN.matcher(path).matches()) {
			return false;
		}
		if (path.startsWith("/../") || path.equals("/..")) {
			return false;
		}
		final int slash2Count = countToken("//", path);
		return slash2Count <= 0;
	}

	/**
	 * Returns the path of the specified URI, or null if not found.
	 * It is the same as new URI(uri).getPath(), except that it returns null
	 * if the path is invalid.
	 *
	 * @since 10.0.0
	 * @see #isValidPath(String)
	 */
	public static String sanitizePath(String path) {
		if (path == null)
			return null;
		String normalizedPath;
		try {
			normalizedPath = normalizePath(path.startsWith("/") ? path : new URI(path).normalize().toString());
		} catch (URISyntaxException e) {
			return null;
		}

		if (!isValidPath(normalizedPath))
			return null;

		// Return the sanitized URL path
		return normalizedPath;
	}

	private static int countToken(final String token, final String target) {
		int tokenIndex = 0;
		int count = 0;
		while (tokenIndex != -1) {
			tokenIndex = target.indexOf(token, tokenIndex);
			if (tokenIndex > -1) {
				tokenIndex++;
				count++;
				}
			}
		return count;
	}

	/**
	 * Converts a data to a string complaint to HTTP protocol.
	 */
	public static final String toString(Date date) {
		return new SimpleDateFormat(_dfs[0], Locale.US).format(date);
	}

	private static final String[] _dfs = { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEEEE, dd-MMM-yy HH:mm:ss zzz",
			"EEE MMMM d HH:mm:ss yyyy" };

	// it's used to lock the media when writing to response.
	private static final Map<Media, Object> LOCKS = Collections.synchronizedMap(new WeakHashMap<>());

	/** Write the specified media to HTTP response.
	 *
	 * @param response the HTTP response to write to
	 * @param media the content to be written
	 * @param download whether to cause the download to show at the client.
	 * If true, it sets the Content-Disposition header.
	 * @param repeatable whether to use {@link RepeatableInputStream}
	 * or {@link RepeatableReader} to read the media.
	 * It is better to specify true if the media might be read repeatedly.
	 * @since 3.5.0
	 */
	public static void write(HttpServletRequest request, HttpServletResponse response, Media media, boolean download,
			boolean repeatable) throws IOException {
		//2012/03/09 TonyQ: ZK-885 Iframe with PDF stop works in IE 8 when we have Accept-Ranges = bytes.

		if (!Servlets.isBrowser(request, "ie")) {
			response.setHeader("Accept-Ranges", "bytes");
		}

		final boolean headOnly = "HEAD".equalsIgnoreCase(request.getMethod());
		final byte[] data;
		int from = -1, to = -1;
		final Object lock;
		synchronized (LOCKS) {
			lock = LOCKS.computeIfAbsent(media, k -> new Object());
		}
		synchronized (lock) { //Bug 1896797: media might be accessed concurrently.
			//reading an image and send it back to client
			final String ctype = media.getContentType();
			if (ctype != null)
				response.setContentType(ctype);

			if (media.isContentDisposition()) {
				String contentDisposition;
				String flnm = "";
				if (download) {
					contentDisposition = "attachment";

					// Bug ZK-1257: Filedownload.save(media, filename) does not save the media as the specified filename
					StringBuffer temp = request.getRequestURL();
					final String update_uri = (String) request.getSession().getServletContext()
							.getAttribute("org.zkoss.zk.ui.http.update-uri"); //B65-ZK-1619
					if (update_uri != null && temp.toString().contains(update_uri + "/view")) {
						// for Bug ZK-2350, we don't specify the filename when coming with ZK Fileupload, but invoke this directly as Bug ZK-1619
						//					final String saveAs = URLDecoder.decode(temp.substring(temp.lastIndexOf("/")+1), "UTF-8");
						//					flnm = ("".equals(saveAs)) ? media.getName() : saveAs;
						// ZK-3058: remove jsessionid if any
						int jsessionPos = temp.indexOf(";jsessionid=");
						if (jsessionPos != -1)
							flnm = URLDecoder.decode(
									temp.substring(temp.lastIndexOf("/") + 1, jsessionPos),
									"UTF-8"
							);
					} else
						flnm = media.getName();
				} else {
					contentDisposition = "inline";
					flnm = media.getName();
				}
				// ZK-3058: filename for legacy browsers, filename* for modern browsers
				if (flnm != null && flnm.length() > 0) {
					contentDisposition +=
							";filename=" + encodeFilename(request, flnm)
									+ ";filename*=UTF-8''" + encodeRfc3986(
									flnm);
				}
				response.setHeader("Content-Disposition", contentDisposition);
			}

			final String rs = request.getHeader("Range");
			if (rs != null && rs.length() > 0) {
				final int[] range = parseRange(rs);
				if (range != null) {
					from = range[0];
					to = range[1];
				}
			}

			if (!media.inMemory()) {
				final ServletOutputStream out = response.getOutputStream();
				if (media.isBinary()) {
					InputStream in = media.getStreamData();
					if (repeatable)
						in = RepeatableInputStream.getInstance(in);
					try {
						if (headOnly) {
							int cnt = 0;
							final byte[] buf = new byte[512];
							for (int v; (v = in.read(buf)) >= 0;)
								cnt += v;
							response.setContentLength(cnt);
							return;
						}

						if (from >= 0) { //partial
							PartialByteStream pbs = new PartialByteStream(from, to);
							Files.copy(pbs, in);
							pbs.responseTo(response);
						} else {
							Files.copy(out, in);
						}
					} catch (IOException ex) {
						//browser might close the connection
						//and reread (test case: B30-1896797.zul)
						//so, read it completely, since 2nd read counts on it
						if (in instanceof org.zkoss.io.Repeatable) {
							try {
								final byte[] buf = new byte[1024 * 8];
								int v;
								do {
									v = in.read(buf);
								} while (v >= 0);
							} catch (Throwable t) { //ignore it
							}
						}
						throw ex;
					} finally {
						in.close();
					}
				} else {
					final String charset = getCharset(ctype);
					Reader in = media.getReaderData();
					if (repeatable)
						in = RepeatableReader.getInstance(in);
					try {
						if (headOnly) {
							int cnt = 0;
							final char[] buf = new char[256];
							for (int v; (v = in.read(buf)) >= 0;)
								cnt += new String(buf, 0, v).getBytes(charset).length;
							response.setContentLength(cnt);
							return;
						}

						if (from >= 0) { //partial
							PartialByteStream pbs = new PartialByteStream(from, to);
							OutputStreamWriter wt = new OutputStreamWriter(pbs, charset);
							Files.copy(wt, in);
							wt.close(); //flush to pbs
							pbs.responseTo(response);
						} else {
							OutputStreamWriter wt = new OutputStreamWriter(out, charset);
							Files.copy(wt, in);
							wt.close(); //flush to out
						}
					} catch (IOException ex) {
						//browser might close the connection and reread
						//so, read it completely, since 2nd read counts on it
						if (in instanceof org.zkoss.io.Repeatable) {
							try {
								final char[] buf = new char[1024 * 4];
								int v;
								do {
									v = in.read(buf);
								} while (v >= 0);
							} catch (Throwable t) { //ignore it
							}
						}
						throw ex;
					} finally {
						in.close();
					}
				}
				out.flush();
				return; //done;
			}

			data = media.isBinary() ? media.getByteData() : media.getStringData().getBytes(getCharset(ctype));
		}

		if (headOnly) {
			response.setContentLength(data.length);
		} else {
			final ServletOutputStream out = response.getOutputStream();
			if (from >= 0) { //partial
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				int f = from <= data.length ? from : data.length - 1;
				int t = to >= 0 && to < data.length ? to : data.length - 1;
				int cnt = t - f + 1;
				response.setContentLength(cnt);
				response.setHeader("Content-Range", "bytes " + f + "-" + t + "/" + data.length);

				out.write(data, f, cnt);
			} else {
				response.setContentLength(data.length);
				out.write(data);
			}
			out.flush();
		}
	}

	/** Filename can be quoted-string.
	 * Refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html
	 * and http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html
	*/
	private static String encodeFilename(HttpServletRequest request, String filename) {
		// ZK-2143: should access Chinese filename
		String agent = request.getHeader("USER-AGENT");
		if (agent != null) {
			try {
				if (agent.contains("Trident")) {
					filename = encodeRfc3986(filename);
				}
			} catch (UnsupportedEncodingException e) {
				// ignore it, if not supported
				log.warn("", e);
			}
		}

		return '"' + Strings.escape(filename, "\"") + '"';
	}

	private static String encodeRfc3986(String data) throws UnsupportedEncodingException {
		return Encode.forUriComponent(data);
	}

	private static String getCharset(String contentType) {
		if (contentType != null) {
			int j = contentType.indexOf("charset=");
			if (j >= 0) {
				String cs = contentType.substring(j + 8).trim();
				if (cs.length() > 0)
					return cs;
			}
		}
		return "UTF-8";
	}

	private static int[] parseRange(String range) {
		range = range.toLowerCase(java.util.Locale.ENGLISH);
		for (int j = 0, k, len = range.length(); (k = range.indexOf("bytes", j)) >= 0;) {
			for (k += 5; k < len;) {
				char cc = range.charAt(k++);
				if (cc == ' ' || cc == '\t')
					continue;
				if (cc == '=') {
					j = range.indexOf('-', k);
					try {
						int from = Integer.parseInt((j >= 0 ? range.substring(k, j) : range.substring(k)).trim());
						if (from >= 0) {
							if (j >= 0) {
								String s = range.substring(j + 1).trim();
								if (s.length() > 0) {
									int to = Integer.parseInt(s);
									if (to >= from)
										return new int[] { from, to };
								}
							}
							return new int[] { from, -1 };
						}
					} catch (Throwable ex) { //ignore
					}
					if (log.isDebugEnabled())
						log.debug("Failed to parse Range: {}", range);
					return null;
				}
			}
			j = k;
		}
		return null;
	}
}
