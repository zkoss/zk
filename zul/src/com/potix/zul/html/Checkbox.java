/* Checkbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 23:45:45     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Checkable;

import com.potix.zul.html.impl.LabelImageElement;

/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>com.potix.zk.ui.event.CheckEvent is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.10 $ $Date: 2006/05/29 04:28:21 $
 */
public class Checkbox extends LabelImageElement implements Checkable {
	/** The name. */
	private String _name;
	private boolean _checked;
	private boolean _disabled, _readonly;

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
	/** Returns whether it is readonly.
	 * <p>Default: false.
	 */
	public boolean isReadonly() {
		return _readonly;
	}
	/** Sets whether it is readonly.
	 */
	public void setReadonly(boolean readonly) {
		if (_readonly != readonly) {
			_readonly = readonly;
			smartUpdate("readOnly", _readonly);
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

	/** Returns the attributes used by the embedded HTML LABEL tag.
	 * It returns text-relevant styles only.
	 * <p>Used only by component developer.
	 */
	public String getLabelAttrs() {
		final String style = HTMLs.getTextRelevantStyle(getRealStyle());
		return style.length() > 0 ? "style=\""+style+'"': "";
	}

	//-- Checkable --//
	public void setCheckedByClient(boolean checked) {
		_checked = checked;
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
		if (isReadonly())
			HTMLs.appendAttribute(sb, "readonly", "readonly");
		if (isChecked())
			HTMLs.appendAttribute(sb, "checked",  "checked");
		return sb.toString();
	}
	/** Appends exterior attributes for generating the HTML span tag
	 * (the event relevant attribute).
	 * <p>Used only by component developers.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		if (isAsapRequired("onFocus"))
			HTMLs.appendAttribute(sb, "zk_onFocus", true);
		if (isAsapRequired("onBlur"))
			HTMLs.appendAttribute(sb, "zk_onBlur", true);
		if (isAsapRequired("onCheck"))
			HTMLs.appendAttribute(sb, "zk_onCheck", true);
		return sb.toString();
	}
}
