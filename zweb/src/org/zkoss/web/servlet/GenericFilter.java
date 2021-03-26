/* GenericFilter.java


	Purpose: A generic class to help implementing servlet filter
	Description: 
	History:
		2001/11/13 14:46:46, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.http.Https;

/**
 * A generic class to help implementing servlet filter.
 * Its role is like jakarta.servlet.GenericServlet to jakarta.servlet.Servlet.
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
	
	/** Called by the web container to indicate to a filter that it is
	 * being placed into service.
	 * However, deriving class usually doesn't override this method
	 * but overriding {@link #init()}.
	 */
	public final void init(FilterConfig config) throws ServletException {
		_config = config;
		init();
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
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String page, Map params,
			int mode) throws ServletException, java.io.IOException {
		Https.sendRedirect(getServletContext(), request, response, page, params, mode);
	}

	/** Forward to the specified page.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void forward(ServletRequest request, ServletResponse response, String uri)
			throws ServletException, java.io.IOException {
		Https.forward(getServletContext(), request, response, uri, null, 0);
	}

	/** Forward to the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void forward(ServletRequest request, ServletResponse response, String uri, Map params, int mode)
			throws ServletException, java.io.IOException {
		Https.forward(getServletContext(), request, response, uri, params, mode);
	}

	/** Includes the specified page.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void include(ServletRequest request, ServletResponse response, String uri)
			throws ServletException, java.io.IOException {
		Https.include(getServletContext(), request, response, uri, null, 0);
	}

	/** Includes the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void include(ServletRequest request, ServletResponse response, String uri, Map params, int mode)
			throws ServletException, java.io.IOException {
		Https.include(getServletContext(), request, response, uri, params, mode);
	}

	//-- Filter --//
	/** Called by the web container to indicate to a filter that it is
	 * being taken out of service.
	 *
	 * <p>This implementation does nothing.
	 */
	public void destroy() {
	}
}
