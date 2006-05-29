/* DHtmlLayoutServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.potix.mesg.Messages;
import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.lang.Exceptions;
import com.potix.util.CollectionsX;
import com.potix.util.prefs.Apps;
import com.potix.util.logging.Log;
import com.potix.util.resource.Labels;
import com.potix.el.impl.AttributesMap;

import com.potix.web.Attributes;
import com.potix.web.servlet.Charsets;
import com.potix.web.servlet.Servlets;
import com.potix.web.servlet.http.Https;
import com.potix.web.servlet.http.Encodes;
import com.potix.web.util.resource.ServletContextLocator;
import com.potix.web.util.resource.ServletLabelLocator;
import com.potix.web.util.resource.ClassWebResource;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.sys.DesktopCacheProvider;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.RequestInfo;
import com.potix.zk.ui.sys.SessionCtrl;
import com.potix.zk.ui.sys.SessionsCtrl;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.sys.ExecutionsCtrl;
import com.potix.zk.ui.sys.ConfigParser;
import com.potix.zk.ui.impl.AbstractWebApp;
import com.potix.zk.ui.impl.RequestInfoImpl;
import com.potix.zk.ui.impl.SessionDesktopCacheProvider;
import com.potix.zk.ui.impl.DesktopImpl;
import com.potix.zk.ui.impl.UiEngineImpl;
import com.potix.zk.ui.impl.UiFactoryImpl;

/**
 * Used to process the request for a ZUML page.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.49 $ $Date: 2006/05/29 04:28:05 $
 */
