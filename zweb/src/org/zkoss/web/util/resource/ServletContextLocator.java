/* ServletContextLocator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 15:16:05     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	private final String _dir;

	public ServletContextLocator(ServletContext ctx) {
		this(ctx, null);
	}
	/**
	 * @param dir the directory used when relative path is specified
	 * (for {@link #getResource} and {@link #getResourceAsStream}).
	 * It must be null, empty, or starts with /.
	 */
	public ServletContextLocator(ServletContext ctx, String dir) {
		if (ctx == null)
			throw new IllegalArgumentException("null");
		if (dir != null) {
			final int len = dir.length();
			if (len == 0) dir = null;
			else {
				if (dir.charAt(0) != '/') throw new IllegalArgumentException("Absolute path required: "+dir);
				if (dir.charAt(len - 1) != '/') dir += '/';
			}
		}

		_ctx = ctx;
		_dir = dir;
	}

	/** Returns the servlet context. */
	public ServletContext getServletContext() {
		return _ctx;
	}

	private String fixName(String name) {
		return _dir != null && name.length() > 0 && name.charAt(0) != '/' ?
			_dir + name: name;
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
