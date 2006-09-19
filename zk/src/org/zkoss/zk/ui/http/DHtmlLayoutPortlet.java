/* DHtmlLayoutPortlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 13:59:27     2006, Created by tomyeh@potix.com
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
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import org.zkoss.lang.D;
import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;
import org.zkoss.web.portlet.Portlets;
import org.zkoss.web.portlet.PortletHttpSession;
import org.zkoss.web.portlet.RenderHttpServletRequest;
import org.zkoss.web.portlet.RenderHttpServletResponse;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
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
 * <li>It is based {@link DHtmlLayoutServlet}, so you have to declare
 * {@link DHtmlLayoutServlet} even if you want every ZUML pages being
 * processed by this portlet.</li>
 * </ul>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DHtmlLayoutPortlet extends GenericPortlet {
	private static final Log log = Log.lookup(DHtmlLayoutPortlet.class);

	/** The parameter or attribute to specify the path of the ZUML page. */
	private static final String ATTR_PAGE = "zk_page";
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
		String path = request.getParameter(ATTR_PAGE);
		if (path == null) {
			path = (String)request.getAttribute(ATTR_PAGE);
			if (path == null) {
				PortletPreferences prefs = request.getPreferences();
				path = prefs.getValue(ATTR_PAGE, null);
				if (path == null) path = _defpage;
			}
		}

		final Session sess = getSession(request);
		SessionsCtrl.setCurrent(sess);
		try {
			process(sess, request, response, path);
		} finally {
			SessionsCtrl.setCurrent(null);
		}
	}

	/** Returns the session. */
	private Session getSession(RenderRequest request)
	throws PortletException {
		final HttpSession hsess =
			PortletHttpSession.getInstance(request.getPortletSession());
		final Session sess = WebManager.getSession(hsess);
		return sess != null ? sess:
			WebManager.newSession(
				getWebManager().getWebApp(), hsess, null, null);
	}
	/** Process a portlet request.
	 * @param path the path of the ZUML page to render. If null or not found,
	 * an error page is rendered.
	 */
	private void process(Session sess, RenderRequest request,
	RenderResponse response, String path)
	throws PortletException, IOException {
		if (D.ON && log.debugable()) log.debug("Creates from "+path);
		final WebApp wapp = getWebManager().getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final Desktop desktop = getDesktop(sess, request, path);
		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request,
			PageDefinitions.getLocator(wapp, path));
		final UiFactory uf = wappc.getUiFactory();
		final PageDefinition pagedef = path != null ?
			uf.getPageDefinition(ri, path): null;
		if (pagedef == null) {
			final String msg = path != null ?
				Messages.get(MZk.PAGE_NOT_FOUND, new Object[] {path}):
				Messages.get(MZk.PORTLET_PAGE_REQUIRED);
			final Map attrs = new HashMap();
			attrs.put(Attributes.ALERT_TYPE, "error");
			attrs.put(Attributes.ALERT, msg);
			Portlets.include(_ctx, request, response,
				"~./html/alert.dsp", attrs, Portlets.OVERWRITE_URI);
				//Portlets doesn't support PASS_THRU_ATTR yet (because
				//protlet request will mangle attribute name)
			return;
		}

		final Page page = uf.newPage(ri, pagedef, path);
		final Execution exec =
			new ExecutionImpl((ServletContext)wapp.getNativeContext(),
				RenderHttpServletRequest.getInstance(request),
				RenderHttpServletResponse.getInstance(response),
				desktop, page);

		//Bug 1548478: content-type is required for some implementation (JBoss Portal)
		if (response.getContentType() == null)
			response.setContentType("text/html;charset=UTF-8");

		wappc.getUiEngine()
			.execNewPage(exec, pagedef, page, response.getWriter());
	}

	/** Returns the desktop of the specified request.
	 */
	private Desktop getDesktop(Session sess, RenderRequest request, String path)
	throws PortletException {
		Desktop desktop = (Desktop)request.getAttribute(WebManager.DESKTOP);
		if (desktop == null)
			request.setAttribute(WebManager.DESKTOP,
				desktop = getWebManager().newDesktop(sess, request, path));
		return desktop;
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
}
