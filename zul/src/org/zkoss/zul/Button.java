/* Button.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 10:31:02     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	private String _orient = "horizontal", _dir = "normal";
	private String _href, _target;
	private int _tabindex = -1;
	private boolean _disabled;

	public Button() {
	}
	public Button(String label) {
		this();
		setLabel(label);
	}
	public Button(String label, String image) {
		this(label);
		setImage(image);
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			invalidate();
		}
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _dir;
	}
	/** Sets the direction.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_dir, dir)) {
			_dir = dir;
			invalidate();
		}
	}
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}

	/** Returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	public String getHref() {
		return _href;
	}
	/** Sets the href.
	 */
	public void setHref(String href) {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_href, href)) {
			_href = href;
			smartUpdateDeferred("z.href", new EncodedHref()); //Bug 1850895
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
			smartUpdate("z.target", _target);
		}
	}
	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}
	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			if (tabindex < 0) smartUpdate("tabindex", null);
			else smartUpdate("tabindex", Integer.toString(_tabindex));
		}
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _href != null && dt != null ? dt.getExecution().encodeURL(_href): null;
			//if desktop is null, it doesn't belong to any execution
	}

	//-- super --//
	protected String getRealSclass() {
		final String scls = super.getRealSclass();
		final String added = isDisabled() ? getZclass() + "-disd" : "";
		return scls == null ? added : scls + " " + added;
	}
	public String getZclass() {
		String added = "os".equals(getMold()) ? "-os" : "";
		return _zclass == null ? "z-button" + added : _zclass;
	}
	
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.href", getEncodedHref());
		HTMLs.appendAttribute(sb, "z.target", getTarget());
		boolean isOS = "os".equals(getMold());
		if (isDisabled()) {
			if (isOS)
				HTMLs.appendAttribute(sb, "disabled",  "disabled");
			else
				HTMLs.appendAttribute(sb, "z.disd", true);
		}

		appendAsapAttr(sb, Events.ON_FOCUS);
		appendAsapAttr(sb, Events.ON_BLUR);
		appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
		appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
		if (_tabindex >= 0) {
			Execution exec = Executions.getCurrent();
			if (isOS || (!exec.isGecko() && !exec.isSafari()))
				HTMLs.appendAttribute(sb, "tabindex", _tabindex);
		}
		
		return sb.toString();
	}

	//Component//
	/** No child is allowed.
	 */
	public boolean isChildable() {
		return false;
	}

	private class EncodedHref implements org.zkoss.zk.ui.util.DeferredValue {
		public String getValue() {
			return getEncodedHref();
		}
	}
}
