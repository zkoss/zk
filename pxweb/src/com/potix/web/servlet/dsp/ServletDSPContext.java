/* ServletDSPContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 19:27:35     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;

import com.potix.util.resource.Locator;
import com.potix.el.RequestResolver;
import com.potix.el.EvaluatorImpl;

import com.potix.web.util.resource.ServletContextLocator;

/**
 * A DSP context based on HTTP servlet request and response.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/29 04:27:38 $
 */
public class ServletDSPContext implements DSPContext {
	private final Locator _locator;
	private final ServletContext _ctx;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;
	private Writer _out;
	private VariableResolver _resolver;
	private ExpressionEvaluator _eval;

	/**
	 * Constructor.
	 *
	 * @param locator used to locate resources, such as taglib.
	 * If null is specified, the locator for the specified servlet context is
	 * used. (In other words, we use ServletContextLocator if locator is null).
	 */
	public ServletDSPContext(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	Locator locator) {
		_locator = locator != null ? locator: new ServletContextLocator(ctx);
		_ctx = ctx;
		_request = request;
		_response = response;
	}

	//-- DSPContext --//
	public Locator getLocator() {
		return _locator;
	}
	public void setContentType(String ctype) {
		_response.setContentType(ctype);
	}
	public void setOut(Writer out) {
		_out = out;
	}

	//-- ELContext --//
	public Writer getOut() throws IOException {
		return _out != null ? _out: _response.getWriter();
	}
	public ServletRequest getRequest() {
		return _request;
	}
	public ServletResponse getResponse() {
		return _response;
	}
	public ServletContext getServletContext() {
		return _ctx;
	}
	public ExpressionEvaluator getExpressionEvaluator() {
		if (_eval == null)
			_eval = new EvaluatorImpl();
		return _eval;
	}
	public VariableResolver getVariableResolver() {
		if (_resolver == null)
			_resolver = new RequestResolver(_ctx, _request, _response);
		return _resolver;
	}
}
