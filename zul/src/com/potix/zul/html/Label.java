/* Label.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 18:53:53     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.io.Writer;
import java.io.IOException;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Components;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.metainfo.LanguageDefinition;
import com.potix.zk.ui.metainfo.ComponentDefinition;

import com.potix.zul.html.impl.XulElement;

/**
 * A label.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Label extends XulElement {
	private String _value = "";
	private boolean _multiline;
	private int _maxlength;

	public Label() {
	}
	public Label(String value) {
		setValue(value);
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
		if (!Objects.equals(_value, value)) {
			_value = value;
			if (isIdRequired()) invalidate(INNER);
			else getParent().invalidate(INNER);
		}
	}

	/** Returns the maximal length of each item's label.
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			if (isIdRequired()) invalidate(OUTER);
			else getParent().invalidate(INNER);
		}
	}
	/** Whether to generate the value directly without ID. */
	private boolean isIdRequired() {
		final Component p = getParent();
		return p == null || !isVisible() || !Components.isAutoId(getId())
			|| !isRawLabel(p) || isAsapRequired("onClick");
	}
	private static boolean isRawLabel(Component comp) {
		final ComponentDefinition compdef = comp.getDefinition();
		if (compdef == null) return false;
		final LanguageDefinition langdef = compdef.getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	/** Returns whether this label supports multilines.
	 * <p>Default: false.
	 * <p>If multiline is false, '\n' is interpreted to a whitespace.
	 */
	public boolean isMultiline() {
		return _multiline;
	}
	public void setMultiline(boolean multiline) {
		if (multiline != _multiline) {
			_multiline = multiline;
			invalidate(INNER);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return isAsapRequired("onClick") ?
			attrs+" zk_onClick=\"true\""
				+" zk_type=\"zul.html.widget.Label\"":
			attrs;
	}

	//-- Component --//
	public void smartUpdate(String attr, String value) {
		//We have to ask the client to re-initialize it (to observe onclick)
		if ("zk_onClick".equals(attr) && "true".equals(value))
			invalidate(OUTER);
		else
			super.smartUpdate(attr, value);
	}
	public void redraw(Writer out) throws IOException {
		if (isIdRequired()) super.redraw(out);
		else out.write(_value); //no processing; direct output if not ZUL
	}
	public boolean isChildable() {
		return false;
	}
}
