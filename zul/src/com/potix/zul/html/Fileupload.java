/* Fileupload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 10:26:42     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Map;
import java.util.HashMap;

import com.potix.mesg.Messages;
import com.potix.zul.mesg.MZul;
import com.potix.util.media.Media;

import com.potix.zk.ui.UiException;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;

import com.potix.zul.html.impl.FileuploadDlg;

/**
 * A fileupload dialog used to let user upload a file.
 *
 * <p>You don't create {@link Fileupload} directly. Rather, use {@link #get()}
 * or {@link #get(String, String)}.
 *
 * <p>A non-XUL extension.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/29 04:28:22 $
 */
public class Fileupload {
	/** Opens a modal dialog with the default message and title,
	 * and let user upload a file.
	 * @return the uploaded content, or null if not ready.
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
		final Map params = new HashMap(5);
		final Execution exec = Executions.getCurrent();
		params.put("action", exec.getDesktop().getUpdateURI("/upload"));
		params.put("message", message == null ?
			Messages.get(MZul.UPLOAD_MESSAGE): message);
		params.put("title", title == null ?
			Messages.get(MZul.UPLOAD_TITLE): title);

		final FileuploadDlg dlg = (FileuploadDlg)
			exec.createComponents(
				"~./zul/html/fileupload.zul", null, params);
		dlg.doModal();
		return dlg.getResult();
	}
}
