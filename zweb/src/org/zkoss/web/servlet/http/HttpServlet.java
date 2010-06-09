/* HttpServlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 22 18:48:22     2003, Created by tomyeh

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.http;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.Servlets;

/**
 * Extended javax.servlet.http.HttpServlet to provide extra utilities.
 *
 * @author tomyeh
 */
public class HttpServlet extends javax.servlet.http.HttpServlet {
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
	protected void
	sendRedirect(HttpServletRequest request, HttpServletResponse response,
	String page, Map params, int mode)
	throws ServletException, java.io.IOException {
		Https.sendRedirect(getServletContext(),
			request, response, page, params, mode);
	}
	/** Forward to the specified page.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void forward(
	ServletRequest request, ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		forward(getServletContext(), request, response, uri, null, 0);
	}
	/** Forward to the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void forward(ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
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
	protected final void include(ServletRequest request,
	ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		include(getServletContext(), request, response, uri, null, 0);
	}
	/** Includes the specified page with parameters.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 * @since 3.6.3
	 */
	protected final void include(ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws ServletException, java.io.IOException {
		Https.include(getServletContext(), request, response, uri, params, mode);
	}
	/** @deprecated As of release 3.6.3, use {@link #forward(ServletRequest, ServletResponse, String)}
	 * instead.
	 */
	protected final void forward(ServletContext ctx,
	ServletRequest request, ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		forward(ctx, request, response, uri, null, 0);
	}
	/** @deprecated As of release 3.6.3, use {@link #forward(ServletRequest, ServletResponse, String, Map, int)}
	 * instead.
	 */
	protected final void forward(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws ServletException, java.io.IOException {
		Https.forward(ctx, request, response, uri, params, mode);
	}
	/** @deprecated As of release 3.6.3, use {@link #include(ServletRequest, ServletResponse, String)}
	 * instead.
	 */
	protected final void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri)
	throws ServletException, java.io.IOException {
		include(ctx, request, response, uri, null, 0);
	}
	/** @deprecated As of release 3.6.3, use {@link #include(ServletRequest, ServletResponse, String, Map, int)}
	 * instead.
	 */
	protected final void include(ServletContext ctx, ServletRequest request,
	ServletResponse response, String uri, Map params, int mode)
	throws ServletException, java.io.IOException {
		Https.include(ctx, request, response, uri, params, mode);
	}
}
