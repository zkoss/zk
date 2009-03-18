/* AuDynaMediar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 11 19:14:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.media.Media;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.http.ExecutionImpl;

/**
 * The AU processor used to response the content for {@link DynamicMedia#getMedia}
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public class AuDynaMediar implements AuProcessor {
	private static final Log log = Log.lookup(AuDynaMediar.class);

	/** Retrieves the media from {@link DynamicMedia#getMedia}.
	 */
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
//		if (D.ON && log.debugable()) log.debug("View "+pi);
		if (sess == null) {
			response.sendError(response.SC_GONE, Messages.get(MZk.PAGE_NOT_FOUND, pi));
			return;
		}

		int j = pi.indexOf('/', 1) + 1;
		int k = pi.indexOf('/', j);
		if (j <= 0 || k <= 0)
			throw new ServletException("Wrong path info: "+pi);

		final String dtid = pi.substring(j, k);
		final int l = pi.indexOf('/', ++k);
		final String uuid = l >= 0 ? pi.substring(k, l): pi.substring(k);

		Media media;
		boolean download = false;
		try {
			final WebApp wapp = sess.getWebApp();
			final WebAppCtrl wappc = (WebAppCtrl)wapp;
			final UiEngine uieng = wappc.getUiEngine();
			final Desktop desktop = wappc.getDesktopCache(sess).getDesktop(dtid);
			final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;

			final Execution oldexec = Executions.getCurrent();
			final Execution exec = new ExecutionImpl(
				ctx, request, response, desktop, null);
			uieng.activate(exec);

			final Configuration config = wapp.getConfiguration();
			boolean err = false;
			try {
				config.invokeExecutionInits(exec, oldexec);
				desktopCtrl.invokeExecutionInits(exec, oldexec);

				media = desktopCtrl.getDownloadMedia(uuid, false);
				if (media != null) {
					download = true; //yes, it is for download
				} else {
					final Component comp = desktop.getComponentByUuid(uuid);
					final Object cc = ((ComponentCtrl)comp).getExtraCtrl();
					if (!(cc instanceof DynamicMedia))
						throw new ServletException(DynamicMedia.class+" must be implemented by getExtraCtrl() of "+comp);
					media = ((DynamicMedia)cc).getMedia(l >= 0 ? pi.substring(l): "");
					if (media == null) {
						response.sendError(response.SC_GONE, Messages.get(MZk.PAGE_NOT_FOUND, pi+" - "+comp));
						return;
					}
				}
			} catch (Throwable ex) {
				err = true;

				final List errs = new LinkedList();
				errs.add(ex);
				desktopCtrl.invokeExecutionCleanups(exec, oldexec, errs);
				config.invokeExecutionCleanups(exec, oldexec, errs);

				final StringBuffer errmsg = new StringBuffer(100);
				if (!errs.isEmpty()) {
					for (Iterator it = errs.iterator(); it.hasNext();) {
						final Throwable t = (Throwable)it.next();
						log.realCauseBriefly("Failed to load media, "+pi, t);
						errmsg.append('\n').append(Exceptions.getMessage(t));
					}
				}

				response.sendError(response.SC_GONE,
					Messages.get(MZk.PAGE_FAILED, new Object[] {pi, errmsg, ""}));
				return;
			} finally {
				if (!err) {
					desktopCtrl.invokeExecutionCleanups(exec, oldexec, null);
					config.invokeExecutionCleanups(exec, oldexec, null);
				}
				uieng.deactivate(exec);
			}
		} catch (ComponentNotFoundException ex) {
			//possible because view might be as late as origin comp is gone
			response.sendError(response.SC_GONE, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid));
			return;
		}

		Https.write(request, response, media, download, true);
			//it has to be resumable since browser might request twice
			//(first block for user's confirmation, second for real download)
		//FUTURE: support last-modified
	}
}
