/* GenericFilter.java


	Purpose: A generic class to help implementing servlet filter
	Description: 
	History:
		2001/11/13 14:46:46, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.util.Map;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.http.Https;

/**
 * A generic class to help implementing servlet filter.
 * Its role is like javax.servlet.GenericServlet to javax.servlet.Servlet.
 *
 * @author tomyeh
 */
public abstract class GenericFilter implements Filter {
	protected FilterConfig _config;

	protected GenericFilter() {
	}

	//-- extra API --//
	/** A convenience method which can be overridden so that there's
	 * no need to call super.init(config).
	 * @see #init(FilterConfig)
	 */
	protected void init() throws ServletException {
	}
	/** Returns a String containing the value of the named initialization
	 * parameter, or null if the parameter does not exist.
	 */
	public final String getInitParameter(String name) {
		return _config.getInitParameter(name);
	}
	/** Returns a String containing the value of the named initialization
	 * parameter, or null if the parameter does not exist.
	 */
	public final Enumeration getInitParameterNames() {
		return _config.getInitParameterNames();
	}
	/** Returns the filter's name.
	 */
	public final String getFilterName() {
		return _config.getFilterName();
	}
	/** Returns the servlet context in which this filter is running.
	 */
	public final ServletContext getServletContext() {
		return _config.getServletContext();
	}

	/**
	 * Redirects to another page. Note: it supports only HTTP.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 *
	 * @param page the page's uri; null to denote the same request
	 * @param mode one of {@link Servlets#OVERWRITE_URI}, {@link Servlets#IGNORE_PARAM},
	 * and {@link Servlets#APPEND_PARAM}. It defines how to handle if both uri
	 * and params contains the same parameter.
	 * mode is used only if both uri contains query string and params is
	 * not empty.
	 */
	protected void sendRedirect(HttpServletRequest request,
	HttpServletResponse response, String page, Map params, int mode)
	throws ServletException, java.io.IOException {
		if (page != null)
			page = Servlets.locate(getServletContext(), request, page, null);
		Https.sendRedirect(getServletContext(),
			request, response, page, params, mode);
	}
	/** Forward to the specified page.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	protected final void forward(ServletContext ctx,
	ServletRequest request, ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		forward(ctx, request, response, uri, null, 0);
	}
	/** Forward to the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	protected final void forward(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws ServletException, java.io.IOException {
		if (uri != null)
			uri = Servlets.locate(getServletContext(), request, uri, null);
		Https.forward(ctx, request, response, uri, params, mode);
	}
	/** Includes the specified page.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	protected final void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		include(ctx, request, response, uri, null, 0);
	}
	/** Includes the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	protected final void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws ServletException, java.io.IOException {
		if (uri != null)
			uri = Servlets.locate(getServletContext(), request, uri, null);
		Https.include(ctx, request, response, uri, params, mode);
	}

	//-- Filter --//
	/** Called by the web container to indicate to a filter that it is
	 * being taken out of service.
	 *
	 * <p>This implementation does nothing.
	 */
	public void destroy() {
	}
	/** Called by the web container to indicate to a filter that it is
	 * being placed into service.
	 * However, deriving class usually doesn't override this method
	 * but overriding {@link #init()}.
	 */
	public final void init(FilterConfig config) throws ServletException {
		_config = config;
		init();
	}
}
