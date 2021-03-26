/* ResourceHttpServletRequest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 6, 2013 2:22:02 PM , Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.zkoss.util.CollectionsX;
import org.zkoss.web.Attributes;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * A facade of ResourceRequest that implements HttpServletRequest.
 * 
 * @author Vincent
 * @since 6.5.2
 */
public class ResourceHttpServletRequest implements HttpServletRequest {
	private final ResourceRequest _req;
	private final HttpServletRequest _hreq;
	private String _enc = "UTF-8";
	private final Map<String, String> _attrs = new HashMap<String, String>(8);

	public static HttpServletRequest getInstance(ResourceRequest req) {
		if (req instanceof HttpServletRequest)
			return (HttpServletRequest) req;
		return new ResourceHttpServletRequest(req);
	}

	private ResourceHttpServletRequest(ResourceRequest req) {
		if (req == null)
			throw new IllegalArgumentException("null");
		_req = req;
		_hreq = getHttpServletRequest(req);

		String ctxpath = req.getContextPath();
		if (ctxpath == null)
			ctxpath = "";
		_attrs.put(Attributes.INCLUDE_CONTEXT_PATH, ctxpath);
		_attrs.put(Attributes.INCLUDE_SERVLET_PATH, "");
		_attrs.put(Attributes.INCLUDE_PATH_INFO, "");
		_attrs.put(Attributes.INCLUDE_QUERY_STRING, "");
		_attrs.put(Attributes.INCLUDE_REQUEST_URI, ctxpath);
	}

	/**
	 * Returns the HTTP servlet request associated with the render request, or
	 * null if not found.
	 */
	protected HttpServletRequest getHttpServletRequest(ResourceRequest req) {
		try {
			Method m;
			try {
				m = req.getClass().getMethod("getHttpServletRequest");
			} catch (NoSuchMethodException ex) {
				try {
					m = req.getClass().getMethod("getRealRequest");
				} catch (NoSuchMethodException e) {
					m = req.getClass().getMethod("getRequest");
				}
			}
			return (HttpServletRequest) m.invoke(req);
		} catch (Throwable ex) {
			return null;
		}
	}

	//-- ServletRequest --//
	@Override
    public Object getAttribute(String name) {
		final String val = _attrs.get(name);
		return val != null ? val : _req.getAttribute(name);
	}

	@Override
    public Enumeration getAttributeNames() {
		final Enumeration _e = _req.getAttributeNames();
		final Iterator _it = _attrs.keySet().iterator();
		return new Enumeration() {
			Object _next;

			{
				next();
			}

			@Override
            public boolean hasMoreElements() {
				return _next != null;
			}

			@Override
            public Object nextElement() {
				Object next = _next;
				next();
				return next;
			}

			private void next() {
				_next = null;
				while (_e.hasMoreElements()) {
					Object next = _e.nextElement();
					if (!_attrs.containsKey(next)) {
						_next = next;
						return; //done
					}
				}
				if (_it.hasNext())
					_next = _it.next();
			}
		};
	}

	public ResourceRequest getResourceRequest() {
		return _req;
	}

	@Override
    public String getCharacterEncoding() {
		return _enc;
	}

	@Override
    public int getContentLength() {
		return -1;
	}

	@Override
    public String getContentType() {
		final String ct = _req.getResponseContentType();
		return ct != null ? ct : "text/html";
	}

