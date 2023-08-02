/* Button.java

	Purpose:

	Description:

	History:
		Wed Jun  8 10:31:02     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.ext.Uploads;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.(since 3.5.0)
 * @author tomyeh
 */
public class Button extends LabelImageElement implements org.zkoss.zk.ui.ext.Disable,
		org.zkoss.zk.ui.ext.Uploadable {
	private AuxInfo _auxinf;

	static {
		addClientEvent(Button.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Button.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
		addClientEvent(Button.class, Events.ON_UPLOAD, 0);
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
	 * @see #setAutodisable
	 */
	public void setDisabled(boolean disabled) {
		if ((_auxinf != null && _auxinf.disabled) != disabled) {
			initAuxInfo().disabled = disabled;
			smartUpdate("disabled", isDisabled());
		}
	}

	public void testCheckOPrFile() {}
	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 * @since 5.0.0
	 */
	public String getAutodisable() {
		return _auxinf != null ? _auxinf.autodisable : null;
	}

	/** Sets a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 *
	 * <p>To represent the button itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;button id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;button id="ok" autodisable="ok,cancel"/></code>
	 * that will disable
	 * both the ok and cancel buttons when a user clicks it.
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
		if (!Objects.equals(_auxinf != null ? _auxinf.autodisable : null, autodisable)) {
			initAuxInfo().autodisable = autodisable;
			smartUpdate("autodisable", getAutodisable());
		}
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _auxinf != null ? _auxinf.dir : NORMAL;
	}

	/** Sets the direction to layout image.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!NORMAL.equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_auxinf != null ? _auxinf.dir : NORMAL, dir)) {
			initAuxInfo().dir = dir;
			smartUpdate("dir", getDir());
		}
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _auxinf != null ? _auxinf.orient : HORIZONTAL;
	}

	/** Sets the orient to layout image.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!HORIZONTAL.equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_auxinf != null ? _auxinf.orient : HORIZONTAL, orient)) {
			initAuxInfo().orient = orient;
			smartUpdate("orient", getOrient());
		}
	}

	/** Returns the button type.
	 * <p>Default: "button".
	 * @since 5.0.4
	 */
	public String getType() {
		return _auxinf != null ? _auxinf.type : BUTTON;
	}

	/** Sets the button type.
	 * <p>Default: "button".
	 * It is meaningful only if it is used with a HTML form.
	 * Refer to <a href="http://www.htmlcodetutorial.com/forms/_BUTTON_TYPE.html">HTML Button Type</a>
	 * for details.
	 * @param type either "button", "submit" or "reset".
	 * @since 5.0.4
	 */
	public void setType(String type) throws WrongValueException {
		if (!BUTTON.equals(type) && !"submit".equals(type) && !"reset".equals(type))
			throw new WrongValueException(type);

		if (!Objects.equals(_auxinf != null ? _auxinf.type : BUTTON, type)) {
			initAuxInfo().type = type;
			smartUpdate("type", getType());
		}
	}

	/** Returns the href that the browser shall jump to, if a user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	public String getHref() {
		return _auxinf != null ? _auxinf.href : null;
	}

	/** Sets the href.
	 */
	public void setHref(String href) {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.href : null, href)) {
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
		return _auxinf != null ? _auxinf.target : null;
	}

	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.target : null, target)) {
			initAuxInfo().target = target;
			smartUpdate("target", getTarget());
		}
	}

	public String getUpload() {
		return _auxinf != null ? _auxinf.upload : null;
	}

	public void setUpload(String upload) {
		if (upload != null && (upload.length() == 0 || "false".equals(upload)))
			upload = null;
		if (!Objects.equals(upload, _auxinf != null ? _auxinf.upload : null)) {
			onUploadChanged(upload);
			initAuxInfo().upload = upload;
			Uploads.parseUpload(this, upload);
			smartUpdate("upload", Uploads.getRealUpload(this, getUpload()));
		}
	}

	/** Called when the upload attribute is modified. */
	private void onUploadChanged(String upload) {
		if (upload != null && !"trendy".equals(getMold()) && getDefinition().getMoldNames().contains("trendy")) //Toolbarbutton doesn't support trendy
			setMold("trendy");
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _auxinf != null && _auxinf.href != null && dt != null ? dt.getExecution().encodeURL(_auxinf.href) : null;
		//if desktop is null, it doesn't belong to any execution
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		String s;
		if (!NORMAL.equals(s = getDir()))
			render(renderer, "dir", s);
		if (!HORIZONTAL.equals(s = getOrient()))
			render(renderer, "orient", s);
		if (!BUTTON.equals(s = getType()))
			render(renderer, "type", s);

		render(renderer, "disabled", isDisabled());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = getEncodedHref());
		render(renderer, "target", getTarget());
		render(renderer, "upload", Uploads.getRealUpload(this, getUpload()));

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	protected void renderCrawlable(String label) throws java.io.IOException {
		//does nothing since generated in renderProperties
	}

	public String getZclass() {
		return _zclass != null ? _zclass : "z-button";
	}

	//Cloneable//
	public Object clone() {
		final Button clone = (Button) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	//Component//
	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}

	private class EncodedHref implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}

	protected void updateByClient(String name, Object value) {
		if ("disabled".equals(name))
			setDisabled(value instanceof Boolean ? ((Boolean) value).booleanValue()
					: "true".equals(Objects.toString(value)));
		else
			super.updateByClient(name, value);
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	@Override
	public void service(AuRequest request, boolean everError) {
		String cmd = request.getCommand();
		if (Events.ON_UPLOAD.equals(cmd)) {
			Events.postEvent(UploadEvent.getUploadEvent(cmd, this, request));
		} else
			super.service(request, everError);
	}

	private static final String HORIZONTAL = "horizontal", NORMAL = "normal", BUTTON = "button";

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private String orient = HORIZONTAL;
		private String dir = NORMAL;
		private String type = BUTTON;
		private String href, target;
		private String autodisable;
		protected String upload;
		private boolean disabled;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
