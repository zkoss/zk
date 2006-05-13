/* Intbox.java

{{IS_NOTE
	$Id: Intbox.java,v 1.6 2006/04/04 08:10:09 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:39:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Locale;

import com.potix.zk.ui.WrongValueException;

import com.potix.zul.mesg.MZul;
import com.potix.zul.html.impl.FormatInputElement;

/**
 * An edit box for holding an integer.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/04/04 08:10:09 $
 */
public class Intbox extends FormatInputElement {
	public Intbox() {
		setCols(11);
	}
	public Intbox(int value) throws WrongValueException {
		this();
		setValue(new Integer(value));
	}

	/** Returns the value (in Integer), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Integer getValue() throws WrongValueException {
		return (Integer)getRawValue();
	}
	/** Returns the value in int. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final Object val = getRawValue();
		return val != null ? ((Integer)val).intValue(): 0;
	}
	/** Sets the value (in Date).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Integer value) throws WrongValueException {
		validate(value);
		if (setRawValue(value))
			smartUpdate("value", getText());
	}

	//-- super --//
	protected Object coerceFromString(String value) throws WrongValueException {
		final String val = ignoreCommas(value);
		if (val == null || val.length() == 0)
			return null;

		try {
			int j = val.indexOf('%');
			if (j <= 0)
				return j == 0 ? new Integer(0): Integer.valueOf(val);

			int v = Integer.parseInt(val.substring(0, j));
			for (final int len = val.length(); j < len && v != 0; ++j)
				if (val.charAt(j) == '%') v /= 100;
				else throw new WrongValueException(this, MZul.INTEGER_REQUIRED, value);
			return new Integer(v);
		} catch (NumberFormatException ex) {
			throw new WrongValueException(this, MZul.INTEGER_REQUIRED, value);
		}
	}
	protected String coerceToString(Object value) {
		return formatNumber(value);
	}
}
