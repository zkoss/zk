/* CharsetFilter.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 18 16:40:37     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * The filter to correct character encoding and to prepare Locale for
 * the current request.
 *
 * <p>Initial parameters:
 * <dl>
 * <dt>charset</dt>
 * <dd>The default character set if it is not specified in the DSP page.
 * Default: UTF-8.</dd>
 * </dl>
 *
 * <p>It is actually based on {@link Charsets#setup}.
 *
 * @author tomyeh
 */
public class CharsetFilter implements Filter {
	//private static final Logger log = LoggerFactory.getLogger(CharsetFilter.class);
	private String _charset = "UTF-8";

	//-- Filter --//
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final Object old = Charsets.setup(request, response, _charset);
		try {
			chain.doFilter(request, response);
		} finally {
			Charsets.cleanup(request, old);
		}
	}

	public void destroy() {
	}

	public final void init(FilterConfig config) throws ServletException {
		final String cs = config.getInitParameter("charset");
		if (cs != null)
			_charset = cs.length() > 0 ? cs : null;
	}
}
