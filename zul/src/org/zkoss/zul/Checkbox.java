/* Checkbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 23:45:45     2005, Created by tomyeh
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

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.client.Checkable;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.CheckEvent is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Checkbox extends LabelImageElement implements org.zkoss.zul.api.Checkbox {
	/** The name. */
	private String _name;
	private int _tabindex = -1;
	private boolean _checked;
	private boolean _disabled;

	public Checkbox() {
	}
	public Checkbox(String label) {
		setLabel(label);
	}
	public Checkbox(String label, String image) {
		setLabel(label);
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
			smartUpdate("disabled", _disabled);
		}
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
			smartUpdate("checked", _checked);
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
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

	/** Returns the attributes used by the embedded HTML LABEL tag.
	 * It returns text-relevant styles only.
	 * <p>Used only by component developer.
	 */
	public String getLabelAttrs() {
		final String style = HTMLs.getTextRelevantStyle(getRealStyle());
		return style.length() > 0 ? " style=\""+style+'"': "";
	}

	//-- super --//
	/** Appends interior attributes for generating the HTML checkbox tag
	 * (the name, disabled and other attribute).
	 * <p>Used only by component developers.
	 */
	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());

		HTMLs.appendAttribute(sb, "name", getName());
		if (isDisabled())
			HTMLs.appendAttribute(sb, "disabled",  "disabled");
		if (isChecked())
			HTMLs.appendAttribute(sb, "checked",  "checked");
		if (_tabindex >= 0)
			HTMLs.appendAttribute(sb, "tabindex", _tabindex);
		return sb.toString();
	}
	/** Appends exterior attributes for generating the HTML span tag
	 * (the event relevant attribute).
	 * <p>Used only by component developers.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_FOCUS);
		appendAsapAttr(sb, Events.ON_BLUR);
		appendAsapAttr(sb, Events.ON_CHECK);
		appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
		appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
			//no z.lfclk since it is handled by widget.js

		return sb.toString();
	}
	/** Returns the Style of checkbox label
	 *
	 * <p>Default: "z-checkbox"
	 * <p>Since 3.5.1
	 * 
	 */
	public String getZclass() {
		return _zclass == null ? "z-checkbox" : _zclass;	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends LabelImageElement.ExtraCtrl
	implements Checkable {
		//-- Checkable --//
		public void setCheckedByClient(boolean checked) {
			_checked = checked;
		}
	}
}
