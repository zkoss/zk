/* RequestResolver.java

{{IS_NOTE
	$Id: RequestResolver.java,v 1.12 2006/02/27 03:41:52 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Mar 29 18:45:46     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.AbstractSet;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.ELException;

import com.potix.util.CollectionsX;
import com.potix.el.impl.StringKeysMap;
import com.potix.el.impl.AttributesMap;

/**
 * Used to wrap a request to provide JSP-like evaluation.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.12 $ $Date: 2006/02/27 03:41:52 $
 * @see ServletContextResolver
 */
public class RequestResolver implements VariableResolver {
	private final ServletContext _ctx;
	private final ServletRequest _request;
	private final ServletResponse _response;
	private HttpSession _sess;
	private Map _reqScope, _sessScope, _appScope;
	/** A fake page context implementation. */
	private PageContextImpl _pc;
	/** cached cookies. */
	private Map _cookies;

	/** Request-based resolver.
	 * @param ctx the context; which might be null
	 * @param request the request; never null.
	 * @param response the response, which might bell.
	 */
	public RequestResolver(ServletContext ctx, ServletRequest request,
	ServletResponse response) {
		if (request == null)
			throw new NullPointerException("request");
		_ctx = ctx;
		_request = request;
		_response = response;
	}
	public RequestResolver(ServletContext ctx, ServletRequest request) {
		this(ctx, request, null);
	}
	public RequestResolver(ServletRequest request) {
		this(null, request, null);
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
    	return _request;
    }
	/** Returns the response. */
    public ServletResponse getResponse() {
    	return _response;
    }
	/** Returns the context. */
    public ServletContext getServletContext() {
    	return _ctx;
    }

