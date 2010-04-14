/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 21:36:13     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.DesktopRecycle;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.ext.Includer;

/**
 * A collection of utilities for this package.
 *
 * @author tomyeh
 * @since 2.4.1
 */
/*package*/ class Utils {
	private static Log log = Log.lookup(Utils.class);

	/** Handles exception being thrown when rendering a page.
	 * @param ex the exception being throw. If null, it means the page
	 * is not found.
	 */
	/*package*/ static
	void handleError(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	String path, Throwable err) throws ServletException, IOException {
		if (Servlets.isIncluded(request)) {
			final String msg =
				err != null ?
					Messages.get(MZk.PAGE_FAILED,
						new Object[] {path, Exceptions.getMessage(err),
						Exceptions.formatStackTrace(null, err, null, 6)}):
					Messages.get(MZk.PAGE_NOT_FOUND, new Object[] {path});

			final Map attrs = new HashMap();
			attrs.put(Attributes.ALERT_TYPE, "error");
			attrs.put(Attributes.ALERT, msg);
			Servlets.include(ctx, request, response,
				"~./html/alert.dsp", attrs, Servlets.PASS_THRU_ATTR);
		} else {
			//If not included, let the Web container handle it
			if (err != null) {
				if (err instanceof ServletException)
					throw (ServletException)err;
				else if (err instanceof IOException)
					throw (IOException)err;
				else
					throw UiException.Aide.wrap(err);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
		}
	}
	/** Resets the child page of the owner, if any.
	 */
	/*package*/ static void resetOwner() {
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			final Component comp = ((ExecutionCtrl)exec).getVisualizer().getOwner();
			if (comp instanceof Includer)
				((Includer)comp).setChildPage(null);
		}
	}

	/*package*/ static Desktop beforeService(
	DesktopRecycle dtrc, ServletContext ctx, Session sess,
	HttpServletRequest request, HttpServletResponse response, String path) {
		if (dtrc != null) {
			final Execution olde = Executions.getCurrent();
			final Object olds = SessionsCtrl.getRawCurrent();
			final Execution exec = new TemporaryExecution(ctx, request, response, null);
			SessionsCtrl.setCurrent(sess);
			ExecutionsCtrl.setCurrent(exec);
			try {
				return dtrc.beforeService(exec, getURI(path, request.getQueryString()));
			} catch (Throwable ex) {
				log.error(ex);
			} finally {
				ExecutionsCtrl.setCurrent(olde);
				SessionsCtrl.setRawCurrent(olds);
			}
		}
		return null;
	}

	/*package*/ static void afterService(DesktopRecycle dtrc, Desktop desktop) {
		if (dtrc != null) {
			try {
				dtrc.afterService(desktop);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}

	/** Returns the request URI of the desktop.
	 * The request URI is a combination of {@link Desktop#getRequestPath}
	 * and {@link Desktop#getQueryURI}.
	 */
	/*package*/ static String getURI(String path, String qs) {
		return qs != null ? path + '?' + qs: path;
	}

	/** Returns the main page of the desktop.
	 * It assumes there is at most one main page (that is, a page without owner)
	 */
	/*package*/ static Page getMainPage(Desktop desktop) {
		for (Iterator it = desktop.getPages().iterator(); it.hasNext();) {
			final Page page = (Page)it.next();
			if (((PageCtrl)page).getOwner() == null)
				return page;
		}
		return null;
	}
}
