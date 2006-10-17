/* DynaMedias.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 12:48:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.D;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.util.logging.Log;

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
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.http.ExecutionImpl;

/**
 * The utility used to response the content for {@link DynamicMedia#getMedia}
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
/*package*/ class DynaMedias {
//	private static final Log log = Log.lookup(DynaMedias.class);

	private DynaMedias() {}

	/** Retrieves the response from {@link DynamicMedia#getMedia}.
	 */
	public static final void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
//		if (D.ON && log.debugable()) log.debug("View "+pi);

		if (pi.length() == 0 || pi.charAt(0) != '/')
			throw new ServletException("Wrong path info: "+pi);
		int j = pi.indexOf('/', 1);
		if (j < 0)
			throw new ServletException("Wrong path info: "+pi);

		final String dtid = pi.substring(1, j);
		final int k = pi.indexOf('/', ++j);
		final String uuid = k >= 0 ? pi.substring(j, k): pi.substring(j);

		final Media media;
		try {
			final WebApp wapp = sess.getWebApp();
			final WebAppCtrl wappc = (WebAppCtrl)wapp;
			final UiEngine uieng = wappc.getUiEngine();
			final Desktop desktop = wappc.getDesktopCache(sess).getDesktop(dtid);

			final Execution oldexec = Executions.getCurrent();
			final Execution exec = new ExecutionImpl(
				ctx, request, response, desktop, null);
			uieng.activate(exec);

			final Configuration config = wapp.getConfiguration();
			boolean err = false;
			try {
				config.invokeExecutionInits(exec, oldexec);

				final Component comp = desktop.getComponentByUuid(uuid);
				final Object cc = ((ComponentCtrl)comp).getExtraCtrl();
				if (!(cc instanceof DynamicMedia))
					throw new ServletException(DynamicMedia.class+" must be implemented by getExtraCtrl() of "+comp);
				media = ((DynamicMedia)cc).getMedia(k >= 0 ? pi.substring(k): "");
				if (media == null) {
					response.sendError(response.SC_GONE, "Media not found in "+comp);
					return;
				}
			} catch (Throwable ex) {
				err = true;
				config.invokeExecutionCleanups(exec, oldexec, ex, null);

				if (ex instanceof ServletException) throw (ServletException)ex;
				if (ex instanceof IOException) throw (IOException)ex;
				throw UiException.Aide.wrap(ex);
			} finally {
				if (!err) config.invokeExecutionCleanups(exec, oldexec, null, null);
				uieng.deactivate(exec);
			}
		} catch (ComponentNotFoundException ex) {
			//possible because view might be as late as origin comp is gone
			response.sendError(response.SC_GONE, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid));
			return;
		}

		//reading an image and send it back to client
		final String ctype = media.getContentType();
		if (ctype != null)
			response.setContentType(ctype);

		if (!media.inMemory()) {
			if (media.isBinary()) {
				final ServletOutputStream out = response.getOutputStream();
				Files.copy(out, media.getStreamData());
				out.flush();
			} else {
				final Writer out = response.getWriter();
				Files.copy(out, media.getReaderData());
				out.flush();
			}
			return; //done;
		}

		final ServletOutputStream out = response.getOutputStream();
		final byte[] data = media.isBinary() ? media.getByteData():
			media.getStringData().getBytes("UTF-8");
		response.setContentLength(data.length);
		out.write(data);
		out.flush();
		//FUTURE: support last-modified
	}
}
