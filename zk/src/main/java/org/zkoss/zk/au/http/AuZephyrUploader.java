/* AuZephyrUploader.java

	Purpose:

	Description:

	History:
		Mon Dec 06 14:40:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.http;

import static org.zkoss.zk.ui.ext.Uploadable.Error.MISSING_REQUIRED_COMPONENT;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.zkoss.image.AImage;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.sound.AAudio;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;
import org.zkoss.video.AVideo;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.ext.Uploadable;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;

/**
 * The AU extension to upload files with HTML5 feature.
 * It is based on Apache Commons File Upload.
 */
public class AuZephyrUploader implements AuExtension {
	private static final Logger log = LoggerFactory.getLogger(AuZephyrUploader.class);
	public AuZephyrUploader() {
	}

	public void init(DHtmlUpdateServlet servlet) {
	}

	public void destroy() {
	}

	/** Processes a file uploaded from the client.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response, String pathInfo)
			throws ServletException, IOException {
		final Session sess = Sessions.getCurrent(false);
		if (sess == null) {
			response.setIntHeader("ZK-Error", HttpServletResponse.SC_GONE);
			return;
		}

		final Map<String, String> attrs = new HashMap<String, String>();
		String alert = null, uuid = null, dtid = null, nextURI = null;
		if (Strings.isEmpty(uuid = fetchParameter(request, "uuid", attrs)))
			alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "uuid is required!");
		if (Strings.isEmpty(dtid = fetchParameter(request, "dtid", attrs)))
			alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "dtid is required!");
		if (Strings.isEmpty(fetchParameter(request, "cmd", attrs)))
			alert = generateAlertMessage(MISSING_REQUIRED_COMPONENT, "cmd is required!");
		fetchParameter(request, "sid", attrs);
		Desktop desktop = null;
		try {
			if (alert == null) {
				desktop = ((WebAppCtrl) sess.getWebApp()).getDesktopCache(sess).getDesktop(dtid);
				final Map<String, Object> params = parseRequest(request, desktop);
				final boolean alwaysNative = Boolean.TRUE.equals(params.get("native"));
				Map<String, Media> meds = new HashMap<>();
				for (Object o : (ArrayList) params.get("file")) {
					if (o instanceof MultipartFile) {
						MultipartFile fi = (MultipartFile) o;
						meds.put(getBaseName(fi), processItem(desktop, fi, alwaysNative, attrs));
					}
				}
				request.setAttribute("media", meds);
				final AuDecoder audec = getAuDecoder(sess.getWebApp());
				List<AuRequest> aureqs = audec.decode(request, desktop);
				Component comp = desktop.getComponentByUuidIfAny(request.getParameter("uuid"));
				((ComponentCtrl) comp).service(StubEvent.getStubEvent(aureqs.get(0)), Scopes.beforeInterpret(comp));
			}
		} catch (Throwable ex) {
			if (uuid == null) {
				uuid = request.getParameter("uuid");
				if (uuid != null)
					attrs.put("uuid", uuid);
			}

			if (ex instanceof ComponentNotFoundException) {
				log.error(generateAlertMessage(MISSING_REQUIRED_COMPONENT, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, uuid)));
			} else {
				log.error("Failed to upload", ex);
			}
		}
		if (alert != null) {
			if (desktop == null) {
				response.setIntHeader("ZK-Error", HttpServletResponse.SC_GONE);
				return;
			}
			response.getWriter().write("error:" + alert);
		} else {
			response.setContentType("text/plain;charset=UTF-8");
		}
		if (log.isTraceEnabled())
			log.trace(Objects.toString(attrs));
	}

	private String fetchParameter(HttpServletRequest request, String key, Map<String, String> attrs) {
		String value = request.getParameter(key);
		if (!Strings.isEmpty(value))
			attrs.put(key, value);
		return value;
	}

	/** Process the specified fileitem.
	 */
	private Media processItem(Desktop desktop, MultipartFile mf, boolean alwaysNative, Map<String, String> attrs) throws IOException {
		String name = getBaseName(mf);
		String ctype = mf.getContentType(),
				ctypelc = ctype != null ? ctype.toLowerCase(java.util.Locale.ENGLISH) : null;
		if (name != null && "application/octet-stream".equals(ctypelc)) { //Bug 1896291: IE limit
			final int j = name.lastIndexOf('.');
			if (j >= 0) {
				String s = ContentTypes.getContentType(name.substring(j + 1));
				if (s != null)
					ctypelc = ctype = s;
			}
		}
		Media media = null;
		if (!alwaysNative && ctypelc != null) {
			if (ctypelc.startsWith("image/")) {
				try {
					media = new AImage(name, mf.getBytes());
				} catch (Throwable ex) {
					if (log.isDebugEnabled())
						log.debug("Unknown file format: " + ctype);
				}
			} else if (ctypelc.startsWith("audio/")) {
				try {
					media = new AAudio(name, mf.getBytes());
				} catch (Throwable ex) {
					if (log.isDebugEnabled())
						log.debug("Unknown file format: " + ctype);
				}
			} else if (ctypelc.startsWith("video/")) {
				try {
					media = new AVideo(name, mf.getBytes());
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
						charset = chfd.getCharset(ctype, new ByteArrayInputStream(mf.getBytes()));
					if (charset == null)
						charset = conf.getUploadCharset();
				}
				media = new AMedia(name, null, ctype, charset);
			} else {
				media = new AMedia(name, null, ctype, mf.getBytes());
			}
		}
		String uploadInfoKey = attrs.getOrDefault("uuid", "") + "." + attrs.getOrDefault("sid", "");
		List<Media> meds = (List<Media>) desktop.getAttribute(uploadInfoKey);
		if (meds == null) {
			meds = new LinkedList<>();
			desktop.setAttribute(uploadInfoKey, meds);
		}
		if (media != null) meds.add(media);
		return media;
	}

