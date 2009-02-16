/* ExtendletContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 29 18:27:04     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.resource.Locator;

/**
 * This interface defines an extended context that
 * {@link org.zkoss.web.servlet.Servlets#getRequestDispatcher} will try to
 * resolve.
 * When {@link org.zkoss.web.servlet.Servlets#getRequestDispatcher} is
 * called, it detects whether ~xxx/ is specified.
 * If specified, it looks up any extended context
 * is registered as xxx. If found, the extend context is used.
 * If not found, it looks up any servlet context called xxx.
 *
 * <p>To registers an extended context, use 
 * {@link org.zkoss.web.servlet.Servlets#addExtendletContext}.
 *
 * @author tomyeh
 */
public interface ExtendletContext {
	/** Returns the encoded URL.
	 * The returned URL is also encoded with HttpServletResponse.encodeURL.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link org.zkoss.web.servlet.Servlets#locate(javax.servlet.ServletContext, ServletRequest, String, org.zkoss.util.resource.Locator)}
	 * for details. 
	 *
	 * @param uri it must be empty or starts with "/". It might contain
	 * "*" for current browser code and Locale.
	 * @return the complete URL (excluding the machine name).
	 * It includes the context path and the servelt to interpret
	 * this extended resource.
	 */
	public String encodeURL(ServletRequest request,
	ServletResponse response, String uri)
	throws ServletException, IOException;
	/** Returns  the encoded URL for send redirect.
	 * The URL is also encoded with HttpServletResposne.encodeRedirectURL.
	 */
	public String encodeRedirectURL(HttpServletRequest request,
	HttpServletResponse response, String uri, Map params, int mode);
	/* Returns the request dispatcher that acts as a wrapper for
	 * the resource located at the given path, or null if not found.
	 */
	public RequestDispatcher getRequestDispatcher(String uri);

	/** Includes the specified page.
	 *
	 * <p>Note: if you want to include the content into a different
	 * writer, you have to use {@link org.zkoss.web.servlet.BufferedResponse}
	 * to 'wrap' the writer in the response.
	 *
	 * @since 3.5.2 
	 */
	public void include(HttpServletRequest request,
	HttpServletResponse response, String uri, Map params)
	throws ServletException, IOException;

	/** Returns the URL of the specified URI, or null if not found.
	 */
	public URL getResource(String uri);
	/** Returns the resource of the specified URI as input stream,
	 * or null if not found.
	 */
	public InputStream getResourceAsStream(String uri);

	/** Returns the servlet context.
	 */
	public ServletContext getServletContext();
	/** Returns the locator of this context used to locate resorces.
	 */
	public Locator getLocator();
	/** Tests whether to compress the specified extension, e.g, "js" and
	 * "css".
	 *
	 * <p>It returns false if the request is included by other Serlets.
	 */
	public boolean shallCompress(ServletRequest request, String ext);
}
