/* AuDownloader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 20 15:12:50     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.au.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Library;
import org.zkoss.lang.Classes;
import org.zkoss.util.CacheMap;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.au.http.AuProcessor;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;

import org.zkoss.zkmax.zul.FiledownloadListener;

/**
 * The AU processor to handle the resumable download.
 *
 * <p>There are two library properties that can control the number of allowed
 * resumable downloads.
 * <dl>
 * <dt>org.zkoss.zk.download.resumable.lifetime</dt>
 * <dd>Specifies when the download URL will be expired (unit: second).<br/>
 * Default: 14400 (i.e., 4 hours).</dd>
 * <dt>org.zkoss.zk.download.resumable.maxsize</dt>
 * <dd>Specifies the maximal allowed number of resumable downloads.<br/>
 * Default: 4096.</dd>
 * </dl>
 * @author tomyeh
 * @since 3.5.0
 * @see org.zkoss.zkmax.zul.Filedownload
 */
public class AuDownloader implements AuProcessor {
	private static final Log log = Log.lookup(AuDownloader.class);
	private static FiledownloadListener _listener;
	private static final String URI_PREFIX = "/download";

	/** A map of allowed download.
	 * To avoid security hole, we have to limit access to recognized URL only
	 */
	private static final CacheMap _dls= new CacheMap();

	/** Initializes the downloader.
	 */
	public static void init(WebApp wapp) {
		if (DHtmlUpdateServlet.getAuProcessor(wapp, URI_PREFIX) == null) {
			final String clsnm = Library.getProperty("org.zkoss.zkmax.zul.FiledownloadListener.class");
			if (clsnm != null) {
				try {
					_listener = (FiledownloadListener)Classes.newInstanceByThread(clsnm);
				} catch (Throwable ex) {
					log.warning("Failed to instantiate "+clsnm);
				}
			}

			DHtmlUpdateServlet
				.addAuProcessor(wapp, URI_PREFIX, new AuDownloader());

			_dls.setMaxSize(Library.getIntProperty(
				"org.zkoss.zk.download.resumable.maxsize", 1024 * 4));
			_dls.setLifetime(Library.getIntProperty(
				"org.zkoss.zk.download.resumable.lifetime", 4 * 60 * 60) * 1000);
		}
	}

	/** Use {@link #init} instead. */
	private AuDownloader() {}

	/** Returns the download URI of the specified file.
	 */
	public static
	String getDownloadURI(File file, String contentType, String data) {
		return getDownloadURI('f', file.getAbsolutePath(), contentType, data);
	}
	/** Returns the download URI of the specified file.
	 */
	public static
	String getDownloadURI(URL url, String contentType, String data) {
		return getDownloadURI('u', url.toExternalForm(), contentType, data);
	}
	/** Returns the download URI of the specified file.
	 */
	public static
	String getDownloadURI(String path, String contentType, String data) {
		return getDownloadURI('p', path, contentType, data);
	}
	private static String
	getDownloadURI(final char type, final String path, String contentType, String data) {
		final Desktop desktop = Executions.getCurrent().getDesktop();

		final StringBuffer sb = new StringBuffer(256)
			.append(URI_PREFIX).append('/').append(type);

		int j = path.length();
		int v3 = 0;
		while (--j >= 0) {
			char cc = path.charAt(j);
			if (cc == '/') break;
			if (cc == '\\') {
				v3 = 16;
				break;
			}
		}

		//bit 0-1 for contentType, 2-3 for data: 0=null, 1=empty, 2=other
		int v1 = contentType != null ? contentType.length() > 0 ? 2: 1: 0;
		int v2 = data != null ? data.length() > 0 ? 8: 4: 0;
		sb.append((char)(v1 + v2 + v3 + 'A'));
		if (v1 == 2) {
			encode(sb, contentType);
			if (v2 == 8) sb.append('/');
		}
		if (v2 == 8)
			encode(sb, data);

		sb.append('/');
		if (j >= 0) {
			encode(sb, path.substring(0, j));
			sb.append('/').append(path.substring(j + 1));
		} else {
			sb.append(path);
		}

		synchronized (_dls) {
			_dls.put(path, new Character(type));
		}
		return desktop.getUpdateURI(sb.toString());
	}

