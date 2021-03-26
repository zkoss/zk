/* PortletServletContext.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 12 17:12:44     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

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
	@Override
    public Object getAttribute(String name) {
		return _ctx.getAttribute(name);
	}

	@Override
    public Enumeration getAttributeNames() {
		return _ctx.getAttributeNames();
	}

	@Override
    public ServletContext getContext(String path) {
		return null;
	}

	@Override
    public String getInitParameter(String name) {
		return _ctx.getInitParameter(name);
	}

	@Override
    public Enumeration getInitParameterNames() {
		return _ctx.getInitParameterNames();
	}

	@Override
    public int getMajorVersion() {
		return _ctx.getMajorVersion();
	}

	@Override
    public String getMimeType(String file) {
		return _ctx.getMimeType(file);
	}

	@Override
    public int getMinorVersion() {
		return _ctx.getMinorVersion();
	}

	@Override
    public RequestDispatcher getNamedDispatcher(String name) {
		final PortletRequestDispatcher prd = _ctx.getNamedDispatcher(name);
		return prd != null ? PortletServletDispatcher.getInstance(prd) : null;
	}

	@Override
    public String getRealPath(String path) {
		return _ctx.getRealPath(path);
	}

	@Override
    public RequestDispatcher getRequestDispatcher(String path) {
		final PortletRequestDispatcher prd = _ctx.getRequestDispatcher(path);
		return prd != null ? PortletServletDispatcher.getInstance(prd) : null;
	}

	@Override
    public URL getResource(String path) throws MalformedURLException {
		return _ctx.getResource(path);
	}

	@Override
    public InputStream getResourceAsStream(String path) {
		return _ctx.getResourceAsStream(path);
	}

	@Override
    public Set getResourcePaths(String path) {
		return _ctx.getResourcePaths(path);
	}

	@Override
    public String getServerInfo() {
		return _ctx.getServerInfo();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public Servlet getServlet(String name) {
		return null;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public Enumeration getServletNames() {
		return null;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public Enumeration getServlets() {
		return null;
	}

	@Override
    public String getServletContextName() {
		return _ctx.getPortletContextName();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public void log(Exception exception, String msg) {
	}

	@Override
    public void log(String msg) {
		_ctx.log(msg);
	}

	@Override
    public void log(String message, Throwable throwable) {
		_ctx.log(message, throwable);
	}

	@Override
    public void removeAttribute(String name) {
		_ctx.removeAttribute(name);
	}

	@Override
    public void setAttribute(String name, Object object) {
		_ctx.setAttribute(name, object);
	}

	@Override
    public String getContextPath() {
        return _ctx.getContextPath();
	}

    @Override
    public int getEffectiveMajorVersion() {
        return _ctx.getEffectiveMajorVersion();
    }

    @Override
    public int getEffectiveMinorVersion() {
        return _ctx.getEffectiveMinorVersion();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public Dynamic addServlet(String servletName, String className) {
        return null;
    }

    @Override
    public Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    @Override
    public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    @Override
    public Dynamic addJspFile(String servletName, String jspFile) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName,
            Class<? extends Filter> filterClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return _ctx.getClassLoader();
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return -1;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {

    }

    @Override
    public String getResponseCharacterEncoding() {
        return null;
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {

    }
}