	@Override
    public jakarta.servlet.ServletInputStream getInputStream() {
		return new jakarta.servlet.ServletInputStream() {
            private ReadListener _listener;
			@Override
            public int read() {
				return -1;
			}

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                _listener = readListener;
            }
		};
	}

	@Override
    public String getLocalAddr() {
		return _hreq != null ? _hreq.getLocalAddr() : "";
	}

	@Override
    public java.util.Locale getLocale() {
		return _req.getLocale();
	}

	@Override
    public java.util.Enumeration getLocales() {
		return _req.getLocales();
	}

	@Override
    public String getLocalName() {
		return _hreq != null ? _hreq.getLocalName() : "";
	}

	@Override
    public int getLocalPort() {
		return _hreq != null ? _hreq.getLocalPort() : -1;
	}

	@Override
    public String getParameter(String name) {
		return _req.getParameter(name);
	}

	@Override
    public java.util.Map getParameterMap() {
		return _req.getParameterMap();
	}

	@Override
    public java.util.Enumeration getParameterNames() {
		return _req.getParameterNames();
	}

	@Override
    public String[] getParameterValues(String name) {
		return _req.getParameterValues(name);
	}

	@Override
    public String getProtocol() {
		return "HTTP/1.0";
	}

	@Override
    public java.io.BufferedReader getReader() {
		return new java.io.BufferedReader(new java.io.StringReader(""));
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public String getRealPath(String path) {
		return _hreq != null ? _hreq.getRealPath(path) : null;
	}

	@Override
    public String getRemoteAddr() {
		return _hreq != null ? _hreq.getRemoteAddr() : "";
	}

	@Override
    public String getRemoteHost() {
		return _hreq != null ? _hreq.getRemoteHost() : "";
	}

	@Override
    public int getRemotePort() {
		return _hreq != null ? _hreq.getRemotePort() : -1;
	}

	@Override
    public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
		return _hreq != null ? _hreq.getRequestDispatcher(path) : null; //implies we don't support relative URI
	}

	@Override
    public String getScheme() {
		return _req.getScheme();
	}

	@Override
    public String getServerName() {
		return _req.getServerName();
	}

	@Override
    public int getServerPort() {
		return _req.getServerPort();
	}

	@Override
    public boolean isSecure() {
		return _req.isSecure();
	}

	@Override
    public void removeAttribute(String name) {
		_req.removeAttribute(name);
	}

	@Override
    public void setAttribute(String name, Object o) {
		_req.setAttribute(name, o);
	}

	@Override
    public void setCharacterEncoding(String enc) throws java.io.UnsupportedEncodingException {
		//Ensure the specified encoding is valid
		byte[] buffer = new byte[1];
		buffer[0] = (byte) 'a';
		new String(buffer, enc); // dummy to check encoding

		_enc = enc;
	}

	//-- HttpServletRequest --//
	@Override
    public String getAuthType() {
		return _req.getAuthType();
	}

	@Override
    public String getContextPath() {
		return _attrs.get(Attributes.INCLUDE_CONTEXT_PATH);
	}

	@Override
    public jakarta.servlet.http.Cookie[] getCookies() {
		return _hreq != null ? _hreq.getCookies() : new jakarta.servlet.http.Cookie[0];
	}

	@Override
    public long getDateHeader(String name) {
		return _hreq != null ? _hreq.getDateHeader(name) : -1;
	}

	@Override
    public String getHeader(String name) {
		return _hreq != null ? _hreq.getHeader(name) : null;
	}

	@Override
    public java.util.Enumeration getHeaderNames() {
		return _hreq != null ? _hreq.getHeaderNames() : CollectionsX.EMPTY_ENUMERATION;
	}

	@Override
    public java.util.Enumeration getHeaders(String name) {
		return _hreq != null ? _hreq.getHeaders(name) : CollectionsX.EMPTY_ENUMERATION;
	}

	@Override
    public int getIntHeader(String name) {
		return _hreq != null ? _hreq.getIntHeader(name) : -1;
	}

	@Override
    public String getMethod() {
		return _hreq != null ? _hreq.getMethod() : "GET";
	}

	@Override
    public String getPathInfo() {
		return _hreq != null ? _hreq.getPathInfo() : _attrs.get(Attributes.INCLUDE_PATH_INFO);
	}

	@Override
    public String getPathTranslated() {
		return _hreq != null ? _hreq.getPathTranslated() : null;
	}

	@Override
    public String getQueryString() {
		return _hreq != null ? _hreq.getQueryString() : _attrs.get(Attributes.INCLUDE_QUERY_STRING);
	}

	@Override
    public String getRemoteUser() {
		return _req.getRemoteUser();
	}

	@Override
    public String getRequestedSessionId() {
		return _req.getRequestedSessionId();
	}

	@Override
    public String getRequestURI() {
		return _hreq != null ? _hreq.getRequestURI() : _attrs.get(Attributes.INCLUDE_REQUEST_URI);
	}

	@Override
    public StringBuffer getRequestURL() {
		return _hreq != null ? _hreq.getRequestURL() : new StringBuffer();
	}

	@Override
    public String getServletPath() {
		return _hreq != null ? _hreq.getServletPath() : _attrs.get(Attributes.INCLUDE_SERVLET_PATH);
	}

	@Override
    public HttpSession getSession() {
		return PortletHttpSession.getInstance(_req.getPortletSession());
	}

	@Override
    public HttpSession getSession(boolean create) {
		final PortletSession sess = _req.getPortletSession(create);
		return sess != null ? PortletHttpSession.getInstance(sess) : null;
	}

	@Override
    public java.security.Principal getUserPrincipal() {
		return _req.getUserPrincipal();
	}

	@Override
    public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	@Override
    public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
    public boolean isRequestedSessionIdValid() {
		return _req.isRequestedSessionIdValid();
	}

	@Override
    public boolean isUserInRole(String role) {
		return _req.isUserInRole(role);
	}

	//Object//
	@Override
    public int hashCode() {
		return _req.hashCode();
	}

	@Override
    public boolean equals(Object o) {
		if (this == o)
			return true;
		ResourceRequest val = o instanceof ResourceRequest ? (ResourceRequest) o
				: o instanceof ResourceHttpServletRequest ? ((ResourceHttpServletRequest) o)._req : null;
		return val != null && val.equals(_req);
	}

    @Override
    public long getContentLengthLong() {
        return _hreq.getContentLengthLong();
    }

    @Override
    public ServletContext getServletContext() {
        return _hreq.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return _hreq.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return _hreq.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return _hreq.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return _hreq.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return _hreq.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return _hreq.getDispatcherType();
    }

    @Override
    public String changeSessionId() {
        return _hreq.changeSessionId();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return _hreq.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {
        _hreq.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        _hreq.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return _hreq.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return _hreq.getPart(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return _hreq.upgrade(handlerClass);
    }
}
