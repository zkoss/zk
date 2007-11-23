/* PageContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Oct 27 09:53:05     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.ExpressionEvaluator;

/**
 * Represents a page context.
 * The pageContext variable in EL expressions references to an instance
 * of this interface.
 *
 * <p>It is a replacement of javax.servlet.jsp.PageContext, since
 * ZK doesn't depend on JSP.
 *
 * @author tomyeh
 * @since 2.4.2
 */
public interface PageContext {
	/** Returns the writer, never null.
	 */
	public Writer getOut() throws IOException;
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
	/** Returns the expression evaluator.
	 */
	public ExpressionEvaluator getExpressionEvaluator();
}
