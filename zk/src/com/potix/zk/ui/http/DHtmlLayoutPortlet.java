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
package com.potix.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.potix.lang.D;
import com.potix.mesg.Messages;
import com.potix.util.logging.Log;

import com.potix.web.Attributes;
import com.potix.web.portlet.Portlets;
import com.potix.web.portlet.PortletServletContext;
import com.potix.web.portlet.PortletHttpSession;
import com.potix.web.portlet.RenderHttpServletRequest;
import com.potix.web.portlet.RenderHttpServletResponse;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.SessionsCtrl;
import com.potix.zk.ui.sys.RequestInfo;
import com.potix.zk.ui.impl.RequestInfoImpl;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * The portlet used to process the request for a ZUML page.
 *
 * <h3>Notes:</h3>
 * <ul>
 * <li>The path of the ZUML page is specified as an parameter
 * called zk_path.</li>
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

	public void init(PortletConfig conf) throws PortletException {
		_ctx = conf.getPortletContext();
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
		if (path == null) path = (String)request.getAttribute(ATTR_PAGE);

		final Session sess = getSession(request);
		SessionsCtrl.setCurrent(sess);
		try {
			process(sess, request, response, path);
		} finally {
			SessionsCtrl.setCurrent(null);
		}
	}

	/** Returns the session. */
	private Session getSession(RenderRequest request) {
		return DHtmlLayoutServlet.getSession(
			PortletHttpSession.getInstance(request.getPortletSession()));
	}
	/** Process a portlet request.
	 * @param path the path of the ZUML page to render. If null or not found,
	 * an error page is rendered.
	 */
	private void process(Session sess, RenderRequest request,
	RenderResponse response, String path)
	throws PortletException, IOException {
		if (D.ON && log.debugable()) log.debug("Creates from "+path);
		final WebApp wapp = getLayoutServlet().getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final Desktop desktop = getDesktop(sess, request, path);
		final ServletContext sctx = (ServletContext)wapp.getNativeContext();
		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request,
			PageDefinitions.getLocator(sctx, path));
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
			new ExecutionImpl(sctx,
				RenderHttpServletRequest.getInstance(request),
				RenderHttpServletResponse.getInstance(response),
				desktop, page);
		wappc.getUiEngine().execNewPage(exec, page, response.getWriter());
	}

	/** Returns the desktop of the specified request.
	 */
	private Desktop getDesktop(Session sess, RenderRequest request, String path)
	throws PortletException {
		Desktop desktop =
			(Desktop)request.getAttribute(DHtmlLayoutServlet.DESKTOP);
		if (desktop == null)
			request.setAttribute(DHtmlLayoutServlet.DESKTOP,
				desktop = getLayoutServlet().newDesktop(sess, request, path));
		return desktop;
	}
	/** Returns the layout servlet.
	 */
	private final DHtmlLayoutServlet getLayoutServlet()
	throws PortletException {
		final DHtmlLayoutServlet svl =
			(DHtmlLayoutServlet)_ctx.getAttribute(DHtmlLayoutServlet.ATTR_LAYOUT_SERVLET);
		if (svl == null)
			throw new PortletException("The Layout Servlet not found. Make sure <load-on-startup> is specified for "+DHtmlLayoutServlet.class.getName());
		return svl;
	}
}
