/* DHtmlLayoutPortlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 13:59:27     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletSession;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;
import org.zkoss.web.portlet.Portlets;
import org.zkoss.web.portlet.RenderHttpServletRequest;
import org.zkoss.web.portlet.RenderHttpServletResponse;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;

/**
 * The portlet used to process the request for a ZUML page.
 *
 * <h3>Notes:</h3>
 * <ul>
 * <li>The portlet looks for the path of the ZUML page from the following locations:
 * <ol>
 *  <li>From the request parameter called zk_page.</li>
 *  <li>From the request attribute called zk_page.</li>
 *  <li>From the portlet preference called zk_page.</li>
 * </ol>
 * </li>
 * <li>If not found, it looks for the portlet from the following locations:
 * <ol>
 *  <li>From the request parameter called zk_richlet.</li>
 *  <li>From the request attribute called zk_richlet.</li>
 *  <li>From the portlet preference called zk_richlet.</li>
 * </ol>
 * </li>
 * <li>It is based {@link DHtmlLayoutServlet}, so you have to declare
 * {@link DHtmlLayoutServlet} even if you want every ZUML pages being
 * processed by this portlet.</li>
 * </ul>
 *
 * @author tomyeh
 */
public class DHtmlLayoutPortlet extends GenericPortlet {
	private static final Log log = Log.lookup(DHtmlLayoutPortlet.class);

	/** The parameter or attribute to specify the path of the ZUML page. */
	private static final String ATTR_PAGE = "zk_page";
	/** The parameter or attribute to specify the path of the richlet. */
	private static final String ATTR_RICHLET = "zk_richlet";
	private PortletContext _ctx;
	/** The default page. */
	private String _defpage;

	public void init(PortletConfig conf) throws PortletException {
		_ctx = conf.getPortletContext();
		_defpage = conf.getInitParameter(ATTR_PAGE);
	}

	public PortletContext getPortletContext() {
		return _ctx;
	}

	protected String getTitle(RenderRequest request) {
		return "ZK";
	}

	protected void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		//try parameter first and then attribute
		boolean bRichlet = false;
		String path = request.getParameter(ATTR_PAGE);
		if (path == null) {
			path = (String)request.getAttribute(ATTR_PAGE);
			if (path == null) {
				PortletPreferences prefs = request.getPreferences();
				path = prefs.getValue(ATTR_PAGE, null);
				if (path == null) {
					path = request.getParameter(ATTR_RICHLET);
					bRichlet = path != null;
					if (!bRichlet) {
						path = (String)request.getAttribute(ATTR_RICHLET);
						bRichlet = path != null;
						if (!bRichlet) {
							path = prefs.getValue(ATTR_RICHLET, null);
							bRichlet = path != null;
							if (!bRichlet)
								path = _defpage;
						}
					}
				}
			}
		}

