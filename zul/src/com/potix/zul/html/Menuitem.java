/* Menuitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:58:23     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Checkable;

import com.potix.zul.html.impl.LabelImageElement;

/**
 * sA single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Menuitem extends LabelImageElement implements Checkable {
	private String _value = "";
	private String _href, _target;
	private boolean _autocheck, _checked;

	public Menuitem() {
	}
	public Menuitem(String label) {
		setLabel(label);
	}
	public Menuitem(String label, String src) {
		setLabel(label);
		setImage(src);
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
		_value = value; //no need to update client
	}
	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	public boolean isChecked() {
		return _checked;
	}
	/** Sets whether it is checked.
	 */
	public void setChecked(boolean checked) {
		if (_checked != checked) {
			_checked = checked;

			final Component parent = getParent();
			if (parent instanceof Menupopup)
				parent.invalidate(INNER);
			//FUTURE: to support checked for top-level menuitems
		}
	}
	/** Returns whether the menuitem check mark will update each time
	 * the menu item is selected
	 * <p>Default: false.
	 */
	public boolean isAutocheck() {
		return _autocheck;
	}
	/** Sets whether the menuitem check mark will update each time
	 * the menu item is selected
	 */
	public void setAutocheck(boolean autocheck) {
		if (_autocheck != autocheck) {
			_autocheck = autocheck;
			invalidate(INNER);
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
			invalidate(INNER);
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

	//-- Checkable --//
	public void setCheckedByClient(boolean checked) {
		setChecked(checked);
	}

	//-- Super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		boolean topmost = isTopmost();
		if (!topmost && !_autocheck) return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		if (topmost) sb.append(" zk_top=\"true\"");
		if (_autocheck) {
			sb.append(" zk_autock=\"true\"");
			if (_checked) sb.append(" zk_checked=\"true\"");
		}
		return sb.toString();
	}
	protected String getRealStyle() {
		final String style = super.getRealStyle();
		return isTopmost() ?
			style + "padding-left:4px;padding-right:4px;": style;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Menupopup)
		&& !(parent instanceof Menubar))
			throw new UiException("Unsupported parent for menuitem: "+parent);
		super.setParent(parent);
	}
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}
