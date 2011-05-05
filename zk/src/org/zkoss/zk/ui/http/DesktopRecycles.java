/* DesktopRecycles.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 09:53:30 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.util.DesktopRecycle;

/**
 * Utilties to handle {@link DesktopRecycle}.
 * @author tomyeh
 * @5.0.7
 */
public class DesktopRecycles extends org.zkoss.zk.ui.impl.DesktopRecycles {
	private static Log log = Log.lookup(DesktopRecycles.class);

	/** Called before serving a HTTP request.
	 * @return the recycled desktop, or null if no recycled desktop is matched
	 * for this request.
	 */
	public static Desktop beforeService(
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
	/** Called after serving a HTTP request.
	 */
	public static void afterService(DesktopRecycle dtrc, Desktop desktop) {
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
	private static String getURI(String path, String qs) {
		return qs != null ? path + '?' + qs: path;
	}
}
