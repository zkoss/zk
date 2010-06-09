/* Filedownload.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 16 14:41:22     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.io.InputStream;
import java.io.Reader;

import org.zkoss.util.media.Media;

/**
 * File download utilities.
 * 
 * @author tomyeh
 */
public class Filedownload {
	/** Open a download dialog to save the specified content at the client.
	 */
	public static void save(Media media) {
		org.zkoss.zul.Filedownload.save(media);
	}
	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.
	 *
	 * @param media the media to download
	 * @param flnm the suggested file name, e.g., myfile.pdf.
	 * If null, {@link Media#getName} is assumed.
	 */
	public static void save(Media media, String flnm) {
		org.zkoss.zul.Filedownload.save(media, flnm);
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
		org.zkoss.zul.Filedownload.save(content, contentType, flnm);
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
		org.zkoss.zul.Filedownload.save(content, contentType, flnm);
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
	public static void save(InputStream content, String contentType, String flnm) {
		org.zkoss.zul.Filedownload.save(content, contentType, flnm);
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
	public static void save(Reader content, String contentType, String flnm) {
		org.zkoss.zul.Filedownload.save(content, contentType, flnm);
	}
}
