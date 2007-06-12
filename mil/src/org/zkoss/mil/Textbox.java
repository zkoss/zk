/* Textbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 25, 2007 9:49:27 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.mil.impl.InputElement;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A generic text box.
 * 
 * @author henrichen
 */
public class Textbox extends InputElement {
	private static final long serialVersionUID = 200705251351L;
	private String _type = "text";

	public Textbox() {
	}
	public Textbox(String value) throws WrongValueException {
		setValue(value);
	}
	public Textbox(String label, String value) throws WrongValueException {
		setValue(value);
	}

	/** Returns the value.
	 * The same as {@link #getText}.
	 * <p>Default: "".
	 * @exception WrongValueException if user entered a wrong value
	 */
	public String getValue() throws WrongValueException {
		return getText();
	}
	/** Sets the value.
	 *
	 * @param value the value; If null, it is considered as empty.
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(String value) throws WrongValueException {
		setText(value);
	}

	//-- super --//
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 */
	protected Object coerceFromString(String value) throws WrongValueException {
		return value != null ? value: "";
	}
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 */
	protected String coerceToString(Object value) {
		return value != null ? (String)value: "";
	}

	/** random text */
	protected int getInternalType() {
		return ANY;
	}
	
	/** Returns the type.
	 * <p>Default: text.
	 */
	public String getType() {
		return _type;
	}
	/** Sets the type.
	 * @param type the type. Acceptable values are "text" and "password".
	 */
	public void setType(String type) throws WrongValueException {
		if (!"text".equals(type) && !"password".equals(type))
			throw new WrongValueException("Illegal type: "+type);

		if (!_type.equals(type)) {
			_type = type;
			smartUpdateConstraints();
		}
	}
}
