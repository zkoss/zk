/* Fileupload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 10:26:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.zkoss.mesg.Messages;
import org.zkoss.zul.mesg.MZul;
import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.ext.Updatable;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.au.out.AuInvoke;


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
 * the onUpload event ({@link UploadEvent}).
 * If the cancel button is pressed or file(s) is uploaded, the onClose event
 * ({@link org.zkoss.zk.ui.event.Event}).
 * is sent to notify the application. By default, it does nothing but
 * invalidate the component, i.e., all fields are cleared.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 * @see Filedownload
 */
public class Fileupload extends HtmlBasedComponent{ //not XulElement since not applicable
	
	//Used when embedded as a component
	/** The maximal alllowed number of files to upload. */
	private int _maxnum = 1;
	/** Wether to treat the uploaded file(s) as binary. */
	private boolean _native;
	private int _maxsize = -1;
	private List meds = new LinkedList();
	private HashMap progressMap = new HashMap();
	
	public void setProgress(String fid, long readSize, long totalSize){
		long lArray[] = {readSize, totalSize};
		progressMap.remove(fid);
		progressMap.put(fid, lArray);
	}
	
	public void addMedia(Object med){
		meds.add(med);
	}
	public List getMedia(){
		return meds;
	}
	/**
	 * Returns the maximal allowed upload size of the component, in kilobytes, or 
	 * a negative value if no limit.
	 * <p> Default: -1.
	 * @since 3.6.0
	 */
	public int getMaxsize() {
		return _maxsize;
	}
	/**
	 * Sets the maximal allowed upload size of the component, in kilobytes.
	 * <p>Note: {@link Configuration#setMaxUploadSize(int)} is also allowed to limit the size,
	 * if the maximal size is -1.
	 * @since 3.6.0
	 */
	public void setMaxsize(int maxsize) {
		if (maxsize < 0) maxsize = -1;
		_maxsize = maxsize;
	}
	/** No child is allowed. */
	protected boolean isChildable() {
		return false;
	}

	/** Returns the maximal allowed number of files to upload.
	 * @since 2.4.0
	 */
	public int getNumber() {
		return _maxnum;
	}
	/** Sets the maximal allowed number of files to upload.
	 * <p>Default: 1.
	 * @param maxnum the maximal allowed number (positive or negative).
	 * Since 3.0.2, the value can be negative, which means no limitation at all and the end user can upload
	 * any numbers he wants (since 3.0.2)
	 * @exception WrongValueException if non-positive, or it exceeds 1000
	 * @since 2.4.0
	 */
	public void setNumber(int maxnum) throws WrongValueException {
		if (maxnum < -1000 || maxnum == 0 || maxnum > 1000)
			throw new WrongValueException(
				maxnum == 0 ? "Positive or Negative is required": "Number too big (maximal 1000)");
		_maxnum = maxnum;
	}
	/** Returns whether to treat the uploaded file(s) as binary, i.e.,
	 * not to convert it to image, audio or text files.
	 * <p>Default: false.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public boolean isNative() {
		return _native;
	}
	/** Sets whether to treat the uploaded file(s) as binary, i.e.,
	 * not to convert it to image, audio or text files.
	 *
	 * @param alwaysNative  whether to treat the uploaded file as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#setUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#setUploadCharsetFinder
	 */
	public void setNative(boolean alwaysNative) {
		_native = alwaysNative;
	}

	/** Hanldes the onClose event which is sent when file(s) is uploaded
	 * or when the cancel button is pressed.
	 *
	 * <p>By default, it simply invalidates itself, i.e.,
	 * all fields are cleared.
	 * If you want to do something different, you can intercept the onClose
	 * event.
	 * @since 2.4.0
	 */
	public void onClose() {
		invalidate();
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link HtmlBasedComponent#service},
	 * it also handles updateResult.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("updateResult")) {
		
		} else if(cmd.equals("getUploadInfo")){
			Map map = request.getData();
			String fid = String.valueOf(map.get("fid"));
			
			if(fid == null)	return;
			
			long lArray[] =  (long [])progressMap.get(fid);
			response("popup", new AuInvoke(this, "uploadProgress", 
					 new Object[] {fid, new Long(lArray[0]),new Long(lArray[1])}));
		}else
			super.service(request, everError);
	}

	/////Open as a Modal Dialog/////
	/** Opens a modal dialog with the default message and title,
	 * and let user upload a file.
	 *
	 * @return the uploaded content, or null if not uploaded.
	 */
	public static Media get() throws InterruptedException {
		return get(null, null, false);
	}
	/** Opens a modal dialog with the default message and title,
	 * and let user upload a file.
	 *
	 * @param alwaysNative  whether to treat the uploaded file as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return the uploaded content, or null if not uploaded.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media get(boolean alwaysNative) throws InterruptedException {
		return get(null, null, alwaysNative);
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
		return get(message, title, false);
	}
	/** Opens a modal dialog with the specified message and title,
	 * and let user upload a file.
	 *
	 * @param message the message. If null, the default is used.
	 * @param title the title. If null, the default is used.
	 * @param alwaysNative  whether to treat the uploaded file as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return the uploaded content, or null if not ready.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media get(String message, String title, boolean alwaysNative)
	throws InterruptedException {
		final Media[] result = get(message, title, 1, alwaysNative);
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
		return get(null, null, max, false);
	}
	/** Opens a modal dialog to upload mulitple files with
	 * the default message and title.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media[] get(int max, boolean alwaysNative)
	throws InterruptedException {
		return get(null, null, max, alwaysNative);
	}
	/** Opens a modal dialog to upload multiple files with
	 * the specified message and title.
	 *
	 * <p>The returned format depending on the content type.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 */
	public static Media[] get(String message, String title, int max)
	throws InterruptedException {
		return get(message, title, max, false);
	}
	/** Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 * @since 3.0.0
	 */
	public static
	Media[] get(String message, String title, int max, boolean alwaysNative)
	throws InterruptedException {
		return get(message, title, max, -1, alwaysNative);
	}
	/** Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If nonpositive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param maxsize the maximal upload size of the component.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return an array of files that an users has uploaded,
	 * or null if uploaded.
	 * @since 3.6.0
	 */
	public static
	Media[] get(String message, String title, int max, int maxsize, boolean alwaysNative)
	throws InterruptedException {

		
		return null;
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

	}
	/** Returns the template used to create the upload modal dialog.
	 */
	public static String getTemplate() {
		return null;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);
		String jsessionid = ((HttpSession) this.getDesktop().getSession().getNativeSession()).getId();
		renderer.render("jsessionid", jsessionid);
		
	}
	
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends Window.ExtraCtrl implements Updatable {
		//-- Updatable --//
		/** Updates the result from the client.
		 * Callback by the System AuUpload.java only Don't invoke it directly.
		 *
		 * @param result a list of media instances, or null
		 */
		public void setProgress(String fid, long readSize, long totalSize){
			Fileupload.this.setProgress(fid, readSize, totalSize);
		}
		public void addMedia(Object obj){
			Fileupload.this.addMedia(obj);
		}
	}
}