	private static char[] _encc = {
		'm', '/', '\\', 'r', ':', 'g', 's', '?', ';', '%'};
	private static void encode(StringBuffer sb, String txt) {
		if (txt != null && txt.length() != 0) {
			final Random rand = new Random();
			final int gpsz = 62 / _encc.length;
			l_out:
			for (int j = 0, len = txt.length(); j < len; ++j) {
				final char cc = txt.charAt(j);
				for (int k = _encc.length; --k >= 0;) {
					if (cc == _encc[k]) {
						sb.append('m');
						int v = gpsz * k + rand.nextInt(gpsz);
						if (v < 26) sb.append((char)('a' + v));
						else if (v < 52) sb.append((char)('A' + v - 26));
						else sb.append((char)('0' + v - 52));
						continue l_out;
					}
				}
				sb.append(cc);
			}
		}
	}
	private static String decode(String txt) {
		final StringBuffer sb = new StringBuffer(256);
		final int gpsz = 62 / _encc.length;
		for (int j = 0, len = txt.length(); j < len; ++j) {
			char cc = txt.charAt(j);
			if (cc == 'm') {
				if (++j < len) {
					cc = txt.charAt(j);
					int v;
					if (cc >= 'a' && cc <= 'z') v = cc - 'a';
					else if (cc >= 'A' && cc <= 'Z') v = cc - 'A' + 26;
					else if (cc >= '0' && cc <= '9') v = cc - '0' + 52;
					else v = -1;
					if (v >= 0) {
						sb.append(_encc[v / gpsz]);
						continue;
					} else {
						sb.append('m');
					}
				}
					
			}
			sb.append(cc);
		}
		return sb.toString();
	}

	//AuProcessor//
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
		String path = null, contentType = null, data = null;
		char type = (char)0;
		int j = pi.indexOf('/', 1);
		if (j >= 0 && j < pi.length() - 3) {
			type = pi.charAt(++j);
			int v = pi.charAt(++j) - 'A';
			int k = pi.indexOf('/', ++j);
			if (k >= 0 && v <= (16+8+2) && v >= 0) {
				switch (v & 3) {
				case 1:
					contentType = "";
					break;
				case 2:
					contentType = decode(pi.substring(j, k));
					k = pi.indexOf('/', j = k + 1);
					break;
				}
				switch (v & 12) {
				case 4:
					data = "";
					break;
				case 8:
					if (k >= 0) {
						data = decode(pi.substring(j, k));
						k = pi.indexOf('/', j = k + 1);
					}
					break;
				}

				if ((v & 15) == 0) //no contentType nor data
					k = pi.indexOf('/', j = k + 1);

				if (k >= 0) {
					path = decode(pi.substring(j, k))
						+ ((v & 16) != 0 ? '\\': '/')
						+ pi.substring(k + 1);
				} else {
					path = pi.substring(j);
				}
			}
		}

		Media media = null;
		Object source = null;
		if (path != null && path.length() > 0) {
			//make sure it is still valid
			Object o;
			synchronized (_dls) {
				_dls.expunge();
				o = _dls.getWithoutExpunge(path);
			}
			if (!(o instanceof Character) || ((Character)o).charValue() != type) {
				response.sendError(response.SC_GONE, Messages.get(MZk.PAGE_NOT_FOUND, path != null ? path: pi));
				return;
			}

			if (type == 'p') {
				source = path;
				URL url = ctx.getResource(path);
				if (url != null)
					media = new AMedia(url, contentType, null);
			} else if (type == 'f') {
				File file = new File(path);
				source = file;
				media = new AMedia(file, contentType, null);
			} else if (type == 'u') {
				URL url = new URL(path);
				source = url;
				media = new AMedia(url, contentType, null);
			}
		}

		if (media == null) {
			response.sendError(response.SC_GONE, Messages.get(MZk.PAGE_NOT_FOUND, path != null ? path: pi));
			return;
		}

		if (_listener != null) {
			media = _listener.onDownload(media, source, data);
			if (media == null) {
				response.sendError(response.SC_FORBIDDEN, "Not allowed "+path);
				return;
			}
		}

		Https.write(request, response, media, true, true);
	}
}
