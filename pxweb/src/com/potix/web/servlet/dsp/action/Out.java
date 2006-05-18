/* Out.java

{{IS_NOTE
	$Id: Out.java,v 1.5 2006/02/27 03:54:31 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 16:10:51     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

import java.io.IOException;

import com.potix.web.mesg.MWeb;
import com.potix.web.servlet.ServletException;
import com.potix.web.servlet.http.Encodes;

/**
 * Generates the specified value into a string.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:54:31 $
 */
public class Out extends AbstractAction {
	private String _value = null;
	private String _mlineReplace = "<br/>";
	private int _maxlength = 0;
	private boolean _escapeXML = true;

	/** Returns whether to escape XML.
	 * Default: true.
	 */
	public boolean getEscapeXML() {
		return _escapeXML;
	}
	/** Sets whether to escape XML.
	 */
	public void setEscapeXML(boolean escapeXML) {
		_escapeXML = escapeXML;
	}
	/** Returns the value.
	 * Default: null.
	 */
	public String getValue() {
		return _value;
	}
	/** Sets the value.
	 */
	public void setValue(String value) {
		_value = value;
	}

	/** Returns the string to replace '\n'. If null,no replacement occur.
	 * Default: <br/>
	 */
	public String getMultilineReplace() {
		return _mlineReplace;
	}
	/** Sets the string to replace '\n'. If null or empty,no replacement occur.
	 */
	public void setMultilineReplace(String mlineReplace) {
		_mlineReplace =
			mlineReplace == null || mlineReplace.length() == 0 ?
				null: mlineReplace;
	}

	/** Returns the maxlength of bytes to output.
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * <p>Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maxlength to output.
	 */
	public void setMaxlength(int maxlength) {
		_maxlength = maxlength;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws javax.servlet.ServletException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new ServletException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});
		if (_value == null)
			return;
		int len = _value.length();
		if (len == 0)
			return;
			
		String value = _value;
		if (_maxlength > 0 && len > _maxlength / 2) {
			int cnt = 0;
			for (int j = 0; j < len; ++j, ++cnt) {
				char cc = value.charAt(j);
				if (cc >= 0x4E00 && cc < 0x9FFF) //Big5 and GB2312
					++cnt;
				if (cnt >= _maxlength) {
					while (j > 0 && Character.isWhitespace(value.charAt(j)))
						--j;
					value = value.substring(0, j) + "...";
					break;
				}
			}
		}

		final boolean escNewLine = _mlineReplace != null;
		if (_escapeXML || escNewLine) {
			StringBuffer sb = null;
			len = value.length();
			for (int j = 0; j < len; ++j) {
				final char cc = value.charAt(j);
				final String replace;
				if (escNewLine && (cc == '\r' || cc == '\n')) {
					replace = cc == '\n' ? _mlineReplace: "";
				} else {
					replace = _escapeXML ? Encodes.escapeXML(cc): null;
				}

				if (replace != null) {
					if (sb == null) {
						sb = new StringBuffer(value.length() + 10);
						sb.append(value.substring(0, j));
					}
					sb.append(replace);
				} else if (sb != null) {
					sb.append(cc);
				}
			}
			if (sb != null)
				value = sb.toString();
		}

		ac.getOut().write(value);
	}

	//-- Object --//
	public String toString() {
		return "out";
	}
}
