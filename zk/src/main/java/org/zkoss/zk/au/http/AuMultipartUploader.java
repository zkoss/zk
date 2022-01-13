/* AuMultipartUploader.java

	Purpose:
		
	Description:
		
	History:
		3:13 PM 2022/1/10, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.http;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.zkoss.zk.ui.ext.Uploadable.Error.SERVER_EXCEPTION;
import static org.zkoss.zk.ui.ext.Uploadable.Error.SIZE_LIMIT_EXCEEDED;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.image.AImage;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.sound.AAudio;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;
import org.zkoss.video.AVideo;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.ext.Uploadable;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;

/**
 * An AU handler to process multipart content.
 * @author jumperchen
 * @since 10.0.0
 */
public class AuMultipartUploader {
	private static final Logger log = LoggerFactory.getLogger(AuMultipartUploader.class);
	public static AuDecoder parseRequest(HttpServletRequest request, AuDecoder decoder) {
		Map<String, Object> params = getFileuploadMetaPerWebApp(
				WebApps.getCurrent());
		ServletFileUpload upload = new ServletFileUpload(
				new DiskFileItemFactory((Integer) params.get("sizeThreadHold"),
						(File) params.get("repository")));
		try {
			List<FileItem> fileItems = upload.parseRequest(request);
			Map<String, Object> dataMap = new HashMap<>(fileItems.size());
			for (FileItem item: fileItems) {
				if (item.isFormField()) {
					dataMap.put(item.getFieldName(), item.getString());
				} else {
					dataMap.put(item.getFieldName(), item);
				}
			}
			return new AuMultipartDecoder(dataMap, decoder);
		} catch (FileUploadException e) {
			throw UiException.Aide.wrap(e);
		}
	}
	public static Map<String, List<String>> splitQuery(String query) {
		if (Strings.isBlank(query)) {
			return Collections.emptyMap();
		}
		return Arrays.stream(query.split("&"))
				.map(AuMultipartUploader::splitQueryParameter)
				.collect(Collectors.groupingBy(
						AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
	}

	public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
		final int idx = it.indexOf("=");
		final String key = idx > 0 ? it.substring(0, idx) : it;
		final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
		try {
			return new AbstractMap.SimpleImmutableEntry<>(
					URLDecoder.decode(key, "UTF-8"),
					URLDecoder.decode(value, "UTF-8")
			);
		} catch (UnsupportedEncodingException e) {
			throw UiException.Aide.wrap(e);
		}
	}
	private static Object reconstructPacket(Object data, Map<String, Object> reqData, Desktop desktop,
			Map<String, Object> params) throws IOException {
		if (data instanceof List) {
			int i = 0;
			List listData = (List) data;
			for (Object v :new ArrayList(listData)) {
				listData.set(i++, reconstructPacket(v, reqData, desktop, params));
			}
			return listData;
		} else if (data instanceof Map) {
			Map<String, Object> mapData = (Map) data;
			if (mapData.containsKey("_placeholder")) {
				int num = (int) mapData.get("num");
				FileItem fileItem = (FileItem) reqData.get("files_" + num);

				// count the file size
				params.put("fileSize", Long.valueOf((Long) params.get("fileSize") + fileItem.getSize()));
				return processItem(desktop,
						fileItem,
						Boolean.parseBoolean(
								String.valueOf(params.get("native"))),
						(org.zkoss.zk.ui.sys.DiskFileItemFactory) params.get(
								"diskFileItemFactory"));
			}
			for (Map.Entry<String, Object> me : mapData.entrySet()) {
				mapData.put(me.getKey(), reconstructPacket(me.getValue(), reqData, desktop,
						params));
			}
			return mapData;
		}
		return data;
	}


	private static  Map<String, Object> getFileuploadMetaPerWebApp(WebApp webApp) {
		final Map<String, Object> params = new HashMap<String, Object>();

		final Configuration conf = webApp.getConfiguration();
		int thrs = conf.getFileSizeThreshold();
		int sizeThreadHold = 1024 * 128; // maximum size that will be stored in memory

		if (thrs > 0)
			sizeThreadHold = 1024 * thrs;

		params.put("sizeThreadHold", sizeThreadHold);

		File repository = null;

		if (conf.getFileRepository() != null) {
			repository = new File(conf.getFileRepository());
			if (!repository.isDirectory()) {
				log.warn("The file repository is not a directory! [" + repository + "]");
			}
			params.put("repository", repository);
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
		return params;
	}

	private static Map<String, Object> getFileuploadMetaPerComp(Map<String, Object> params, Desktop desktop, String uuid) {

		// zephyr may not have comp at server.
		Component comp = desktop.getComponentByUuidIfAny(uuid);
		if (comp != null) {
			Integer maxsz = null;
			try {
				Integer compMaxsz = (Integer) comp.getAttribute(Attributes.UPLOAD_MAX_SIZE);
				maxsz = compMaxsz != null ? compMaxsz :
							desktop.getWebApp().getConfiguration()
									.getMaxUploadSize();
				params.put("maxSize", maxsz);
			} catch (NumberFormatException e) {
				throw new UiException("The upload max size must be a number");
			}
			if (Boolean.TRUE.equals(comp.getAttribute(Attributes.UPLOAD_NATIVE))) {
				params.put("native", true);
			}
		}
		return params;
	}

	private static Map<String, Object> getFileuploadMeta(Desktop desktop, String uuid) {
		WebApp webApp = desktop.getWebApp();
		Map<String, Object> params = getFileuploadMetaPerWebApp(
				webApp);
		return getFileuploadMetaPerComp(params, desktop, uuid);
	}

	private static class AuMultipartDecoder implements AuDecoder {
		private AuDecoder _origin;
		private String _desktop;
		private String _firstCommand;
		private Map<String, Object> _reqData;
		private Map<String, List<String>> _queryData;

		public AuMultipartDecoder(Map<String, Object> reqData, AuDecoder origin) {
			_origin = origin;
			_queryData = splitQuery((String) reqData.get("data"));
			_reqData = reqData;
		}

		public String getDesktopId(Object request) {
			return _origin.getDesktopId(new MultipartRequestWrapper((HttpServletRequest) request, _queryData));
		}

		public String getFirstCommand(Object request) {
			return _origin.getFirstCommand(new MultipartRequestWrapper((HttpServletRequest) request, _queryData));
		}

		public List<AuRequest> decode(Object request, Desktop desktop) {
			List<AuRequest> auRequests = _origin.decode(
					new MultipartRequestWrapper((HttpServletRequest) request,
							_queryData), desktop);

			auRequests.forEach(auRequest -> {
				try {
					Map<String, Object> params = getFileuploadMeta(desktop,
							auRequest.getUuid());
					params.put("fileSize", 0L);
					Integer maxSize = (Integer) params.get("maxSize");
					Long maxSizeLong = -1L;
					if (maxSize != null) {
						if (maxSize >= 0) {
							maxSizeLong = 1024L * maxSize;
						}
					}
					reconstructPacket(auRequest.getData(), _reqData, desktop, params);
					Long fileSize = (Long) params.get("fileSize");
					if (maxSizeLong >= 0 && fileSize > maxSizeLong) {
						String errorMessage = uploadErrorMessage(new FileUploadBase.SizeLimitExceededException(null, fileSize, maxSizeLong));
						throw new FileUploadBase.SizeLimitExceededException(errorMessage, fileSize, maxSizeLong);
					}
				} catch (Exception e) {
					throw UiException.Aide.wrap(e);
				}
			});
			return auRequests;
		}

		public boolean isIgnorable(Object request, WebApp wapp) {
			return _origin.isIgnorable((new MultipartRequestWrapper((HttpServletRequest) request, _queryData)), wapp);
		}
	}
	private static String uploadErrorMessage(Throwable ex) {
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
						(Long) (size / divisor1) + units[1],
						(Long) (limit / divisor1) + units[1],
						(Long) (size / divisor2) + units[2],
						(Long) (limit / divisor2) + units[2] };

				return generateAlertMessage(SIZE_LIMIT_EXCEEDED, Messages.get(msgField.getInt(null), args));
			} catch (Throwable e) {
				log.error("Failed to parse upload error message..", e);
			}
		}
		return generateAlertMessage(SERVER_EXCEPTION, Exceptions.getMessage(ex));
	}

	private static String generateAlertMessage(Uploadable.Error type, String message) {
		return type.toString() + ":" + message;
	}

	private static final void processItems(Desktop desktop, Map<String, Object> params, Map<String, String> attrs,  List<Media> meds)
			throws IOException {
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
				throw new UiException("Unable to read " + _fi, ex);
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

		public ReaderMedia(String name, String format, String ctype, FileItem fi, String charset) {
			super(name, format, ctype, DYNAMIC_READER);
			_fi = fi;
			_charset = charset;
		}

		public java.io.Reader getReaderData() {
			try {
				return new java.io.InputStreamReader(_fi.getInputStream(), _charset);
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
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
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		public String getFormat() {
			if (_format == null) {
				_format = ContentTypes.getFormat(getContentType());
			}
			return _format;
		}

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

		public java.io.InputStream getStreamData() {
			try {
				return _fi.getInputStream();
			} catch (IOException ex) {
				throw new UiException("Unable to read " + _fi, ex);
			}
		}

		public String getFormat() {
			if (_format == null) {
				_format = ContentTypes.getFormat(getContentType());
			}
			return _format;
		}

		public String getContentType() {
			return _ctype != null ? _ctype : _fi.getContentType();
		}
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
			int k = org.zkoss.lang.Strings.skipWhitespacesBackward(ctype, j - 1);
			if (k < 0 || ctype.charAt(k) == ';') {
				k = org.zkoss.lang.Strings.skipWhitespaces(ctype, j + 7);
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
	private static class MultipartRequestWrapper extends HttpServletRequestWrapper {
		private Map<String, List<String>> _data;
		public MultipartRequestWrapper(HttpServletRequest request, Map<String, List<String>> data) {
			super(request);
			_data = data;
		}

		public String getParameter(String name) {
			if (_data.containsKey(name)) {
				return _data.get(name).get(0);
			}
			return super.getParameter(name);
		}

		public Map getParameterMap() {
			HashMap<String, List<String>> hashMap = new HashMap<>(
					_data);
			hashMap.putAll(super.getParameterMap());
			return hashMap;
		}

		public Enumeration getParameterNames() {
			Set<String> keys = new LinkedHashSet<>(_data.keySet());
			Enumeration parameterNames = super.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				keys.add((String) parameterNames.nextElement());
			}
			return Collections.enumeration(keys);
		}

		public String[] getParameterValues(String name) {
			if (_data.containsKey(name)) {
				return _data.get(name).toArray(new String[0]);
			}
			return super.getParameterValues(name);
		}
	}
}
