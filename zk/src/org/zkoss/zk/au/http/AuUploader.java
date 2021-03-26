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

import static org.zkoss.lang.Generics.cast;
import static org.zkoss.zk.ui.ext.Uploadable.Error.ILLEGAL_UPLOAD;
import static org.zkoss.zk.ui.ext.Uploadable.Error.MISSING_REQUIRED_COMPONENT;
import static org.zkoss.zk.ui.ext.Uploadable.Error.SERVER_EXCEPTION;
import static org.zkoss.zk.ui.ext.Uploadable.Error.SIZE_LIMIT_EXCEEDED;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.image.AImage;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.sound.AAudio;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;
import org.zkoss.video.AVideo;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.xml.XMLs;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.Uploadable;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The AU extension to upload files.
 * It is based on Apache Commons File Upload.
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public class AuUploader implements AuExtension {
	private static final Logger log = LoggerFactory.getLogger(AuUploader.class);

	private ServletContext _ctx;

	public AuUploader() {
	}

	@Override
    public void init(DHtmlUpdateServlet servlet) {
		_ctx = servlet.getServletContext();
	}

	@Override
    public void destroy() {
	}

	/** Processes a file uploaded from the client.
	 */
	@Override
    public void service(HttpServletRequest request, HttpServletResponse response, String pathInfo)
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
					// refix ZK-2056: should escape both XML and Javascript
					uuid = escapeParam(request.getParameter("wid"));
					sid = escapeParam(request.getParameter("sid"));
					desktop = ((WebAppCtrl) sess.getWebApp()).getDesktopCache(sess)
							.getDesktop(XMLs.encodeText(request.getParameter("dtid")));

					Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
					Map<String, Object> size = cast((Map) desktop.getAttribute(Attributes.UPLOAD_SIZE));

					// ZK-2329
					if (percent == null || size == null) {
						response.getWriter().write("ignore");
						return;
					}

					final String key = uuid + '_' + sid;
					Object sinfo = size.get(key);
					if (sinfo instanceof String) {
						response.getWriter().write("error:" + sinfo);
						size.remove(key);
						percent.remove(key);
						return;
					}
					final Integer p = percent.get(key);
					final Long cb = (Long) sinfo;
					response.getWriter()
							.write((p != null ? p.intValue() : -1) + "," + (cb != null ? cb.longValue() : -1));
					return;
				} else
					alert = generateAlertMessage(ILLEGAL_UPLOAD, "enctype must be multipart/form-data");
			} else {
				// refix ZK-2056: should escape both XML and Javascript
				uuid = escapeParam(request.getParameter("uuid"));
				sid = escapeParam(request.getParameter("sid"));

				if (uuid == null || uuid.length() == 0) {
					alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "uuid is required!");
				} else {
					attrs.put("uuid", uuid);
					attrs.put("sid", sid);

					// refix ZK-2056: should escape both XML and Javascript
					final String dtid = escapeParam(request.getParameter("dtid"));
					if (dtid == null || dtid.length() == 0) {
						alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "dtid is required!");
					} else {
						desktop = ((WebAppCtrl) sess.getWebApp()).getDesktopCache(sess).getDesktop(dtid);
						final Map<String, Object> params = parseRequest(request, desktop, uuid + '_' + sid);
						nextURI = (String) params.get("nextURI");

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
				alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid));
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
			//B65-ZK-1724: display more meaningful errormessage
			alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "Upload Aborted : (contentId is required)");

		if (alert != null) {
			if (desktop == null) {
				response.setIntHeader("ZK-Error", HttpServletResponse.SC_GONE);
				return;
			}
			Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
			Map<String, Object> size = cast((Map) desktop.getAttribute(Attributes.UPLOAD_SIZE));
			final String key = uuid + '_' + sid;
			if (percent != null) {
				percent.remove(key);
				size.put(key, alert);
			}
		}
		if (log.isTraceEnabled())
			log.trace(Objects.toString(attrs));

		if (nextURI == null || nextURI.length() == 0)
			nextURI = "~./zul/html/fileupload-done.html.dsp";
		Servlets.forward(_ctx, request, response, nextURI, attrs, Servlets.PASS_THRU_ATTR);
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
		log.error("Failed to upload", ex);
		if (ex instanceof FileUploadBase.SizeLimitExceededException) {
			try {
				FileUploadBase.SizeLimitExceededException fex = (FileUploadBase.SizeLimitExceededException) ex;
				long size = fex.getActualSize();
				long limit = fex.getPermittedSize();
				final Class<?> msgClass = Classes.forNameByThread("org.zkoss.zul.mesg.MZul");
				Field msgField = msgClass.getField("UPLOAD_ERROR_EXCEED_MAXSIZE");
				int divisor1 = 1024;
				int divisor2 = 1024 * 1024;
				String[] units = new String[] { " Bytes", " KB", " MB" };
				int i1 = (int) (Math.log(size) / Math.log(1024));
				int i2 = (int) (Math.log(limit) / Math.log(1024));
				String sizeAuto = Math.round(size / Math.pow(1024, i1)) + units[i1];
				String limitAuto = Math.round(limit / Math.pow(1024, i2)) + units[i2];

				Object[] args = new Object[] { sizeAuto, limitAuto, size, limit,
						String.valueOf((Long) (size / divisor1)) + units[1],
						String.valueOf((Long) (limit / divisor1)) + units[1],
						String.valueOf((Long) (size / divisor2)) + units[2],
						String.valueOf((Long) (limit / divisor2)) + units[2] };

				return generateAlertMessage(SIZE_LIMIT_EXCEEDED, Messages.get(msgField.getInt(null), args));
			} catch (Throwable e) {
				log.error("Failed to parse upload error message..", e);
			}
		}
		return generateAlertMessage(SERVER_EXCEPTION, Exceptions.getMessage(ex));
	}

	private String generateAlertMessage(Uploadable.Error type, String message) {
		return type.toString() + ":" + message;
	}

	/** Process fileitems named file0, file1 and so on.
	 */
	private static final void processItems(Desktop desktop, Map<String, Object> params, Map<String, String> attrs)
			throws IOException {
		final List<Media> meds = new LinkedList<Media>();
		final boolean alwaysNative = Boolean.TRUE.equals(params.get("native"));
		final Object fis = params.get("file");
		if (fis instanceof FileItem) {
			meds.add(processItem(desktop, (FileItem) fis, alwaysNative,
					(org.zkoss.zk.ui.sys.DiskFileItemFactory) params.get("diskFileItemFactory")));
		} else if (fis != null) {
			for (Iterator it = ((List) fis).iterator(); it.hasNext();) {
				meds.add(processItem(desktop, (FileItem) it.next(), alwaysNative,
						(org.zkoss.zk.ui.sys.DiskFileItemFactory) params.get("diskFileItemFactory")));
			}
		}

		final String contentId = Strings
				.encode(new StringBuffer(12).append("z__ul_"), ((DesktopCtrl) desktop).getNextKey()).toString();
		attrs.put("contentId", contentId);
		desktop.setAttribute(contentId, meds);
	}

	/** Process the specified fileitem.
	 */
	private static final Media processItem(Desktop desktop, FileItem fi, boolean alwaysNative,
			org.zkoss.zk.ui.sys.DiskFileItemFactory factory) throws IOException {
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
				ctypelc = ctype != null ? ctype.toLowerCase(java.util.Locale.ENGLISH) : null;
		if (name != null && "application/octet-stream".equals(ctypelc)) { //Bug 1896291: IE limit
			final int j = name.lastIndexOf('.');
			if (j >= 0) {
				String s = ContentTypes.getContentType(name.substring(j + 1));
				if (s != null)
					ctypelc = ctype = s;
			}
		}

		// ZK 3132, a way to customize it
		if (factory != null) {
			return factory.createMedia(fi, ctype, name, alwaysNative);
		}

		if (!alwaysNative && ctypelc != null) {
			if (ctypelc.startsWith("image/")) {
				try {
					return fi.isInMemory() ? new AImage(name, fi.get()) : new AImage(name, fi.getInputStream());
					//note: AImage converts stream to binary array
				} catch (Throwable ex) {
					if (log.isDebugEnabled())
						log.debug("Unknown file format: " + ctype);
				}
			} else if (ctypelc.startsWith("audio/")) {
				try {
					return fi.isInMemory() ? new AAudio(name, fi.get()) : new StreamAudio(name, fi, ctypelc);
				} catch (Throwable ex) {
					if (log.isDebugEnabled())
						log.debug("Unknown file format: " + ctype);
				}
			} else if (ctypelc.startsWith("video/")) {
				try {
					return fi.isInMemory() ? new AVideo(name, fi.get()) : new StreamVideo(name, fi, ctypelc);
				} catch (Throwable ex) {
					if (log.isDebugEnabled())
						log.debug("Unknown file format: " + ctype);
				}
			} else if (ctypelc.startsWith("text/")) {
				String charset = getCharset(ctype);
				if (charset == null) {
					final Configuration conf = desktop.getWebApp().getConfiguration();
					final CharsetFinder chfd = conf.getUploadCharsetFinder();
					if (chfd != null)
						charset = chfd.getCharset(ctype,
								fi.isInMemory() ? new ByteArrayInputStream(fi.get()) : fi.getInputStream());
					if (charset == null)
						charset = conf.getUploadCharset();
				}
				return fi.isInMemory() ? new AMedia(name, null, ctype, fi.getString(charset))
						: new ReaderMedia(name, null, ctype, fi, charset);
			}
		}

		return fi.isInMemory() ? new AMedia(name, null, ctype, fi.get()) : new StreamMedia(name, null, ctype, fi);
	}

	private static String getCharset(String ctype) {
		final String ctypelc = ctype.toLowerCase(java.util.Locale.ENGLISH);
		for (int j = 0; (j = ctypelc.indexOf("charset", j)) >= 0; j += 7) {
			int k = Strings.skipWhitespacesBackward(ctype, j - 1);
			if (k < 0 || ctype.charAt(k) == ';') {
				k = Strings.skipWhitespaces(ctype, j + 7);
				if (k <= ctype.length() && ctype.charAt(k) == '=') {
					j = ctype.indexOf(';', ++k);
					String charset = (j >= 0 ? ctype.substring(k, j) : ctype.substring(k)).trim();
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
	private static Map<String, Object> parseRequest(HttpServletRequest request, Desktop desktop, String key)
			throws FileUploadException {
		final Map<String, Object> params = new HashMap<String, Object>();
		final Configuration conf = desktop.getWebApp().getConfiguration();
		int thrs = conf.getFileSizeThreshold();
		int sizeThreadHold = 1024 * 128; // maximum size that will be stored in memory

		if (thrs > 0)
			sizeThreadHold = 1024 * thrs;

		File repository = null;

		if (conf.getFileRepository() != null) {
			repository = new File(conf.getFileRepository());
			if (!repository.isDirectory()) {
				log.warn("The file repository is not a directory! [" + repository + "]");
			}
		}

		org.zkoss.zk.ui.sys.DiskFileItemFactory dfiFactory = null;
		if (conf.getFileItemFactoryClass() != null) {
			Class<?> cls = conf.getFileItemFactoryClass();
			try {
				dfiFactory = (org.zkoss.zk.ui.sys.DiskFileItemFactory) cls.newInstance();
				params.put("diskFileItemFactory", dfiFactory);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		}

		final ItemFactory fty = new ItemFactory(desktop, request, key, sizeThreadHold, repository, dfiFactory);
		final ServletFileUpload sfu = new ServletFileUpload(fty);

		sfu.setProgressListener(fty.new ProgressCallback());

		Component comp = desktop.getComponentByUuid(request.getParameter("uuid"));
		Integer maxsz = null;
		try {
			Integer compMaxsz = (Integer) comp.getAttribute(Attributes.UPLOAD_MAX_SIZE);
			maxsz = compMaxsz != null ? compMaxsz : conf.getMaxUploadSize();
		} catch (NumberFormatException e) {
			throw new UiException("The upload max size must be a number");
		}
		if (Boolean.TRUE.equals(comp.getAttribute(Attributes.UPLOAD_NATIVE))) {
			params.put("native", true);
		}

		sfu.setSizeMax(maxsz != null ? (maxsz >= 0 ? 1024L * maxsz : -1) : -1);

		for (Iterator it = sfu.parseRequest(request).iterator(); it.hasNext();) {
			final FileItem fi = (FileItem) it.next();
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
					params.put(nm, vals = cast((List) old));
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

		final String[] seps = { "/", "\\", "%5c", "%5C", "%2f", "%2F" };
		for (int j = seps.length; --j >= 0;) {
			final int k = name.lastIndexOf(seps[j]);
			if (k >= 0)
				name = name.substring(k + seps[j].length());
		}
		return name;
	}

	/** 
	 * Internal Use Only.
	 */
	private static String escapeParam(String param) {
		return Strings.escape(XMLs.encodeText(param), Strings.ESCAPE_JAVASCRIPT);
	}

	/** Returns whether the request contains multipart content.
	 */
	public static final boolean isMultipartContent(HttpServletRequest request) {
		return "post".equals(request.getMethod().toLowerCase(java.util.Locale.ENGLISH))
				&& FileUploadBase.isMultipartContent(new ServletRequestContext(request));
	}

	private static class StreamMedia extends AMedia {
		private final FileItem _fi;

		public StreamMedia(String name, String format, String ctype, FileItem fi) {
			super(name, format, ctype, DYNAMIC_STREAM);
			_fi = fi;
		}

		@Override
        public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		@Override
        public boolean isBinary() {
			return true;
		}

		@Override
        public boolean inMemory() {
			return false;
		}
	}

	private static class ReaderMedia extends AMedia {
		private final FileItem _fi;
		private final String _charset;

		public ReaderMedia(String name, String format, String ctype, FileItem fi, String charset) {
			super(name, format, ctype, DYNAMIC_READER);
			_fi = fi;
			_charset = charset;
		}

		@Override
        public java.io.Reader getReaderData() {
			try {
				return new java.io.InputStreamReader(_fi.getInputStream(), _charset);
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		@Override
        public boolean isBinary() {
			return false;
		}

		@Override
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

		@Override
        public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		@Override
        public String getFormat() {
			if (_format == null) {
				_format = ContentTypes.getFormat(getContentType());
			}
			return _format;
		}

		@Override
        public String getContentType() {
			return _ctype != null ? _ctype : _fi.getContentType();
		}
	}

	private static class StreamVideo extends AVideo {
		private final FileItem _fi;
		private String _format;
		private String _ctype;

		public StreamVideo(String name, FileItem fi, String ctype) throws IOException {
			super(name, DYNAMIC_STREAM);
			_fi = fi;
			_ctype = ctype;
		}

		@Override
        public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		@Override
        public String getFormat() {
			if (_format == null) {
				_format = ContentTypes.getFormat(getContentType());
			}
			return _format;
		}

		@Override
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
		private org.zkoss.zk.ui.sys.DiskFileItemFactory _factory;

		@SuppressWarnings("unchecked")
		/*package*/ ItemFactory(Desktop desktop, HttpServletRequest request, String key, int sizeThreshold,
				File repository, org.zkoss.zk.ui.sys.DiskFileItemFactory factory) {
			super(sizeThreshold, repository);

			_factory = factory;
			_desktop = desktop;
			_key = key;
			long cbtotal = 0;
			String ctlen = request.getHeader("content-length");
			if (ctlen != null)
				try {
					cbtotal = Long.parseLong(ctlen.trim());
					//if (log.isDebugEnabled()) log.debug("content-length="+cbtotal);
				} catch (Throwable ex) {
					log.warn("", ex);
				}
			_cbtotal = cbtotal;

			if (_desktop.getAttribute(Attributes.UPLOAD_PERCENT) == null) {
				_desktop.setAttribute(Attributes.UPLOAD_PERCENT, new HashMap());
				_desktop.setAttribute(Attributes.UPLOAD_SIZE, new HashMap());
			}
			((Map) _desktop.getAttribute(Attributes.UPLOAD_PERCENT)).put(key, new Integer(0));
			((Map) _desktop.getAttribute(Attributes.UPLOAD_SIZE)).put(key, new Long(_cbtotal));
		}

		@SuppressWarnings("unchecked")
		/*package*/ void onProgress(long cbRead) {
			int percent = 0;
			if (_cbtotal > 0) {
				_cbrcv = cbRead;
				percent = (int) (_cbrcv * 100 / _cbtotal);
			}
			((Map) _desktop.getAttribute(Attributes.UPLOAD_PERCENT)).put(_key, new Integer(percent));
		}

		//-- FileItemFactory --//
		@Override
        public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
			if (_factory != null)
				return _factory.createItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(),
						getRepository());
			return new ZkFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository());
		}

		//-- helper classes --//
		/** FileItem created by {@link ItemFactory}.
		 */
		/*package*/ class ZkFileItem extends DiskFileItem {
			/*package*/ ZkFileItem(String fieldName, String contentType, boolean isFormField, String fileName,
					int sizeThreshold, File repository) {
				super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
			}

			/** Returns the charset by parsing the content type.
			 * If none is defined, UTF-8 is assumed.
			 */
			@Override
            public String getCharSet() {
				final String charset = super.getCharSet();
				return charset != null ? charset : "UTF-8";
			}
		}

		/*package*/ class ProgressCallback implements ProgressListener {
			@Override
            public void update(long pBytesRead, long pContentLength, int pItems) {
				onProgress(pBytesRead);
				if (pContentLength >= 0)
					_cbtotal = pContentLength;
			}
		}
	}
}
