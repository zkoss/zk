/* AuUploader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 11 18:53:30     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.zkoss.image.AImage;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.sound.AAudio;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;
import org.zkoss.web.servlet.http.Encodes;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.au.out.AuObsolete;
import org.zkoss.zk.au.out.AuSendRedirect;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.Updatable;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zul.Fileupload;


/**
 * The utility used to process file upload.
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public class AuUploader implements AuProcessor{
	private static final Log log = Log.lookup(AuUploader.class);

	/** Processes a file uploaded from the client.
	 */
	public void process(Session sess, ServletContext ctx,
			HttpServletRequest request, HttpServletResponse response, String pathInfo)
	throws ServletException, IOException {		
		if (sess == null) {
			response.sendError(response.SC_GONE, Messages.get(MZk.PAGE_NOT_FOUND, pathInfo));
			return;
		}
		
		//
		String uuid = request.getParameter("uuid");
		String dtid = request.getParameter("dtid");
		String fid = ""+request.getParameter("fid");
		int maxsize = Integer.parseInt(request.getParameter("maxsize"));
		
		Desktop desktop = null;
		try{
			desktop = (Desktop)((WebAppCtrl)sess.getWebApp()).getDesktopCache(sess).getDesktop(dtid);
		}catch(Exception e){
			return; //session time out do nothing
		}
		
		AbstractComponent fileupload=(Fileupload) desktop.getComponentByUuid(uuid);
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setProgressListener(new ProgressCallback(fileupload,fid));
		upload.setHeaderEncoding("UTF-8"); //Deal with Chinese/Japanese/.... file names
		try {
			List items = upload.parseRequest(request);
			if (items != null) {
				Iterator itr = items.iterator();
				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
					if (item.isFormField()){
						continue;
					}
					Media med = convertFileType(item, desktop);
					if(med != null){
						((Updatable)fileupload.getExtraCtrl()).addMedia(med);
					}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//returns ok to swfupload. Otherwise swfupload will be at 100% for ever
		response.getOutputStream().println("200 OK");
	}
	private void sessionTimeout(ServletContext ctx, HttpServletRequest request,
			HttpServletResponse response, String dtid)
			throws ServletException, IOException {
				final String sid = request.getHeader("ZK-SID");
				if (sid != null)
					response.setHeader("ZK-SID", sid);

				final AuWriter out =
					AuWriters.newInstance().open(request, response, 0);

						String uri = Devices.getTimeoutURI(getDeviceType(request));
						final AuResponse resp;
						if (uri != null) {
							if (uri.length() != 0)
								uri = Encodes.encodeURL(ctx, request, response, uri);
							resp = new AuSendRedirect(uri, null);
						} else {
							resp = new AuObsolete(
								dtid, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, dtid));
						}
						out.write(resp);

				out.close(request, response);
			}
			private static String getDeviceType(HttpServletRequest request) {
				final String agt = request.getHeader("user-agent");
				if (agt != null && agt.length() > 0) {
					try {
						return Devices.getDeviceByClient(agt).getType();
					} catch (Throwable ex) {
						log.warning("Unknown device for "+agt);
					}
				}
				return "ajax";
			}
	private static Media convertFileType(FileItem fi, Desktop desktop) throws IOException{
		String ctype = fi.getContentType(),
			ctypelc = ctype.toLowerCase();
		String name = fi.getName();
		if (name != null && "application/octet-stream".equals(ctype)) { //Bug 1896291: IE limit
			final int j = name.lastIndexOf('.');
			if (j >= 0) {
				String s = ContentTypes.getContentType(name.substring(j + 1));
				if (s != null)
					ctype = ctypelc = s;
			}
		}
		if ( ctypelc != null) {
			if (ctypelc.startsWith("image/")) {
				try {
					return fi.isInMemory() ? new AImage(name, fi.get()):
						new AImage(name, fi.getInputStream());
							//note: AImage converts stream to binary array
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			} else if (ctypelc.startsWith("audio/")) {
				try {
					return fi.isInMemory() ? new AAudio(name, fi.get()):
						new StreamAudio(name, fi);
				} catch (Throwable ex) {
					if (log.debugable()) log.debug("Unknown file format: "+ctype);
				}
			} else if (ctypelc.startsWith("text/")) {
				String charset = getCharset(ctype);
				if (charset == null) {
					final Configuration conf = desktop.getWebApp().getConfiguration();
					final CharsetFinder chfd = conf.getUploadCharsetFinder();
					if (chfd != null)
						charset = chfd.getCharset(ctype,
							fi.isInMemory() ?
								new ByteArrayInputStream(fi.get()):
								fi.getInputStream());
					if (charset == null)
						charset = conf.getUploadCharset();
				}
				return fi.isInMemory() ?
					new AMedia(name, null, ctype, fi.getString(charset)):
					new ReaderMedia(name, null, ctype, fi, charset);
			}
		}

		return fi.isInMemory() ?
			new AMedia(name, null, ctype, fi.get()):
			new StreamMedia(name, null, ctype, fi);
		
	}
	private static String getCharset(String ctype) {
		final String ctypelc = ctype.toLowerCase();
		for (int j = 0; (j = ctypelc.indexOf("charset", j)) >= 0; j += 7) {
			int k = Strings.skipWhitespacesBackward(ctype, j - 1);
			if (k < 0 || ctype.charAt(k) == ';') {
				k = Strings.skipWhitespaces(ctype, j + 7);
				if (k <= ctype.length() && ctype.charAt(k) == '=') {
					j = ctype.indexOf(';', ++k);
					String charset =
						(j >= 0 ? ctype.substring(k, j): ctype.substring(k)).trim();
					if (charset.length() > 0)
						return charset;
					break; //use default
				}
			}
		}
		return null;
	}
	private static class StreamAudio extends AAudio {
		private final FileItem _fi;
		public StreamAudio(String name, FileItem fi) throws IOException {
			super(name, DYNAMIC_STREAM);
			_fi = fi;
		}
		public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read "+_fi, ex);
			}
		}
	}
	private static class StreamMedia extends AMedia {
		private final FileItem _fi;
		public StreamMedia(String name, String format, String ctype, FileItem fi) {
			super(name, format, ctype, DYNAMIC_STREAM);
			_fi = fi;
		}
		public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read "+_fi, ex);
			}
		}
	}
	private static class ReaderMedia extends AMedia {
		private final FileItem _fi;
		private final String _charset;
		public ReaderMedia(String name, String format, String ctype,
		FileItem fi, String charset) {
			super(name, format, ctype, DYNAMIC_READER);
			_fi = fi;
			_charset = charset;
		}
		public java.io.Reader getReaderData() {
			try {
				return new java.io.InputStreamReader(
					_fi.getInputStream(), _charset);
			} catch (IOException ex) {
				throw new UiException("Unable to read "+_fi, ex);
			}
		}
	}

	class ProgressCallback implements ProgressListener {
		Desktop desktop;
		AbstractComponent fileupload;
		String fid;
		ProgressCallback(AbstractComponent fileupload, String fid){
			this.fileupload = fileupload;
			this.fid = fid;
		}
		public void update(long pBytesRead, long pContentLength, int pItems) {
			 ((Updatable)fileupload.getExtraCtrl()).setProgress(this.fid, pBytesRead ,pContentLength);
		}
	}
}

