/* Toolbarbutton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A tool button.
 *
 * <p>The default CSS class is "z-toolbar-button".
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 * <p>Default {@link #getZclass}: z-toolbar-button.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Toolbarbutton extends LabelImageElement implements org.zkoss.zul.api.Toolbarbutton {
	private String _orient = "horizontal", _dir = "normal";
	private String _href, _target;
	private int _tabindex = -1;
	private boolean _disabled = false;
	
	static {
		addClientEvent(Toolbarbutton.class, Events.ON_FOCUS);
		addClientEvent(Toolbarbutton.class, Events.ON_BLUR);
	}
	
	public Toolbarbutton() {
	}
	public Toolbarbutton(String label) {
		setLabel(label);
	}
	public Toolbarbutton(String label, String image) {
		setLabel(label);
		setImage(image);
	}

	// super
	public String getZclass() {
		return _zclass == null ?  "z-toolbar-button" : super.getZclass();
	}
	
	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _disabled;
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
			smartUpdate("dir", _dir);
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
			smartUpdate("href", _href);
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
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
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
			if (tabindex < 0) smartUpdate("tabindex", (Object)null);
			else smartUpdate("tabindex", _tabindex);
		}
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (_href == null) {
			render(renderer, "target", _target);
		} else {
			render(renderer, "href", getDesktop().getExecution().encodeURL(
					_href));
			// When hyper to other page, we always show progress dlg
		}
		if (isDisabled())
			render(renderer, "disabled", "disabled");
		if (!"normal".equals(_dir))
			render(renderer, "dir", _dir);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (_tabindex >= 0)
			render(renderer, "tabindex", _tabindex);
	}
	//Component//
	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}
}
