/* XMLs.java

{{IS_NOTE

Purpose: 
Description: 
History:
C91/01/07 17:36:07, reate, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.xml;

import com.potix.idom.Verifier;

/**
 * The XML relevant utilities.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @see com.potix.idom.Element
 */
public class XMLs {
	/** Converting a name to a valid XML name.
	 * Note: it is not reversible.
	 */
	public static final String toXMLName(String name) {
		if (name == null || name.length() == 0)
			return "_";

		StringBuffer sb = null;
		for (int j = 0, len = name.length(); j < len; ++j) {
			final char cc = name.charAt(j);
			if ((j == 0 && !Verifier.isXMLNameStartCharacter(cc))
			|| cc == ':' || !Verifier.isXMLNameCharacter(cc)) {
				if (sb == null) {
					sb = new StringBuffer(len + 8);
					if (j > 0) sb.append(name.substring(0, j));
				}
				sb.append('_').append(Integer.toHexString(cc));
			} else if (sb != null) {
				sb.append(cc);
			}
		}
		return sb != null ? sb.toString(): name;
	}

	/** Encodes a value such that it could be used as XML attribute.
	 */
	public static final String encodeAttribute(String value) {
		StringBuffer sb = null;
		for (int j = 0, len = value.length(); j < len; ++j) {
			final char cc = value.charAt(j);
			final String rep;
			switch (cc) {
			case '"': rep = "&quot;"; break;
			case '&': rep = "&amp;"; break;
			default:
				if (sb != null) sb.append(cc);
				continue;
			}

			if (sb == null) {
				sb = new StringBuffer(len + 8);
				if (j > 0) sb.append(value.substring(0, j));
			}
			sb.append(rep);
		}
		return sb != null ? sb.toString(): value;
	}

	/** Encodes a value such that it could be enclosed by a XML elemnt.
	 *
	 * <p>Note: It is sometime inproper to use CDATA if the text contains
	 * CDATA, too. The simplest way is NOT to use CDATA but encoding
	 * the string by this method.
	 */
	public static final String encodeText(String value) {
		StringBuffer sb = null;
		for (int j = 0, len = value.length(); j < len; ++j) {
			final char cc = value.charAt(j);
			final String rep;
			switch (cc) {
			case '<': rep = "&lt;"; break;
			case '>': rep = "&gt;"; break;
			case '&': rep = "&amp;"; break;
			default:
				if (sb != null) sb.append(cc);
				continue;
			}

			if (sb == null) {
				sb = new StringBuffer(len + 8);
				if (j > 0) sb.append(value.substring(0, j));
			}
			sb.append(rep);
		}
		return sb != null ? sb.toString(): value;
	}
	/** Encodes a value and appends it to a string buffer,
	 * such that it could be enclosed by a XML elemnt.
	 *
	 * <p>Note: It is sometime inproper to use CDATA if the text contains
	 * CDATA, too. The simplest way is NOT to use CDATA but encoding
	 * the string by this method.
	 */
	public static final
	StringBuffer encodeText(StringBuffer sb, String value) {
		if (sb == null) sb = new StringBuffer(value.length());
		for (int j = 0, len = value.length(); j < len; ++j) {
			final char cc = value.charAt(j);
			final String rep;
			switch (cc) {
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '&': sb.append("&amp;"); break;
			default: sb.append(cc); break;
			}
		}
		return sb;
	}
}
