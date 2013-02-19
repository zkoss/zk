/* DHtmlLayoutPortlet.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 13:59:27     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Library;
import org.zkoss.mesg.Messages;
import org.zkoss.util.logging.Log;
import org.zkoss.web.Attributes;
import org.zkoss.web.portlet.Portlets;
import org.zkoss.web.portlet.RenderHttpServletRequest;
import org.zkoss.web.portlet.RenderHttpServletResponse;
import org.zkoss.web.portlet.ResourceHttpServletRequest;
import org.zkoss.web.portlet.ResourceHttpServletResponse;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.PageRenderPatch;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.DesktopRecycle;

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
 * <p>To patch the rendering result of a ZK portlet, you can implement
 * {@link PageRenderPatch} (and specified it in {@link org.zkoss.zk.ui.sys.Attributes#PORTLET_RENDER_PATCH_CLASS}).
 * @author tomyeh
 */
public class DHtmlLayoutPortlet extends GenericPortlet {
	private static final Log log = Log.lookup(DHtmlLayoutPortlet.class);

	/** The parameter or attribute to specify the path of the ZUML page. */
	private static final String ATTR_PAGE = "zk_page";
	/** The parameter or attribute to specify the path of the richlet. */
	private static final String ATTR_RICHLET = "zk_richlet";
	/** The default page. */
	private String _defpage;
	/** Check if support JSR 286 */
	private boolean isJSR286 = true;

	public void init() throws PortletException {
		_defpage = getPortletConfig().getInitParameter(ATTR_PAGE);
		try {
			Class.forName("javax.portlet.ResourceURL");
		} catch (ClassNotFoundException e) {
			isJSR286 = false;
		}
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

		final Session sess = getSession(request, true);
		if (!SessionsCtrl.requestEnter(sess)) {
			handleError(sess, request, response, path, null,
				Messages.get(MZk.TOO_MANY_REQUESTS));
			return;
		}
		SessionsCtrl.setCurrent(sess);
		try {
			// Bug ZK-1179: process I18N in portlet environment
			HttpServletRequest httpreq = RenderHttpServletRequest.getInstance(request);
			HttpServletResponse httpres = RenderHttpServletResponse.getInstance(response);
			final Object old = I18Ns.setup(httpreq.getSession(), httpreq, httpres,
					sess.getWebApp().getConfiguration().getResponseCharset()); 
			try {
				if (!process(sess, request, response, path, bRichlet))
					handleError(sess, request, response, path, null, null);
			} catch (Throwable ex) {
				handleError(sess, request, response, path, ex, null);
			} finally {
				I18Ns.cleanup(httpreq, old);
			}
		} finally {
			SessionsCtrl.requestExit(sess);
			SessionsCtrl.setCurrent((Session)null);
		}
	}

	/**
	 * Process AJAX request here instead of DHtmlUpdateServlet if the Portal Container support JSR 286. 
	 * @since 6.5.2
	 */
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {
		final WebManager webman = getWebManager();
		final WebApp wapp = webman.getWebApp();
		
		final HttpServletRequest httpreq = ResourceHttpServletRequest.getInstance(request);
		final HttpServletResponse httpres = ResourceHttpServletResponse.getInstance(response);
		final Session sess = getSession(request, false);
		
		final DHtmlUpdateServlet updateServlet = DHtmlUpdateServlet.getUpdateServlet(wapp);
		boolean compress = false; //Some portal container (a.k.a GateIn) doesn't work with gzipped output stream.
		final String sid = httpreq.getHeader("ZK-SID");
		if (sid != null)
			response.setProperty("ZK-SID", sid);
		if (sess == null) {
			try {
				updateServlet.denoteSessionTimeout(wapp, httpreq, httpres, compress);
			} catch (ServletException e) {
				e.printStackTrace();
			}
			return;
		}
		final Object old = I18Ns.setup(httpreq.getSession(), httpreq, httpres, "UTF-8");
		try {
			response.setProperty("Pragma", "no-cache");
			response.setProperty("Cache-Control", "no-cache");
			response.setProperty("Cache-Control", "no-store");
			response.setProperty("Expires", "-1");
			
			updateServlet.process(sess, httpreq, httpres, compress);
		} catch (ServletException e) {
			e.printStackTrace();
		} finally {
			I18Ns.cleanup(httpreq, old);
		}
	}
	
	/** Returns the session. */
	private Session getSession(Object request, boolean create)
	throws PortletException {
		final WebApp wapp = getWebManager().getWebApp();
		
		PortletSession psess = null;
		if (request instanceof RenderRequest)
			psess = ((RenderRequest) request).getPortletSession();
		else if (request instanceof ResourceRequest)
			psess = ((ResourceRequest) request).getPortletSession();
		
		Session sess = SessionsCtrl.getSession(wapp, psess);
		if (sess == null && create)
			sess = SessionsCtrl.newSession(wapp, psess, request);
		
		return sess;
	}

