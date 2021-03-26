/* ClassWebResource.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 20 16:49:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Files;
import org.zkoss.io.WriterOutputStream;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.FastReadArray;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.Locators;
import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.xml.XMLs;

/**
 * Used to access resources located in class path and under /web.
 * It doesn't work alone. Rather, it is a helper for servlet, such as
 * ZK's update servlet.
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
 * <p>Note: it assumes the file being loaded and the output stream is
 * encoded in UTF-8 unless the extendlet handles it differently.
 *
 * @author tomyeh
 */
public class ClassWebResource {
	private static final Logger log = LoggerFactory.getLogger(ClassWebResource.class);

	private final ServletContext _ctx;
	/** mapping URI including PATH_PREFIX. */
	private String _mappingURI;
	private final CWC _cwc;
	/** An array of extensions that have to be compressed (with gzip). */
	private Set<String> _compressExts;
	/** Map(String ext, Extendlet). */
	private final Map<String, Extendlet> _extlets = new HashMap<String, Extendlet>();
	/** Filers for requests. Map(String ext, FastReadArray(Filter)). */
	private final Map<String, FastReadArray<Filter>> _reqfilters = new HashMap<String, FastReadArray<Filter>>(2);
	/** Filers for includes. Map(String ext, FastReadArray(Filter)). */
	private final Map<String, FastReadArray<Filter>> _incfilters = new HashMap<String, FastReadArray<Filter>>(2);
	/** Additional locator. */
	private Locator _extraloc;
	/** The prefix used to encode URL. */
	private String _encURLPrefix;
	/** Whether to debug JavaScript files. */
	private boolean _debugJS;

	/** The prefix of path of web resources ("/web"). */
	public static final String PATH_PREFIX = "/web";
	/** The prefix of ZK version used to prevent browser cache. */
	private static final String ZVER = "/_zv";

	/** Indicates that the filter is applicable if the request comes
	 * directly from the client.
	 * @see #addFilter
	 * @since 3.5.1
	 */
	public static final int FILTER_REQUEST = 0x1;
	/** Indicates that the filter is applicable if the request is dispatched
	 * due to the inclusion. In other words, it is included by other servlet.
	 * @see #addFilter
	 * @since 3.5.1
	 */
	public static final int FILTER_INCLUDE = 0x2;

	/** Returns the URL of the resource of the specified URI by searching
	 * only the class path (with {@link #PATH_PREFIX}).
	 * <p>On the other hand, {@link #getResource} will search
	 * the extra locator first ({@link #getExtraLocator}) and then
	 * the class path.
	 * @since 5.0.0
	 */
	public static URL getClassResource(String uri) {
		return Locators.getDefault().getResource(PATH_PREFIX + fixURI(uri));
	}

	/** Returns the resource in a stream of the specified URI by searching
	 * only the class path (with {@link #PATH_PREFIX}).
	 * <p>On the other hand, {@link #getResourceAsStream} will search
	 * the extra locator first ({@link #getExtraLocator}) and then
	 * the class path.
	 * @since 5.0.0
	 */
	public static InputStream getClassResourceAsStream(String uri) {
		return Locators.getDefault().getResourceAsStream(PATH_PREFIX + fixURI(uri));
	}

	/** Returns the URL of the resource of the specified URI by searching
	 * the extra locator, if any, and then the class path
	 * (with {@link #PATH_PREFIX}).
	 * <p>This method becomes non-static since 5.0.0, and it
	 * will search the extra locator ({@link #getExtraLocator}) first.
	 */
	public URL getResource(String uri) {
		uri = fixURI(uri);
		if (_extraloc != null) {
			final URL url = _extraloc.getResource(uri);
			if (url != null)
				return url;
		}
		return Locators.getDefault().getResource(PATH_PREFIX + uri);
	}

