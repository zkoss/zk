/* Filter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 22 18:18:53     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter is an object that performs filtering task on a request to
 * {@link Extendlet}.
 *
 * @author tomyeh
 * @since 3.5.1
 */
public interface Filter {
	/** Initializes the resouorce processor.
	 */
	public void init(FilterConfig config);
	/** Causes the next filter in the chain to be invoked, or
	 * if the calling filter is the last filter in the chain, causes the resource at the end of the chain to be invoked.
	 *
	 * @param request the request (never null).
	 * @param response the response (never null).
	 * @param pi the path info that the request is targeting.
	 */
	public void doFilter(HttpServletRequest request, HttpServletResponse response,
	String pi, FilterChain chain)
	throws ServletException, IOException;
}
