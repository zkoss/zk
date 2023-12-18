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
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.zkoss.util.CollectionsX;
import org.zkoss.web.Attributes;

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
	public Object getAttribute(String name) {
		final String val = _attrs.get(name);
		return val != null ? val : _req.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		final Enumeration _e = _req.getAttributeNames();
		final Iterator _it = _attrs.keySet().iterator();
		return new Enumeration() {
			Object _next;

			{
				next();
			}

			public boolean hasMoreElements() {
				return _next != null;
			}

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

	public String getCharacterEncoding() {
		return _enc;
	}

	public int getContentLength() {
		return -1;
	}

	public String getContentType() {
		final String ct = _req.getResponseContentType();
		return ct != null ? ct : "text/html";
	}

	public javax.servlet.ServletInputStream getInputStream() {
		return new javax.servlet.ServletInputStream() {
			public boolean isFinished() {
				return false;
			}

			public boolean isReady() {
				return false;
			}

			public void setReadListener(ReadListener readListener) {

			}

			public int read() {
				return -1;
			}
		};
	}

	public String getLocalAddr() {
		return _hreq != null ? _hreq.getLocalAddr() : "";
	}

	public java.util.Locale getLocale() {
		return _req.getLocale();
	}

	public java.util.Enumeration getLocales() {
		return _req.getLocales();
	}

	public String getLocalName() {
		return _hreq != null ? _hreq.getLocalName() : "";
	}

	public int getLocalPort() {
		return _hreq != null ? _hreq.getLocalPort() : -1;
	}

	public String getParameter(String name) {
		return _req.getParameter(name);
	}

	public java.util.Map getParameterMap() {
		return _req.getParameterMap();
	}

	public java.util.Enumeration getParameterNames() {
		return _req.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		return _req.getParameterValues(name);
	}

	public String getProtocol() {
		return "HTTP/1.0";
	}

	public java.io.BufferedReader getReader() {
		return new java.io.BufferedReader(new java.io.StringReader(""));
	}

	/**
	 * @deprecated
	 */
	public String getRealPath(String path) {
		return _hreq != null ? _hreq.getRealPath(path) : null;
	}

	public String getRemoteAddr() {
		return _hreq != null ? _hreq.getRemoteAddr() : "";
	}

	public String getRemoteHost() {
		return _hreq != null ? _hreq.getRemoteHost() : "";
	}

	public int getRemotePort() {
		return _hreq != null ? _hreq.getRemotePort() : -1;
	}

	public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
		return _hreq != null ? _hreq.getRequestDispatcher(path) : null; //implies we don't support relative URI
	}

	public String getScheme() {
		return _req.getScheme();
	}

	public String getServerName() {
		return _req.getServerName();
	}

	public int getServerPort() {
		return _req.getServerPort();
	}

	public boolean isSecure() {
		return _req.isSecure();
	}

	public void removeAttribute(String name) {
		_req.removeAttribute(name);
	}

	public void setAttribute(String name, Object o) {
		_req.setAttribute(name, o);
	}

	public void setCharacterEncoding(String enc) throws java.io.UnsupportedEncodingException {
		//Ensure the specified encoding is valid
		byte[] buffer = new byte[1];
		buffer[0] = (byte) 'a';
		new String(buffer, enc); // dummy to check encoding

		_enc = enc;
	}

	//-- HttpServletRequest --//
	public String getAuthType() {
		return _req.getAuthType();
	}

	public String getContextPath() {
		return _attrs.get(Attributes.INCLUDE_CONTEXT_PATH);
	}

	public javax.servlet.http.Cookie[] getCookies() {
		return _hreq != null ? _hreq.getCookies() : new javax.servlet.http.Cookie[0];
	}

	public long getDateHeader(String name) {
		return _hreq != null ? _hreq.getDateHeader(name) : -1;
	}

	public String getHeader(String name) {
		return _hreq != null ? _hreq.getHeader(name) : null;
	}

	public java.util.Enumeration getHeaderNames() {
		return _hreq != null ? _hreq.getHeaderNames() : CollectionsX.EMPTY_ENUMERATION;
	}

	public java.util.Enumeration getHeaders(String name) {
		return _hreq != null ? _hreq.getHeaders(name) : CollectionsX.EMPTY_ENUMERATION;
	}

	public int getIntHeader(String name) {
		return _hreq != null ? _hreq.getIntHeader(name) : -1;
	}

	public String getMethod() {
		return _hreq != null ? _hreq.getMethod() : "GET";
	}

	public String getPathInfo() {
		return _hreq != null ? _hreq.getPathInfo() : _attrs.get(Attributes.INCLUDE_PATH_INFO);
	}

	public String getPathTranslated() {
		return _hreq != null ? _hreq.getPathTranslated() : null;
	}

	public String getQueryString() {
		return _hreq != null ? _hreq.getQueryString() : _attrs.get(Attributes.INCLUDE_QUERY_STRING);
	}

	public String getRemoteUser() {
		return _req.getRemoteUser();
	}

	public String getRequestedSessionId() {
		return _req.getRequestedSessionId();
	}

	public String getRequestURI() {
		return _hreq != null ? _hreq.getRequestURI() : _attrs.get(Attributes.INCLUDE_REQUEST_URI);
	}

	public StringBuffer getRequestURL() {
		return _hreq != null ? _hreq.getRequestURL() : new StringBuffer();
	}

	public String getServletPath() {
		return _hreq != null ? _hreq.getServletPath() : _attrs.get(Attributes.INCLUDE_SERVLET_PATH);
	}

	public HttpSession getSession() {
		return PortletHttpSession.getInstance(_req.getPortletSession());
	}

	public HttpSession getSession(boolean create) {
		final PortletSession sess = _req.getPortletSession(create);
		return sess != null ? PortletHttpSession.getInstance(sess) : null;
	}

	public java.security.Principal getUserPrincipal() {
		return _req.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	/**
	 * @deprecated
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		return _req.isRequestedSessionIdValid();
	}

	public boolean isUserInRole(String role) {
		return _req.isUserInRole(role);
	}

	public String changeSessionId() {
		return _hreq != null ? _hreq.changeSessionId() : null;
	}

	public boolean authenticate(HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		return _hreq != null ? _hreq.authenticate(httpServletResponse) : false;
	}

	public void login(String s, String s1) throws ServletException {
		if (_hreq != null)
			_hreq.login(s, s1);
	}

	public void logout() throws ServletException {
		if (_hreq != null)
			_hreq.logout();
	}

	public Collection<Part> getParts() throws IOException, ServletException {
		return _hreq != null ? _hreq.getParts() : null;
	}

	public Part getPart(String s) throws IOException, ServletException {
		return _hreq != null ? _hreq.getPart(s) : null;
	}

	public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass)
			throws IOException, ServletException {
		return _hreq != null ? _hreq.upgrade(aClass) : null;
	}

	public long getContentLengthLong() {
		return _hreq != null ? _hreq.getContentLengthLong() : -1;
	}

	public ServletContext getServletContext() {
		return _hreq != null ? _hreq.getServletContext() : null;
	}

	public AsyncContext startAsync() throws IllegalStateException {
		return _hreq != null ? _hreq.startAsync() : null;
	}

	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		return _hreq != null ? _hreq.startAsync(servletRequest, servletResponse) : null;
	}

	public boolean isAsyncStarted() {
		return _hreq != null && _hreq.isAsyncStarted();
	}

	public boolean isAsyncSupported() {
		return _hreq != null && _hreq.isAsyncSupported();
	}

	public AsyncContext getAsyncContext() {
		return _hreq != null ? _hreq.getAsyncContext() : null;
	}

	public DispatcherType getDispatcherType() {
		return _hreq != null ? _hreq.getDispatcherType() : null;
	}

	//Object//
	public int hashCode() {
		return _req.hashCode();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		ResourceRequest val = o instanceof ResourceRequest ? (ResourceRequest) o
				: o instanceof ResourceHttpServletRequest ? ((ResourceHttpServletRequest) o)._req : null;
		return val != null && val.equals(_req);
	}
}