	/** Returns the resource in a stream of the specified URI by searching
	 * the extra locator, if any, and then, the class path (with {@link #PATH_PREFIX}).
	 * <p>This method becomes non-static since 5.0.0, and it
	 * will search the extra locator ({@link #getExtraLocator}) first.
	 */
	public InputStream getResourceAsStream(String uri) {
		uri = fixURI(uri);
		if (_extraloc != null) {
			final InputStream is = _extraloc.getResourceAsStream(uri);
			if (is != null)
				return is;
		}
		return Locators.getDefault().getResourceAsStream(PATH_PREFIX + uri);
	}

	private static String fixURI(String uri) {
		int j = uri.lastIndexOf('?');
		if (j >= 0)
			uri = uri.substring(0, j);
		j = uri.lastIndexOf(";jsession");
		if (j >= 0)
			uri = uri.substring(0, j);
		return uri;
	}

	/** Sets the required headers (e.g., Cache-Control) to notify the
	 * the client is allowed to cache the content as long as possible.
	 */
	private static void setClientCacheForever(HttpServletResponse response) {
		if (!"false".equals(Library.getProperty("org.zkoss.web.classWebResource.cache"))) {
			response.setHeader("Cache-Control", "public, max-age=31536000"); //a year (unit: seconds)
			response.setDateHeader("Expires", _expires);
		}
	}

	private static final long _expires;

