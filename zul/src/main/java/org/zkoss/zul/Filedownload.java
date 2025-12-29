/* Filedownload.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 16 09:29:44     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.zkoss.io.Files;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.au.DeferredValue;
import org.zkoss.zk.au.out.AuDownload;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.WebAppCtrl;

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
		((WebAppCtrl) desktop.getWebApp()).getUiEngine().addResponse(new AuDownload(new DownloadURL(media, flnm))); //Bug 2114380
	}

	/** Open a download dialog to save the specified content at the client
	 * with the suggested file name.
	 *
	 * @param content the content
	 * @param contentType the content type (a.k.a., MIME type),
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
	 * @param contentType the content type (a.k.a., MIME type),
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
	 * @param contentType the content type (a.k.a., MIME type),
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
	 * @param contentType the content type (a.k.a., MIME type),
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
	public static void save(File file, String contentType) throws FileNotFoundException {
		save(new AMedia(file, contentType, null), file.getName());
	}

	/** Open a download dialog to save the resource of the specified URL
	 * at the client.
	 * The path must be retrievable by use of {@link org.zkoss.zk.ui.WebApp#getResource}.
	 *
	 * @param url the URL to get the resource
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the path's
	 * extension is used to determine the content type.
	 * @exception FileNotFoundException if the resource is not found.
	 * @since 3.0.8
	 */
	public static void save(URL url, String contentType) throws FileNotFoundException {
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
	 * It must be retrievable by use of {@link org.zkoss.zk.ui.WebApp#getResource}.
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the path's
	 * extension is used to determine the content type.
	 * @exception FileNotFoundException if the resource is not found.
	 * @since 3.0.8
	 */
	public static void save(String path, String contentType) throws FileNotFoundException {
		final URL url = Executions.getCurrent().getDesktop().getWebApp().getResource(path);
		if (url == null)
			throw new FileNotFoundException(path);
		save(url, contentType);
	}


	/**
	 * Downloads multiple media files using their original filenames.
	 * The files will be downloaded sequentially to avoid browser cancellation issues.
	 *
	 * @param medias the media files to download
	 * @since 10.3.0
	 */
	public static void saveMultiple(Media... medias) {
		if (medias == null || medias.length == 0)
			return;

		for (Media media : medias) {
			save(media, null);
		}
	}

	/**
	 * Downloads multiple media files with custom filenames.
	 * The files will be downloaded sequentially to avoid browser cancellation issues.
	 *
	 * @param mediaMap a map of media to filename pairs. If filename is null, the original media name is used.
	 * @since 10.3.0
	 */
	public static void saveMultiple(Map<Media, String> mediaMap) {
		if (mediaMap == null || mediaMap.isEmpty())
			return;

		for (Map.Entry<Media, String> entry : mediaMap.entrySet()) {
			save(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Downloads multiple files using DownloadItem objects.
	 * The files will be downloaded sequentially to avoid browser cancellation issues.
	 *
	 * @param items the download items containing media and optional custom filenames
	 * @since 10.3.0
	 */
	public static void saveMultiple(DownloadItem... items) {
		if (items == null || items.length == 0)
			return;

		for (DownloadItem item : items) {
			save(item.getMedia(), item.getFilename());
		}
	}

	/**
	 * Downloads multiple media files as a single ZIP archive.
	 *
	 * @param medias the media files to include in the ZIP
	 * @param zipFilename the name of the ZIP file (e.g., "download.zip")
	 * @since 10.3.0
	 */
	public static void saveAsZip(Media[] medias, String zipFilename) {
		if (medias == null || medias.length == 0)
			return;

		Map<Media, String> mediaMap = new LinkedHashMap<>();
		for (Media media : medias) {
			mediaMap.put(media, null); // use original filename
		}
		saveAsZip(mediaMap, zipFilename);
	}

	/**
	 * Downloads multiple media files with custom names as a single ZIP archive.
	 *
	 * @param mediaMap a map of media to filename pairs. If filename is null, the original media name is used.
	 * @param zipFilename the name of the ZIP file (e.g., "download.zip")
	 * @since 10.3.0
	 */
	public static void saveAsZip(Map<Media, String> mediaMap, String zipFilename) {
		if (mediaMap == null || mediaMap.isEmpty())
			return;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);

			for (Map.Entry<Media, String> entry : mediaMap.entrySet()) {
				Media media = entry.getKey();
				String filename = entry.getValue();

				if (filename == null || filename.isEmpty()) {
					filename = media.getName();
				}

				// Ensure filename has extension
				if (filename != null && filename.lastIndexOf('.') < 0) {
					String format = media.getFormat();
					if (format != null && !format.isEmpty()) {
						filename = filename + "." + format;
					}
				}

				// Add entry to ZIP
				ZipEntry zipEntry = new ZipEntry(filename != null ? filename : "file");
				zos.putNextEntry(zipEntry);

				// Write media content
				if (media.isBinary()) {
					if (media.inMemory()) {
						zos.write(media.getByteData());
					} else {
						try (InputStream is = media.getStreamData()) {
							Files.copy(zos, is);
						}
					}
				} else {
					if (media.inMemory()) {
						zos.write(media.getStringData().getBytes("UTF-8"));
					} else {
						try (Reader reader = media.getReaderData()) {
							// Convert Reader content to bytes
							char[] buffer = new char[8192];
							int len;
							while ((len = reader.read(buffer)) > 0) {
								zos.write(new String(buffer, 0, len).getBytes("UTF-8"));
							}
						}
					}
				}

				zos.closeEntry();
			}

			zos.close();

			// Ensure ZIP filename
			if (zipFilename == null || zipFilename.isEmpty()) {
				zipFilename = "download.zip";
			} else if (!zipFilename.toLowerCase().endsWith(".zip")) {
				zipFilename = zipFilename + ".zip";
			}

			// Create ZIP media and download
			byte[] zipData = baos.toByteArray();
			AMedia zipMedia = new AMedia(zipFilename, "zip", "application/zip", zipData);
			save(zipMedia, zipFilename);

		} catch (IOException e) {
			throw new UiException("Failed to create ZIP file: " + e.getMessage(), e);
		}
	}

	/**
	 * Downloads multiple files using DownloadItem objects as a single ZIP archive.
	 *
	 * @param items the download items to include in the ZIP
	 * @param zipFilename the name of the ZIP file (e.g., "download.zip")
	 * @since 10.3.0
	 */
	public static void saveAsZip(DownloadItem[] items, String zipFilename) {
		if (items == null || items.length == 0)
			return;

		Map<Media, String> mediaMap = new LinkedHashMap<>();
		for (DownloadItem item : items) {
			mediaMap.put(item.getMedia(), item.getFilename());
		}
		saveAsZip(mediaMap, zipFilename);
	}

	/**
	 * Helper class for specifying media and optional custom filename for multiple file downloads.
	 *
	 * @since 10.3.0
	 */
	public static class DownloadItem {
		private final Media media;
		private final String filename;

		/**
		 * Creates a download item with custom filename.
		 *
		 * @param media the media to download
		 * @param filename the custom filename (can be null to use media's original name)
		 */
		public DownloadItem(Media media, String filename) {
			if (media == null)
				throw new IllegalArgumentException("media cannot be null");
			this.media = media;
			this.filename = filename;
		}

		/**
		 * Creates a download item using the media's original filename.
		 *
		 * @param media the media to download
		 */
		public DownloadItem(Media media) {
			this(media, null);
		}

		/**
		 * Static factory method to create a download item with custom filename.
		 *
		 * @param media the media to download
		 * @param filename the custom filename
		 * @return a new DownloadItem instance
		 */
		public static DownloadItem of(Media media, String filename) {
			return new DownloadItem(media, filename);
		}

		/**
		 * Static factory method to create a download item using media's original filename.
		 *
		 * @param media the media to download
		 * @return a new DownloadItem instance
		 */
		public static DownloadItem of(Media media) {
			return new DownloadItem(media);
		}

		public Media getMedia() {
			return media;
		}

		public String getFilename() {
			return filename;
		}
	}

	private static class DownloadURL implements DeferredValue {
		private final Media _media;
		private final String _path;

		private DownloadURL(Media media, String flnm) {
			_media = media;

			if (flnm == null)
				flnm = media.getName();

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
			return Executions.getCurrent().getDesktop().getDownloadMediaURI(_media, _path);
		}
	}
}
