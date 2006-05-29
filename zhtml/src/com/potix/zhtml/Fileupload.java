/* Fileupload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 24 15:11:04     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import com.potix.util.media.Media;

import com.potix.zk.ui.UiException;

/**
 * A fileupload dialog used to let user upload a file.
 *
 * <p>You don't create {@link Fileupload} directly. Rather, use {@link #get()}
 * or {@link #get(String, String)}.
 *
 * <p>A non-XHTML extension.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:27:48 $
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
		return com.potix.zul.html.Fileupload.get(message, title);
	}
}