	static {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 365);
		_expires = cal.getTime().getTime();
	}

	/** Returns the instance (singleton in the whole app) for
	 * handling resources located in class path.
	 */
	public static final ClassWebResource getInstance(ServletContext ctx, String mappingURI) {
		synchronized (ctx) {
			final CWC cwc = (CWC) Servlets.getExtendletContext(ctx, ".");
			if (cwc != null)
				return cwc.getClassWebResource();

			final ClassWebResource cwr = new ClassWebResource(ctx, mappingURI);
			Servlets.addExtendletContext(ctx, ".", cwr._cwc);
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
		_mappingURI = mappingURI + PATH_PREFIX;
		_cwc = new CWC();

		addExtendlet("dsp", new DspExtendlet());
	}

	/** Returns the extra locator, or null if not available.
	 * The extra locator, if specified, has the higher priority than
	 * the class path.
	 * <p>Default: null.
	 * @since 5.0.0
	 */
	public Locator getExtraLocator() {
		return _extraloc;
	}

	/** Sets the extra locator.
	 * The extra locator, if specified, has the higher priority than
	 * the class path.
	 * <p>Default: null.
	 * @since 5.0.0
	 */
	public void setExtraLocator(Locator loc) {
		_extraloc = loc;
	}

	/** Process the request by retrieving the path from the path info.
	 * It invokes {@link Https#getThisPathInfo} to retrieve the path info,
	 * and then invoke {@link #service(HttpServletRequest,HttpServletResponse,String)}.
	 *
	 * <p>If the path info is not found, nothing is generated.
	 *
	 * @since 2.4.1
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String pi = Https.getThisPathInfo(request);
		//		if (log.isDebugEnabled()) log.debug("Path info: "+pi);
		if (pi != null)
			service(request, response, pi.substring(PATH_PREFIX.length()));
	}

	/** Process the request with the specified path.
	 *
	 * @param path the path related to the class path
	 * @since 3.0.0
	 */
	public void service(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		Servlets.getBrowser(request); //update request info

		final Object old = Charsets.setup(request, response, "UTF-8");
		try {
			web(request, response, path);
		} finally {
			Charsets.cleanup(request, old);
		}
	}

	/** Returns the Extendlet (aka., resource processor) of the
	 * specified extension, or null if not associated.
	 *
	 * @param ext the extension, e.g, "js" and "css.dsp".
	 * @param lookup whether to search different combination of the given
	 * extension. For example, if the extension is "js.dsp" and
	 * <code>lookup</code> is true, it first searches
	 * any extendlet registered for "js.dsp". If not found, it searches
	 * any extendlet for "dsp". However, if <code>lookup</code> is false,
	 * it searches only "js.dsp".
	 * @return the Extendlet (aka., resource processor),
	 * or null if not associated yet.
	 * @since 6.0.0
	 */
	public Extendlet getExtendlet(String ext, boolean lookup) {
		if (ext == null)
			return null;

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		for (;;) {
			synchronized (_extlets) {
				Extendlet exlet = _extlets.get(ext);
				if (!lookup || exlet != null)
					return exlet;
			}

			int j = ext.indexOf('.');
			if (j < 0)
				return null;
			ext = ext.substring(j + 1);
		}
	}

	/** Returns the Extendlet (a.k.a., resource processor) of the
	 * specified extension, or null if not associated.
	 *
	 * <p>It is a shortcut of <code>getExtendlet(ext, true)</code>.
	 *
	 * @param ext the extension, e.g, "js" and "css.dsp".
	 * @return the Extendlet (a.k.a., resource processor),
	 * or null if not associated yet.
	 * @since 2.4.1
	 */
	public Extendlet getExtendlet(String ext) {
		return getExtendlet(ext, true);
	}

	/** Adds an {@link Extendlet} (a.k.a., resource processor) to process
	 * the resource of the specified extension.
	 *
	 * @param ext the extension, e.g, "js" and "css".
	 * @param extlet the Extendlet (a.k.a., resource processor) to add
	 * @return the previous Extendlet, or null if not associated before.
	 * @since 2.4.1
	 */
	public Extendlet addExtendlet(String ext, Extendlet extlet) {
		if (ext == null || extlet == null)
			throw new IllegalArgumentException("null");

		extlet.init(new ExtendletConfig() {
			public ExtendletContext getExtendletContext() {
				return _cwc;
			}

			public void addCompressExtension(String ext) {
				synchronized (ClassWebResource.this) {
					if (_compressExts == null)
						_compressExts = new LinkedHashSet<String>();
					_compressExts.add(ext);
				}
			}
		});

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		synchronized (_extlets) {
			return _extlets.put(ext, extlet);
		}
	}

	/** Removes the {@link Extendlet} (a.k.a., resource processor)
	 * for the specified extension.
	 *
	 * @param ext the extension, e.g, "js" and "css.dsp".
	 * @return the previous Extendlet, or null if no Extendlet
	 * was associated with the specified extension.
	 * @since 2.4.1
	 */
	public Extendlet removeExtendlet(String ext) {
		if (ext == null)
			return null;

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		synchronized (_extlets) {
			return _extlets.remove(ext);
		}
	}

	/** Returns an array of the filters ({@link Filter}) of the specified
	 *  extension, or null if not associated.
	 *
	 * <p>Note: if the extension is "js.dsp", then it searches
	 * any filters registered for "js.dsp". If not found, it searches
	 * any filters for "dsp". In other words, "dsp" is searched only
	 * if no filter is registered for "js.dsp".
	 *
	 * @param ext the extension, such as "js" and "css.dsp".
	 * @param flag either {@link #FILTER_REQUEST} or
	 * and {@link #FILTER_INCLUDE}. If 0, {@link #FILTER_REQUEST}
	 * is assumed.
	 * @since 3.5.1
	 */
	public Filter[] getFilters(String ext, int flag) {
		if (ext == null)
			return null;

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		final Map<String, FastReadArray<Filter>> filters = flag == 0 || (flag & FILTER_REQUEST) != 0 ? _reqfilters
				: _incfilters;
		if (filters.isEmpty()) //no need to sync
			return null; //optimize

		for (;;) {
			FastReadArray ary;
			synchronized (filters) {
				ary = filters.get(ext);
			}
			if (ary != null)
				return (Filter[]) ary.toArray();

			int j = ext.indexOf('.');
			if (j < 0)
				return null;
			ext = ext.substring(j + 1);
		}

	}

	/** Adds a filter ({@link Filter}) to perform filtering task for
	 * the resource of the specified extension.
	 *
	 * <p>Unlike {@link #addExtendlet}, multiple filters can be applied to
	 * the same extension. The first one being added will be called first.
	 *
	 * @param ext the extension
	 * @param filter the filter
	 * @param flags a combination of {@link #FILTER_REQUEST}
	 * and {@link #FILTER_INCLUDE}. If 0, {@link #FILTER_REQUEST}
	 * is assumed.
	 * @since 3.5.1
	 */
	public void addFilter(String ext, Filter filter, int flags) {
		if (ext == null || filter == null)
			throw new IllegalArgumentException("null");

		filter.init(new FilterConfig() {
			public ExtendletContext getExtendletContext() {
				return _cwc;
			}
		});

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		if (flags == 0 || (flags & FILTER_REQUEST) != 0)
			addFilter(_reqfilters, ext, filter);
		if ((flags & FILTER_INCLUDE) != 0)
			addFilter(_incfilters, ext, filter);
	}

	private static void addFilter(Map<String, FastReadArray<Filter>> filters, String ext, Filter filter) {
		FastReadArray<Filter> ary;
		synchronized (filters) {
			ary = filters.get(ext);
			if (ary == null)
				filters.put(ext, ary = new FastReadArray<Filter>(Filter.class));
		}
		ary.add(filter);
	}

	/** Removes the filter ({@link Filter}) for the specified extension.
	 * @param flags a combination of {@link #FILTER_REQUEST}
	 * and {@link #FILTER_INCLUDE}. If 0, {@link #FILTER_REQUEST}
	 * is assumed.
	 * @return whether the filter has been removed successfully.
	 * @since 3.5.1
	 */
	public boolean removeFilter(String ext, Filter filter, int flags) {
		if (ext == null || filter == null)
			return false;

		ext = ext.toLowerCase(java.util.Locale.ENGLISH);
		boolean removed = false;
		if (flags == 0 || (flags & FILTER_REQUEST) != 0)
			removed = rmFilter(_reqfilters, ext, filter);
		if ((flags & FILTER_INCLUDE) != 0)
			removed = rmFilter(_incfilters, ext, filter) || removed;
		return removed;
	}

	private static boolean rmFilter(Map<String, FastReadArray<Filter>> filters, String ext, Filter filter) {
		FastReadArray<Filter> ary;
		synchronized (filters) {
			ary = filters.get(ext);
		}
		if (ary != null && ary.remove(filter)) {
			if (ary.isEmpty())
				synchronized (filters) {
					ary = filters.remove(ext);
					if (ary != null && !ary.isEmpty())
						filters.put(ext, ary); //modify by other, so restore
				}
			return true;
		}
		return false;
	}

	/** Sets the extension that shall be compressed if the browser
	 * supports the compression encoding (accept-encoding).
	 *
	 * <p>Default: null (no compression at all).
	 *
	 * @param exts an array of extensions, e.g., {"js", "css", "html"}.
	 * If null or zero-length, it means no compression at all.
	 *@since 2.4.1
	 */
	public void setCompress(String[] exts) {
		if (exts != null && exts.length > 0) {
			final Set<String> set = new LinkedHashSet<String>();
			for (int j = exts.length; --j >= 0;)
				set.add(exts[j]);
			_compressExts = set;
		} else {
			_compressExts = null;
		}
	}

	/**Returns the extension that shall be compressed if the browser
	 * supports the compression encoding (accept-encoding).
	 *
	 * <p>Default: null (no compression at all).
	 *@since 2.4.1
	 */
	public String[] getCompress() {
		return _compressExts != null ? _compressExts.toArray(new String[_compressExts.size()]) : null;
	}

	/** Returns whether to debug JavaScript files.
	 * If true, it means the original (i.e., uncompressed) JavaScript files
	 * shall be loaded instead of compressed JavaScript files.
	 *
	 * @since 3.0.4
	 * @see #setDebugJS
	 */
	public boolean isDebugJS() {
		return _debugJS;
	}

	/**Sets whether to debug JavaScript files.
	 *
	 * <p>Default: false.
	 *
	 * <p>If true is specified, it will try to load the original
	 * Java (i.e., uncompressed) file instead of the compressed one.
	 * For example, if {@link #service} is called to load abc.js,
	 * and {@link #isDebugJS}, then {@link #service} will try
	 * to load abc.src.js first. If not found, it loads ab.js instead.
	 *
	 * <p>If {@link #isDebugJS} is false (default),
	 * abc.js is always loaded.
	 *
	 * @param debug whether to debug JavaScript files.
	 * If true, the original JavaScript files shall be
	 * loaded instead of the compressed files.
	 * @since 3.0.4
	 */
	public void setDebugJS(boolean debug) {
		_debugJS = debug;
	}

	/** Called by WebManager#setUpdateUri when WebManager is created
	 * by HttpSessionListener#contextInitialized
	 * 
	 * @param mappingURI mapping URI excluding PATH_PREFIX.
	 */
	public void setMappingURI(String mappingURI) {
		if (!mappingURI.startsWith("/") || mappingURI.endsWith("/"))
			throw new IllegalArgumentException("mappingURI must start with /, but not ends with /");

		_mappingURI = mappingURI + PATH_PREFIX;
	}

	//-- Work with CWC --//
	/** Works with {@link CWC} to
	 * load resources from class path (thru this servlet).
	 */
	private void web(HttpServletRequest request, HttpServletResponse response, String pi)
			throws ServletException, IOException {
		//A trick used to enforce browser to load new version JavaScript
		//How it work: client engine prefix URI with /_zv123, where
		//123 is the build version that changes once reload is required
		//Then, the server eliminate such prefix before locating resource
		if (pi.startsWith(ZVER)) {
			final int j = pi.indexOf('/', ZVER.length());
			if (j >= 0)
				pi = pi.substring(j);
			else
				log.warn("Unknown path info: " + Encodes.encodeURI(pi));
		} else if (_encURLPrefix != null && pi.startsWith(_encURLPrefix)) {
			final int len1 = pi.length(), len2 = _encURLPrefix.length();
			if (len1 > len2 && pi.charAt(len2) == '/')
				pi = pi.substring(len2);
		}

		final String ext = Servlets.getExtension(pi, false); //complete extension
		final Filter[] filters = getFilters(ext, Servlets.isIncluded(request) ? FILTER_INCLUDE : FILTER_REQUEST);
		if (filters == null) {
			web0(request, response, pi, ext);
		} else {
			new FilterChainImpl(filters, pi, ext).doFilter(request, response);
		}
	}

	/** Processes the request without calling filter. */
	private void web0(HttpServletRequest request, HttpServletResponse response, String pi, String ext)
			throws ServletException, IOException {
		if (ext != null) {
			//Invoke the resource processor (Extendlet)
			final Extendlet extlet = getExtendlet(ext);
			if (extlet != null) {
				extlet.service(request, response, pi);
				return;
			}
		}

		//NOTE: Unlike DspExtendlet, the output stream is always UTF-8
		//since we load the source directly.

		if (!Servlets.isIncluded(request)) {
			//Bug 1998613: we have to set content type to avoid Apache's bug
			//Apache sets content-type automatically if not set
			//but if jsessionid is part of URL, Apache thought it is text/plain
			String ctype = ContentTypes.getContentType(ext);
			if (ctype == null) {
				ctype = ";charset=UTF-8";
			} else {
				final int k = ctype.indexOf(';');
				if (k >= 0)
					ctype = ctype.substring(0, k);
				if (!ContentTypes.isBinary(ctype))
					ctype += ";charset=UTF-8";
			}

			response.setContentType(ctype);
			if (pi.indexOf('*') < 0) //not locale dependent
				setClientCacheForever(response);
		}

		InputStream is = null;

		if (_debugJS && "js".equals(ext)) {
			final String orgpi = Servlets.locate(_ctx, request, pi.substring(0, pi.length() - 3) + ".src.js",
					_cwc.getLocator());
			is = getResourceAsStream(orgpi);
			if (is != null)
				pi = orgpi;
		}

		if (is == null) {
			final String p = Servlets.locate(_ctx, request, pi, _cwc.getLocator());
			is = getResourceAsStream(p);
		}

		boolean compressed = false;
		byte[] data;
		if (is == null) {
			if ("js".equals(ext)) {
				//Don't sendError. Reason: 1) IE waits and no onerror fired
				//2) better to debug (user will tell us what went wrong)
				// B65-ZK-1897 Sanitizing pi to prevent possible cross-site scripting vulnerability 
				data = ("(window.zk&&zk.error?zk.error:alert)('" + XMLs.encodeText(pi) + " not found');")
						.getBytes("UTF-8");
				//FUTURE: zweb shall not depend on zk
			} else {
				if (Servlets.isIncluded(request))
					log.error("Resource not found: " + Encodes.encodeURI(pi));
				response.sendError(HttpServletResponse.SC_NOT_FOUND, XMLs.escapeXML(pi));
				return;
			}
		} else {
			//Note: don't compress images
			data = shallCompress(request, ext) ? Https.gzip(request, response, is, null) : null;
			if (!(compressed = (data != null)))
				data = Files.readAll(is);
			//since what is embedded in the jar is not big, so load completely

			Files.close(is);
		}

		int len = data.length;
		response.setContentLength(len);

		OutputStream out;
		if (Servlets.isIncluded(request)) { //usually getWriter
			try {
				out = new WriterOutputStream(response.getWriter(), "UTF-8");
				//Not response.getCharacterEncoding because it is for "data"
			} catch (IllegalStateException ex) {
				try {
					out = response.getOutputStream();
				} catch (Throwable t) {
					log.warn("getOutputStream: failed" + Exceptions.getMessage(t));
					throw ex;
				}
			}
		} else {
			try {
				out = response.getOutputStream();
			} catch (IllegalStateException ex) {
				if (compressed)
					throw ex;
				try {
					out = new WriterOutputStream(response.getWriter(), "UTF-8");
				} catch (Throwable t) {
					log.warn("getWriter: failed" + Exceptions.getMessage(t));
					throw ex;
				}
			}
		}
		out.write(data);
		out.flush();
	}

	private boolean shallCompress(ServletRequest request, String ext) {
		return _compressExts != null && _compressExts.contains(ext) && !Servlets.isIncluded(request);
	}

	/** Sets the prefix used to encoding the URL.
	 * The prefix will be encoded when {@link ExtendletContext#encodeURL}
	 * is called. The prefix is usually the build number, such that browser
	 * won't use the wrong cached version
	 *
	 * <p>Default: null (no special encoding).
	 *
	 * @param prefix the prefix used to encode URL for the extendlet.
	 * If prefix is not empty and doesn't start with '/', it will
	 * be prefixed with '/'.
	 * @since 3.6.3
	 */
	public void setEncodeURLPrefix(String prefix) {
		if (prefix == null || prefix.length() == 0)
			prefix = null;
		else if (prefix.charAt(0) != '/')
			prefix = '/' + prefix;
		_encURLPrefix = prefix;
	}

	/** Returns the prefix used to encoding the URL, or null if no prefix.
	 * If an non-null prefix is returned, it must start with '/'.
	 * @see ExtendletContext#encodeURL
	 * @since 3.6.3
	 */
	public String getEncodeURLPrefix() {
		return _encURLPrefix;
	}

	/**
	 * An implementation of ExtendletContext to load resources from
	 * the class path rooted at /web.
	 */
	private class CWC implements ExtendletContext {
		private final Locator _locator = new Locator() {
			public String getDirectory() {
				return null;
			}

			public URL getResource(String name) {
				return ClassWebResource.this.getResource(name);
			}

			public InputStream getResourceAsStream(String name) {
				return ClassWebResource.this.getResourceAsStream(name);
				//Note: it doesn't handle _debugJS
			}
		};

		/** Returns the associated class web resource. */
		public ClassWebResource getClassWebResource() {
			return ClassWebResource.this;
		}

		//-- ExtendletContext --//
		public ServletContext getServletContext() {
			return _ctx;
		}

		public Locator getLocator() {
			return _locator;
		}

		public boolean shallCompress(ServletRequest request, String ext) {
			return ClassWebResource.this.shallCompress(request, ext);
		}

		public String encodeURL(ServletRequest request, ServletResponse response, String uri)
				throws ServletException, UnsupportedEncodingException {
			uri = Servlets.locate(_ctx, request, uri, getLocator()); //resolves "*"
			uri = (_encURLPrefix != null ? _mappingURI + _encURLPrefix : _mappingURI) + uri; //prefix with mapping

			//prefix context path
			if (request instanceof HttpServletRequest) {
				String ctxpath = ((HttpServletRequest) request).getContextPath();
				if (ctxpath == null)
					throw new NullPointerException(
							"HttpServletRequest#getContentPath() returns a null value from [ " + request + " ]");
				final int ctxlen = ctxpath.length();
				if (ctxlen > 0) {
					final char cc = ctxpath.charAt(0);
					if (cc != '/')
						ctxpath = '/' + ctxpath;
					//Work around a bug for Pluto's RenderRequest (1.0.1)
					else if (ctxlen == 1)
						ctxpath = ""; // "/" =>  ""
					//Work around liferay's issue: Upload 1627928 (not verified)
				}
				uri = ctxpath + uri;
			}

			int j = uri.indexOf('?');
			if (j < 0) {
				uri = Encodes.encodeURI(uri);
			} else {
				uri = Encodes.encodeURI(uri.substring(0, j)) + uri.substring(j);
			}
			//encode
			if (response instanceof HttpServletResponse)
				uri = ((HttpServletResponse) response).encodeURL(uri);
			return uri;
		}

		public String encodeRedirectURL(HttpServletRequest request, HttpServletResponse response, String uri,
				Map params, int mode) {
			return Https.encodeRedirectURL(_ctx, request, response, _mappingURI + uri, params, mode);
		}

		public RequestDispatcher getRequestDispatcher(String uri) {
			//			if (log.isDebugEnabled()) log.debug("getRequestDispatcher: "+uri);
			return _ctx.getRequestDispatcher(_mappingURI + uri);
		}

		public void include(HttpServletRequest request, HttpServletResponse response, String uri, Map params)
				throws ServletException, IOException {
			//Note: it is caller's job to convert related path to ~./
			if (uri.startsWith("~./") && uri.indexOf('?') < 0 && isDirectInclude(uri)) {
				Object old = request.getAttribute(Attributes.ARG);
				if (params != null)
					request.setAttribute(Attributes.ARG, params);
				//If params=null, use the 'inherited' one (same as Servlets.include)

				final String attrnm = "org.zkoss.web.servlet.include";
				request.setAttribute(attrnm, Boolean.TRUE);
				//so Servlets.isIncluded returns correctly
				try {
					service(request, response, Servlets.locate(_ctx, request, uri.substring(2), _cwc.getLocator()));
				} finally {
					request.removeAttribute(attrnm);
					request.setAttribute(Attributes.ARG, old);
				}
			} else {
				Servlets.include(_ctx, request, response, uri, params, Servlets.PASS_THRU_ATTR);
			}
		}

		/** Returns whether the page can be directly included.
		 */
		private boolean isDirectInclude(String path) {
			final String ext = Servlets.getExtension(path, false);
			final Extendlet extlet = ext != null ? getExtendlet(ext) : null;
			if (extlet != null) {
				try {
					return extlet.getFeature(Extendlet.ALLOW_DIRECT_INCLUDE);
				} catch (Throwable ex) { //backward compatibility
				}
			}
			return true;
		}
		//Note: Due to the source map since 8.5.0, it doesn't handle _debugJS
		public URL getResource(String uri) {
			return ClassWebResource.this.getResource(uri);
		}

		//Note: Due to the source map since 8.5.0, it doesn't handle _debugJS
		public InputStream getResourceAsStream(String uri) {
			return ClassWebResource.this.getResourceAsStream(uri);
		}
	}

	private class FilterChainImpl implements FilterChain {
		private final Filter[] _filters;
		private final String _pi, _ext;

		/** Which filter to process. */
		private int _j;

		private FilterChainImpl(Filter[] filters, String pi, String ext) throws ServletException, IOException {
			_pi = pi;
			_filters = filters;
			_ext = ext;
		}

		public void doFilter(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			if (_j > _filters.length)
				throw new IllegalStateException("Out of bound: " + _j + ", filter=" + Objects.toString(_filters));
			final int j = _j++;
			if (j == _filters.length) {
				web0(request, response, _pi, _ext);
			} else {
				_filters[j].doFilter(request, response, _pi, this);
			}
		}
	}
}
