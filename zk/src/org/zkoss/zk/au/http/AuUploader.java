/* AuUploader.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 11 18:53:30     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.IOFileUploadException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.zkoss.image.AImage;
import static org.zkoss.lang.Generics.cast;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.sound.AAudio;
import org.zkoss.util.logging.Log;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;

/**
 * The AU extension to upload files.
 * It is based on Apache Commons File Upload.
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public class AuUploader implements AuExtension {
	private static final Log log = Log.lookup(AuUploader.class);

	private ServletContext _ctx;

	public AuUploader() {
	}
	public void init(DHtmlUpdateServlet servlet) {
		_ctx = servlet.getServletContext();
	}
	public void destroy() {
	}

	/** Processes a file uploaded from the client.
	 */
	public void service(
	HttpServletRequest request, HttpServletResponse response, String pathInfo)
	throws ServletException, IOException {
		final Session sess = Sessions.getCurrent(false);
		if (sess == null) {
			response.setIntHeader("ZK-Error", HttpServletResponse.SC_GONE);
			return;
		}

		final Map<String, String> attrs = new HashMap<String, String>();
		String alert = null, uuid = null, nextURI = null, sid = null;
		Desktop desktop = null;
		try {
			if (!isMultipartContent(request)) {
				if ("uploadInfo".equals(request.getParameter("cmd"))) {
					uuid = request.getParameter("wid");
					sid = request.getParameter("sid");
					desktop = ((WebAppCtrl)sess.getWebApp()).getDesktopCache(sess).getDesktop(request.getParameter("dtid"));
					Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
					Map<String, Object> size = cast((Map) desktop.getAttribute(Attributes.UPLOAD_SIZE));
					final String key = uuid + '_' + sid;
					Object sinfo = size.get(key);
					if (sinfo instanceof String) {
						response.getWriter().write("error:" + sinfo);
						size.remove(key);
						percent.remove(key);
						return;
					}
					final Integer p = percent.get(key);
					final Long cb = (Long)sinfo;
					response.getWriter().write((p != null ? p.intValue(): -1)+ ","
								+(cb != null ? cb.longValue(): -1));
					return;
				} else 
					alert = "enctype must be multipart/form-data";
			} else {
				uuid = request.getParameter("uuid");
				sid = request.getParameter("sid");
				if (uuid == null || uuid.length() == 0) {
					alert = "uuid is required!";
				} else {
					attrs.put("uuid", uuid);
					attrs.put("sid", sid);

					final String dtid = request.getParameter("dtid");
					if (dtid == null || dtid.length() == 0) {
						alert = "dtid is required!";
					} else {
						desktop = ((WebAppCtrl)sess.getWebApp())
							.getDesktopCache(sess).getDesktop(dtid);
						final Map<String, Object> params = parseRequest(request, desktop, uuid + '_' + sid);
						nextURI = (String)params.get("nextURI");
						
						// Bug 3054784
						params.put("native", request.getParameter("native"));
						processItems(desktop, params, attrs);
					}
				}
			}
		} catch (Throwable ex) {
			if (uuid == null) {
				uuid = request.getParameter("uuid");
				if (uuid != null)
					attrs.put("uuid", uuid);
			}
			if (nextURI == null)
				nextURI = request.getParameter("nextURI");

			if (ex instanceof ComponentNotFoundException) {
				alert = Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid);
			} else if (ex instanceof IOFileUploadException) {
				log.debug("File upload cancelled!");
			} else {
				alert = handleError(ex);
			}

			if (desktop != null) {
				Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
				Map<String, Object> size = cast((Map) desktop.getAttribute(Attributes.UPLOAD_SIZE));
				final String key = uuid + '_' + sid;
				if (percent != null) {
					percent.remove(key);
					size.remove(key);
				}
			}
		}
		if (attrs.get("contentId") == null && alert == null)
			alert = "contentId is required!";
			
		if (alert != null) {
			if (desktop == null) {
				response.setIntHeader("ZK-Error", HttpServletResponse.SC_GONE);
				return;
			}
			Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
			Map<String, Object> size = cast((Map)desktop.getAttribute(Attributes.UPLOAD_SIZE));
			final String key = uuid + '_' + sid;
			if (percent != null) {
				percent.remove(key);
				size.put(key, alert);
			}
		}
		if (log.finerable()) log.finer(attrs);

		if (nextURI == null || nextURI.length() == 0)
			nextURI = "~./zul/html/fileupload-done.html.dsp";
		Servlets.forward(_ctx, request, response,
			nextURI, attrs, Servlets.PASS_THRU_ATTR);
	}
	/** Handles the exception that was thrown when uploading files,
	 * and returns the error message.
	 * When uploading file(s) causes an exception, this method will be
	 * called to generate the proper error message.
	 *
	 * <p>By default, it logs the error and then use {@link Exceptions#getMessage}
	 * to retrieve the error message.
	 *
	 * <p>If you prefer not to log or to generate the custom error message,
	 * you can extend this class and override this method.
	 * Then, specify it in web.xml as follows. 
	 * (we change from processor0 to extension0 after ZK5.)
	 * @see DHtmlUpdateServlet
<code><pre>&lt;servlet&gt;
  &lt;servlet-class&gt;org.zkoss.zk.au.http.DHtmlUpdateServlet&lt;/servlet-class&gt;
  &lt;init-param&gt;
    &lt;param-name&gt;extension0&lt;/param-name&gt;
    &lt;param-value&gt;/upload=com.my.MyUploader&lt;/param-value&gt;
  &lt;/init-param&gt;
...</pre></code>
	 * 
	 * @param ex the exception.
	 * Typical exceptions include org.apache.commons.fileupload .FileUploadBase.SizeLimitExceededException
	 * @since 3.0.4
	 */
	protected String handleError(Throwable ex) {
		log.realCauseBriefly("Failed to upload", ex);
		return Exceptions.getMessage(ex);
	}

	/** Process fileitems named file0, file1 and so on.
	 */
	private static final
	void processItems(Desktop desktop, Map<String, Object> params, Map<String, String> attrs)
	throws IOException {
		final List<Media> meds = new LinkedList<Media>();
		final boolean alwaysNative = "true".equals(params.get("native"));
		final Object fis = params.get("file");
		if (fis instanceof FileItem) {
			meds.add(processItem(desktop, (FileItem)fis, alwaysNative));
		} else if (fis != null) {
			for (Iterator it = ((List)fis).iterator(); it.hasNext();) {
				meds.add(processItem(desktop, (FileItem)it.next(), alwaysNative));
			}
		}

		final String contentId = Strings.encode(
			new StringBuffer(12).append("z__ul_"),
			((DesktopCtrl)desktop).getNextKey()).toString();
		attrs.put("contentId", contentId);
		desktop.setAttribute(contentId, meds);
	}
	/** Process the specified fileitem.
	 */
	private static final
	Media processItem(Desktop desktop, FileItem fi, boolean alwaysNative)
	throws IOException {
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

		String ctype = fi.getContentType(),
			ctypelc = ctype != null ? ctype.toLowerCase(): null;
		if (name != null && "application/octet-stream".equals(ctypelc)) { //Bug 1896291: IE limit
			final int j = name.lastIndexOf('.');
			if (j >= 0) {
				String s = ContentTypes.getContentType(name.substring(j + 1));
				if (s != null)
					ctypelc = ctype = s;
			}
		}

		if (!alwaysNative && ctypelc != null) {
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
						new StreamAudio(name, fi, ctypelc);
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

	/** Parses the multipart request into a map of
	 * (String nm, FileItem/String/List(FileItem/String)).
	 */
	private static Map<String, Object> parseRequest(HttpServletRequest request,
	Desktop desktop, String key)
	throws FileUploadException {
		final Map<String, Object> params = new HashMap<String, Object>();
		final ItemFactory fty = new ItemFactory(desktop, request, key);
		final ServletFileUpload sfu = new ServletFileUpload(fty);
		
		final Configuration conf = desktop.getWebApp().getConfiguration();
		int thrs = conf.getFileSizeThreshold();
		if (thrs > 0)
			fty.setSizeThreshold(1024*thrs);
		
		sfu.setProgressListener(fty.new ProgressCallback());
		
		int maxsz = conf.getMaxUploadSize();
		try {
			maxsz = Integer.parseInt(request.getParameter("maxsize"));
		} catch (NumberFormatException e) {}
		
		sfu.setSizeMax(maxsz >= 0 ? 1024L*maxsz: -1);

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
				final List<Object> vals;
				if (old instanceof List) {
					params.put(nm, vals = cast((List)old));
				} else {
					params.put(nm, vals = new LinkedList<Object>());
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
		public boolean isBinary() {
			return true;
		}
		public boolean inMemory() {
			return false;
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
		public boolean isBinary() {
			return false;
		}
		public boolean inMemory() {
			return false;
		}
	}
	private static class StreamAudio extends AAudio {
		private final FileItem _fi;
		private String _format;
		private String _ctype;
		public StreamAudio(String name, FileItem fi, String ctype) throws IOException {
			super(name, DYNAMIC_STREAM);
			_fi = fi;
			_ctype = ctype;
		}
		public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read "+_fi, ex);
			}
		}
		public String getFormat(){
			if (_format == null) {
				_format = ContentTypes.getFormat(getContentType());
			}
			return _format;
		}
		public String getContentType() {
			return _ctype != null ? _ctype : _fi.getContentType();
		}
	}

	/**
	 * The file item factory that monitors the progress of uploading.
	 */
	private static class ItemFactory extends DiskFileItemFactory implements Serializable {
		private final Desktop _desktop;
		private final String _key;
		/** The total length (content length). */
		private long _cbtotal;
		/** # of bytes being received. */
		private long _cbrcv;

		@SuppressWarnings("unchecked")
		/*package*/ ItemFactory(Desktop desktop, HttpServletRequest request, String key) {
	    	setSizeThreshold(1024*128);	// maximum size that will be stored in memory

			_desktop = desktop;
			_key = key;
			long cbtotal = 0;
			String ctlen = request.getHeader("content-length");
			if (ctlen != null)
				try {
					cbtotal = Long.parseLong(ctlen.trim());
					//if (log.debugable()) log.debug("content-length="+cbtotal);
				} catch (Throwable ex) {
					log.warning(ex);
				}
			_cbtotal = cbtotal;

			if (_desktop.getAttribute(Attributes.UPLOAD_PERCENT) == null) {
				_desktop.setAttribute(Attributes.UPLOAD_PERCENT, new HashMap());
				_desktop.setAttribute(Attributes.UPLOAD_SIZE, new HashMap());
			}
			((Map)_desktop.getAttribute(Attributes.UPLOAD_PERCENT)).put(key, new Integer(0));
			((Map)_desktop.getAttribute(Attributes.UPLOAD_SIZE)).put(key, new Long(_cbtotal));
		}

		@SuppressWarnings("unchecked")
		/*package*/ void onProgress(long cbRead) {
			int percent = 0;
			if (_cbtotal > 0) {
				_cbrcv = cbRead;
				percent = (int)(_cbrcv * 100 / _cbtotal);
			}
			((Map)_desktop.getAttribute(Attributes.UPLOAD_PERCENT)).put(_key, new Integer(percent));
		}

		//-- FileItemFactory --//
	    public FileItem createItem(String fieldName, String contentType,
		boolean isFormField, String fileName) {
			return new ZkFileItem(fieldName, contentType, isFormField, fileName,
				getSizeThreshold(), getRepository());
		}

		//-- helper classes --//
		/** FileItem created by {@link ItemFactory}.
		 */
		/*package*/ class ZkFileItem extends DiskFileItem {
			/*package*/ ZkFileItem(String fieldName, String contentType,
			boolean isFormField, String fileName, int sizeThreshold,
			File repository) {
				super(fieldName, contentType, isFormField,
					fileName, sizeThreshold, repository);
			}

			/** Returns the charset by parsing the content type.
			 * If none is defined, UTF-8 is assumed.
			 */
		    public String getCharSet() {
				final String charset = super.getCharSet();
				return charset != null ? charset: "UTF-8";
			}
		}

		/*package*/ class ProgressCallback implements ProgressListener {
		    public void update(long pBytesRead, long pContentLength, int pItems) {
		    	onProgress(pBytesRead);
		    	if (pContentLength >= 0)
		    		_cbtotal = pContentLength;
		    }
		}
	}
}
