/* RenderHttpServletRequest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:58:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.portlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.RenderRequest;
import javax.portlet.PortletSession;

import com.potix.util.CollectionsX;

/**
 * A facade of RenderRequest that implements HttpServletRespose.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:27:37 $
 */
public class RenderHttpServletRequest implements HttpServletRequest {
	private final RenderRequest _req;
	public static HttpServletRequest getInstance(RenderRequest req) {
		if (req instanceof HttpServletRequest)
			return (HttpServletRequest)req;
		return new RenderHttpServletRequest(req);
	}
	private RenderHttpServletRequest(RenderRequest req) {
		if (req == null)
			throw new IllegalArgumentException("null");
		_req = req;
	}

	//-- ServletRequest --//
	public Object getAttribute(String name) {
		return _req.getAttribute(name);
	}
	public java.util.Enumeration getAttributeNames() {
		return _req.getAttributeNames();
	}
	public String getCharacterEncoding() {
		return null;
	}
	public int getContentLength() {
		return 0;
	}
	public String getContentType() {
		return null;
	}
	public javax.servlet.ServletInputStream getInputStream() {
		throw new UnsupportedOperationException();
	}
	public String getLocalAddr() {
		return null;
	}
	public java.util.Locale getLocale() {
		return _req.getLocale();
	}
	public java.util.Enumeration getLocales() {
		return _req.getLocales();
	}
	public String getLocalName() {
		return null;
	}
	public int getLocalPort() {
		return 80;
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
		return null;
	}
	public java.io.BufferedReader getReader() {
		throw new UnsupportedOperationException();
	}
	/**
	 * @deprecated
	 */
	public String getRealPath(String path) {
		return null;
	}
	public String getRemoteAddr() {
		return null;
	}
	public String getRemoteHost() {
		return null;
	}
	public int getRemotePort() {
		return 0;
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
	public void setCharacterEncoding(String env) {
	}

	//-- HttpServletRequest --//
	public String 	getAuthType() {
		return _req.getAuthType();
	}
	public String getContextPath() {
		return _req.getContextPath();
	}
	public javax.servlet.http.Cookie[] getCookies() {
		return new javax.servlet.http.Cookie[0];
	}
	public long getDateHeader(String name) {
		return 0;
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
		return 0;
	}
	public String getMethod() {
		return null;
	}
	public String getPathInfo() {
		return (String)getAttribute("javax.servlet.include.path_info");
	}
	public String getPathTranslated() {
		return null;
	}
	public String getQueryString() {
		return (String)getAttribute("javax.servlet.include.query_string");
	}
	public String getRemoteUser() {
		return _req.getRemoteUser();
	}
	public String getRequestedSessionId() {
		return _req.getRequestedSessionId();
	}
	public String getRequestURI() {
		return (String)getAttribute("javax.servlet.include.request_uri");
	}
	public StringBuffer getRequestURL() {
		return null;
	}
	public String getServletPath() {
		return (String)getAttribute("javax.servlet.include.servlet_path");
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
}
