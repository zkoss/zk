/* WebManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 15 13:28:19     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.RenderRequest;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Labels;

import org.zkoss.web.util.resource.ServletContextLocator;
import org.zkoss.web.util.resource.ServletLabelLocator;
import org.zkoss.web.util.resource.ServletLabelResovler;
import org.zkoss.web.util.resource.ClassWebResource;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.ConfigParser;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * A bridge bewteen Web server and ZK.
 *
 * <p>Each Web application that uses ZK will have an independent instance
 * of {@link WebManager}.
 *
 * @author tomyeh
 */
public class WebManager {
	private static final Log log = Log.lookup(WebManager.class);

	/** A session attribute. */
	private static final String ATTR_SESS = "javax.zkoss.zk.ui.Session";
		//Naming with javax to be able to shared among portlets

	/** A context attribute for storing an instance of this class. */
	/*package*/ static final String ATTR_WEB_MANAGER
		= "javax.zkoss.zk.ui.WebManager";
	/** A request attribute to store the current desktop.
	 * Because we store this portlet request, we have to name it
	 * with javax such that it is visible to other servlets and portlets.
	 */
	/*package*/ static final String ATTR_DESKTOP = "javax.zkoss.zk.ui.desktop";

	/** Map(ServletContext, List(ActivationListener)). */
	private static final Map _actListeners = new HashMap();
	/** Used to inter-communicate among portlet. */
	private final static ThreadLocal _reqLocal = new ThreadLocal();

	private final ServletContext _ctx;
	private final WebApp _wapp;
	private final String _updateURI;
	private final ClassWebResource _cwr;

