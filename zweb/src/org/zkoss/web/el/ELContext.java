/* ELContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 15 21:01:44     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.el;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ExpressionEvaluator;

/**
 * Represents the current context that is used to evaluate EL expressions.
 * ZK EL functions (see {@link org.zkoss.web.fn.ServletFns}) assume that
 * {@link ELContexts#getCurrent} provdies the current context to
 * evaluate EL expressions.
 *
 * <p>Thus, to use ZK EL functions, {@link ELContexts#push} must be called
 * first before evaluating any EL expressions.
 * However, you rarely need to call {@link ELContexts#push} explicitly,
 * since DSP's interpretation (and ZUML's) has pushed the context.
 *
 * <p>You need to push the context only if you want to use ZK EL functions
 * in the environment other than DSP and ZK. For example, JSP pages.
 *
 * @author tomyeh
 */
public interface ELContext {
	/** Returns the writer, never null.
	 */
	public Writer getOut() throws IOException;
	/** Returns the request, or null if not available.
	 */
	public ServletRequest getRequest();
	/** Returns the response, or null if not available.
	 */
	public ServletResponse getResponse();
	/** Returns the request, or null if not available.
	 */
	public ServletContext getServletContext();
	/** Returns the expression evaluator.
	 */
	public ExpressionEvaluator getExpressionEvaluator();
	/** Returns the variable resolver.
	 */
	public VariableResolver getVariableResolver();
}
