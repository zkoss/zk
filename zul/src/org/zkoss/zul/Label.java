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
package org.zkoss.zul;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.ComponentCtrl;

import org.zkoss.zul.impl.XulElement;

/**
 * A label.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Label extends XulElement {
	private String _value = "";
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
			invalidate();
		}
	}

	/** Returns the maximal length of each item's label.
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
			invalidate();
		}
	}
	/** Whether to generate the value directly without ID. */
	private boolean isIdRequired() {
		final Component p = getParent();
		return p == null || !isVisible() || !Components.isAutoId(getId())
			|| !isRawLabel(p) || isAsapRequired(Events.ON_CLICK)
			|| isAsapRequired(Events.ON_RIGHT_CLICK)
			|| isAsapRequired(Events.ON_DOUBLE_CLICK);
	}
	private static boolean isRawLabel(Component comp) {
		final LanguageDefinition langdef =
			((ComponentCtrl)comp).getMilieu().getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	/** Returns the text for generating HTML tags (Internal Use Only).
	 *
	 * <p>Used only for component generation. Not for applications.
	 */
	public String getEncodedText() {
		return _value;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs(false);
		return clkattrs == null ? attrs: attrs + clkattrs;
	}

	//-- Component --//
	public void invalidate() {
		if (isIdRequired()) super.invalidate();
		else getParent().invalidate();
	}
	public void redraw(Writer out) throws IOException {
		if (isIdRequired()) super.redraw(out);
		else out.write(getEncodedText()); //no processing; direct output if not ZUL
	}
	public boolean isChildable() {
		return false;
	}
}
