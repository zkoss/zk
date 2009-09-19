/* ServletContextLocator.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 15:16:05     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletContext;

import org.zkoss.lang.SystemException;
import org.zkoss.util.resource.Locator;

/**
 * Locator based on ServletContext.
 *
 * @author tomyeh
 */
public class ServletContextLocator implements Locator {
	private final ServletContext _ctx;
	private final String _dir, _prefix;

	public ServletContextLocator(ServletContext ctx) {
		this(ctx, null, null);
	}
	/**
	 * @param dir the directory used when relative path is specified
	 * (for {@link #getResource} and {@link #getResourceAsStream}).
	 * It must be null, empty, or starts with /.
	 */
	public ServletContextLocator(ServletContext ctx, String dir) {
		this(ctx, null, dir);
	}
	/** Constructor.
	 * For example, if prefix is "/WEB-INF/cwr", then getResource("/abc") will
	 * look for "/WEB-INF/cwr/abc".
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
	 */
	public ServletContextLocator(ServletContext ctx, String dir, String prefix) {
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
	}

	/** Returns the servlet context. */
	public ServletContext getServletContext() {
		return _ctx;
	}

	private String fixName(String name) {
		name = name.length() > 0 && name.charAt(0) != '/' ?
			_dir != null ? _dir + name:
				_prefix != null ? '/' + name: name: name;
		return _prefix != null ? _prefix + name: name;
	}

	//-- Locator --//
	public String getDirectory() {
		return _dir;
	}
	public URL getResource(String name) {
		try {
			return _ctx.getResource(fixName(name));
		} catch (java.net.MalformedURLException ex) {
			throw new SystemException(ex);
		}
	}
	public InputStream getResourceAsStream(String name) {
		return _ctx.getResourceAsStream(fixName(name));
	}

	//-- Object --//
	public int hashCode() {
		return _ctx.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof ServletContextLocator
			&& ((ServletContextLocator)o)._ctx.equals(_ctx);
	}
}
