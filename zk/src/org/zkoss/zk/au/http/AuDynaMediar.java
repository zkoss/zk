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

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.net.URLEncoder;

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
import org.zkoss.lang.Exceptions;
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

	/** Retrieves the response from {@link DynamicMedia#getMedia}.
	 */
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
//		if (D.ON && log.debugable()) log.debug("View "+pi);
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

				media = desktopCtrl.getDownloadMedia(uuid, true);
				if (media != null) {
					download = true; //yes, it is for download
				} else {
					final Component comp = desktop.getComponentByUuid(uuid);
					final Object cc = ((ComponentCtrl)comp).getExtraCtrl();
					if (!(cc instanceof DynamicMedia))
						throw new ServletException(DynamicMedia.class+" must be implemented by getExtraCtrl() of "+comp);
					media = ((DynamicMedia)cc).getMedia(l >= 0 ? pi.substring(l): "");
					if (media == null) {
						response.sendError(response.SC_GONE, "Media not found in "+comp);
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
						if (t == ex) //not eaten
							log.error("Failed to load the media.", t);
						errmsg.append('\n').append(Exceptions.getMessage(t));
					}
				}

				response.sendError(response.SC_GONE, "Failed to load the media."+errmsg);
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

		final byte[] data;
		synchronized (media) { //Bug 1896797: media might be access concurr.
			//reading an image and send it back to client
			final String ctype = media.getContentType();
			if (ctype != null)
				response.setContentType(ctype);

			if (download) {
				String value = "attachment";
				final String flnm = media.getName();
				if (flnm != null && flnm.length() > 0)
					value += ";filename=\"" + URLEncoder.encode(flnm, "UTF-8") +'"';
				response.setHeader("Content-Disposition", value);
				//response.setHeader("Content-Transfer-Encoding", "binary");
				//response.setHeader("Accept-Ranges", "bytes");
			}

			if (!media.inMemory()) {
				if (media.isBinary()) {
					final ServletOutputStream out = response.getOutputStream();
					final InputStream in = media.getStreamData();
					try {
						Files.copy(out, in);
					} catch (IOException ex) {
						//browser might close the connection
						//and reread (test case: B30-1896797.zul)
						//so, read it completely, since 2nd read counts on it
						if (in instanceof org.zkoss.io.Repeatable) {
							try {
								final byte[] buf = new byte[1024*8];
								for (int v; (v = in.read(buf)) >= 0;)
									;
							} catch (Throwable t) { //ignore it
							}
						}
						throw ex;
					} finally {
						in.close();
					}
					out.flush();
				} else {
					final Writer out = response.getWriter();
					final Reader in = media.getReaderData();
					try {
						Files.copy(out, in);
					} catch (IOException ex) {
						//browser might close the connection and reread
						//so, read it completely, since 2nd read counts on it
						if (in instanceof org.zkoss.io.Repeatable) {
							try {
								final char[] buf = new char[1024*4];
								for (int v; (v = in.read(buf)) >= 0;)
									;
							} catch (Throwable t) { //ignore it
							}
						}
						throw ex;
					} finally {
						in.close();
					}
					out.flush();
				}
				return; //done;
			}

			data = media.isBinary() ? media.getByteData():
				media.getStringData().getBytes("UTF-8");
		}

		response.setContentLength(data.length);
		final ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		//FUTURE: support last-modified
	}
}
