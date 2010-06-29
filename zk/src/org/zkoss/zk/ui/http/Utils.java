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
import java.util.Calendar;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Library;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.web.util.resource.Extendlet;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
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
public class Utils {
	private static Log log = Log.lookup(Utils.class);

	/** Update the status of debug-js.
	 * It is called by {@link org.zkoss.zk.ui.util.Configuration#setDebugJS}
	 * to change the loading of JavaScript files dynamically.
	 * @since 5.0.3
	 */
	public static void updateDebugJS(WebApp wapp, boolean debug) {
		final WebManager wm = WebManager.getWebManagerIfAny(wapp);
		if (wm != null) {
			final ClassWebResource cwr = wm.getClassWebResource();
			cwr.setDebugJS(debug);
			final Extendlet ext = cwr.getExtendlet("wpd");
			if (ext instanceof WpdExtendlet)
				((WpdExtendlet)ext).setDebugJS(debug);
		}
	}

	/** Returns the first day of the week, or -1 if no defined by the application.
	 * It is the same as <code>getFirstDayOfWeek(Sessions.getCurrent())</code>.
	 * @since 5.0.3
	 */
	public final static int getFirstDayOfWeek() {
		return getFirstDayOfWeek(Sessions.getCurrent());
	}
	/** Returns the first day of the week of the given session, or -1 if no defined by the application.
	 * It searches the following attributes and properties until found.
	 * Notice that it doesn't look for {@link java.util.Calendar#getFirstDayOfWeek}.
	 * <ol>
	 * <li>Session attribute: {@link Attributes#PREFERRED_FIRST_DAY_OF_WEEK}</li>
	 * <li>Application attribute: {@link Attributes#PREFERRED_FIRST_DAY_OF_WEEK}</li>
	 * <li>Library property: {@link Attributes#PREFERRED_FIRST_DAY_OF_WEEK}</li>
	 * </ol>
	 * @param sess the session to look at. Ignored if null.
	 * @since 5.0.3
	 */
	public final static int getFirstDayOfWeek(Session sess) {
		int firstDayOfWeek = -1;
		try {
			Object o = null;
			if (sess != null) {
				o = sess.getAttribute(Attributes.PREFERRED_FIRST_DAY_OF_WEEK);
				if (o == null) {
					final Object hsess = sess.getNativeSession();
					if (hsess instanceof HttpSession)
						o = ((HttpSession)hsess).getServletContext().getAttribute(Attributes.PREFERRED_FIRST_DAY_OF_WEEK);
				}
			}
			if (o == null)
				o = Library.getProperty(Attributes.PREFERRED_FIRST_DAY_OF_WEEK);

			if (o instanceof Integer)
				firstDayOfWeek = ((Integer)o).intValue();
			else if (o instanceof String)
				firstDayOfWeek = Integer.parseInt((String)o);
		} catch (Throwable ex) { //ignore
		}
		if (firstDayOfWeek < Calendar.SUNDAY || firstDayOfWeek > Calendar.SATURDAY)
			firstDayOfWeek = -1;
		return firstDayOfWeek;
	}

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
