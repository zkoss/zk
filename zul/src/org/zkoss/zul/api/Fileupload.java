/* Fileupload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;//for javadoc

/**
 * A fileupload dialog used to let user upload a file.
 * 
 * <p>
 * There are two ways to use {@link Fileupload}:
 * 
 * <h3>1. Open as a modal dialog:</h3>
 * 
 * <p>
 * You don't create {@link Fileupload} directly. Rather, use
 * {@link org.zkoss.zul.Fileupload#get()} or
 * {@link org.zkoss.zul.Fileupload#get(String, String)}.
 * 
 * <h3>2. Embed as part of the page:</h3>
 * 
 * <p>
 * You can create it as a component and then listen to the onUpload event (
 * {@link UploadEvent}). If the cancel button is pressed or file(s) is uploaded,
 * 
 * <p>
 * A non-XUL extension.
 * 
 * @since 3.5.2
 * @author tomyeh
 * @see org.zkoss.zul.Filedownload
 */
public interface Fileupload extends org.zkoss.zk.ui.api.HtmlBasedComponent {

	/**
	 * Returns the maximal allowed number of files to upload.
	 * 
	 */
	public int getNumber();

	/**
	 * Sets the maximal allowed number of files to upload.
	 * <p>
	 * Default: 1.
	 * 
	 * @param maxnum
	 *            the maximal allowed number (positive or negative). The value can be negative, which means no limitation at
	 *            all and the end user can upload any numbers he wants 
	 * @exception WrongValueException
	 *                if non-positive, or it exceeds 1000
	 */
	public void setNumber(int maxnum) throws WrongValueException;

	/**
	 * Returns whether to treat the uploaded file(s) as binary, i.e., not to
	 * convert it to image, audio or text files.
	 * <p>
	 * Default: false.
	 * 
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public boolean isNative();

	/**
	 * Sets whether to treat the uploaded file(s) as binary, i.e., not to
	 * convert it to image, audio or text files.
	 * 
	 * @param alwaysNative
	 *            whether to treat the uploaded file as binary stream,
	 *            regardless its content type. If false (the default), it will
	 *            convert to {@link org.zkoss.image.Image},
	 *            {@link org.zkoss.sound.Audio}, binary stream, or text files
	 *            depending on the content type.
	 * @see org.zkoss.zk.ui.util.Configuration#setUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#setUploadCharsetFinder
	 */
	public void setNative(boolean alwaysNative);

}
