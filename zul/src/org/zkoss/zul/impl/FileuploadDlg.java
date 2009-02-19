/* FileuploadDlg.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:33:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.List;
import java.util.Iterator;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.au.AuRequests;

import org.zkoss.zul.Window;

/**
 * Used with {@link org.zkoss.zul.Fileupload} to implement
 * the upload feature.
 *
 * @author tomyeh
 */
public class FileuploadDlg extends Window {
	private Media[] _result;

	/** Returns the result.
	 * @return an array of media (length >= 1), or null if nothing.
	 */
	public Media[] getResult() {
		return _result;
	}

	public void onCancel() {
		onClose();
	}
	public void onClose() {
		response("endUpload", new AuScript(null, "zAu.endUpload()"));
		detach();
	}

	/** Sets the result.
	 */
	public void setResult(Media[] result) {
		_result = result;
	}

	public static Media[] parseResult(List result) {
		if (result != null) {
			//we have to filter items that user doesn't specify any file
			for (Iterator it = result.iterator(); it.hasNext();) {
				final Media media = (Media)it.next();
				if (media != null && media.inMemory() && media.isBinary()) {
					final String nm = media.getName();
					if (nm == null || nm.length() == 0) {
						final byte[] bs = media.getByteData();
						if (bs == null || bs.length == 0)
							it.remove(); //Upload is pressed without specifying a file
					}
				}
			}

			if (!result.isEmpty())
				return (Media[])result.toArray(new Media[result.size()]);
		}
		return null;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#process},
	 * it also handles updateResult.
	 * @since 5.0.0
	 */
	public void process(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getName();
		if (name.equals("updateResult")) {
			FileuploadDlg.this.setResult(
				parseResult((List)AuRequests.getUpdateResult(request)));
		} else
			super.process(request, everError);
	}
}
