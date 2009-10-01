/* Menuitem.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:23     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.Utils;

/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 * 
 * <p>Default {@link #getZclass}: z-menu-item. (since 3.5.0)
 * @author tomyeh
 */
public class Menuitem extends LabelImageElement implements org.zkoss.zul.api.Menuitem {
	private String _value = "";
	private String _href, _target;
	private boolean _autocheck, _checked;
	private boolean _disabled = false;
	private boolean _checkmark;
	private String _upload;

	static {
		addClientEvent(Menuitem.class, Events.ON_CHECK, CE_IMPORTANT);
	}
	public Menuitem() {
	}
	public Menuitem(String label) {
		super(label);
	}
	public Menuitem(String label, String src) {
		super(label, src);
	}
	
	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public final boolean isCheckmark() {
		return _checkmark;
	}
	/** Sets whether the check mark shall be displayed in front
	 * of each item.
	 * @since 3.5.0
	 */
	public void setCheckmark(boolean checkmark) {
		if (_checkmark != checkmark) {
			_checkmark = checkmark;
			smartUpdate("checkmark", checkmark);
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-menu-item" : _zclass;
	}
	
	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", disabled);
		}
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/** Returns the value.
	 * <p>Default: "".
	 */
	public String getValue() {
		return _value;
	}
	/** Sets the value.
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!_value.equals(value)) {
			_value = value;
			smartUpdate("value", value);
		}
	}
	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	public boolean isChecked() {
		return _checked;
	}
	/** Sets whether it is checked.
	 * <p> This only applies when {@link #isCheckmark()} = true. (since 3.5.0)
	 */
	public void setChecked(boolean checked) {
		if (_checked != checked) {
			_checked = checked;
			if (_checked)
				_checkmark = true;
			smartUpdate("checked", checked);
		}
	}
	/** Returns whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p>Default: false.
	 */
	public boolean isAutocheck() {
		return _autocheck;
	}
	/** Sets whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p> This only applies when {@link #isCheckmark()} = true. (since 3.5.0)
	 */
	public void setAutocheck(boolean autocheck) {
		if (_autocheck != autocheck) {
			_autocheck = autocheck;
			smartUpdate("autocheck", autocheck);
		}
	}

	/** Returns the href.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick handler.
	 */
	public String getHref() {
		return _href;
	}
	/** Sets the href.
	 */
	public void setHref(String href) throws WrongValueException {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_href, href)) {
			_href = href;
			smartUpdate("href", new EncodedHref()); //Bug #2871082
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
		return _target;
	}
	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_target, target)) {
			_target = target;
			smartUpdate("target", _target);
		}
	}

	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 */
	public boolean isTopmost() {
		return !(getParent() instanceof Menupopup);
	}

	/** Returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @since 5.0.0
	 */
	public String getUpload() {
		return _upload;
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
	 * {@link UploadEvent} is sent this component.
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
	 *  <li>native: Sets whether to treat the uploaded file(s) as binary, i.e.,
	 * not to convert it to image, audio or text files.</li>
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

		if (!Objects.equals(upload, _upload)) {
			_upload = upload;
			smartUpdate("upload", _upload);
		}
	}

	//Bug #2871082
	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _href != null && dt != null ? dt.getExecution().encodeURL(_href): null;
			//if desktop is null, it doesn't belong to any execution
	}
	
	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Menupopup)
		&& !(parent instanceof Menubar))
			throw new UiException("Unsupported parent for menuitem: "+parent);
		super.beforeParentChanged(parent);
	}
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_checkmark) render(renderer, "checkmark", _checkmark);
		if (_disabled) render(renderer, "disabled", _disabled);
		if (_checked) render(renderer, "checked", _checked);
		if (_autocheck) render(renderer, "autocheck", _autocheck);
		if (_href != null) render(renderer, "href", getEncodedHref()); //Bug #2871082
		if (_target != null) render(renderer, "target", _target);
		render(renderer, "upload", _upload);
		render(renderer, "value", _value);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onCheck.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			_checked = evt.isChecked();
			if (_checked) _checkmark = true;
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	//Bug #2871082
	private class EncodedHref implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}
}
