/* PageRequestContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 15:35:29     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;

import org.zkoss.xel.VariableResolver;

/**
 * A request context that is based on the page context
 * (import javax.servlet.jsp.PageContext).
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class PageRequestContext implements RequestContext {
	private final PageContext _pgctx;

	public PageRequestContext(PageContext pgctx) {
		if (pgctx == null)
			throw new IllegalArgumentException();
		_pgctx = pgctx;
	}

	public Writer getOut() throws IOException {
		return _pgctx.getOut();
	}
	public ServletRequest getRequest() {
		return _pgctx.getRequest();
	}
	public ServletResponse getResponse() {
		return _pgctx.getResponse();
	}
	public ServletContext getServletContext() {
		return _pgctx.getServletContext();
	}
	public VariableResolver getVariableResolver() {
		return new ELXelResolver(_pgctx.getVariableResolver());
	}
}
