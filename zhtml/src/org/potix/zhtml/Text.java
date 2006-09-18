/* Text.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 24 15:17:07     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import java.io.Writer;
import java.io.IOException;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Components;
import com.potix.zk.ui.AbstractComponent;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.RawId;
import com.potix.zk.ui.metainfo.LanguageDefinition;
import com.potix.zk.ui.sys.ComponentCtrl;

/**
 * Represents a piece of text (of DOM).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Text extends AbstractComponent implements RawId {
	private String _value = "";

	public Text() {
	}
	public Text(String value) {
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
			invalidate();
		}
	}

	/** Whether to generate the value directly without ID. */
	private boolean isIdRequired() {
		final Component p = getParent();
		return p == null || !isVisible()
			|| !Components.isAutoId(getId()) || !isRawLabel(p);
	}
	private static boolean isRawLabel(Component comp) {
		final LanguageDefinition langdef =
			((ComponentCtrl)comp).getMillieu().getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (!isIdRequired()) {
			final Component old = getParent();
			if (old != parent) {
				if (old != null) old.invalidate();
				if (parent != null) parent.invalidate();
			}
		}
		super.setParent(parent);
	}
	public void invalidate() {
		if (isIdRequired()) super.invalidate();
		else getParent().invalidate();
	}
	public void redraw(Writer out) throws IOException {
		if (isIdRequired()) super.redraw(out);
		else out.write(_value); //no processing; direct output if not ZUL
	}
	public boolean isChildable() {
		return false;
	}
}