	private String generateAlertMessage(Uploadable.Error type, String message) {
		return type.toString() + ":" + message;
	}

	/** Parses the multipart request into a map of
	 * (String nm, FileItem/String/List(FileItem/String)).
	 */
	private static Map<String, Object> parseRequest(HttpServletRequest request, Desktop desktop) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final Configuration conf = desktop.getWebApp().getConfiguration();
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

		Component comp = desktop.getComponentByUuid(request.getParameter("uuid"));
		try {
			Integer compMaxsz = (Integer) comp.getAttribute(Attributes.UPLOAD_MAX_SIZE);
		} catch (NumberFormatException e) {
			throw new UiException("The upload max size must be a number");
		}
		if (Boolean.TRUE.equals(comp.getAttribute(Attributes.UPLOAD_NATIVE))) {
			params.put("native", true);
		}

		MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
		List<MultipartFile> files = multipartRequest.getFiles("file");
		params.put("file", files);
		return params;
	}

	private static final AuDecoder getAuDecoder(WebApp wapp) {
		AuDecoder audec = wapp != null ? ((WebAppCtrl) wapp).getAuDecoder() : null;
		return audec != null ? audec : _audec;
	}

	private static final AuDecoder _audec = new AuDecoder() {
		public String getDesktopId(Object request) {
			return ((HttpServletRequest) request).getParameter("dtid");
		}

		public String getFirstCommand(Object request) {
			return ((HttpServletRequest) request).getParameter("cmd_0");
		}

		@SuppressWarnings("unchecked")
		public List<AuRequest> decode(Object request, Desktop desktop) {
			final List<AuRequest> aureqs = new LinkedList<>();
			final HttpServletRequest hreq = (HttpServletRequest) request;
			final String cmd = hreq.getParameter("cmd");
			final String uuid = hreq.getParameter("uuid");

			Map<String, Object> decdata= new HashMap();
			decdata.put("media", hreq.getAttribute("media"));
			aureqs.add(uuid == null || uuid.length() == 0 ? new AuRequest(desktop, cmd, decdata)
					: new AuRequest(desktop, uuid, cmd, decdata));
			return aureqs;
		}

		public boolean isIgnorable(Object request, WebApp wapp) {
			final HttpServletRequest hreq = (HttpServletRequest) request;
			for (int j = 0;; ++j) {
				if (hreq.getParameter("cmd_" + j) == null)
					break;

				final String opt = hreq.getParameter("opt_" + j);
				if (opt == null || opt.indexOf("i") < 0)
					return false; //not ignorable
			}
			return true;
		}
	};

	/** Returns the base name for FileItem (i.e., removing path).
	 */
	private static String getBaseName(MultipartFile mf) {
		String name = mf.getOriginalFilename();
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
}
