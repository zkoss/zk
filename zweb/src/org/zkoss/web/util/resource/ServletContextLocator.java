/* ServletContextLocator.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 15:16:05     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletContext;

import org.zkoss.lang.SystemException;
import org.zkoss.util.resource.Locator;
import org.zkoss.web.servlet.Servlets;

/**
 * Locator based on ServletContext.
 *
 * @author tomyeh
 */
public class ServletContextLocator implements Locator {
	private final ServletContext _ctx;
	private final String _dir, _prefix;
	private final boolean _acceptURL;

	/** Constructor.
	 * A short cut of ServletContextLocator(ctx, null, null, false)
	 */
	public ServletContextLocator(ServletContext ctx) {
		this(ctx, null, null, false);
	}
	/** Constructor.
	 * @param acceptURL whether to URL (such as file:/, http:// and
	 * ftp://) are accepted. In other words, {@link Servlets#getResource}
	 * will be used.
	 * @see Servlets#getResource
	 * @since 5.0.7
	 */
	public ServletContextLocator(ServletContext ctx, boolean acceptURL) {
		this(ctx, null, null, acceptURL);
	}
	/** Constructor.
	 * A short of ServletContextLocator(ctx, dir, null, false).
	 * @param dir the directory used when relative path is specified
	 * (for {@link #getResource} and {@link #getResourceAsStream}).
	 * It must be null, empty, or starts with /.
	 */
	public ServletContextLocator(ServletContext ctx, String dir) {
		this(ctx, dir, null, false);
	}
	/** Constructor.
	 * A short cut of ServletContextLocator(ctx, dir, prefix, false).
	 */
	public ServletContextLocator(ServletContext ctx, String dir, String prefix) {
		this(ctx, dir, prefix, false);
	}
	/** Constructor.
	 * For example, if prefix is "/WEB-INF/cwr", then getResource("/abc") will
	 * look for "/WEB-INF/cwr/abc" first, and then "/abc".
	 *
	 * <p>Another example, if prefix is "/WEB-INF/cwr" and dir is "/subdir",
	 * then getResource("abc") will look for "/WEB-INF/cwr/subdir/abc".
	 *
	 * @param dir the directory used when relative path is specified
	 * (for {@link #getResource} and {@link #getResourceAsStream}).
	 * It must be null, empty, or starts with /.
	 * @param prefix the directory to prefix each directory specified
	 * (for {@link #getResource} and {@link #getResourceAsStream}).
	 * It must be null, empty, or starts with /.
	 * @param acceptURL whether to URL (such as file:/, http:// and
	 * ftp://) are accepted. In other words, {@link Servlets#getResource}
	 * will be used.
	 */
	public ServletContextLocator(ServletContext ctx, String dir, String prefix,
	boolean acceptURL) {
		if (ctx == null)
			throw new IllegalArgumentException("null");
		if (dir != null) {
			final int len = dir.length();
			if (len == 0) dir = null;
			else {
				if (dir.charAt(0) != '/')
					throw new IllegalArgumentException("Absolute path required: "+dir);
				if (dir.charAt(len - 1) != '/')
					dir += '/';
			}
		}
		if (prefix != null) {
			final int len = prefix.length();
			if (len == 0) prefix = null;
			else {
				if (prefix.charAt(0) != '/')
					throw new IllegalArgumentException("Absolute path required: "+prefix);
				if (len == 1)
					prefix = null; // "/" only
				else if (prefix.charAt(len - 1) == '/')
					prefix = prefix.substring(0, len - 1);
			}
		}

		_ctx = ctx;
		_dir = dir;
		_prefix = prefix;
		_acceptURL = acceptURL;
	}

	/** Returns the servlet context. */
	public ServletContext getServletContext() {
		return _ctx;
	}

	private String fixName(String name, boolean prefix) {
		name = name.length() > 0 && name.charAt(0) != '/' ?
			_dir != null ? _dir + name:
				prefix && _prefix != null ? '/' + name: name: name;
		return prefix && _prefix != null ? _prefix + name: name;
	}

	//-- Locator --//
	public String getDirectory() {
		return _dir;
	}
	public URL getResource(String name) {
		try {
			URL url = getResource0(fixName(name, true));
			return url == null && _prefix != null ?
				getResource0(fixName(name, false)): url;
		} catch (java.net.MalformedURLException ex) {
			throw new SystemException(ex);
		}
	}
	public InputStream getResourceAsStream(String name) {
		try {
			InputStream is = getResourceAsStream0(fixName(name, true));
			return is == null && _prefix != null ?
				getResourceAsStream0(fixName(name, false)): is;
		} catch (java.io.IOException ex) {
			throw new SystemException(ex);
		}
	}
	private URL getResource0(String path)
	throws java.net.MalformedURLException {
		return _acceptURL ? Servlets.getResource(_ctx, path): _ctx.getResource(path);
	}
	private InputStream getResourceAsStream0(String path)
	throws java.io.IOException {
		return _acceptURL ? Servlets.getResourceAsStream(_ctx, path):
			_ctx.getResourceAsStream(path);
	}

	//-- Object --//
	public int hashCode() {
		return _ctx.hashCode();
	}
	public boolean equals(Object o) {
		if (this == o)
			return true;
		return o instanceof ServletContextLocator
			&& ((ServletContextLocator)o)._ctx.equals(_ctx);
	}
}
