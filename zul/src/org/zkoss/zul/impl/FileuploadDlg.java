/* FileuploadDlg.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:33:06     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.LinkedList;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Window;

/**
 * Used with {@link org.zkoss.zul.Fileupload} to implement
 * the upload feature.
 *
 * @author tomyeh
 */
public class FileuploadDlg extends Window {
	private LinkedList _result = new LinkedList();
	
	public void onClose(Event evt) {
		if (evt.getData() == null)
			_result.clear();
		detach();
	}
	/**
	 * Called when a file is received.
	 * It is used only for component development.
	 * @since 5.0.0
	 */
	public void onUpload(ForwardEvent evt) {
		_result.add(((UploadEvent)evt.getOrigin()).getMedia());
	}
	
	/** Returns the result.
	 * @return an array of media (length >= 1), or null if nothing.
	 */
	public Media[] getResult() {
		return _result.isEmpty() ? null : (Media[])_result.toArray(new Media[0]);
	}
	
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("onRemove")) {
			_result.remove(((Integer)request.getData().get("")).intValue());
		} else
			super.service(request, everError);
	}
}
