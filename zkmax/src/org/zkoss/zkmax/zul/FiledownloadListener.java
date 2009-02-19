/* FiledownloadListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 20 14:23:01     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import java.io.File;
import java.net.URL;

import org.zkoss.util.media.Media;

/**
 * The listener used with {@link Filedownload#saveResumable} to reject
 * a download if the user tried to resume it in unacceptable situation.
 *
 * <p>For example, you might allow the user to reuse the download
 * for, say, 24 hours, then you can pass a special key to denote it
 * in the data argument when calling {@link Filedownload#saveResumable}.
 * Then, you can check the data argument when {@link #onDownload}
 * is called back.
 *
 * @author tomyeh
 * @since 3.5.0
 * @see Filedownload
 */
public interface FiledownloadListener {
	/** Notifies that the user is requesting the media to download.
	 * It is used with {@link Filedownload#saveResumable} to authenticate
	 * or filter the download.
	 *
	 * @param media the media that will be downloaded to the client.
	 * @param source the source of the download. It is the file argument
	 * of {@link Filedownload#saveResumable(File, String, String)},
	 * the url argument
	 * of {@link Filedownload#saveResumable(URL, String, String)}, and
	 * the path argument
	 * of {@link Filedownload#saveResumable(String, String, String)}.
	 * @param data the data argument passed to {@link Filedownload#saveResumable}.
	 * @return the media to be downloaded to the client.
	 * If you allow the download, you can return the value of the media argument.
	 * If you deny the download, just return null.
	 * If you want to alter the content, you can create a new instance and
	 * return it.
	 */
	public Media onDownload(Media media, Object source, String data);
}
