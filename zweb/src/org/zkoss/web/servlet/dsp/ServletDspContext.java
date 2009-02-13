/* ServletDspContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 19:27:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.resource.Locator;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expressions;

import org.zkoss.web.util.resource.ServletContextLocator;
import org.zkoss.web.servlet.xel.RequestXelResolver;

/**
 * A DSP context based on HTTP servlet request and response.
 *
 * @author tomyeh
 */
public class ServletDspContext implements DspContext {
	private final Locator _locator;
	private final ServletContext _ctx;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;
	private Writer _out;
	private VariableResolver _resolver;
	private ExpressionFactory _expf;

	/**
	 * Constructor.
	 *
	 * @param locator used to locate resources, such as taglib.
	 * If null is specified, the locator for the specified servlet context
	 * is used. (In other words, we use ServletContextLocator(ctx), if locator is null).
	 */
	public ServletDspContext(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	Locator locator) {
		this(ctx, request, response, null, locator);
	}
	/**
	 * Constructor with the specified writer.
	 *
	 * @param locator used to locate resources, such as taglib.
	 * If null is specified, the locator for the specified servlet context
	 * is used. (In other words, we use ServletContextLocator(ctx), if locator is null).
	 * @param out the output to generate the result.
	 * If null, it is the same as {@link #ServletDspContext(ServletContext,HttpServletRequest,HttpServletResponse,Locator)}
	 * In other words, response.getWriter() is used.
	 * @since 2.4.1
	 */
	public ServletDspContext(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	Writer out, Locator locator) {
		_locator = locator != null ? locator: new ServletContextLocator(ctx);
		_ctx = ctx;
		_request = request;
		_response = response;
		_out = out;
	}

	//-- DspContext --//
	public Locator getLocator() {
		return _locator;
	}
	public void setContentType(String ctype) {
		_response.setContentType(ctype);
	}
	public void setOut(Writer out) {
		_out = out;
	}

	//-- XelContext --//
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
	public VariableResolver getVariableResolver() {
		if (_resolver == null)
			_resolver =
				new RequestXelResolver(_ctx, _request, _response) {
					public ExpressionFactory getExpressionFactory() {
						return ServletDspContext.this.getExpressionFactory();
					}
				};
		return _resolver;
	}
	private ExpressionFactory getExpressionFactory() {
		if (_expf == null)
			_expf = Expressions.newExpressionFactory(); //TODO: expfcls
		return _expf;
	}
	public FunctionMapper getFunctionMapper() {
		return null;
	}
}
