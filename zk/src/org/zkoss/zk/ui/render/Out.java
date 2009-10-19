/* Out.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 2:51:53 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zk.ui.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.xml.XMLs;

/**
 * A utility to simulate DSP's out action.
 * It is designed to simplify the job to port DSP to
 * {@link ComponentRenderer}.
 *
 * <p>For example,<br/>
 * <code>new Out(self.getLabel()).setMaxlength(maxlen).render(out);</code>
 *
 * @author jumperchen
 * @author tomyeh
 * @since 3.0.0 
 */
public class Out {
	private String _value;
	private int _maxlength;
	private boolean _escapeXML = true;
	private boolean _nbsp;
	private boolean _pre;

	public Out(String value) {
		_value = value;
	}

	/**
	 * Returns whether to escape XML. Default: true.
	 */
	public boolean getEscapeXML() {
		return _escapeXML;
	}
	/**
	 * Sets whether to escape XML.
	 */
	public Out setEscapeXML(boolean escapeXML) {
		_escapeXML = escapeXML;
		return this;
	}

	/**
	 * Returns whether to generate &amp;nbsp; if the content is empty.
	 * Default: false.
	 */
	public boolean getNbsp() {
		return _escapeXML;
	}
	/**
	 * Sets whether to generate &amp;nbsp; if the content is empty.
	 */
	public Out setNbsp(boolean nbsp) {
		_nbsp = nbsp;
		return this;
	}

	/**
	 * Returns the value.
	 */
	public String getValue() {
		return _value;
	}
	/**
	 * Sets the value.
	 */
	public Out setValue(String value) {
		_value = value;
		return this;
	}

	/**
	 * Returns the maxlength of bytes to output.
	 * <p>Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/**
	 * Sets the maxlength to output.
	 */
	public Out setMaxlength(int maxlength) {
		_maxlength = maxlength;
		return this;
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
	
	/** Generates the output to the specified writer.
	 */
	public void render(Writer out) throws IOException {
		int len = _value != null ? _value.length() : 0;
		if (len == 0 || (_nbsp && _value.trim().length() == 0)) {
			if (_nbsp)
				out.write("&nbsp;");
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

		out.write(value);
	}
}
