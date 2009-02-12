/* XMLs.java

{{IS_NOTE

Purpose: 
Description: 
History:
91/01/07 17:36:07, Create, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xml;

import org.zkoss.idom.Verifier;

/**
 * The XML relevant utilities.
 *
 * @author tomyeh
 * @see org.zkoss.idom.Element
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
			case '"': rep = "&quot;"; break;
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
			case '"': sb.append("&quot;"); break;
			default: sb.append(cc); break;
			}
		}
		return sb;
	}

	/** Encodes a string that special characters are quoted to be compatible
	 * with HTML/XML.
	 * For example, &lt; is translated to &amp;lt;.
	 *
     *    &amp; -> &amp;amp;<br/>
     *    &lt; -> &amp;lt;<br/>
     *    &gt; -> &amp;gt;<br/>
     *    " -> &amp;#034;<br/>
     *    ' -> &amp;#039;<br/>
	 *
	 * @param s the string to quote; null is OK
	 * @return the escaped string, or an empty string if s is null
	 */
	public static final String escapeXML(String s) {
		if (s == null) return "";
		final StringBuffer sb = new StringBuffer(s.length() + 16);
		for (int j = 0, len = s.length(); j < len; ++j) {
			final char cc = s.charAt(j);
			final String esc = escapeXML(cc);
			if (esc != null) sb.append(esc);
			else sb.append(cc);
		}
		return s.length() == sb.length() ? s: sb.toString();
	}
	/** Enscapes a character into a string if it is a special XML character,
	 * returns null if not a special character.
	 *
     *    &amp; -> &amp;amp;<br/>
     *    &lt; -> &amp;lt;<br/>
     *    &gt; -> &amp;gt;<br/>
     *    " -> &amp;#034;<br/>
     *    ' -> &amp;#039;<br/>
	 */
	public static final String escapeXML(char cc) {
		switch (cc) {
		case '"': return "&#034;";
		case '\'': return "&#039;";
		case '>': return "&gt;";
		case '<': return "&lt;";
		case '&': return "&amp;";
		}
		return null;
	}}