	/** Creates a new Web
	 */
	/*package*/ WebManager(ServletContext ctx, String updateURI) {
		if (log.debugable()) log.debug("Starting WebManager at "+ctx);

		if (ctx == null || updateURI == null)
			throw new IllegalArgumentException("null");
		_ctx = ctx;
		_updateURI = updateURI;

		Labels.register(new ServletLabelLocator(_ctx));
		Labels.setVariableResolver(new ServletLabelResovler());

		_cwr = ClassWebResource.getInstance(_ctx, _updateURI);
		_ctx.setAttribute(ATTR_WEB_MANAGER, this);

		final Configuration config = new Configuration();
		try {
			final URL cfgUrl = _ctx.getResource("/WEB-INF/zk.xml");
			if (cfgUrl != null)
				new ConfigParser()
					.parse(cfgUrl, config, new ServletContextLocator(_ctx));
		} catch (Throwable ex) {
			log.error("Unable to load /WEB-INF/zk.xml", ex);
		}

		//create a WebApp instance
		_wapp = newWebApp(config);
		((WebAppCtrl)_wapp).init(_ctx, config);

		final List listeners = (List)_actListeners.remove(_ctx); //called and drop
		if (listeners != null) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				try {
					((ActivationListener)it.next()).onActivated(this);
				} catch (java.util.ConcurrentModificationException ex) {
					throw ex;
				} catch (Throwable ex) {
					log.realCause(ex);
				}
			}
		}
	}
	private static WebApp newWebApp(Configuration config) {
		Class cls = config.getWebAppClass();
		if (cls == null) {
			return new SimpleWebApp();
		} else {
			try {
				return (WebApp)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		}
	}

	public void destroy() {
		((WebAppCtrl)_wapp).destroy();
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
	/** Initializes the request-local storage that is used to lift the limitation
	 * of incapability of inter-portlet communication.
	 *
	 * <p>In other words, {@link #getRequestLocal} will look for this storage
	 * in addition to request's attributes.
	 */
	/*package*/ static void initRequestLocal() {
		_reqLocal.set(new HashMap());
	}
	/** Cleans up the request-local storage set by {@link #initRequestLocal}
	/*package*/ static void cleanRequestLocal() {
		_reqLocal.set(null);
	}
	/** Returns the value of the specified attribute in the request.
	 * The implementation shall use this method instead of request.getAttribute,
	 * since it resolves the limitation of incapability of inter-portlet
	 * communication.
	 *
	 * @param name the attribute's name
	 */
	public static Object getRequestLocal(ServletRequest request, String name) {
		final Object o = request.getAttribute(name);
		if (o != null) return o;

		final Map local = (Map)_reqLocal.get();
		return local != null ? local.get(name): null;
	}
	/** Sets the value of the specified attribute in the request.
	 * The implementation shall use this method instead of request.setAttribute,
	 * since it resolves the limitation of incapability of inter-portlet
	 * communication.
	 * @param name the attribute's name
	 * @param value the attribute's value
	 */
	public static
	void setRequestLocal(ServletRequest request, String name, Object value) {
		request.setAttribute(name, value);

		final Map local = (Map)_reqLocal.get();
		if (local != null) local.put(name, value);
	}
	/** Returns the value of the specified attribute in the request.
	 * The implementation shall use this method instead of request.getAttribute,
	 * since it resolves the limitation of incapability of inter-portlet
	 * communication.
	 *
	 * @param name the attribute's name
	 */
	public static Object getRequestLocal(RenderRequest request, String name) {
		final Object o = request.getAttribute(name);
		if (o != null) return o;

		final Map local = (Map)_reqLocal.get();
		return local != null ? local.get(name): null;
	}
	/** Sets the value of the specified attribute in the request.
	 * The implementation shall use this method instead of request.setAttribute,
	 * since it resolves the limitation of incapability of inter-portlet
	 * communication.
	 * @param name the attribute's name
	 * @param value the attribute's value
	 */
	public static
	void setRequestLocal(RenderRequest request, String name, Object value) {
		request.setAttribute(name, value);

		final Map local = (Map)_reqLocal.get();
		if (local != null) local.put(name, value);
	}

	/** Register a listener to the specified context such that
	 * it will be invoked if the corresponding {@link WebManager} is created.
	 *
	 * <p>Note: if the Web manager is created already, {@link ActivationListener#onActivated}
	 * will be invoked immediately before this method returns.
	 */
	public static final
	void addListener(ServletContext ctx, ActivationListener listener) {
		if (ctx == null || listener == null)
			throw new IllegalArgumentException("null");

		final WebManager webman = getWebManagerIfAny(ctx);
		if (webman != null) {
			listener.onActivated(webman);
		} else {
			synchronized (WebManager.class) {
				List l = (List)_actListeners.get(ctx);
				if (l == null) _actListeners.put(ctx, l = new LinkedList());
				l.add(listener);
			}
		}
	}

	/** Returns the Web manager of the specified context.
	 * @exception UiException if not found (i.e., not initialized
	 * properly)
	 */
	public static final WebManager getWebManager(ServletContext ctx) {
		final WebManager webman = getWebManagerIfAny(ctx);
		if (webman == null)
			throw new UiException("The Web manager not found. Make sure <load-on-startup> is specified for "+DHtmlLayoutServlet.class.getName());
		return webman;
	}
	/** Returns the Web manager, or null if not found.
	 */
	/*package*/ static final WebManager getWebManagerIfAny(ServletContext ctx) {
		return (WebManager)ctx.getAttribute(ATTR_WEB_MANAGER);
	}

	/** Returns the session. */
	public static final
	Session getSession(ServletContext ctx, HttpServletRequest request) {
		final HttpSession hsess = request.getSession();
		final Session sess = getSession(hsess);
		return sess != null ? sess:
			newSession(getWebManager(ctx).getWebApp(),
				hsess, request.getRemoteAddr(), request.getRemoteHost());
	}
	/** Returns the session.
	 */
	/*package*/ static final Session newSession(WebApp wapp,
	HttpSession hsess, String remoteAddr, String remoteHost) {
		if (D.ON && log.debugable()) log.debug("Creating a new sess for "+hsess);

		final Session sess = ((WebAppCtrl)wapp).getUiFactory()
			.newSession(wapp, hsess, remoteAddr, remoteHost);
		hsess.setAttribute(ATTR_SESS, sess);

		//Note: we set timeout here, because HttpSession might have been created
		//by other servlet or filter
		final Integer v = wapp.getConfiguration().getSessionMaxInactiveInterval();
		if (v != null)
			hsess.setMaxInactiveInterval(v.intValue());
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
		final Session sess = (Session)getSession(hsess);
		if (sess != null) {
			try {
				final WebApp wapp =
					getWebManager(hsess.getServletContext()).getWebApp();
				((WebAppCtrl)wapp)
					.getDesktopCacheProvider().sessionDestroyed(sess);
			} catch (Throwable ex) {
				log.error("Failed to cleanup session", ex);
			}

			try {
				((SessionCtrl)sess).onDestroyed();
			} catch (Throwable ex) {
				log.error("Failed to cleanup session", ex);
			}
			hsess.removeAttribute(ATTR_SESS);
		}
	}

	/** Returns the desktop of the specified request.
	 * @param path the path of the ZUML page.
	 */
	/*package*/ Desktop getDesktop(Session sess, ServletRequest request,
	String path) {
		Desktop desktop = (Desktop)getRequestLocal(request, ATTR_DESKTOP);
		if (desktop == null) {
			if (D.ON && log.debugable()) log.debug("Create desktop for "+path);
			WebManager.setRequestLocal(request, ATTR_DESKTOP,
				desktop = newDesktop(sess, request, path));
		}
		return desktop;
	}
	/** Creates an desktop. */
	/*package*/ Desktop newDesktop(Session sess, Object request, String path) {
		return ((WebAppCtrl)_wapp).getUiFactory().newDesktop(
			new RequestInfoImpl(_wapp, sess, null, request,
				PageDefinitions.getLocator(_wapp, path)),
			_updateURI, path);
	}
	/** Sets the desktop to the specified request.
	 * Used internally for implementation only.
	 */
	public static void setDesktop(HttpServletRequest request,
	Desktop desktop) {
		/*if (D.ON) {
			final Desktop dt = (Desktop)getRequestLocal(ATTR_DESKTOP);
			assert dt == null || dt == desktop: "old:"+dt+", new:"+desktop;
		}*/
		WebManager.setRequestLocal(request, ATTR_DESKTOP, desktop);
	}
}
