/* Label.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 6:09:02 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.WrongValueException;

/**
 * @author henrichen
 */
public class Label extends Item {
	private static final long serialVersionUID = 200706060913L;
	private String _value;
	
	public Label() {
	}
	public Label(String value) throws WrongValueException {
		setValue(value);
	}
	
	public void setValue(String value) {
		if (value != null && value.length() == 0)
			value = null;

		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("tx", encodeString(value));
		}
	}
	
	public String getValue() {
		return _value;
	}
	
	protected int getAppearanceMode() {
		return PLAIN;
	}

	//-- super --//
	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "tx",  encodeString(_value)); //text
		HTMLs.appendAttribute(sb, "md",  getAppearanceMode()); //appearanceMode
		
		return sb.toString();
	}
}
