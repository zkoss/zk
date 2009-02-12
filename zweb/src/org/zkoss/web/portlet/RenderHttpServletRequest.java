/* RenderHttpServletRequest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:58:56     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.RenderRequest;
import javax.portlet.PortletSession;

import org.zkoss.util.CollectionsX;
import org.zkoss.web.Attributes;

/**
 * A facade of RenderRequest that implements HttpServletRespose.
 * 
 * @author tomyeh
 */
public class RenderHttpServletRequest implements HttpServletRequest {
	private final RenderRequest _req;
	private String _enc = "UTF-8";
	private final Map _attrs = new HashMap(9);

	public static HttpServletRequest getInstance(RenderRequest req) {
		if (req instanceof HttpServletRequest)
			return (HttpServletRequest)req;
		return new RenderHttpServletRequest(req);
	}
	private RenderHttpServletRequest(RenderRequest req) {
		if (req == null)
			throw new IllegalArgumentException("null");
		_req = req;

		String ctxpath = req.getContextPath();
		if (ctxpath == null) ctxpath = "";
		_attrs.put(Attributes.INCLUDE_CONTEXT_PATH, ctxpath);
		_attrs.put(Attributes.INCLUDE_SERVLET_PATH, "");
		_attrs.put(Attributes.INCLUDE_PATH_INFO, "");
		_attrs.put(Attributes.INCLUDE_QUERY_STRING, "");
		_attrs.put(Attributes.INCLUDE_REQUEST_URI, ctxpath);
	}

	//-- ServletRequest --//
	public Object getAttribute(String name) {
		final String val = (String)_attrs.get(name);
		return val != null ? val: _req.getAttribute(name);
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
	public String getCharacterEncoding() {
		return _enc;
	}
	public int getContentLength() {
		return -1;
	}
	public String getContentType() {
		final String ct = _req.getResponseContentType();
		return ct != null ? ct: "text/html";
	}
	public javax.servlet.ServletInputStream getInputStream() {
		return new javax.servlet.ServletInputStream() {
			public int read() {return -1;}
		};
	}
	public String getLocalAddr() {
		return "";
	}
	public java.util.Locale getLocale() {
		return _req.getLocale();
	}
	public java.util.Enumeration getLocales() {
		return _req.getLocales();
	}
	public String getLocalName() {
		return "";
	}
	public int getLocalPort() {
		return -1;
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
		return null;
	}
	public String getRemoteAddr() {
		return "";
	}
	public String getRemoteHost() {
		return "";
	}
	public int getRemotePort() {
		return -1;
	}
	public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
		return null; //implies we don't support relative URI
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
	public void setCharacterEncoding(String enc)
	throws java.io.UnsupportedEncodingException {
		//Ensure the specified encoding is valid
		byte buffer[] = new byte[1];
		buffer[0] = (byte) 'a';
		String dummy = new String(buffer, enc);

		_enc = enc;
	}

	//-- HttpServletRequest --//
	public String getAuthType() {
		return _req.getAuthType();
	}
	public String getContextPath() {
		return (String)_attrs.get(Attributes.INCLUDE_CONTEXT_PATH);
	}
	public javax.servlet.http.Cookie[] getCookies() {
		return new javax.servlet.http.Cookie[0];
	}
	public long getDateHeader(String name) {
		return -1; //not available
	}
	public String getHeader(String name) {
		return null;
	}
	public java.util.Enumeration getHeaderNames() {
		return CollectionsX.EMPTY_ENUMERATION;
	}
	public java.util.Enumeration getHeaders(String name) {
		return CollectionsX.EMPTY_ENUMERATION;
	}
	public int getIntHeader(String name) {
		return -1; //not available
	}
	public String getMethod() {
		return "GET";
	}
	public String getPathInfo() {
		return (String)_attrs.get(Attributes.INCLUDE_PATH_INFO);
	}
	public String getPathTranslated() {
		return null;
	}
	public String getQueryString() {
		return (String)_attrs.get(Attributes.INCLUDE_QUERY_STRING);
	}
	public String getRemoteUser() {
		return _req.getRemoteUser();
	}
	public String getRequestedSessionId() {
		return _req.getRequestedSessionId();
	}
	public String getRequestURI() {
		return (String)_attrs.get(Attributes.INCLUDE_REQUEST_URI);
	}
	public StringBuffer getRequestURL() {
		return new StringBuffer();
	}
	public String getServletPath() {
		return (String)_attrs.get(Attributes.INCLUDE_SERVLET_PATH);
	}
	public HttpSession getSession() {
		return PortletHttpSession.getInstance(_req.getPortletSession());
	}
	public HttpSession getSession(boolean create) {
		final PortletSession sess = _req.getPortletSession(create);
		return sess != null ? PortletHttpSession.getInstance(sess): null;
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

	//Object//
	public int hashCode() {
		return _req.hashCode();
	}
	public boolean equals(Object o) {
		RenderRequest val =
			o instanceof RenderRequest ? (RenderRequest)o:
			o instanceof RenderHttpServletRequest ? ((RenderHttpServletRequest)o)._req: null;
		return val != null && val.equals(_req);
	}
}
