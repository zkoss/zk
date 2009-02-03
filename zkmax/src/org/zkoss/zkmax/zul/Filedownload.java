/* Filedownload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 20 13:28:26     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import java.io.File;
import java.net.URL;

import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.au.out.AuDownload;

import org.zkoss.zkmax.au.http.AuDownloader;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * More filedownload utilities, such as resumable filedownload.
 *
 * <p><b>Resumable Download</b>
 *
 * <p>By resumable we mean the user can bookmark the URL and download
 * it later (and even resume the download in the middle).
 * On the other hand, the download URL of {@link org.zkoss.zul.Filedownload}
 * is obsolte as soon as the desktop (or session) is gone.
 *
 * <p>Since the resumable download can be used in any session or
 * without any session, or with a different client (such flashget),
 * you might want to limit the download under certain condition.
 * If so, you can implement {@link FiledownloadListener} and specify
 * it as a library (or system) property called org.zkoss.zkmax.zul.FiledownloadListener.class
 * by use of {@link org.zkoss.lang.Library#setProperty}
 * or the library-property element in zk.xml.
 *
 *<pre><code>&lt;library-property&gt;
  &lt;name&gt;org.zkoss.zkmax.zul.FiledownloadListener.class&lt;/name&gt;
  &lt;value&gt;com.foo.MyDownloadListener&lt;/value&gt;
&lt;/library-property&gt;
</code></pre>
 *
 *
 * @author tomyeh
 * @since 3.5.0
 * @see FiledownloadListener
 */
public class Filedownload extends org.zkoss.zul.Filedownload {
	/** Open a download dialog to save the specified file at the client.
	 * Unlike {@link org.zkoss.zul.Filedownload#save(File,String)},
	 * the download URL is resumable.
	 *
	 * @param file the file to download to the client
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the file name's
	 * extension is used to determine the content type.
	 * @param data the application-specific data to be passed to
	 * {@link FiledownloadListener#onDownload}.
	 */
	public static void
	saveResumable(File file, String contentType, String data) {
		saveResumable0(new DownloadURL(file, contentType, data));
	}
	/** Open a download dialog to save the resouce of the specified URL at the client.
	 * Unlike {@link org.zkoss.zul.Filedownload#save(File,String)},
	 * the download URL is resumable.
	 *
	 * @param url the URL to get the resource
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the file name's
	 * extension is used to determine the content type.
	 * @param data the application-specific data to be passed to
	 * {@link FiledownloadListener#onDownload}.
	 */
	public static void
	saveResumable(URL url, String contentType, String data) {
		saveResumable0(new DownloadURL(url, contentType, data));
	}
	/** Open a download dialog to save the resource of the specified path at the client.
	 * Unlike {@link org.zkoss.zul.Filedownload#save(File,String)},
	 * the download URL is resumable.
	 *
	 * @param path the path of the resource.
	 * It must be retrieveable by use of {@link org.zkoss.zk.ui.WebApp#getResource}.
	 * @param contentType the content type, e.g., application/pdf.
	 * Unlike other save methods, it is optional. If null, the file name's
	 * extension is used to determine the content type.
	 * @param data the application-specific data to be passed to
	 * {@link FiledownloadListener#onDownload}.
	 */
	public static
	void saveResumable(String path, String contentType, String data) {
		saveResumable0(new DownloadURL(path, contentType, data));
	}
	private static void saveResumable0(DownloadURL downloadURL) {
		((WebAppCtrl)Executions.getCurrent().getDesktop().getWebApp())
			.getUiEngine().addResponse(null, new AuDownload(downloadURL));
	}

	private static class DownloadURL implements DeferredValue {
		private final Object _target;
		private final String _contentType;
		private final String _data;
		private DownloadURL(Object target, String contentType, String data) {
			_target = target;
			_contentType = contentType;
			_data = data;
		}
		public Object getValue() {
			return
			_target instanceof File ?
				AuDownloader.getDownloadURI((File)_target, _contentType, _data):
			_target instanceof URL ?
				AuDownloader.getDownloadURI((URL)_target, _contentType, _data):
				AuDownloader.getDownloadURI((String)_target, _contentType, _data);
		}
	}
}