		final Session sess = getSession(request);
		if (!SessionsCtrl.requestEnter(sess)) {
			handleError(sess, request, response, path, null,
				Messages.get(MZk.TOO_MANY_REQUESTS));
			return;
		}
		SessionsCtrl.setCurrent(sess);
		try {
			if (!process(sess, request, response, path, bRichlet))
				handleError(sess, request, response, path, null, null);
		} catch (Throwable ex) {
			handleError(sess, request, response, path, ex, null);
		} finally {
			SessionsCtrl.requestExit(sess);
			SessionsCtrl.setCurrent(null);
		}
	}

	/** Returns the session. */
	private Session getSession(RenderRequest request)
	throws PortletException {
		final WebApp wapp = getWebManager().getWebApp();
		final PortletSession psess = request.getPortletSession();
		final Session sess = SessionsCtrl.getSession(wapp, psess);
		return sess != null ? sess:
			SessionsCtrl.newSession(wapp, psess, request);
	}

	/** Process a portlet request.
	 * @return false if the page is not found.
	 * @since 3.0.0
	 */
	protected boolean process(Session sess, RenderRequest request,
	RenderResponse response, String path, boolean bRichlet)
	throws PortletException, IOException {
		if (D.ON && log.debugable()) log.debug("Creates from "+path);
		final WebManager webman = getWebManager();
		final WebApp wapp = webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final HttpServletRequest httpreq = RenderHttpServletRequest.getInstance(request);
		final HttpServletResponse httpres = RenderHttpServletResponse.getInstance(response);
		final Desktop desktop = webman.getDesktop(sess, httpreq, httpres, path, true);
		if (desktop == null) //forward or redirect
			return true;

		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, httpreq, PageDefinitions.getLocator(wapp, path));
		((SessionCtrl)sess).notifyClientRequest(true);

		final UiFactory uf = wappc.getUiFactory();
		if (uf.isRichlet(ri, bRichlet)) {
			final Richlet richlet = uf.getRichlet(ri, path);
			if (richlet == null)
				return false; //not found

			final Page page = WebManager.newPage(uf, ri, richlet, httpres, path);
			final Execution exec =
				new ExecutionImpl(
					(ServletContext)wapp.getNativeContext(),
					httpreq, httpres, desktop, page);

			//Bug 1548478: content-type is required for some implementation (JBoss Portal)
			if (response.getContentType() == null)
				response.setContentType("text/html;charset=UTF-8");

			wappc.getUiEngine().execNewPage(exec, richlet, page, response.getWriter());
		} else if (path != null) {
			final PageDefinition pagedef = uf.getPageDefinition(ri, path);
			if (pagedef == null)
				return false; //not found

			final Page page = WebManager.newPage(uf, ri, pagedef, httpres, path);
			final Execution exec =
				new ExecutionImpl(
					(ServletContext)wapp.getNativeContext(),
					httpreq, httpres, desktop, page);

			//Bug 1548478: content-type is required for some implementation (JBoss Portal)
			if (response.getContentType() == null)
				response.setContentType("text/html;charset=UTF-8");

			wappc.getUiEngine()
				.execNewPage(exec, pagedef, page, response.getWriter());
		}
		return true; //success
	}

	/** Returns the layout servlet.
	 */
	private final WebManager getWebManager()
	throws PortletException {
		final WebManager webman =
			(WebManager)_ctx.getAttribute(WebManager.ATTR_WEB_MANAGER);
		if (webman == null)
			throw new PortletException("The Layout Servlet not found. Make sure <load-on-startup> is specified for "+DHtmlLayoutServlet.class.getName());
		return webman;
	}
	/** Handles exception being thrown when rendering a page.
	 * @param ex the exception being throw. If null, it means the page
	 * is not found.
	 */
	private void handleError(Session sess, RenderRequest request,
	RenderResponse response, String path, Throwable err, String msg)
	throws PortletException, IOException {
		if (err != null) {
		//Bug 1714094: we have to handle err, because Web container
		//didn't allow developer to intercept errors caused by inclusion
			final String errpg = sess.getWebApp().getConfiguration()
				.getErrorPage(sess.getDeviceType(), err);
			if (errpg != null) {
				try {
					request.setAttribute("javax.servlet.error.message", Exceptions.getMessage(err));
					request.setAttribute("javax.servlet.error.exception", err);
					request.setAttribute("javax.servlet.error.exception_type", err.getClass());
					request.setAttribute("javax.servlet.error.status_code", new Integer(500));
					if (process(sess, request, response, errpg, false))
						return; //done
					log.warning("The error page not found: "+errpg);
				} catch (IOException ex) { //eat it (connection off)
				} catch (Throwable ex) {
					log.warning("Failed to load the error page: "+errpg, ex);
				}
			}

			if (msg == null)
				msg = Messages.get(MZk.PAGE_FAILED,
					new Object[] {path, Exceptions.getMessage(err),
						Exceptions.formatStackTrace(null, err, null, 6)});
		} else {
			if (msg == null)
				msg = path != null ?
					Messages.get(MZk.PAGE_NOT_FOUND, new Object[] {path}):
					Messages.get(MZk.PORTLET_PAGE_REQUIRED);
		}

		final Map attrs = new HashMap();
		attrs.put(Attributes.ALERT_TYPE, "error");
		attrs.put(Attributes.ALERT, msg);
		Portlets.include(_ctx, request, response,
			"~./html/alert.dsp", attrs, Portlets.OVERWRITE_URI);
			//Portlets doesn't support PASS_THRU_ATTR yet (because
			//protlet request will mangle attribute name)
	}
}
