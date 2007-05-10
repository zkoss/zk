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

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.SuspendNotAllowedException;

import org.zkoss.zul.impl.FileuploadDlg;
import org.zkoss.zul.impl.XulElement;

/**
 * A fileupload dialog used to let user upload a file.
 *
 * <p>There are two ways to use {@link Fileupload}:
 *
 * <h3>1. Open as a modal dialog:</h3>
 *
 * <p>You don't create {@link Fileupload} directly. Rather, use {@link #get()}
 * or {@link #get(String, String)}.
 *
 * <h3>2. Embed as part of the page:</h3>
 *
 * <p>You can create it as a component and then listen to
 * the onUpload event.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 * @see Filedownload
 */
public class Fileupload extends XulElement {
	private static String _templ = "~./zul/html/fileuploaddlg.zul";

	/////Embed: as a component/////
	/** No child is allowed. */
	public boolean isChildable() {
		return false;
	}

	/////Open as a Modal Dialog/////
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
		try {
			dlg.doModal();
		} catch (SuspendNotAllowedException ex) {
			dlg.detach();
			throw ex;
		}
		return dlg.getResult();
	}

	/** Sets the template used to create the upload modal dialog.
	 *
	 * <p>The template must follow the default template:
	 * ~./zul/html/fileuploaddlg.zul
	 *
	 * <p>In other words, just adjust the label and layout and don't
	 * change the component's ID.
	 *
	 * <p>Note: the template has no effect, if you use {@link Fileupload} as
	 * a component (and embed it to a page).
	 */
	public static void setTemplate(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("empty");
		_templ = uri;
	}
	/** Returns the template used to create the upload modal dialog.
	 */
	public static String getTemplate() {
		return _templ;
	}
}
