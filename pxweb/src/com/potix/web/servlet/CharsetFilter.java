/* CharsetFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 18 16:40:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import com.potix.util.logging.Log;

/**
 * The filter to correct character encoding and to prepare Locale for
 * the current request.
 *
* <p>It is actually based on {@link Charsets#setup}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CharsetFilter extends GenericFilter {
	//private static final Log log = Log.lookup(CharsetFilter.class);

	//-- Filter --//
	public void doFilter(ServletRequest request, ServletResponse response,
	FilterChain chain) throws IOException, ServletException {
		final Object old = Charsets.setup(request, response);
		try {
			chain.doFilter(request, response);
		} finally {
			Charsets.cleanup(request, old);
		}
	}
}
