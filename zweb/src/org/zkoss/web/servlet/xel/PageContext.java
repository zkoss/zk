/* PageContext.java

	Purpose:
		
	Description:
		
	History:
		Sat Oct 27 09:53:05     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;

import org.zkoss.xel.VariableResolver;

/**
 * Represents a page context.
 * The pageContext variable in EL expressions references to an instance
 * of this interface.
 *
 * <p>It is a replacement of jakarta.servlet.jsp.PageContext, since
 * ZK doesn't depend on JSP.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface PageContext {
	/** The current request.
	 */
	public ServletRequest getRequest();

	/** The current response.
	 */
	public ServletResponse getResponse();

	/** The Servlet configuration.
	 */
	public ServletConfig getServletConfig();

	/** The Servlet context.
	 */
	public ServletContext getServletContext();

	/** The current session.
	 */
	public HttpSession getSession();

	/** The current variable resolver.
	 */
	public VariableResolver getVariableResolver();
}
