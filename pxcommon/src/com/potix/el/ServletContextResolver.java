/* ServletContextResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Aug 20 19:59:17     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.ELException;

import com.potix.el.impl.AttributesMap;

/**
 * Used to wrap a servlet context to provide JSP-like evaluation.
 * In most cases, what you need is {@link RequestResolver},
 * which this class is mainly used to evaluate
 * expression that DO NOT depend on request. For example, you want to
 * evaluate some expression during initializing a servlet.
 *
 * <p>To avoid user from misuse, it throws IllegalStateException
 * if variables that depend on request or session are accessed.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:27:17 $
 */
public class ServletContextResolver implements VariableResolver {
	private final ServletContext _ctx;
	/** A fake page context implementation. */
	private PageContextImpl _pc;

	/** Servlet context-based resolver.
	 * @param ctx the context; never null
	 */
	public ServletContextResolver(ServletContext ctx) {
		if (ctx == null)
			throw new NullPointerException();
		_ctx = ctx;
	}

	//-- extra --//
	/** Returns the page context. */
	public PageContext getPageContext() {
		if (_pc == null)
			_pc = new PageContextImpl();
		return _pc;
	}
	/** Returns the request. */
    public ServletRequest getRequest() {
    	throw new IllegalStateException("You cannot access request or session here");
    }
	/** Returns the response. */
    public ServletResponse getResponse() {
    	throw new IllegalStateException("You cannot access response here");
    }
	/** Returns the context. */
    public ServletContext getServletContext() {
    	return _ctx;
    }

	//-- VariableResovler --//
	public Object resolveVariable (String pName) throws ELException {
		if ("pageContext".equals(pName)) {
			return getPageContext();
		} else if ("pageScope".equals(pName) || "requestScope".equals(pName)
		|| "sessionScope".equals(pName) || "param".equals(pName)
		|| "paramValues".equals(pName) || "header".equals(pName)
		|| "headerValues".equals(pName) || "cookie".equals(pName)) {
	    	throw new IllegalStateException("You cannot access request or session here");
		} else if ("applicationScope".equals(pName)) {
			return new AttributesMap() {
				protected Enumeration getKeys() {
					return _ctx.getAttributeNames();
				}
				protected Object getValue(String key) {
					return _ctx.getAttribute(key);
				}
				protected void setValue(String key, Object val) {
					_ctx.setAttribute(key, val);
				}
				protected void removeValue(String key) {
					_ctx.removeAttribute(key);
				}
			};
		} else if ("initParam".equals(pName)) {
			return new AttributesMap() {
				protected Enumeration getKeys() {
					return _ctx.getInitParameterNames();
				}
				protected Object getValue(String key) {
					return _ctx.getInitParameter(key);
				}
			};
		}
		return _ctx.getAttribute(pName);
			//according EL spec, we have to search attribute
	}

	/** Fake page context implementation.
	 * It is too costly to implement PageContext,
	 * but only implement a subset that VariableResolver might need
	 * (i.e., only getters are implemented).
	 */
	private class PageContextImpl extends PageContext {
	    public Exception getException() {
	    	return null;
	    }
	    public Object getPage() {
	    	throw new IllegalStateException("You cannot access request or session here");
	    }
	    public ServletRequest getRequest() {
	    	throw new IllegalStateException("You cannot access request or session here");
	    }
	    public ServletResponse getResponse() {
	    	throw new IllegalStateException("You cannot access response here");
	    }
	    public ServletConfig getServletConfig() {
	    	return null;
	    }
	    public ServletContext getServletContext() {
	    	return _ctx;
	    }
	    public HttpSession getSession() {
	    	throw new IllegalStateException("You cannot access request or session here");
	    }
		public VariableResolver getVariableResolver() {
			return ServletContextResolver.this;
		}
		public ExpressionEvaluator getExpressionEvaluator() {
	    	return new EvaluatorImpl();
	    }
 
		public void forward(String relativeUrlPath) {
	    	throw new UnsupportedOperationException();
	    }
		public void include(String relativeUrlPath) {
	    	throw new UnsupportedOperationException();
	    }
		public void include(String relativeUrlPath, boolean flush) {
	    	throw new UnsupportedOperationException();
	    }
		public void handlePageException(Exception e) {
	    	throw new UnsupportedOperationException();
	    }
		public void handlePageException(Throwable e) {
	    	throw new UnsupportedOperationException();
	    }
		public void initialize(Servlet servlet, ServletRequest request,
		ServletResponse response, String errorPageURL, boolean needsSession,
		int bufferSize, boolean autoFlush) {
		}
		public BodyContent pushBody() {
 	    	throw new UnsupportedOperationException();
	    }
		public void release() {
		}
		public Object findAttribute(String name) {
	    	throw new UnsupportedOperationException();
	    }
		public Object getAttribute(String name) {
	    	throw new UnsupportedOperationException();
	    }
		public Object getAttribute(String name, int scope) {
	    	throw new UnsupportedOperationException();
	    }
		public Enumeration getAttributeNamesInScope(int scope) {
	    	throw new UnsupportedOperationException();
	    }
		public int getAttributesScope(String name) {
	    	throw new UnsupportedOperationException();
	    }
		public JspWriter getOut() {
	    	throw new UnsupportedOperationException();
	    }
		public JspWriter popBody() {
	    	throw new UnsupportedOperationException();
	    }
		public JspWriter pushBody(java.io.Writer writer) {
	    	throw new UnsupportedOperationException();
	    }
		public void removeAttribute(String name) {
	    	throw new UnsupportedOperationException();
	    }
		public void removeAttribute(String name, int scope) {
	    	throw new UnsupportedOperationException();
	    }
		public void setAttribute(String name, Object value) {
	    	throw new UnsupportedOperationException();
	    }
		public void setAttribute(String name, Object value, int scope) {
	    	throw new UnsupportedOperationException();
	    }
	}
}
