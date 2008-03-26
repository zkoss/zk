/* Filedownload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 16 09:29:44     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.InputStream;
import java.io.Reader;

import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.au.out.AuDownload;

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
		final String uri = desktop.getDownloadMediaURI(media, sb.toString());
		((WebAppCtrl)desktop.getWebApp())
			.getUiEngine().addResponse(null, new AuDownload(uri));
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
}
