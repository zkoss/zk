/* FileuploadDlg.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:33:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import com.potix.util.media.Media;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ext.Updatable;
import com.potix.zul.html.Window;

/**
 * Used with {@link com.potix.zul.html.Fileupload} to implement
 * the upload feature.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class FileuploadDlg extends Window implements Updatable {
	private Media _result;

	/** Returns the result.
	 */
	public Media getResult() {
		return _result;
	}

	public void onCancel() {
		detach();
	}

	//-- Updatable --//
	/** Updates the result from the client.
	 * Callback by the system only. Don't invoke it directly.
	 */
	public void setResult(Object result) {
		_result = (Media)result;
	}
}
