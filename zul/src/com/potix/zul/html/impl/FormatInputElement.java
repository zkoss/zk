/* FormatInputElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:27:34     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import com.potix.lang.Objects;
import com.potix.util.prefs.Apps;

import com.potix.zk.ui.WrongValueException;

/**
 * An input box that supports format.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/29 04:28:29 $
 */
abstract public class FormatInputElement extends InputElement {
	private String _format;
	/** Returns the format.
	 * <p>Default: null (used what is defined in the format sheet).
	 */
	public String getFormat() {
		return _format;
	}
	/** Sets the format.
	 */
	public void setFormat(String format) throws WrongValueException {
		if (!Objects.equals(_format, format)) {
			final String old = _format;
			_format = format;
			try {
				smartUpdate("value", getText());
				//Yes, the value attribute is changed! (no format attr in client)
			} catch (WrongValueException ex) {
				//ignore it (safe because it will keep throwing exception)
			}
		}
	}

	/** Formats a number (Integer, BigDecimal...) into a string.
	 * If null, an empty string is returned.
	 * A utility to assist the handling of numeric data.
	 * @see #ignoreCommas
	 */
	protected String formatNumber(Object value) {
		if (value == null) return "";

		final String fmt = getFormat();
		if (fmt == null) {
			return value.toString();
		} else {
			final DecimalFormat df = (DecimalFormat)
				NumberFormat.getInstance(Apps.getCurrentLocale());
			df.applyPattern(fmt);
			return df.format(value);
		}
	}
	/** Filters out comma and space from the specified value.
	 * It is used to parse a string to numeric data.
	 * @see #formatNumber
	 */
	protected String ignoreCommas(String val) {
		if (val == null) return val;

		StringBuffer sb = null;
		for (int j = 0, len = val.length(); j < len; ++j) {
			final char cc = val.charAt(j);
			if (cc == ',' || Character.isWhitespace(cc)) {
				if (sb == null)
					sb = new StringBuffer(len).append(val.substring(0, j));
			} else {
				if (sb != null) sb.append(cc);
			}
		}
		return sb != null ? sb.toString(): val;
	}

	//-- super --//
	protected boolean isAsapRequired(String evtnm) {
		return ("onChange".equals(evtnm) && getFormat() != null)
			|| super.isAsapRequired(evtnm);
	}
}
