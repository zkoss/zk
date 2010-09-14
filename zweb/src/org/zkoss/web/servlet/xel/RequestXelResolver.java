/* RequestXelResolver.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 29 18:45:46     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

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

import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

/**
 * A XEL variable resolver that is based on Servlet request, response,
 * and EL.
 *
 * @author tomyeh
 * @since 3.0.0
 */
abstract public class RequestXelResolver implements VariableResolver {
	private final ServletContext _ctx;
	private final ServletRequest _request;
	private final ServletResponse _response;
	private HttpSession _sess;
	private Map<String, Object> _reqScope, _sessScope, _appScope;
	/** A fake page context implementation. */
	private PageContext _pc;
	/** cached cookies. */
	private Map<String, Cookie> _cookies;

	/** Request-based resolver.
	 * @param ctx the context; which might be null
	 * @param request the request. It cannot be null.
	 * @param response the response, which might bell.
	 * @exception IllegalArgumentException if request is null.
	 */
	public RequestXelResolver(ServletContext ctx, ServletRequest request,
	ServletResponse response) {
		_ctx = ctx;
		_request = request;
		_response = response;
	}
	public RequestXelResolver(ServletContext ctx, ServletRequest request) {
		this(ctx, request, null);
	}
	public RequestXelResolver(ServletRequest request) {
		this(null, request, null);
	}

	//-- extra --//
	/** Returns the expression factory (never null).
	 * <p>The deriving class must override it.
	 */
	abstract public ExpressionFactory getExpressionFactory();

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
	@SuppressWarnings("unchecked")
	public Object resolveVariable (String name) throws XelException {
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
			return _request != null ? new ParameterMap(_request): Collections.EMPTY_MAP;
		} else if ("paramValues".equals(name)) {
			return _request != null ? _request.getParameterMap(): Collections.EMPTY_MAP;
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
				protected void setValue(String key, Object val) {
					throw new UnsupportedOperationException("readonly");
				}
				protected void removeValue(String key) {
					throw new UnsupportedOperationException("readonly");
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
				protected void setValue(String key, Object val) {
					throw new UnsupportedOperationException("readonly");
				}
				protected void removeValue(String key) {
					throw new UnsupportedOperationException("readonly");
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
				protected void setValue(String key, Object val) {
					throw new UnsupportedOperationException("readonly");
				}
				protected void removeValue(String key) {
					throw new UnsupportedOperationException("readonly");
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

			_cookies = new HashMap<String, Cookie>();	
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
	private Map<String, Object> getRequestScope() {
		if (_reqScope != null)
			return _reqScope;
		if (_request == null)
			return Collections.emptyMap();
		return _reqScope = new RequestScope(_request);
	}
	private Map<String, Object> getSessionScope() {
		if (_sessScope != null)
			return _sessScope;
		final HttpSession sess = getSession();
		if (sess == null)
			return _sessScope = Collections.emptyMap();
		return _sessScope = new AttributesMap() {
			@SuppressWarnings("unchecked")
			protected Enumeration<String> getKeys() {
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
	private Map<String, Object> getApplicationScope() {
		if (_appScope != null)
			return _appScope;
		if (_ctx == null)
			return _appScope = Collections.emptyMap();
		return _appScope = new AttributesMap() {
			@SuppressWarnings("unchecked")
			protected Enumeration<String> getKeys() {
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

	/** An implemnetation of PageContext. */
	private class PageContextImpl implements PageContext {
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
			return RequestXelResolver.this.getSession();
		}
		public VariableResolver getVariableResolver() {
			return RequestXelResolver.this;
		}
	}
}
