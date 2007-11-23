/* ExecutionImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 14:14:02     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.Enumeration;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.el.RequestResolver;
import org.zkoss.el.PageContext;
import org.zkoss.el.impl.AttributesMap;
import org.zkoss.xel.XelException;
import org.zkoss.idom.Document;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.BufferedResponse;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.el.ELContext;
import org.zkoss.web.el.PageELContext;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.AbstractExecution;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * An {@link org.zkoss.zk.ui.Execution} implementation for HTTP request
 * and response.
 *
 * @author tomyeh
 */
public class ExecutionImpl extends AbstractExecution {
	private final ServletContext _ctx;
	private final ServletRequest _request;
	private final ServletResponse _response;
	private final ELContext _elctx;
	private final Map _attrs;
	private boolean _voided;

	/** Constructs an execution for HTTP request.
	 * @param creating which page is being creating for this execution, or
	 * null if none is being created.
	 * {@link #isAsyncUpdate} returns based on this.
	 */
	public ExecutionImpl(ServletContext ctx, ServletRequest request,
	ServletResponse response, Desktop desktop, Page creating) {
		super(desktop, creating, new RequestResolver(ctx, request, response));
		_ctx = ctx;
		_request = request;
		_response = response;

		final PageContext pgctx = (PageContext)
			getVariableResolver().resolveVariable("pageContext");
		if (pgctx == null)
			throw new UiException("Unable to resolve pageContext");
		_elctx = new PageELContext(pgctx);

		_attrs = new AttributesMap() {
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

	//-- super --//
	protected ELContext getELContext() {
		return _elctx;
	}

	//-- Execution --//
	public String[] getParameterValues(String name) {
		return _request.getParameterValues(name);
	}
	public String getParameter(String name) {
		return _request.getParameter(name);
	}
	public Map getParameterMap() {
		return _request.getParameterMap();
	}

	public void include(Writer out, String page, Map params, int mode)
	throws IOException {
		try {
			Servlets.include(_ctx, _request,
				BufferedResponse.getInstance(_response, out),
				page, params, mode);
		} catch (ServletException ex) {
			throw new UiException(ex);
		}
	}
	public void include(String page)
	throws IOException {
		include(null, page, null, 0);
	}
	public void forward(Writer out, String page, Map params, int mode)
	throws IOException {
		if (getVisualizer().isEverAsyncUpdate())
			throw new IllegalStateException("Use sendRedirect instead when processing user's request");

		setVoided(true);

		try {
			Servlets.forward(_ctx, _request,
				BufferedResponse.getInstance(_response, out),
				page, params, mode);
		} catch (ServletException ex) {
			throw new UiException(ex);
		}
	}
	public void forward(String page)
	throws IOException {
		forward(null, page, null, 0);
	}
	public boolean isIncluded() {
		return Servlets.isIncluded(_request);
	}
	public boolean isForwarded() {
		return Servlets.isForwarded(_request);
	}

	public boolean isVoided() {
		return _voided;
	}
	public void setVoided(boolean voided) {
		_voided = voided;
	}

	public String encodeURL(String uri) {
		try {
			return Encodes.encodeURL(_ctx, _request, _response, uri);
		} catch (ServletException ex) {
			throw new UiException(ex);
		}
	}

	public Principal getUserPrincipal() {
		return _request instanceof HttpServletRequest ?
			((HttpServletRequest)_request).getUserPrincipal(): null;
	}
	public boolean isUserInRole(String role) {
		return _request instanceof HttpServletRequest &&
			((HttpServletRequest)_request).isUserInRole(role);
	}
	public String getRemoteUser() {
		return _request instanceof HttpServletRequest ?
			((HttpServletRequest)_request).getRemoteUser(): null;
	}
	public String getRemoteName() {
		return _request.getRemoteHost();
	}
	public String getRemoteAddr() {
		return _request.getRemoteAddr();
	}
	public String getServerName() {
		return _request.getServerName();
	}
	public int getServerPort() {
		return _request.getServerPort();
	}
	public String getLocalName() {
		return _request.getLocalName();
	}
	public String getLocalAddr() {
		return _request.getLocalAddr();
	}
	public int getLocalPort() {
		return _request.getLocalPort();
	}
	public String getContextPath() {
		return _request instanceof HttpServletRequest ?
			((HttpServletRequest)_request).getContextPath(): null;
	}

	public PageDefinition getPageDefinition(String uri) {
		//Note: we have to go thru UiFactory (so user can override it)
		uri = toAbsoluteURI(uri, false);
		final PageDefinition pagedef = ((WebAppCtrl)getDesktop().getWebApp()).
			getUiFactory().getPageDefinition(newRequestInfo(uri), uri);
		if (pagedef == null)
			throw new UiException("Page not found: "+uri);
		return pagedef;
	}
	public PageDefinition getPageDefinitionDirectly(String content, String ext) {
		//Note: we have to go thru UiFactory (so user can override it)
		return ((WebAppCtrl)getDesktop().getWebApp()).getUiFactory()
			.getPageDefinitionDirectly(newRequestInfo(null), content, ext);
	}
	public PageDefinition getPageDefinitionDirectly(Document content, String ext) {
		//Note: we have to go thru UiFactory (so user can override it)
		return ((WebAppCtrl)getDesktop().getWebApp()).getUiFactory()
			.getPageDefinitionDirectly(newRequestInfo(null), content, ext);
	}
	public PageDefinition getPageDefinitionDirectly(Reader reader, String ext)
	throws IOException {
		//Note: we have to go thru UiFactory (so user can override it)
		return ((WebAppCtrl)getDesktop().getWebApp()).getUiFactory()
			.getPageDefinitionDirectly(newRequestInfo(null), reader, ext);
	}
	private RequestInfo newRequestInfo(String uri) {
		final Desktop dt = getDesktop();
		return new RequestInfoImpl(
			dt, _request, PageDefinitions.getLocator(getDesktop().getWebApp(), uri));
	}

	public void setHeader(String name, String value) {
		if (_response instanceof HttpServletResponse)
			((HttpServletResponse)_response).setHeader(name, value);
	}

	public Object getRequestAttribute(String name) {
		return _request.getAttribute(name);
	}
	public void setRequestAttribute(String name, Object value) {
		_request.setAttribute(name, value);
	}

	public boolean isBrowser() {
		return true;
	}
	public boolean isRobot() {
		return Servlets.isRobot(_request);
	}
	public boolean isExplorer() {
		return Servlets.isExplorer(_request);
	}
	public boolean isExplorer7() {
		return Servlets.isExplorer7(_request);
	}
	public boolean isGecko() {
		return Servlets.isGecko(_request);
	}
	public boolean isSafari() {
		return Servlets.isSafari(_request);
	}
	public boolean isMilDevice() {
		return Servlets.isMilDevice(_request);
	}

	public Object getNativeRequest() {
		return _request;
	}
	public Object getNativeResponse() {
		return _response;
	}

	public Object getAttribute(String name) {
		return _request.getAttribute(name);
	}
	public void setAttribute(String name, Object value) {
		_request.setAttribute(name, value);
	}
	public void removeAttribute(String name) {
		_request.removeAttribute(name);
	}

	public Map getAttributes() {
		return _attrs;
	}
}
