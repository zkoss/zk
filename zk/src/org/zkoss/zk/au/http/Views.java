/* Views.java

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
package com.potix.zk.au.http;

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

import com.potix.mesg.Messages;
import com.potix.lang.D;
import com.potix.io.Files;
import com.potix.util.media.Media;
import com.potix.util.logging.Log;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.ext.Viewable;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.http.ExecutionImpl;

/**
 * The utility used to response the content for {@link Viewable#getView}
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
/*package*/ class Views {
//	private static final Log log = Log.lookup(Views.class);

	private Views() {}

	/** Retrieves the response from {@link Viewable#getView}.
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
				if (!(comp instanceof Viewable))
					throw new ServletException(Viewable.class+" must be implemented: "+comp.getClass());
				media = ((Viewable)comp).getView(k >= 0 ? pi.substring(k): "");
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
