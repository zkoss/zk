/* PortletServletContext.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 12 17:12:44     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.Set;
import java.util.Enumeration;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;

/**
 * A facade servlet context based on a given portlet context.
 *
 * @author tomyeh
 */
public class PortletServletContext implements ServletContext {
	private final PortletContext _ctx;

	/** Returns an instance of ServletContext that representing
	 * the specified portlet context.
	 * <p>Use this method instead of the constructor.
	 */
	public static ServletContext getInstance(PortletContext ctx) {	
		return new PortletServletContext(ctx);
	}

	private PortletServletContext(PortletContext ctx) {
		if (ctx == null)
			throw new IllegalArgumentException("null");
		_ctx = ctx;
	}

	//-- ServletContext --//
	public Object getAttribute(String name) {
		return _ctx.getAttribute(name);
	}
	public Enumeration getAttributeNames() {
		return _ctx.getAttributeNames();
	}
	public ServletContext getContext(String path) {
		return null;
	}
	public String getInitParameter(String name) {
		return _ctx.getInitParameter(name);
	}
	public Enumeration getInitParameterNames() {
		return _ctx.getInitParameterNames();
	}
	public int 	getMajorVersion() {
		return _ctx.getMajorVersion();
	}
	public String getMimeType(String file) {
		return _ctx.getMimeType(file);
	}
	public int getMinorVersion() {
		return _ctx.getMinorVersion();
	}
	public RequestDispatcher getNamedDispatcher(String name) {
		final PortletRequestDispatcher prd = _ctx.getNamedDispatcher(name);
		return prd != null ? PortletServletDispatcher.getInstance(prd): null;
	}
	public String getRealPath(String path) {
		return _ctx.getRealPath(path);
	}
	public RequestDispatcher getRequestDispatcher(String path) {
		final PortletRequestDispatcher prd = _ctx.getRequestDispatcher(path);
		return prd != null ? PortletServletDispatcher.getInstance(prd): null;
	}
	public URL getResource(String path) throws MalformedURLException {
		return _ctx.getResource(path);
	}
	public InputStream getResourceAsStream(String path) {
		return _ctx.getResourceAsStream(path);
	}
 	public Set getResourcePaths(String path) {
 		return _ctx.getResourcePaths(path);
 	}
	public String getServerInfo() {
		return _ctx.getServerInfo();
	}
	/**
	 * @deprecated
	 */
	public Servlet getServlet(String name) {
		return null;
	}
	/**
	 * @deprecated
	 */
	public Enumeration getServletNames() {
		return null;
	}
	/**
	 * @deprecated
	 */
	public Enumeration getServlets() {
		return null;
	}
	public String getServletContextName() {
		return _ctx.getPortletContextName();
	}
	/**
	 * @deprecated
	 */
	public void log(Exception exception, String msg) {
	}
	public void log(String msg) {
		_ctx.log(msg);
	}
    public void log(String message, Throwable throwable) {
    	_ctx.log(message, throwable);
    }
	public void removeAttribute(String name) {
		_ctx.removeAttribute(name);
	}
	public void setAttribute(String name, Object object) {
		_ctx.setAttribute(name, object);
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}
}