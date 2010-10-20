/* Filedownload.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 16 09:29:44     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.au.out.AuDownload;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * File download utilities.
 *
 * @author tomyeh
 * @see Fileupload
 */
public class Filedownload {
	/** Open a download dialog to save the specified content at the client.
	 */
	public static void save(Media media) {
		save(media, null);
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.
	 *
	 * @param media the media to download
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, {@link Media#getName} is assumed.
	 */
	public static void save(Media media, String flnm) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		((WebAppCtrl)desktop.getWebApp())
			.getUiEngine().addResponse(
				new AuDownload(new DownloadURL(media, flnm))); //Bug 2114380
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.
	 *
	 * @param content the content
	 * @param contentType the content type (aka., mine type),
	 * e.g., application/pdf
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, no suggested name is provided.
	 */
	public static void save(byte[] content, String contentType, String flnm) {
		save(new AMedia(flnm, null, contentType, content), flnm);
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.
	 *
	 * @param content the content
	 * @param contentType the content type (aka., mine type),
	 * e.g., application/pdf
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, no suggested name is provided.
	 */
	public static void save(String content, String contentType, String flnm) {
		save(new AMedia(flnm, null, contentType, content), flnm);
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.<br/>
	 * Note: You don't need to close the content (a InputStream), it will be closed automatically after download. 
	 * @param content the content
	 * @param contentType the content type (aka., mine type),
	 * e.g., application/pdf
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, no suggested name is provided.
	 */
	public static void save(InputStream content, String contentType, String flnm) {
		save(new AMedia(flnm, null, contentType, content), flnm);
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.<br/>
	 * Note: You don't need to close the content (a Reader), it will be closed automatically after download.
	 * @param content the content
	 * @param contentType the content type (aka., mine type),
	 * e.g., application/pdf
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, no suggested name is provided.
	 */
	public static void save(Reader content, String contentType, String flnm) {
		save(new AMedia(flnm, null, contentType, content), flnm);
	}
	/** Open a download dialog to save the specified file at the client.
	 *
	 * @param file the file to download to the client
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the file name's
	 * extension is used to determine the content type.
	 * @exception FileNotFoundException if the file is not found.
	 * @since 3.0.8
	 */
	public static void save(File file, String contentType)
	throws FileNotFoundException {
		save(new AMedia(file, contentType, null), file.getName());
	}
	/** Open a download dialog to save the resource of the specified URL
	 * at the client.
	 * The path must be retrieveable by use of {@link org.zkoss.zk.ui.WebApp#getResource}.
	 *
	 * @param url the URL to get the resource
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the path's
	 * extension is used to determine the content type.
	 * @exception FileNotFoundException if the resource is not found.
	 * @since 3.0.8
	 */
	public static void save(URL url, String contentType)
	throws FileNotFoundException {
		String name = url.toExternalForm();
		int j = name.lastIndexOf('/');
		if (j >= 0 && j < name.length() - 1)
			name = name.substring(j + 1);
		save(new AMedia(url, contentType, null), name);
	}
	/** Open a download dialog to save the resource of the specified path
	 * at the client.
	 *
	 * @param path the path of the resource.
	 * It must be retrieveable by use of {@link org.zkoss.zk.ui.WebApp#getResource}.
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the path's
	 * extension is used to determine the content type.
	 * @exception FileNotFoundException if the resource is not found.
	 * @since 3.0.8
	 */
	public static void save(String path, String contentType)
	throws FileNotFoundException {
		final URL url = Executions.getCurrent().getDesktop().getWebApp()
			.getResource(path);
		if (url == null)
			throw new FileNotFoundException(path);
		save(url, contentType);
	}

	private static class DownloadURL implements DeferredValue {
		private final Media _media;
		private final String _path;
		private DownloadURL(Media media, String flnm) {
			_media = media;

			if (flnm == null) flnm = media.getName();

			final StringBuffer sb = new StringBuffer(32);
			if (flnm != null && flnm.length() != 0) {
				sb.append('/');
				sb.append(flnm);
				if (flnm.lastIndexOf('.') < 0) {
					final String format = media.getFormat();
					if (format != null)
						sb.append('.').append(format);
				}
			}
			_path = sb.toString();
		}
		public Object getValue() {
			return Executions.getCurrent().getDesktop()
				.getDownloadMediaURI(_media, _path);
		}
	}
}