	//-- VariableResovler --//
	public Object resolveVariable (String name) throws ELException {
		if ("pageContext".equals(name)) {
			return getPageContext();
		} else if ("pageScope".equals(name)) {
			return Collections.EMPTY_MAP;
		} else if ("requestScope".equals(name)) {
			return getRequestScope();
		} else if ("sessionScope".equals(name)) {
			return getSessionScope();
		} else if ("applicationScope".equals(name)) {
			return getApplicationScope();
		} else if ("param".equals(name)) {
			return new ParamMap();
		} else if ("paramValues".equals(name)) {
			return _request.getParameterMap();
		} else if ("header".equals(name)) {
			if (!(_request instanceof HttpServletRequest))
				return Collections.EMPTY_MAP;

			final HttpServletRequest hreq = (HttpServletRequest)_request;
			return new AttributesMap() {
				protected Enumeration getKeys() {
					return hreq.getHeaderNames();
				}
				protected Object getValue(String key) {
					return hreq.getHeader(key);
				}
			};
		} else if ("headerValues".equals(name)) {
			if (!(_request instanceof HttpServletRequest))
				return Collections.EMPTY_MAP;

			final HttpServletRequest hreq = (HttpServletRequest)_request;
			return new AttributesMap() {
				//It is OK to cache because it is readonly
				private final Map _values = new HashMap();
				protected Enumeration getKeys() {
					return hreq.getHeaderNames();
				}
				protected Object getValue(String key) {
					Object o = _values.get(key);
					if (o == null) {
						final Enumeration e = hreq.getHeaders(key);
						if (e == null || !e.hasMoreElements())
							return null;

						final List l = new LinkedList();
						do {
							l.add(e.nextElement());
						} while (e.hasMoreElements());
						o = l.toArray(new String[l.size()]);
						_values.put(key, o);
					}
					return o;
				}
			};
		} else if ("initParam".equals(name)) {
			if (_ctx == null)
				return Collections.EMPTY_MAP;

			return new AttributesMap() {
				protected Enumeration getKeys() {
					return _ctx.getInitParameterNames();
				}
				protected Object getValue(String key) {
					return _ctx.getInitParameter(key);
				}
			};
		} else if ("cookie".equals(name)) {
			if (_cookies != null)
				return _cookies;

			final Cookie[] cookies;
			if (!(_request instanceof HttpServletRequest)
			|| (cookies = ((HttpServletRequest)_request).getCookies()) == null
			|| cookies.length == 0)
				return Collections.EMPTY_MAP;

			_cookies = new HashMap();	
			for (int j = cookies.length; --j >=0;)
				_cookies.put(cookies[j].getName(), cookies[j]);
		}

		return findAttribute(name);
			//according EL spec, we have to search attribute
	}
	private HttpSession getSession() {
		if (_sess != null)
			return _sess;

		if (!(_request instanceof HttpServletRequest))
			return null;
		return _sess = ((HttpServletRequest)_request).getSession(false);
	}
	private Object findAttribute(String name) {
		Object o = getRequestScope().get(name);
		if (o != null) return o;
		o = getSessionScope().get(name);
		return o != null ? o: getApplicationScope().get(name);
	}
	private Map getRequestScope() {
		if (_reqScope != null)
			return _reqScope;
		return _reqScope = new AttributesMap() {
			protected Enumeration getKeys() {
				return _request.getAttributeNames();
			}
			protected Object getValue(String key) {
				return _request.getAttribute(key);
			}
			protected void setValue(String key, Object val) {
				_request.setAttribute(key, val);
			}
			protected void removeValue(String key) {
				_request.removeAttribute(key);
			}
		};
	}
	private Map getSessionScope() {
		if (_sessScope != null)
			return _sessScope;
		final HttpSession sess = getSession();
		if (sess == null)
			return _sessScope = Collections.EMPTY_MAP;
		return _sessScope = new AttributesMap() {
			protected Enumeration getKeys() {
				return sess.getAttributeNames();
			}
			protected Object getValue(String key) {
				return sess.getAttribute(key);
			}
			protected void setValue(String key, Object val) {
				sess.setAttribute(key, val);
			}
			protected void removeValue(String key) {
				sess.removeAttribute(key);
			}
		};
	}
	private Map getApplicationScope() {
		if (_appScope != null)
			return _appScope;
		if (_ctx == null)
			return _appScope = Collections.EMPTY_MAP;
		return _appScope = new AttributesMap() {
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
	}

	/** Used to access attributes of request, session and context. */
	private class ParamMap extends StringKeysMap {
		private Set _entries;
		public Set entrySet() {
			if (_entries == null) {
				_entries = new AbstractSet() {
					public int size() {
						return ParamMap.this.size();
					}
					public boolean contains(Object o) {
						return ParamMap.this.containsKey(o);
					}
					public Iterator iterator() {
						return new EntryIter();
					}
				};
			}
			return _entries;
		}

		public int size() {
			return _request.getParameterMap().size();
		}
		public boolean containsKey(Object key) {
			return _request.getParameterMap().containsKey(key);
		}

		protected Object getValue(String key) {
			return _request.getParameter(key);
		}
		protected Enumeration getKeys() {
			return _request.getParameterNames();
		}
	} //ParamMap
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
	    	return null;
	    }
	    public ServletRequest getRequest() {
	    	return _request;
	    }
	    public ServletResponse getResponse() {
	    	return _response;
	    }
	    public ServletConfig getServletConfig() {
	    	return null;
	    }
	    public ServletContext getServletContext() {
	    	return _ctx;
	    }
	    public HttpSession getSession() {
	    	return RequestResolver.this.getSession();
	    }
		public VariableResolver getVariableResolver() {
			return RequestResolver.this;
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
			return RequestResolver.this.findAttribute(name);
	    }
		public Object getAttribute(String name) {
			return null;
	    }
		public Object getAttribute(String name, int scope) {
			switch (scope) {
			case PageContext.REQUEST_SCOPE:
				return getRequestScope().get(name);
			case PageContext.SESSION_SCOPE:
				return getSessionScope().get(name);
			case PageContext.APPLICATION_SCOPE:
				return getSessionScope().get(name);
			default:
				return null;
			}
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
	    }
		public void removeAttribute(String name, int scope) {
			switch (scope) {
			case PageContext.REQUEST_SCOPE:
				getRequestScope().remove(name); return;
			case PageContext.SESSION_SCOPE:
				getSessionScope().remove(name); return;
			case PageContext.APPLICATION_SCOPE:
				getSessionScope().remove(name); return;
			}
	    }
		public void setAttribute(String name, Object value) {
	    	throw new UnsupportedOperationException();
	    }
		public void setAttribute(String name, Object value, int scope) {
			if (value == null) {
				removeAttribute(name, scope);
				return;
			}
			switch (scope) {
			case PageContext.REQUEST_SCOPE:
				getRequestScope().put(name, value); return;
			case PageContext.SESSION_SCOPE:
				getSessionScope().put(name, value); return;
			case PageContext.APPLICATION_SCOPE:
				getSessionScope().put(name, value); return;
			}
	    }
	}
}
