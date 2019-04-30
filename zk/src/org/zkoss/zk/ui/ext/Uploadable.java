package org.zkoss.zk.ui.ext;

import org.zkoss.zk.ui.util.Configuration;

/**
 * Implemented with {@link org.zkoss.zk.ui.Component} to indicate
 * that a component can be used for file upload.
 *
 * @author rudyhuang
 * @since 8.6.2
 */
public interface Uploadable {
	/**
	 * Returns non-null if this component is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @since 5.0.0
	 */
	String getUpload();

	/**
	 * Sets the JavaScript class at the client to handle the upload if this
	 * component is used for file upload.
	 * <p>Default: null.
	 *
	 * <p>For example, the following example declares a button for file upload:
	 * <pre><code>&lt;button label="Upload" upload="true"
	 * onUpload="handle(event.media)"/&gt;</code></pre>
	 *
	 * <p>As shown above, after the file is uploaded, an instance of
	 * {@link org.zkoss.zk.ui.event.UploadEvent} is sent this component.
	 *
	 * <p>If you want to customize the handling of the file upload at
	 * the client, you can specify a JavaScript class when calling
	 * this method:
	 * <code>&lt;button upload="foo.Upload"/&gt;</code>
	 *
	 * <p> Another options for the upload can be specified as follows:
	 *  <pre><code>&lt;button label="Upload" upload="true,maxsize=-1,multiple=true,accept=audio/*|video/*|image/*,native"</code></pre>
	 *  <ul>
	 *  <li>maxsize: the maximal allowed upload size of the component, in kilobytes, or
	 * a negative value if no limit, if the maxsize is not specified, it will use {@link Configuration#getMaxUploadSize()}</li>
	 *  <li>native: treating the uploaded file(s) as binary, i.e., not to convert it to
	 * image, audio or text files.</li>
	 *  <li>multiple: treating the file chooser allows multiple files to upload,
	 *  the setting only works with HTML5 supported browsers (since ZK 6.0.0).</li>
	 *  <li>accept: specifies the types of files that the server accepts,
	 *  the setting only works with HTML5 supported browsers (since ZK 7.0.0).</li>
	 *  </ul>
	 *
	 * <p> Note: if the options of the <code>false</code> or the customized handler
	 * (like <code>foo.Upload</code>) are not specified, the option of <code>true</code>
	 * is implicit by default.
	 *
	 * @param upload a JavaScript class to handle the file upload
	 * at the client, or "true" if the default class is used,
	 * or null or "false" to disable the file download (and then
	 * this button behaves like a normal button).
	 * @since 5.0.0
	 */
	void setUpload(String upload);
}