	/** Process a portlet request.
	 * @return false if the page is not found.
	 * @since 3.0.0
	 */
	protected boolean process(Session sess, RenderRequest request,
	RenderResponse response, String path, boolean bRichlet)
	throws PortletException, IOException {
//		if (log.debugable()) log.debug("Creates from "+path);
		final WebManager webman = getWebManager();
		final WebApp wapp = webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final HttpServletRequest httpreq = RenderHttpServletRequest.getInstance(request);
		final HttpServletResponse httpres = RenderHttpServletResponse.getInstance(response);
		final ServletContext svlctx = wapp.getServletContext();

		final DesktopRecycle dtrc = wapp.getConfiguration().getDesktopRecycle();
		Desktop desktop = dtrc != null ?
			DesktopRecycles.beforeService(dtrc, svlctx, sess, httpreq, httpres, path): null;

		try {
			if (desktop != null) { //recycle
				final Page page = Utils.getMainPage(desktop);
				if (page != null) {
					final Execution exec =
						new ExecutionImpl(svlctx, httpreq, httpres, desktop, page);
					fixContentType(response);
					wappc.getUiEngine()
						.recycleDesktop(exec, page, response.getWriter());
				} else
					desktop = null; //something wrong (not possible; just in case)
			}

			if (desktop == null) {
				desktop = webman.getDesktop(sess, httpreq, httpres, path, true);
				if (desktop == null) //forward or redirect
					return true;

				final RequestInfo ri = new RequestInfoImpl(
					wapp, sess, desktop, httpreq,
					PageDefinitions.getLocator(wapp, path));
				((SessionCtrl)sess).notifyClientRequest(true);

				final Page page;
				final PageRenderPatch patch = getRenderPatch();
				final Writer out = patch.beforeRender(ri);
				final UiFactory uf = wappc.getUiFactory();
				if (uf.isRichlet(ri, bRichlet)) {
					final Richlet richlet = uf.getRichlet(ri, path);
					if (richlet == null)
						return false; //not found

					page = WebManager.newPage(uf, ri, richlet, httpres, path);
					final Execution exec =
						new ExecutionImpl(svlctx, httpreq, httpres, desktop, page);
					fixContentType(response);
					if (isJSR286) {
						ResourceURL url = response.createResourceURL();
						page.setAttribute("org.zkoss.portlet2.resourceURL", response.encodeURL(url.toString()), Page.PAGE_SCOPE);
					}
					wappc.getUiEngine().execNewPage(exec, richlet, page,
						out != null ? out: response.getWriter());
				} else if (path != null) {
					final PageDefinition pagedef = uf.getPageDefinition(ri, path);
					if (pagedef == null)
						return false; //not found

					page = WebManager.newPage(uf, ri, pagedef, httpres, path);
					final Execution exec =
						new ExecutionImpl(svlctx, httpreq, httpres, desktop, page);
					fixContentType(response);
					if (isJSR286) {
						ResourceURL url = response.createResourceURL();
						page.setAttribute("org.zkoss.portlet2.resourceURL", response.encodeURL(url.toString()), Page.PAGE_SCOPE);
					}
					wappc.getUiEngine().execNewPage(exec, pagedef, page,
						out != null ? out: response.getWriter());
				} else
					return true; //nothing to do

				if (out != null)
					patch.patchRender(ri, page, out, response.getWriter());
			}
		} finally {
			if (dtrc != null)
				DesktopRecycles.afterService(dtrc, desktop);
		}
		return true; //success
	}
	private static PageRenderPatch getRenderPatch() {
		if (_prpatch != null)
			return _prpatch;

		synchronized (DHtmlLayoutPortlet.class) {
			if (_prpatch != null)
				return _prpatch;

			final PageRenderPatch patch;
			final String clsnm = Library.getProperty(
				org.zkoss.zk.ui.sys.Attributes.PORTLET_RENDER_PATCH_CLASS);
			if (clsnm == null) {
				patch = new PageRenderPatch() {
					public Writer beforeRender(RequestInfo reqInfo) {
						return null;
					}
					public void patchRender(RequestInfo reqInfo, Page page, Writer result, Writer out)
					throws IOException {
					}
				};
			} else {
				try {
					patch = (PageRenderPatch)Classes.newInstanceByThread(clsnm);
				} catch (ClassCastException ex) {
					throw new UiException(clsnm+" must implement "+PageRenderPatch.class.getName());
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex, "Unable to instantiate");
				}
			}
			return _prpatch = patch;
		}
	}
	private static volatile PageRenderPatch _prpatch;

	private static void fixContentType(RenderResponse response) {
		//Bug 1548478: content-type is required for some implementation (JBoss Portal)
		if (response.getContentType() == null)
			response.setContentType("text/html;charset=UTF-8");
	}

	/** Returns the layout servlet.
	 */
	private final WebManager getWebManager()
	throws PortletException {
		final WebManager webman =
			(WebManager)getPortletContext().getAttribute(WebManager.ATTR_WEB_MANAGER);
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

		final Map<String, String> attrs = new HashMap<String, String>();
		attrs.put(Attributes.ALERT_TYPE, "error");
		attrs.put(Attributes.ALERT, msg);
		Portlets.include(getPortletContext(), request, response,
			"~./html/alert.dsp", attrs, Portlets.OVERWRITE_URI);
			//Portlets doesn't support PASS_THRU_ATTR yet (because
			//protlet request will mangle attribute name)
	}
}
