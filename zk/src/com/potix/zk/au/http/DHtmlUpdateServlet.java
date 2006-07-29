/* DHtmlUpdateServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.http;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.commons.fileupload.FileItem;

import com.potix.mesg.Messages;
import com.potix.lang.D;
import com.potix.lang.Strings;
import com.potix.lang.Exceptions;
import com.potix.util.logging.Log;
import com.potix.util.media.Media;
import com.potix.util.media.AMedia;
import com.potix.io.Files;
import com.potix.image.AImage;
import com.potix.sound.AAudio;

import com.potix.web.servlet.Servlets;
import com.potix.web.servlet.http.Https;
import com.potix.web.servlet.http.Encodes;
import com.potix.web.servlet.http.HttpMultipartRequest;
import com.potix.web.servlet.auth.Authens;
import com.potix.web.util.resource.ClassWebResource;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.ext.Viewable;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.SessionCtrl;
import com.potix.zk.ui.sys.ComponentsCtrl;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.http.ExecutionImpl;
import com.potix.zk.ui.http.WebManager;
import com.potix.zk.ui.http.I18Ns;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.AuResponse;
import com.potix.zk.au.AuObsolete;
import com.potix.zk.au.AuAlert;
import com.potix.zk.au.AuSendRedirect;
import com.potix.zk.au.CommandNotFoundException;

/**
 * Used to receive command from the server and send result back to client.
 *
 * <p>Design decision: it is better to use independent servlets for
 * /web, /upload and /view. However, to simplify the configuration,
 * we choose not to.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DHtmlUpdateServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlUpdateServlet.class);

	private ServletContext _ctx;
	private int _nextContentId;
	private long _lastModified;

	public void init(ServletConfig config) throws ServletException {
		if (log.debugable()) log.debug("Starting DHtmlUpdateServlet at "+config.getServletContext());
		_ctx = config.getServletContext();
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	//-- super --//
	protected long getLastModified(HttpServletRequest request) {
		final String pi = Https.getThisPathInfo(request);
		if (pi != null && pi.startsWith(ClassWebResource.PATH_PREFIX)
		&& !pi.endsWith(".dsp") && !Servlets.isIncluded(request)) {
			if (_lastModified == 0) _lastModified = new Date().getTime();
				//Hard to know when it is modified, so cheat it..
			return _lastModified;
		}
		return -1;
	}
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		Authens.isAuthenticated(request);
		//Enforce Web container to authenticate again
		//CONSIDER: we might use login="required" to define pages

		final Session sess = WebManager.getSession(_ctx, request);
		final Object old = I18Ns.setup(sess, request, response);
		try {
			final String pi = Https.getThisPathInfo(request);
			if (D.ON && log.finerable()) log.finer("Path info: "+pi);
			if (pi != null) {
				if (pi.startsWith(ClassWebResource.PATH_PREFIX)) {
					WebManager.getWebManager(_ctx).
						getClassWebResource().doGet(request, response);
					return;
				}
				if (pi.startsWith("/upload")) {
					upload(sess, request, response);
					return;
				}
				if (pi.startsWith("/view")) {
					view(sess, request, response, pi.substring(5));
					return;
				}
			}

			process(sess, request, response);
		} finally {
			I18Ns.cleanup(request, old);
		}
	}
	protected
	void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	//-- ASYNC-UPDATE --//
	/** Process update requests from the client. */
	private void process(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final UiEngine uieng = wappc.getUiEngine();
		final List aureqs = new LinkedList();

		//parse desktop ID
		final String dtid = request.getParameter("dtid");
		if (dtid == null) {
			responseError(uieng, response, "Illegal request: dtid is required");
			return;
		}
		final Desktop desktop;
		try {
			desktop = wappc.getDesktopCache(sess).getDesktop(dtid);
			WebManager.setDesktop(request, desktop);
				//reason: a new page might be created (such as include)
		} catch (ComponentNotFoundException ex) {
			final StringWriter out = getXmlWriter();

			final String scmd = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(scmd) && !"onRender".equals(scmd)
			&& !"onTimer".equals(scmd)) {//possible in FF due to cache
				String uri = wapp.getConfiguration().getTimeoutURI();
				final AuResponse resp;
				if (uri != null) {
					if (uri.length() != 0)
						uri = Encodes.encodeURL(_ctx, request, response, uri);
					resp = new AuSendRedirect(uri, null);
				} else {
					resp = new AuObsolete(
						dtid, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, dtid));
				}
				uieng.response(resp, out);
			}

			flushXmlWriter(response, out);
			return;
		}

		//parse commands
		try {
			for (int j = 0;; ++j) {
				final String scmd = request.getParameter("cmd."+j);
				if (scmd == null)
					break;

				final AuRequest.Command cmd = AuRequest.getCommand(scmd);
				final String uuid = request.getParameter("uuid."+j);
				final String[] data = request.getParameterValues("data."+j);
				if (data != null) {
					for (int k = data.length; --k >= 0;)
						if ("zk_null~q".equals(data[k]))
							data[k] = null;
				}
				if (uuid == null || uuid.length() == 0) {
					aureqs.add(new AuRequest(desktop, cmd, data));
				} else {
					aureqs.add(new AuRequest(desktop, uuid, cmd, data));
				}
			}
			if (aureqs.isEmpty()) {
				responseError(uieng, response, "Illegal request: cmd is required");
				return;
			}
		} catch (CommandNotFoundException ex) {
			responseError(uieng, response, Exceptions.getMessage(ex));
			return;
		}

		if (D.ON && log.debugable()) log.debug("AU request: "+aureqs);
		final StringWriter out = getXmlWriter();

		final Execution exec = new ExecutionImpl(
			_ctx, request, response, desktop, null);
		uieng.execUpdate(exec, aureqs, out);

		flushXmlWriter(response, out);
	}

	/** Returns the writer for output XML.
	 * @param withrs whether to output <rs> first.
	 */
	private static StringWriter getXmlWriter() {
		final StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs>\n");
		return out;
	}
	/** Flushes all content in out to the response.
	 * Don't write the response thereafter.
	 * @param withrs whether to output </rs> first.
	 */
	private static final
	void flushXmlWriter(HttpServletResponse response, StringWriter out)
	throws IOException {
		out.write("\n</rs>");

		//Use OutputStream due to Bug 1528592 (Jetty 6)
		final byte[] bs = out.toString().getBytes("UTF-8");
		response.setContentType("text/xml;charset=UTF-8");
		response.setContentLength(bs.length);
		response.getOutputStream().write(bs);
		response.flushBuffer();
	}

	/** Generates a response for an error message.
	 */
	private static
	void responseError(UiEngine uieng, HttpServletResponse response,
	String errmsg) throws IOException {
		log.debug(errmsg);

		//Don't use sendError because Browser cannot handle UTF-8
		final StringWriter out = getXmlWriter();
		uieng.response(new AuAlert(errmsg), out);
		flushXmlWriter(response, out);
	}

	//-- UPLOAD --//
	/** Receives a file upload from the client.
	 */
	private void upload(Session sess, HttpServletRequest request,
	HttpServletResponse response)
	throws ServletException, IOException {
		final Map attrs = new HashMap(5);
		String alert = null, uuid = null,
			nextURI = "~./zul/html/fileupload-done.dsp";
		try {
			request = HttpMultipartRequest.getInstance(request);

			uuid = request.getParameter("uuid");
			if (uuid != null)
				attrs.put("uuid", uuid);
			if (D.ON && log.debugable()) log.debug("Upload for "+uuid);

			final String uri = request.getParameter("nextURI");
			if (uri != null && uri.length() != 0)
				nextURI = uri;

			if (!(request instanceof HttpMultipartRequest)) {
				alert = "enctype must be multipart/form-data";
			} else {
				final HttpMultipartRequest mreq = (HttpMultipartRequest)request;
				try {
					alert = upload0(sess, attrs, mreq, uuid);
				} catch (ComponentNotFoundException ex) {
					alert = Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid);
				}
			}
		} catch (Throwable ex) {
			log.realCauseBriefly("Failed to upload", ex);
			alert = Exceptions.getMessage(ex);
			if (uuid == null) {
				uuid = request.getParameter("uuid");
				if (uuid != null)
					attrs.put("uuid", uuid);
			}
		}

		if (alert != null)
			attrs.put("alert", alert);
		if (log.debugable()) log.debug(attrs);

		Servlets.forward(_ctx, request, response,
			nextURI, attrs, Servlets.PASS_THRU_ATTR);
	}
	/** Process the upload
	 * @return the error message if failed, or null if succeed.
	 */
	private String upload0(Session sess, Map attrs, HttpMultipartRequest mreq,
	String uuid) throws IOException {
		final FileItem fi = mreq.getUploadItem("file");
		if (fi == null)
			return null; //OK; nothing uploaded.

		if (uuid == null || uuid.length() == 0)
			return "uuid is required";
		final String dtid = mreq.getParameter("dtid");
		if (dtid == null || dtid.length() == 0)
			return "dtid is required";

		//parse uuid
		//CONSIDER TO FIX:
		//for components that implements RawId won't work here!!
		if (!ComponentsCtrl.isUuid(uuid))
			return "Not a component UUID: "+uuid;

		final WebAppCtrl wappc = (WebAppCtrl)sess.getWebApp();
		final Desktop desktop = wappc.getDesktopCache(sess).getDesktop(dtid);

		final int v;
		synchronized (this) {
			v = _nextContentId++;
		}
		final String contentId = Strings.encode(
			new StringBuffer(12).append("_pctt"), v).toString();

		Media media = null;
		String name = getBaseName(fi);
		if (name != null) {
		//Not sure whether a name might contain ;jsessionid or similar
		//But we handle this case: x.y;z
			final int j = name.lastIndexOf(';');
			if (j > 0) {
				final int k = name.lastIndexOf('.');
				if (k >= 0 && j > k && k > name.lastIndexOf('/'))
					name = name.substring(0, j);
			}
		}
		final String ctype = fi.getContentType();
		if (ctype != null)
			if (ctype.startsWith("image/")) {
				try {
					media = new AImage(name, fi.get());
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			} else if (ctype.startsWith("audio/")) {
				try {
					media = new AAudio(name, fi.get());
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			}

		if (media == null)
			media = new AMedia(name, null, ctype, fi.getInputStream());
		desktop.setAttribute(contentId, media);
		attrs.put("contentId", contentId);

		//Note: we don't invoke Updatable.setResult() here because it might
		//cause a sequence of updates. Thus, we use javascript to go thru
		//standard async-update (by the doUpdate request)
		return null; //success
	}
	/** Returns the base name for FileItem (i.e., removing path).
	 */
	private static String getBaseName(FileItem fi) {
		String name = fi.getName();
		if (name == null)
			return null;

		final String[] seps = {"/", "\\", "%5c", "%5C", "%2f", "%2F"};
		for (int j = seps.length; --j >= 0;) {
			final int k = name.lastIndexOf(seps[j]);
			if (k >= 0)
				name = name.substring(k + seps[j].length());
		}
		return name;
	}

	//-- VIEW --//
	/** Retrieves the response from {@link Viewable#getView}.
	 */
	private void view(Session sess, HttpServletRequest request,
	HttpServletResponse response, String pi)
	throws ServletException, IOException {
		if (D.ON && log.debugable()) log.debug("View "+pi);

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
			final WebAppCtrl wappc = (WebAppCtrl)sess.getWebApp();
			final UiEngine uieng = wappc.getUiEngine();
			final Desktop desktop = wappc.getDesktopCache(sess).getDesktop(dtid);

			final Execution exec = new ExecutionImpl(
				_ctx, request, response, desktop, null);
			uieng.activate(exec);
			try {
				final Component comp = desktop.getComponentByUuid(uuid);
				if (!(comp instanceof Viewable))
					throw new ServletException(Viewable.class+" must be implemented: "+comp.getClass());
				media = ((Viewable)comp).getView(k >= 0 ? pi.substring(k): "");
				if (media == null) {
					response.sendError(response.SC_GONE, "Media not found in "+comp);
					return;
				}
			} finally {
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
