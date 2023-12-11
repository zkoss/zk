/* DHtmlLayoutServlet.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;
import org.zkoss.mesg.Messages;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.OperationException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DesktopRecycle;

/**
 * Used to process the request for a ZUML page. Though it is called
 * DHtmlLayoutServlet, it is used to serve all kind of HTTP-based clients,
 * including ajax (HTML+Ajax), mil (Mobile Interactive Language)
 * and others (see {@link Desktop#getDeviceType}.
 *
 * <p>Init parameters:
 *
 * <dl>
 * <dt>update-uri</dt>
 * <dd>It specifies the URI which the ZK AU engine is mapped to.</dd>
 * <dt>compress</dt>
 * <dd>It specifies whether to compress the output if the browser supports the compression (Accept-Encoding) and this Servlet is not included by other Servlets.</dd>
 * <dt>log-level</dt>
 * <dd>It specifies the default log level for org.zkoss. If not specified, the system default (usually INFO) is used.</dd>
 * </dl>
 * @author tomyeh
 */
public class DHtmlLayoutServlet extends HttpServlet {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(DHtmlLayoutServlet.class);

	private WebManager _webman;
	private boolean _webmanCreated;
	private boolean _compress = true;

	//Servlet//
	@SuppressWarnings("deprecation")
	public void init() throws ServletException {
		final ServletConfig config = getServletConfig();
		String param = config.getInitParameter("log-level");
		if (param != null && param.length() > 0) {
			final Level level = org.zkoss.util.logging.Log.getLevel(param);
			if (level != null)
				Logger.getLogger("org.zkoss").setLevel(level);
			else
				log.error("Unknown log-level: " + param);
		}

		param = config.getInitParameter("compress");
		_compress = param == null || param.length() == 0 || "true".equals(param);

		final ServletContext ctx = getServletContext();
		_webman = WebManager.getWebManagerIfAny(ctx);
		String updateURI = Utils.checkUpdateURI(config.getInitParameter("update-uri"), "The update-uri parameter");
		ctx.setAttribute("org.zkoss.zk.ui.http.update-uri", updateURI); //B65-ZK-1619

		String resourceURI = config.getInitParameter("resource-uri");
		if (resourceURI == null)
			resourceURI = updateURI;
		else
			resourceURI = Utils.checkUpdateURI(resourceURI, "The resource-uri parameter");
		if (_webman == null) {
			log.warn("WebManager not initialized. Please check if HttpSessionListener is configured properly.");
			_webman = new WebManager(ctx, updateURI, resourceURI);
			_webmanCreated = true;
		} else {
			_webman.setUpdateUri(updateURI);
			_webman.setResourceUri(resourceURI);
		}
	}

	public void destroy() {
		if (_webman != null) {
			if (_webmanCreated)
				_webman.destroy();
			_webman = null;
		}
	}

	//-- super --//
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = Https.getThisPathInfo(request);
		final boolean bRichlet = path != null && path.length() > 0;
		if (!bRichlet)
			path = Https.getThisServletPath(request);
		//		if (log.finerable()) log.finer("Creates from "+path);

