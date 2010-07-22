/* Button.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 10:31:02     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.(since 3.5.0)
 * @author tomyeh
 */
public class Button extends LabelImageElement implements org.zkoss.zul.api.Button {
	private AuxInfo _auxinf;

	static {
		addClientEvent(Button.class, Events.ON_FOCUS, 0);
		addClientEvent(Button.class, Events.ON_BLUR, 0);
	}

	public Button() {
	}
	public Button(String label) {
		super(label);
	}
	public Button(String label, String image) {
		super(label, image);
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _auxinf != null && _auxinf.disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if ((_auxinf != null && _auxinf.disabled) != disabled) {
			initAuxInfo().disabled = disabled;
			smartUpdate("disabled", isDisabled());
		}
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 * @since 5.0.0
	 */
	public String getAutodisable() {
		return _auxinf != null ? _auxinf.autodisable: null;
	}
	/** Sets a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 *
	 * <p>To represent the button itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;button id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;button id="ok" autodisable="ok,cancel"/></code>
	 * that will disable
	 * both the ok and cancel buttons when an user clicks it.
	 *
	 * <p>The button being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a button is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>&lt;button id="ok" autodisable="+self,+cancel"/></code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 *</code></pre>
	 *
	 * <p>Default: null.
	 * @since 5.0.0
	 */
	public void setAutodisable(String autodisable) {
		if (!Objects.equals(_auxinf != null ? _auxinf.autodisable: null, autodisable)) {
			initAuxInfo().autodisable = autodisable;
			smartUpdate("autodisable", getAutodisable());
		}
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _auxinf != null ? _auxinf.dir: NORMAL;
	}
	/** Sets the direction.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_auxinf != null ? _auxinf.dir: NORMAL, dir)) {
			initAuxInfo().dir = dir;
			smartUpdate("dir", getDir());
		}
	}
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _auxinf != null ? _auxinf.orient: HORIZONTAL;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!HORIZONTAL.equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_auxinf != null ? _auxinf.orient: HORIZONTAL, orient)) {
			initAuxInfo().orient = orient;
			smartUpdate("orient", getOrient());
		}
	}

	/** Returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	public String getHref() {
		return _auxinf != null ? _auxinf.href: null;
	}
	/** Sets the href.
	 */
	public void setHref(String href) {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.href: null, href)) {
			initAuxInfo().href = href;
			smartUpdate("href", new EncodedHref()); //Bug 1850895
		}
	}

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 */
	public String getTarget() {
		return _auxinf != null ? _auxinf.target: null;
	}
	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.target: null, target)) {
			initAuxInfo().target = target;
			smartUpdate("target", getTarget());
		}
	}

	/** Returns the tab order of this component.
	 * <p>Default: 0 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _auxinf != null ? _auxinf.tabindex: 0;
	}
	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if ((_auxinf != null ? _auxinf.tabindex: 0) != tabindex) {
			initAuxInfo().tabindex = tabindex;
			smartUpdate("tabindex", getTabindex());
		}
	}

	/** Returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @since 5.0.0
	 */
	public String getUpload() {
		return _auxinf != null ? _auxinf.upload: null;
	}
	/** Sets the JavaScript class at the client to handle the upload if this
	 * button is used for file upload.
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
	 *  <pre><code>&lt;button label="Upload" upload="true,maxsize=-1,native"</code></pre>
	 *  <ul>
	 *  <li>maxsize: the maximal allowed upload size of the component, in kilobytes, or 
	 * a negative value if no limit.</li>
	 *  <li>native: treating the uploaded file(s) as binary, i.e., not to convert it to
	 * image, audio or text files.</li>
	 *  </ul>
	 *  
	 * @param upload a JavaScript class to handle the file upload
	 * at the client, or "true" if the default class is used,
	 * or null or "false" to disable the file download (and then
	 * this button behaves like a normal button).
	 * @since 5.0.0
	 */
	public void setUpload(String upload) {
		if (upload != null
		&& (upload.length() == 0 || "false".equals(upload)))
			upload = null;
		if (!Objects.equals(upload, _auxinf != null ? _auxinf.upload: null)) {
			onUploadChanged(upload);
			initAuxInfo().upload = upload;
			smartUpdate("upload", getUpload());
		}
	}
	/** Called when the upload attribute is modified. */
	private void onUploadChanged(String upload) {
		if (upload != null && !"trendy".equals(getMold())
		&& getDefinition().getMoldNames().contains("trendy")) //Toolbarbutton doesn't support trendy
			setMold("trendy");
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _auxinf != null && _auxinf.href != null && dt != null ?
			dt.getExecution().encodeURL(_auxinf.href): null;
			//if desktop is null, it doesn't belong to any execution
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		int v;
		if ((v = getTabindex()) != 0)
			renderer.render("tabindex", v);
		String s;
		if (!NORMAL.equals(s = getDir())) render(renderer, "dir", s);
		if (!HORIZONTAL.equals(s = getOrient())) render(renderer, "orient", s);

		if (isDisabled())
			render(renderer, "disabled", true);
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = getEncodedHref());
		render(renderer, "target", getTarget());
		render(renderer, "upload", getUpload());

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}
	public String getZclass() {
		return _zclass != null ? _zclass:
			!"trendy".equals(getMold()) ? "z-button-os": "z-button";
	}

	//Component//
	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}

	private class EncodedHref implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}
	private static final String HORIZONTAL = "horizontal", NORMAL = "normal";
	private static class AuxInfo implements java.io.Serializable {
		private String orient = HORIZONTAL, dir = NORMAL;
		private String href, target;
		private String autodisable;
		protected String upload;
		private int tabindex;
		private boolean disabled;
	}
}