public class DHtmlLayoutServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlLayoutServlet.class);

	/** A session attribute. */
	private static final String ATTR_SESS = "javax.potix.zk.ui.Session";
		//Naming with javax to be able to shared among portlets

	/** A context attribute for storing an instance of this class. */
	/*package*/ static final String ATTR_LAYOUT_SERVLET
		= "com.potix.zk.ui.LayoutServlet";
	/** A request attribute to store the current desktop.
	 * Because we store this portlet request, we have to name it
	 * with javax such that it is visible to other servlets and portlets.
	 */
	/*package*/ static final String DESKTOP = "javax.potix.zk.ui.desktop";

	private WebApp _wapp;
	private ServletContext _ctx;
	private String _updateURI;
	private ClassWebResource _cwr;

	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

		if (log.debugable()) log.debug("Starting DHtmlLayoutServlet at "+config.getServletContext());

		_ctx = config.getServletContext();
		if (_ctx.getAttribute(ATTR_LAYOUT_SERVLET) != null)
			throw new ServletException("Only one layout servlet is allowed in one context: "+_ctx);
		_ctx.setAttribute(ATTR_LAYOUT_SERVLET, this);

		Labels.the().register(new ServletLabelLocator(_ctx));

		_updateURI = config.getInitParameter("update-uri");
		if (_updateURI == null
		|| (_updateURI = _updateURI.trim()).length() == 0
		|| _updateURI.charAt(0) != '/')
			throw new ServletException("The update-uri parameter must be specified and starts with /");
		if (_updateURI.indexOf(';') >= 0 || _updateURI.indexOf('?') >= 0)
			throw new ServletException("The update-uri parameter cannot contain ';' or '?'");
			//Jetty will encode URL by appending ';jsess..' and we have to
			//remove it under certain situations, so not alow it
		if (_updateURI.charAt(_updateURI.length() - 1) == '\\') {
			if (_updateURI.length() == 1)
				throw new ServletException("The update-uri parameter cannot contain only '/'");
			_updateURI = _updateURI.substring(0, _updateURI.length() - 1);
				//remove the trailing '\\' if any
		}

		final Configuration cfg = new Configuration();
		try {
			final URL cfgUrl = _ctx.getResource("/WEB-INF/zk.xml");
			if (cfgUrl != null)
				new ConfigParser()
					.parse(cfgUrl, cfg, new ServletContextLocator(_ctx));
		} catch (Throwable ex) {
			log.error("Unable to load /WEB-INF/zk.xml", ex);
		}

		_cwr = ClassWebResource.getInstance(_ctx, _updateURI);

		String clsnm = Apps.getProperty("com.potix.zk.ui.engine.class", null);
		final UiEngine engine;
		if (clsnm == null) {
			engine = new UiEngineImpl();
		} else {
			try {
				engine = (UiEngine)Classes.newInstanceByThread(clsnm);
			} catch (Exception ex) {
				throw (ServletException)
					Exceptions.wrap(ex, ServletException.class, "Unable to construct "+clsnm);
			}
		}

		clsnm = Apps.getProperty("com.potix.zk.ui.cache-provider.class", null);
		final DesktopCacheProvider provider;
		if (clsnm == null) {
			provider = new SessionDesktopCacheProvider();
		} else {
			try {
				provider = (DesktopCacheProvider)Classes.newInstanceByThread(clsnm);
			} catch (Exception ex) {
				throw (ServletException)
					Exceptions.wrap(ex, ServletException.class, "Unable to construct "+clsnm);
			}
		}

		clsnm = Apps.getProperty("com.potix.zk.ui.ui-factory.class", null);
		final UiFactory factory;
		if (clsnm == null) {
			factory = new UiFactoryImpl();
		} else {
			try {
				factory = (UiFactory)Classes.newInstanceByThread(clsnm);
			} catch (Exception ex) {
				throw (ServletException)
					Exceptions.wrap(ex, ServletException.class, "Unable to construct "+clsnm);
			}
		}

		_wapp = new MyWebApp(cfg, engine, provider, factory);
		engine.start(_wapp);
		provider.start(_wapp);
		factory.start(_wapp);
	}
	public void destroy() {
		if (_wapp != null) {
			final WebAppCtrl wappc = (WebAppCtrl)_wapp;
			wappc.getUiFactory().stop(_wapp);
			wappc.getDesktopCacheProvider().stop(_wapp);
			wappc.getUiEngine().stop(_wapp);
		}
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	/** Returns the handler to retrieve resource from class path,
	 * under /web.
	 */
	public final ClassWebResource getClassWebResource() {
		return _cwr;
	}

	/** Returns the Web application.
	 * Notice: a Web application is allocated for each servlet.
	 */
	public final WebApp getWebApp() {
		return _wapp;
	}

	//-- static --//

	/** Returns the layout servlet.
	 */
	public static final
	DHtmlLayoutServlet getLayoutServlet(ServletContext ctx)
	throws ServletException {
		final DHtmlLayoutServlet svl =
			(DHtmlLayoutServlet)ctx.getAttribute(ATTR_LAYOUT_SERVLET);
		if (svl == null)
			throw new ServletException("The Layout Servlet not found. Make sure <load-on-startup> is specified for "+DHtmlLayoutServlet.class.getName());
		return svl;
	}

	/** Returns the session. */
	public static final
	Session getSession(ServletContext ctx, HttpServletRequest request)
	throws ServletException {
		final HttpSession hsess = request.getSession();
		Session sess = getSession(hsess);
		if (sess != null)
			return sess;

		if (D.ON && log.debugable()) log.debug("Creating a new sess for "+hsess);
		final DHtmlLayoutServlet loader = getLayoutServlet(ctx);
		sess = new SessionImpl(
			hsess, loader.getWebApp(),
			request.getRemoteAddr(), request.getRemoteHost());
		hsess.setAttribute(ATTR_SESS, sess);

		//Note: we set timeout here, because HttpSession might have been created
		//by other servlet or filter
		final int NOT_FOUND = -1356;
		final int v = Apps.getInteger(
			"com.potix.zk.session.MaxInactiveInterval", NOT_FOUND);
		if (v != NOT_FOUND)
			hsess.setMaxInactiveInterval(v);
		return sess;
	}
	/** Returns the session of the specified HTTP session, or null if n/a. */
	/*package*/ static final Session getSession(HttpSession hsess) {
		return (Session)hsess.getAttribute(ATTR_SESS);
	}
	/** Called when a HTTP session listner is notified.
	 * <p>Once called the session is cleaned. All desktops are dropped.
	 */
	/*package*/ static final
	void onSessionDestroyed(HttpSession hsess) {
		final Session sess =
			(Session)DHtmlLayoutServlet.getSession(hsess);
		if (sess != null) {
			try {
				final WebApp wapp =
					getLayoutServlet(hsess.getServletContext()).getWebApp();
				((WebAppCtrl)wapp)
					.getDesktopCacheProvider().sessionDestroyed(sess);
			} catch (ServletException ex) {
				log.error("Failed to cleanup session", ex);
			}

			((SessionCtrl)sess).onDestroyed();
			hsess.removeAttribute(ATTR_SESS);
		}
	}

	//-- super --//
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final Object old = Charsets.setup(request, response);
		final Session sess = getSession(getServletContext(), request);
		SessionsCtrl.setCurrent(sess);
		try {
			process(sess, request, response);
		} finally {
			SessionsCtrl.setCurrent(null);
			Charsets.cleanup(old);
		}
	}
	protected
	void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	//-- private --//
	private void process(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final String path = Https.getThisServletPath(request);
		if (D.ON && log.debugable()) log.debug("Creates from "+path);

		final Desktop desktop = getDesktop(sess, request, path);
		final RequestInfo ri = new RequestInfoImpl(
			_wapp, sess, desktop, request,
			PageDefinitions.getLocator(_ctx, path));
		final WebAppCtrl wappc = (WebAppCtrl)_wapp;
		final UiFactory uf = wappc.getUiFactory();
		final PageDefinition pagedef = uf.getPageDefinition(ri, path);
		if (pagedef == null) {
			if (Servlets.isIncluded(request)) {
				final String msg = Messages.get(MZk.PAGE_NOT_FOUND,
					new Object[] {path});
				final Map attrs = new HashMap();
				attrs.put(Attributes.ALERT_TYPE, "error");
				attrs.put(Attributes.ALERT, msg);
				Servlets.include(_ctx, request, response,
					"~./html/alert.dsp", attrs, Servlets.PASS_THRU_ATTR);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			return;
		}

		final Page page = uf.newPage(ri, pagedef, path);
		final Execution exec = new ExecutionImpl(
			_ctx, request, response, desktop, page);
		wappc.getUiEngine().execNewPage(exec, page, response.getWriter());
	}
	/** Returns the desktop of the specified request.
	 * @param path the path of the ZUML page.
	 */
	/*package*/ Desktop getDesktop(Session sess, ServletRequest request,
	String path) {
		Desktop desktop = (Desktop)request.getAttribute(DESKTOP);
		if (desktop == null) {
			if (D.ON && log.debugable()) log.debug("Create desktop for "+path);
			request.setAttribute(DESKTOP,
				desktop = newDesktop(sess, request, path));
		}
		return desktop;
	}
	/** Creates an desktop. */
	/*package*/ Desktop newDesktop(Session sess, Object request, String path) {
		return ((WebAppCtrl)_wapp).getUiFactory().newDesktop(
			new RequestInfoImpl(_wapp, sess, null, request,
				PageDefinitions.getLocator(_ctx, path)),
			_updateURI, path);
	}
	/** Sets the desktop to the specified request.
	 * Used by {@link com.potix.zk.au.http.DHtmlUpdateServlet}.
	 */
	public static void setDesktop(HttpServletRequest request,
	Desktop desktop) {
		if (D.ON) {
			final Desktop dt = (Desktop)request.getAttribute(DESKTOP);
			assert dt == null || dt == desktop: "old:"+dt+", new:"+desktop;
		}
		request.setAttribute(DESKTOP, desktop);
	}

	//-- inner classes --//
	private class MyWebApp extends AbstractWebApp {
		private MyWebApp(Configuration cfg, UiEngine engine,
		DesktopCacheProvider provider, UiFactory factory) {
			super(cfg, engine, provider, factory);
		}
		private final Map _attrs = new AttributesMap() {
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

		public Object getAttribute(String name) {
			return _ctx.getAttribute(name);
		}
		public void setAttribute(String name, Object value) {
			_ctx.setAttribute(name, value);
		}
		public void removeAttribute(String name) {
			_ctx.removeAttribute(name);
		}
		public Map getAttributes() {
			return _attrs;
		}

		public WebApp getWebApp(String uripath) {
			final ServletContext another = _ctx.getContext(uripath);
			if (another != null) {
				final DHtmlLayoutServlet svl =
					(DHtmlLayoutServlet)_ctx.getAttribute(ATTR_LAYOUT_SERVLET);
				if (svl != null)
					return svl.getWebApp();
			}
			return null;
		}
		public URL getResource(String path) {
			try {
				return _ctx.getResource(path);
			} catch (MalformedURLException ex) {
				throw new UiException("Failed to retrieve "+path, ex);
			}
		}
		public InputStream getResourceAsStream(String path) {
			return _ctx.getResourceAsStream(path);
		}

		public String getInitParameter(String name) {
			return _ctx.getInitParameter(name);
		}
		public Iterator getInitParameterNames() {
			return new CollectionsX.EnumerationIterator(
				_ctx.getInitParameterNames());
		}
		public String getRealPath(String path) {
			return _ctx.getRealPath(path);
		}
		public String getMimeType(String file) {
			return _ctx.getMimeType(file);
		}
		public Set getResourcePaths(String path) {
			return _ctx.getResourcePaths(path);
		}
		public Object getNativeContext() {
			return _ctx;
		}
	}
}
