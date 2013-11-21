/* ExecutionImpl.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 14:14:02     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import java.util.Date;
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

import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.resource.Locators;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.idom.Document;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.HttpBufferedResponse;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestXelResolver;
import org.zkoss.web.servlet.xel.AttributesMap;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.web.util.resource.Extendlet;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.impl.AbstractExecution;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.SimpleEvaluator;
import org.zkoss.zk.xel.impl.ExecutionResolver;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.impl.ScopeListeners;

/**
 * An {@link org.zkoss.zk.ui.Execution} implementation for HTTP request
 * and response.
 *
 * @author tomyeh
 */
public class ExecutionImpl extends AbstractExecution {
	private final ServletContext _ctx;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;
	private final RequestContext _xelctx;
	private final Map<String, Object> _attrs;
	private MyEval _eval;
	private ExecutionResolver _resolver;
	private final ScopeListeners _scopeListeners = new ScopeListeners(this);
	private boolean _voided;

	/** Constructs an execution for the given HTTP request.
	 * @param creating which page is being creating for this execution, or
	 * null if none is being created.
	 */
	public ExecutionImpl(ServletContext ctx, HttpServletRequest request,
	HttpServletResponse response, Desktop desktop, Page creating) {
		super(desktop, creating);
		_ctx = ctx;
		_request = request;
		_response = response;
		_xelctx = new ReqContext();


		// the execution may be fake if request is null.
		// Fixed for ZK-1890: Can't subscribe eventqueue in desktop cleanup
		if (request != null)
			Servlets.getBrowser(request); //update request info

		_attrs = new AttributesMap() {
			@SuppressWarnings("unchecked")
			protected Enumeration<String> getKeys() {
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

	public void onActivate() {
		super.onActivate();
		RequestContexts.push(_xelctx);
	}
	public void onDeactivate() {
		RequestContexts.pop();
		super.onDeactivate();
	}

	//-- Execution --//
	public String[] getParameterValues(String name) {
		return _request.getParameterValues(name);
	}
	public String getParameter(String name) {
		return _request.getParameter(name);
	}
	@SuppressWarnings("unchecked")
	public Map<String, String[]> getParameterMap() {
		return _request.getParameterMap();
	}

	public VariableResolver getVariableResolver() {
		if (_resolver == null)
			_resolver =
				new ExecutionResolver(this,
					new RequestXelResolver(_ctx, _request, _response) {
						public ExpressionFactory getExpressionFactory() {
							return ExecutionImpl.this.getExpressionFactory();
						}
					});
		return _resolver;
	}
	private ExpressionFactory getExpressionFactory() {
		//TODO: how to make it depends on page's expf
		return Expressions.newExpressionFactory();
	}

	public Evaluator getEvaluator(Page page, Class<? extends ExpressionFactory> expfcls) {
		if (page == null)
			page = getCurrentPage();

		if (page != null && expfcls == null)
			expfcls = page.getExpressionFactoryClass();

		if (_eval == null || _eval.page != page
		|| _eval.getExpressionFactoryClass() != expfcls)
			_eval = new MyEval(page, expfcls);
		return _eval;
	}
	public Evaluator getEvaluator(Component comp, Class<? extends ExpressionFactory> expfcls) {
		return getEvaluator(comp != null ? comp.getPage(): null, expfcls);
	}

	public Object evaluate(Component comp, String expr, Class<?> expectedType) {
		return evaluate0(comp, expr, expectedType,
			comp != null ? comp.getPage(): null);
	}
	public Object evaluate(Page page, String expr, Class<?> expectedType) {
		return evaluate0(page, expr, expectedType, page);
	}
	private Object evaluate0(Object self, String expr,
	Class<?> expectedType, Page page) {
		if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0) {
			if (expectedType == Object.class || expectedType == String.class)
				return expr;
			return Classes.coerce(expectedType, expr);
		}

		final Evaluator eval = getEvaluator(page, null);
		final Expression expression = eval.parseExpression(expr, expectedType);
		return self instanceof Page ?
			eval.evaluate((Page)self, expression):
			eval.evaluate((Component)self, expression);
	}

	public void include(Writer out, String page, Map<String, ?> params, int mode)
	throws IOException {
		try {
			if (!dispatch(out, page, params, mode, true))
				Servlets.include(_ctx, _request,
					HttpBufferedResponse.getInstance(_response, out),
					page, params, mode);
		} catch (ServletException ex) {
			throw new UiException(ex);
		}
	}
	private boolean dispatch(Writer out, String page, Map params, int mode,
	boolean include) throws IOException, ServletException {
		//FUTURE: handle if ~./, PASS_THRU_ATTR and with query string
		//In other words, we convert query string to params if
		//PASS_THRU_ATTR and ~./ (to have a better performance)
		if ((mode != PASS_THRU_ATTR && params != null)
		|| !page.startsWith("~./") || page.indexOf('?') >= 0)
			return false;

		//Bug 1801028: We cannot invoke ZumlExtendlet directly
		//The real reason is unknown yet -- it could be due to
		//the re-creation of ExecutionImpl
		//However, the performance is not a major issue, so just skip
		final ClassWebResource cwr =
			WebManager.getWebManager(_ctx).getClassWebResource();
		if (!isDirectInclude(cwr, page))
			return false;
		
		Object old = null;
		if (mode == PASS_THRU_ATTR) {
			old = _request.getAttribute(Attributes.ARG);
			if (params != null)
				_request.setAttribute(Attributes.ARG, params);
				//If params=null, use the 'inherited' one (same as Servlets.include)
		}

		final String attrnm = include ?
			"org.zkoss.web.servlet.include": "org.zkoss.web.servlet.forward";
		_request.setAttribute(attrnm, Boolean.TRUE);
			//so Servlets.isIncluded returns correctly
		try {
			cwr.service(_request,
				HttpBufferedResponse.getInstance(_response, out),
				page.substring(2));
		} finally {
			_request.removeAttribute(attrnm);
			if (mode == PASS_THRU_ATTR)
				_request.setAttribute(Attributes.ARG, old);
		}
		
		return true;
	}
	/** Returns whether the page can be directly included.
	 */
	private static boolean isDirectInclude(ClassWebResource cwr, String path) {
		final String ext = Servlets.getExtension(path);
		final Extendlet extlet = ext != null ? cwr.getExtendlet(ext): null;
		if (extlet != null) {
			try {
				return extlet.getFeature(Extendlet.ALLOW_DIRECT_INCLUDE);
			} catch (Throwable ex) { //backward compatibility
			}
		}
		return true;
	}
	public void include(String page)
	throws IOException {
		include(null, page, null, 0);
	}
	public void forward(Writer out, String page, Map<String, ?> params, int mode)
	throws IOException {
		final Visualizer uv = getVisualizer();
			//uv is null if it is called in DesktopInit (with TemporaryExecution)
		if (uv != null && uv.isEverAsyncUpdate())
			throw new IllegalStateException("Use sendRedirect instead when processing user's request");

		setVoided(true);

		try {
			if (!dispatch(out, page, params, mode, false))
				Servlets.forward(_ctx, _request,
					HttpBufferedResponse.getInstance(_response, out),
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

	public String locate(String path) {
		try {
			if (path.startsWith("~./")) {
				path = Servlets.locate(_ctx, _request,
					ClassWebResource.PATH_PREFIX + path.substring(2),
					Locators.getDefault());
				return "~." + path.substring(ClassWebResource.PATH_PREFIX.length());
			}
			return Servlets.locate(_ctx, _request, path, null);
		} catch (ServletException ex) {
			throw new UiException(ex);
		}
	}

	public boolean isVoided() {
		return _voided;
	}
	public void setVoided(boolean voided) {
		Object created = getAttribute(org.zkoss.zk.ui.impl.Attributes.PAGE_CREATED);
		if (created instanceof Boolean && ((Boolean)created).booleanValue())
			if (!getDesktop().getComponents().isEmpty())
				throw new IllegalStateException("The page has been created, so the execution cannot be voided!");
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
		return _request.getUserPrincipal();
	}
	public boolean isUserInRole(String role) {
		return _request.isUserInRole(role);
	}
	public String getRemoteUser() {
		return _request.getRemoteUser();
	}
	public String getRemoteHost() {
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
		final String s = _request.getContextPath();
		return s == null || "/".equals(s) ? "": s; //to avoid bug in some Web server
	}
	public String getScheme() {
		return _request.getScheme();
	}

	public PageDefinition getPageDefinition(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("uri not specified: "+uri);

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

	public void setContentType(String contentType) {
		if (_response != null)
			(_response).setContentType(contentType);
	}

	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isBrowser() {
		return true;
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isBrowser(String type) {
		return Servlets.isBrowser(_request, type);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isRobot() {
		return Servlets.isRobot(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isExplorer() {
		return Servlets.isExplorer(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isExplorer7() {
		return Servlets.isExplorer7(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isGecko() {
		return Servlets.isGecko(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isGecko3() {
		return Servlets.isGecko3(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isOpera() {
		return Servlets.isOpera(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isSafari() {
		return Servlets.isSafari(_request);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
	public boolean isHilDevice() {
		return Servlets.isHilDevice(_request);
	}
	
	public Double getBrowser(String name) {
		return Servlets.getBrowser(_request, name);
	}
	
	public String getBrowser() {
		return Servlets.getBrowser(_request);
	}
	
	public String getUserAgent() {
		return Servlets.getUserAgent(_request);
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
	public boolean hasAttribute(String name) {
		return getAttribute(name) != null; //Servlet limitation
	}
	public Object setAttribute(String name, Object value) {
		Object old = _request.getAttribute(name);
		_request.setAttribute(name, value);
		return old;
	}
	public Object removeAttribute(String name) {
		Object old = _request.getAttribute(name);
		_request.removeAttribute(name);
		return old;
	}

	public Map<String, Object> getAttributes() {
		return _attrs;
	}
	public boolean addScopeListener(ScopeListener listener) {
		return _scopeListeners.addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _scopeListeners.removeScopeListener(listener);
	}
	/** Returns all scope listeners.
	 */
	/*package*/ ScopeListeners getScopeListeners() {
		return _scopeListeners;
	}

	public String getHeader(String name) {
		return _request.getHeader(name);
	}
	@SuppressWarnings("unchecked")
	public Iterable<String> getHeaders(String name) {
		final Enumeration<String> headers = _request.getHeaders(name);
		return headers != null ? new Headers(name, headers): null;
			//Follow Servlet spec: null if not accessible
	}
	@SuppressWarnings("unchecked")
	public Iterable<String> getHeaderNames() {
		final Enumeration<String> headers = _request.getHeaderNames();
		return headers != null ? new HeaderNames(headers): null;
			//Follow Servlet spec: null if not accessible
	}
	public void setResponseHeader(String name, String value) {
		_response.setHeader(name, value);
	}
	public void setResponseHeader(String name, Date value) {
		_response.setDateHeader(name, value.getTime());
	}
	public void addResponseHeader(String name, String value) {
		_response.addHeader(name, value);
	}
	public void addResponseHeader(String name, Date value) {
		_response.addDateHeader(name, value.getTime());
	}
	public boolean containsResponseHeader(String name) {
		return _response.containsHeader(name);
	}

	private class Headers implements Iterable<String> {
		private final String _name;
		private Enumeration<String> _cache;
		private Headers(String name, Enumeration<String> cache) {
			_name = name;
			_cache = cache;
		}
		@SuppressWarnings("unchecked")
		public Iterator<String> iterator() {
			final Enumeration<String> headers;
			if (_cache != null) {
				headers = _cache;
				_cache = null; //used only once
			} else {
				headers = _request.getHeaders(_name);
			}
			return new CollectionsX.EnumerationIterator<String>(headers);
		}
	}
	private class HeaderNames implements Iterable<String> {
		private Enumeration<String> _cache;
		private HeaderNames(Enumeration<String> cache) {
			_cache = cache;
		}
		@SuppressWarnings("unchecked")
		public Iterator<String> iterator() {
			final Enumeration<String> headers;
			if (_cache != null) {
				headers = _cache;
				_cache = null; //used only once
			} else {
				headers = _request.getHeaderNames();
			}
			return new CollectionsX.EnumerationIterator<String>(headers);
		}
	}
	private class ReqContext implements RequestContext {
		public Writer getOut() throws IOException {
			return _response.getWriter();
		}
		public VariableResolver getVariableResolver() {
			return ExecutionImpl.this.getVariableResolver();
		}
		public ServletRequest getRequest() {
			return _request;
		}
		public ServletResponse getResponse() {
			return _response;
		}
		public ServletContext getServletContext() {
			return _ctx;
		}
	}
	private class MyEval extends SimpleEvaluator { //not serializable
		private Page page;

		private MyEval(Page page, Class<? extends ExpressionFactory> expfcls) {
			super(null, expfcls);
			this.page = page;
		}

		//super//
		public FunctionMapper getFunctionMapper(Object ref) {
			return page != null ? page.getFunctionMapper(): null;
		}
	}
}
