/* Fileupload.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 19 10:26:42     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.mesg.Messages;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.impl.FileuploadDlg;
import org.zkoss.zul.mesg.MZul;

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
 * <p>5.0.2: If the event thread is disabled, an onUpload event ({@link UploadEvent})
 * is posted to all root components when the upload dialog is closed.
 * If you want the event being sent to a particular component, specify the
 * component in the desktop's attribute called <code>org.zkoss.zul.Fileupload.target</code>.
 * For example,
 *
 * <pre><code>desktop.setAttribute("org.zkoss.zul.Fileupload.target", mainWindow);
 *Fileupload.get();</code></pre>
 *
 * <h3>2. Embed as part of the page:</h3>
 *
 * <p>You can create it as a component and then listen to
 * the onUpload event ({@link UploadEvent}).
 *
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload">ZK Component Reference: Fileupload</a>.
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 * @see Filedownload
 */
public class Fileupload extends Button { //not XulElement since not applicable
	private static final Logger log = LoggerFactory.getLogger(Fileupload.class);
	private static String _templ = "~./zul/html/fileuploaddlg.zul";

	public Fileupload() {
		setUpload("true");
	}

	public Fileupload(String label) {
		this();
		setLabel(label);
	}

	public Fileupload(String label, String image) {
		this(label);
		setImage(image);
	}

	/////Open as a Modal Dialog/////
	/** Opens a modal dialog with the default message and title,
	 * and let user upload a file.
	 *
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 */
	public static Media get() {
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
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media get(boolean alwaysNative) {
		return get(null, null, alwaysNative);
	}

	/** Opens a modal dialog with the specified message and title,
	 * and let user upload a file.
	 *
	 * @param message the message. If null, the default is used.
	 * @param title the title. If null, the default is used.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 */
	public static Media get(String message, String title) {
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
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media get(String message, String title, boolean alwaysNative) {
		final Media[] result = get(message, title, 1, alwaysNative);
		return result != null ? result[0] : null;
	}

	/**
	 * Opens a modal dialog to upload multiple files with
	 * the default message and title.
	 * 
	 * @param listener The callback event listener when the event thread is disabled, the listener will be invoked, if it is not null.
	 * Note: the target of the listener is always null.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * Set the listener parameter to retrieve the upload content when the upload has finished.
	 * . For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 */
	public static Media[] get(EventListener<UploadEvent> listener) {
		return get(1, listener);
	}

	/**
	 * Opens a modal dialog to upload multiple files with
	 * the default message and title.
	 * 
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * @param listener The callback event listener when the event thread is disabled, the listener will be invoked, if it is not null.
	 * Note: the target of the listener is always null.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * Set the listener parameter to retrieve the upload content when the upload has finished.
	 * . For more information, refer to
	 */
	public static Media[] get(int max, EventListener<UploadEvent> listener) {
		return get(new HashMap<String, Object>(8), null, null, max, -1, false, listener);
	}

	/** Opens a modal dialog to upload multiple files with
	 * the default message and title.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 */
	public static Media[] get(int max) {
		return get(null, null, max, false);
	}

	/** Opens a modal dialog to upload multiple files with
	 * the default message and title.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 3.0.0
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharset
	 * @see org.zkoss.zk.ui.util.Configuration#getUploadCharsetFinder
	 */
	public static Media[] get(int max, boolean alwaysNative) {
		return get(null, null, max, alwaysNative);
	}

	/** Opens a modal dialog to upload multiple files with
	 * the specified message and title.
	 *
	 * <p>The returned format depending on the content type.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 */
	public static Media[] get(String message, String title, int max) {
		return get(message, title, max, false);
	}

	/** Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 3.0.0
	 */
	public static Media[] get(String message, String title, int max, boolean alwaysNative) {
		return get(message, title, max, -1, alwaysNative);
	}

	/** Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param maxsize the maximal upload size of the component.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event. For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 3.6.0
	 */
	public static Media[] get(String message, String title, int max, int maxsize, boolean alwaysNative) {
		return get(new HashMap<String, Object>(8), message, title, max, maxsize, alwaysNative);
	}

	/**
	 * Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param maxsize the maximal upload size of the component.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @param listener The callback event listener when the event thread is disabled, the listener will be invoked, if it is not null.
	 * Note: the target of the listener is always null.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event or set the listener parameter to retrieve the upload content when the upload has finished.
	 * . For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 6.5.3
	 */
	public static Media[] get(Map<String, Object> params, String message, String title, int max, int maxsize,
			boolean alwaysNative, EventListener<UploadEvent> listener) {
		return get(params, message, title, null, max, maxsize, alwaysNative, listener);
	}

	/**
	 * Opens a modal dialog to upload multiple files with
	 * the specified message, title and options.
	 *
	 * @param accept specifies the types of files that the server accepts,
	 *  the setting only works with HTML5 supported browsers
	 * @param max the maximal allowed number that an user can upload
	 * at once. If non-positive, 1 is assumed.
	 * If max is larger than 1000, 1000 is assumed.
	 * @param maxsize the maximal upload size of the component.
	 * @param alwaysNative  whether to treat the uploaded files as binary
	 * stream, regardless its content type.
	 * If false (the default), it will convert to
	 * {@link org.zkoss.image.Image}, {@link org.zkoss.sound.Audio},
	 * binary stream, or text files depending on the content type.
	 * @param listener The callback event listener when the event thread is disabled, the listener will be invoked, if it is not null.
	 * Note: the target of the listener is always null.
	 * @return null if the uploaded content, or null if not uploaded.
	 * Notice, by default, the event thread is disabled, and this
	 * method won't suspend and always returns null.
	 * To retrieve the uploaded content, the developer
	 * has to listen the onUpload event or set the listener parameter to retrieve the upload content when the upload has finished.
	 * . For more information, refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload#Event_Thread_Disabled">ZK Component Reference: Fileupload</a>.
	 * @since 7.0.2
	 */
	public static Media[] get(Map<String, Object> params, String message, String title, String accept, int max,
			int maxsize, boolean alwaysNative, EventListener<UploadEvent> listener) {
		final Execution exec = Executions.getCurrent();
		params.put("message", message == null ? Messages.get(MZul.UPLOAD_MESSAGE) : message);
		params.put("title", title == null ? Messages.get(MZul.UPLOAD_TITLE) : title);
		params.put("max", new Integer(max == 0 ? 1 : max > 1000 ? 1000 : max < -1000 ? -1000 : max));
		params.put("accept", accept);
		params.put("native", Boolean.valueOf(alwaysNative));
		params.put("maxsize", String.valueOf(maxsize));
		params.put("listener", listener);

		final FileuploadDlg dlg = (FileuploadDlg) exec.createComponents(_templ, null, params);
		try {
			dlg.doModal();
		} catch (Throwable ex) {
			try {
				dlg.detach();
			} catch (Throwable ex2) {
				log.warn("Failed to detach when recovering from an error", ex2);
			}
			throw UiException.Aide.wrap(ex);
		}
		return dlg.getResult();
	}

	/** The implementation of all public get methods.
	 * In other words, all public <code>get</code> methods will prepare
	 * an empty map and invoke this method to retrieve the media.
	 * It is designed to allow applications to customize the creation of 
	 * the dialog, such as adding more parameters.
	 * @since 5.0.11
	 */
	protected static Media[] get(Map<String, Object> params, String message, String title, int max, int maxsize,
			boolean alwaysNative) {
		return get(params, message, title, max, maxsize, alwaysNative, null);
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