		final Session sess = WebManager.getSession(getServletContext(), request);
		if (!SessionsCtrl.requestEnter(sess)) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, Messages.get(MZk.TOO_MANY_REQUESTS));
			return;
		}
		try {
			final Object old = I18Ns.setup(sess, request, response,
					sess.getWebApp().getConfiguration().getResponseCharset());
			try {
				if (!process(sess, request, response, path, bRichlet))
					handleError(sess, request, response, path, null);
			} catch (Throwable ex) {
				handleError(sess, request, response, path, ex);
			} finally {
				I18Ns.cleanup(request, old);
			}
		} finally {
			SessionsCtrl.requestExit(sess);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	//-- private --//
	/**
	 * Process the request.
	 * @return false if the page is not found.
	 * @since 3.0.0
	 */
	protected boolean process(Session sess, HttpServletRequest request, HttpServletResponse response, String path,
			boolean bRichlet) throws ServletException, IOException {

		// Fix Server-Side Request Forgery (SSRF)
		if (!Https.isValidPath(path)) return false;

		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl) wapp;
		final Configuration config = wapp.getConfiguration();

		final boolean bInclude = Servlets.isIncluded(request);
		final boolean compress = _compress && !bInclude;
		final Writer out = compress ? (Writer) new StringWriter() : response.getWriter();
		final DesktopRecycle dtrc = bInclude ? null : config.getDesktopRecycle();
		final ServletContext ctx = getServletContext();
		Desktop desktop = dtrc != null ? DesktopRecycles.beforeService(dtrc, ctx, sess, request, response, path) : null;

		try {
			if (desktop != null) { //recycle
				final Page page = Utils.getMainPage(desktop);
				if (page != null) {
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					WebManager.setDesktop(request, desktop);
					wappc.getUiEngine().recycleDesktop(exec, page, out);
				} else
					desktop = null; //something wrong (not possible; just in case)
			}
			// check voided for ZK-2352: Executions.forward() will show IOException warning
			boolean voided = false;
			if (desktop == null) {
				desktop = _webman.getDesktop(sess, request, response, path, true);
				if (desktop == null) //forward or redirect
					return true;

				final RequestInfo ri = new RequestInfoImpl(wapp, sess, desktop, request,
						PageDefinitions.getLocator(wapp, path));
				((SessionCtrl) sess).notifyClientRequest(true);

				final UiFactory uf = wappc.getUiFactory();
				if (uf.isRichlet(ri, bRichlet)) {
					final Richlet richlet = uf.getRichlet(ri, path);
					if (richlet == null)
						return false; //not found

					final Page page = WebManager.newPage(uf, ri, richlet, response, path);
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					wappc.getUiEngine().execNewPage(exec, richlet, page, out);
					//no need to set device type here, since UiEngine will do it later
				} else {
					final PageDefinition pagedef = uf.getPageDefinition(ri, path);
					if (pagedef == null)
						return false; //not found

					final Page page = WebManager.newPage(uf, ri, pagedef, response, path);
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					wappc.getUiEngine().execNewPage(exec, pagedef, page, out);
					voided = exec.isVoided();
				}
			}

			// check voided to ignore the IOExecuption that caused by Executions.forward()
			if (compress && !voided) {
				final String result = ((StringWriter) out).toString();

				try {
					final OutputStream os = response.getOutputStream();
					//Call it first to ensure getWrite() is not called yet

					byte[] data = result.getBytes(config.getResponseCharset());
					if (data.length > 200) {
						byte[] bs = Https.gzip(request, response, null, data);
						if (bs != null)
							data = bs; //yes, browser support compress
					}

					response.setContentLength(data.length);
					os.write(data);
					response.flushBuffer();
				} catch (IllegalStateException ex) { //getWriter is called
					response.getWriter().write(result);
				}
			}
		} finally {
			if (dtrc != null)
				DesktopRecycles.afterService(dtrc, desktop);
		}
		return true; //success
	}

	/** Handles exception being thrown when rendering a page.
	 * @param err the exception being throw. If null, it means the page
	 * is not found.
	 */
	private void handleError(Session sess, HttpServletRequest request, HttpServletResponse response, String path,
			Throwable err) throws ServletException, IOException {
		Utils.resetOwner();

		// ZK-3679
		Throwable cause;
		if (err instanceof OperationException && (cause = err.getCause()) instanceof Expectable)
			err = cause;

		//Note: if not included, it is handled by Web container
		if (err != null && Servlets.isIncluded(request)) {
			//Bug 1714094: we have to handle err, because Web container
			//didn't allow developer to intercept errors caused by inclusion
			final String errpg = sess.getWebApp().getConfiguration().getErrorPage(sess.getDeviceType(), err);
			if (errpg != null) {
				try {
					request.setAttribute("javax.servlet.error.message", Exceptions.getMessage(err));
					request.setAttribute("javax.servlet.error.exception", err);
					request.setAttribute("javax.servlet.error.exception_type", err.getClass());
					request.setAttribute("javax.servlet.error.status_code", new Integer(500));
					if (process(sess, request, response, errpg, false))
						return; //done

					log.warn("The error page not found: " + errpg);
				} catch (IOException ex) { //eat it (connection off)
				} catch (Throwable ex) {
					log.warn("Failed to load the error page: " + errpg, ex);
				}
			}
		}

		Utils.handleError(getServletContext(), request, response, path, err);
	}
}
