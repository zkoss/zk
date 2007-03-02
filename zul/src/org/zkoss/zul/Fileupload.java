/* Fileupload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 10:26:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.mesg.Messages;
import org.zkoss.zul.mesg.MZul;
import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import org.zkoss.zul.impl.FileuploadDlg;

/**
 * A fileupload dialog used to let user upload a file.
 *
 * <p>You don't create {@link Fileupload} directly. Rather, use {@link #get()}
 * or {@link #get(String, String)}.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 */
public class Fileupload {
	private static String _templ = "~./zul/html/fileupload.zul";

	/** Opens a modal dialog with the default message and title,
	 * and let user upload a file.
	 * @return the uploaded content, or null if not uploaded.
	 */
	public static Media get() throws InterruptedException {
		return get(null, null);
	}
	/** Opens a modal dialog with the specified message and title,
	 * and let user upload a file.
	 *
	 * @param message the message. If null, the default is used.
	 * @param title the title. If null, the default is used.
	 * @return the uploaded content, or null if not ready.
	 */
	public static Media get(String message, String title)
	throws InterruptedException {
		final Media[] result = get(message, title, 1);
		return result != null ? result[0]: null;
	}
	/** Opens a modal dialog to upload mulitple files with
	 * the default message and title.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 */
	public static Media[] get(int max) throws InterruptedException {
		return get(null, null, max);
	}
	/** Opens a modal dialog to upload multiple files with
	 * the specified message and title.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 */
	public static Media[] get(String message, String title, int max)
	throws InterruptedException {
		final Map params = new HashMap(5);
		final Execution exec = Executions.getCurrent();
		params.put("action", exec.getDesktop().getUpdateURI("/upload"));
		params.put("message", message == null ?
			Messages.get(MZul.UPLOAD_MESSAGE): message);
		params.put("title", title == null ?
			Messages.get(MZul.UPLOAD_TITLE): title);
		params.put("max", new Integer(max <= 1 ? 1: max));

		final FileuploadDlg dlg = (FileuploadDlg)
			exec.createComponents(
				_templ, null, params);
		dlg.doModal();
		return dlg.getResult();
	}

	/** Sets the template used to create the upload dialog.
	 *
	 * <p>The template must follow the default template:
	 * ~./zul/html/fileupload.zul
	 *
	 * <p>In other words, just adjust the label and layout and don't
	 * change the component's ID.
	 */
	public static void setTemplate(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("empty");
		_templ = uri;
	}
	/** Returns the template used to create the upload dialog.
	 */
	public static String getTemplate() {
		return _templ;
	}
}
