/* Out.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 16:10:51     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;
import org.zkoss.xml.XMLs;

/**
 * Generates the specified value into a string.
 *
 * @author tomyeh
 */
public class Out extends AbstractAction {
	private String _value = null;
	private int _maxlength;
	private boolean _escapeXML = true;
	private boolean _nbsp;
	private boolean _pre;
	
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
	/** Returns whether to generate &amp;nbsp; if the content is empty.
	 * Default: false.
	 */
	public boolean getNbsp() {
		return _escapeXML;
	}
	/** Sets whether to generate &amp;nbsp; if the content is empty.
	 */
	public void setNbsp(boolean nbsp) {
		_nbsp = nbsp;
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

	/** Returns the maxlength of bytes to output.
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

	/**
	 * Sets whether to preserve the white spaces, such as space.
	 * @since 3.6.3.
	 */
	public void setPre(boolean pre) {
		_pre = pre;
	}

	/**
	 * Returns whether to preserve the white spaces, such as space.
	 * <p> Default: false;
	 * 
	 * @since 3.6.3.
	 */
	public boolean isPre() {
		return _pre;
	}
	
	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new DspException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});

		int len = _value != null ? _value.length(): 0;
		if (len == 0 || (_nbsp && _value.trim().length() == 0)) {
			if (_nbsp)
				ac.getOut().write("&nbsp;");
			return;
		}
			
		String value = _value;
		if (_maxlength > 0 && len > _maxlength) {
			int j = _maxlength;
			while (j > 0 && Character.isWhitespace(value.charAt(j - 1)))
				--j;
			value = value.substring(0, j) + "...";
		}

		if (_escapeXML) {
			StringBuffer sb = null;
			len = value.length();
			for (int j = 0; j < len; ++j) {
				final char cc = value.charAt(j);
				final String replace;
				
				if (_pre && cc == ' ')
					replace = "&nbsp;";
				else
					replace = XMLs.escapeXML(cc);

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
