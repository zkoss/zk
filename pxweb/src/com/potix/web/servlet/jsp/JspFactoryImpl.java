/* JspFactoryImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr  8 11:07:01     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.PageContext;

import com.potix.web.el.ELContexts;
import com.potix.web.el.ELContext;

/**
 * To intercept how a JSP page is rendered.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/29 04:27:43 $
 */
/*package*/ class JspFactoryImpl extends JspFactory {
	/** The factory to wrap. */
	private JspFactory _factory;
	public JspFactoryImpl(JspFactory factory) {
		if (factory == null)
			throw new NullPointerException("factory");
		if (factory instanceof JspFactoryImpl)
			throw new IllegalArgumentException("Don't wrap twice");
		_factory = factory;
	}

	public PageContext getPageContext(Servlet servlet,
	ServletRequest request, ServletResponse response,
	String errorPageURL, boolean needsSession,
	int bufferSize, boolean autoflush) {
		final PageContext pc = _factory.getPageContext(
			servlet, request, response,
			errorPageURL, needsSession, bufferSize, autoflush);
		ELContexts.push(pc);
		return pc;
	}
    public void releasePageContext(PageContext pc) {
    	ELContexts.pop();
    	_factory.releasePageContext(pc);
    }
    public JspEngineInfo getEngineInfo() {
    	return _factory.getEngineInfo();
    }
}
