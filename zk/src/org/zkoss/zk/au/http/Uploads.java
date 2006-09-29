/* Uploads.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 12:46:31     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Exceptions;
import org.zkoss.mesg.Messages;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;
import org.zkoss.image.AImage;
import org.zkoss.sound.AAudio;

import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;

/**
 * The utility used to process file upload.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
/*package*/ class Uploads {
	private static final Log log = Log.lookup(Uploads.class);

	/** Processes a file uploaded from the client.
	 */
	public static void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final Map attrs = new HashMap();
		String alert = null, uuid = null,
			nextURI = "~./zul/html/fileupload-done.dsp";
		try {
			if (!isMultipartContent(request)) {
				alert = "enctype must be multipart/form-data";
			} else {
				uuid = request.getParameter("uuid");
				if (uuid == null || uuid.length() == 0) {
					alert = "uuid is required";
				} else {
					attrs.put("uuid", uuid);

					final String dtid = (String)request.getParameter("dtid");
					if (dtid == null || dtid.length() == 0) {
						alert = "dtid is required";
					} else {
						final Desktop desktop = ((WebAppCtrl)sess.getWebApp())
							.getDesktopCache(sess).getDesktop(dtid);
						final Map params = parseRequest(request, desktop, false);

						final String uri = (String)params.get("nextURI");
						if (uri != null && uri.length() != 0)
							nextURI = uri;

						final FileItem fi = (FileItem)params.get("file");
						if (fi != null)
							process0(sess, desktop, fi, attrs);
					}
				}
			}
		} catch (Throwable ex) {
			if (uuid == null) {
				uuid = request.getParameter("uuid");
				if (uuid != null)
					attrs.put("uuid", uuid);
			}
			if (ex instanceof ComponentNotFoundException) {
				alert = Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid);
			} else {
				log.realCauseBriefly("Failed to upload", ex);
				alert = Exceptions.getMessage(ex);
			}
		}

		if (alert != null)
			attrs.put("alert", alert);
		if (D.ON && log.finerable()) log.finer(attrs);

		Servlets.forward(ctx, request, response,
			nextURI, attrs, Servlets.PASS_THRU_ATTR);
	}

	/** Returns the error message, or null if success.
	 */
	private static final
	String process0(Session sess, Desktop desktop, FileItem fi, Map attrs)
	throws IOException {
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
					media = fi.isInMemory() ? new AImage(name, fi.get()):
						new AImage(name, fi.getInputStream());
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			} else if (ctype.startsWith("audio/")) {
				try {
					media = fi.isInMemory() ? new AAudio(name, fi.get()):
						new AAudio(name, fi.getInputStream());
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			}

		if (media == null)
			media = fi.isInMemory() ?
				new AMedia(name, null, ctype, fi.get()):
				new AMedia(name, null, ctype, fi.getInputStream());

		final String contentId = Strings.encode(
			new StringBuffer(12).append("_pctt"),
			((DesktopCtrl)desktop).getNextId()).toString();
		desktop.setAttribute(contentId, media);
		attrs.put("contentId", contentId);

		//Note: we don't invoke Updatable.setResult() here because it might
		//cause a sequence of updates. Thus, we use javascript to go thru
		//standard async-update (by the doUpdate request)
		return null; //success
	}

	/** Parses the multipart request into a map of
	 * (String nm, FileItem/String/List(FileItem/String)).
	 *
	 * @param incPramMap whether to include request.getParameterMap in
	 * the returned map
	 */
	private static Map parseRequest(HttpServletRequest request,
	Desktop desktop, boolean incParamMap)
	throws FileUploadException {
		final Map params = new HashMap();

		if (incParamMap) {
			for (Iterator it = request.getParameterMap().entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String nm = (String)me.getKey();
				final String[] vals = (String[])me.getValue();
				if (vals.length == 0) params.put(nm, "");
				else if (vals.length == 1) params.put(nm, vals[0]);
				else {
					final List l = new LinkedList();
					CollectionsX.addAll(l, vals);
					params.put(nm, l);
				}
			}
		}

		final ServletFileUpload sfu =
			new ServletFileUpload(new ZkFileItemFactory(desktop, request));
		final Configuration cfg = desktop.getWebApp().getConfiguration();
		final Integer maxsz = cfg.getMaxUploadSize();
		sfu.setSizeMax(maxsz != null ? 1024L*maxsz.intValue(): -1);
		for (Iterator it = sfu.parseRequest(request).iterator(); it.hasNext();) {
			final FileItem fi = (FileItem)it.next();
			final String nm = fi.getFieldName();
			final Object val;
			if (fi.isFormField()) {
				val = fi.getString();
			} else {
				val = fi;
			}

			final Object old = params.put(nm, val);
			if (old != null) {
				final List vals;
				if (old instanceof List) {
					vals = (List)old;
				} else {
					params.put(nm, vals = new LinkedList());
					vals.add(old);
				}
				vals.add(val);
			}
		}
		return params;
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

	/** Returns whether the request contains multipart content.
	 */
	public static final boolean isMultipartContent(HttpServletRequest request) {
		return "post".equals(request.getMethod().toLowerCase())
			&& FileUploadBase.isMultipartContent(new ServletRequestContext(request));
	}
}
