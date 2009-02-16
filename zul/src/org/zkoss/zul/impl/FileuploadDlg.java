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
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.zk.au.out.AuScript;
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
		response("endUpload", new AuScript(null, "zkau.endUpload()"));
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
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends Window.ExtraCtrl implements Updatable {
		//-- Updatable --//
		/** Updates the result from the client.
		 * Callback by the system only. Don't invoke it directly.
		 *
		 * @param result a list of media instances, or null
		 */
		public void setResult(Object result) {
			FileuploadDlg.this.setResult(parseResult((List)result));
		}
	}
}
