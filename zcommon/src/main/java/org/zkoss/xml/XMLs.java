/* XMLs.java


Purpose:
Description:
History:
91/01/07 17:36:07, Create, Tom M. Yeh

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xml;

import org.owasp.encoder.Encode;

import org.zkoss.idom.Verifier;

/**
 * The XML relevant utilities.
 *
 * @author tomyeh
 * @see org.zkoss.idom.Element
 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode}
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
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXmlAttribute(String)}
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

	/** Encodes a value such that it could be enclosed by an XML element.
	 *
	 * <p>Note: It is sometime improper to use CDATA if the text contains
	 * CDATA, too. The simplest way is NOT to use CDATA but encoding
	 * the string by this method.
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXmlContent(String)}
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
	 * such that it could be enclosed by an XML element.
	 *
	 * <p>Note: It is sometime improper to use CDATA if the text contains
	 * CDATA, too. The simplest way is NOT to use CDATA but encoding
	 * the string by this method.
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXmlContent(String)}
	 */
	public static final
	StringBuffer encodeText(StringBuffer sb, String value) {
		int len = value.length();
		if (sb == null) sb = new StringBuffer(len);
		for (int j = 0; j < len; ++j) {
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
	/** Encodes a value of the specified range,
	 * and appends it to a string buffer,
	 * such that it could be enclosed by an XML element.
	 *
	 * <p>Note: It is sometime improper to use CDATA if the text contains
	 * CDATA, too. The simplest way is NOT to use CDATA but encoding
	 * the string by this method.
	 *
	 * @param value the string to encode
	 * @param begin the beginning index, inclusive of the string to encode (i.e., value), included
	 * @param end the ending index, exclusive of the string to encode (i.e., value), excluded
	 * @since 5.0.0
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXmlContent(String)}
	 */
	public static final
	StringBuffer encodeText(StringBuffer sb, String value, int begin, int end) {
		if (end > value.length()) end = value.length();
		if (sb == null) sb = new StringBuffer(end - begin + 8);
		for (int j = begin; j < end; ++j) {
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
     *    &amp; -&gt; &amp;amp;<br/>
     *    &lt; -&gt; &amp;lt;<br/>
     *    &gt; -&gt; &amp;gt;<br/>
     *    " -&gt; &amp;#034;<br/>
     *    ' -&gt; &amp;#039;<br/>
	 *
	 * @param s the string to quote; null is OK
	 * @return the escaped string, or an empty string if s is null
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXml(String)}
	 */
	public static final String escapeXML(String s) {
		return Encode.forHtml(s);
	}
	/** Escapes a character into a string if it is a special XML character,
	 * returns null if not a special character.
	 *
     *    &amp; -&gt; &amp;amp;<br/>
     *    &lt; -&gt; &amp;lt;<br/>
     *    &gt; -&gt; &amp;gt;<br/>
     *    " -&gt; &amp;#034;<br/>
     *    ' -&gt; &amp;#039;<br/>
	 * @deprecated as of release 10.1.0, replaced with {@link org.owasp.encoder.Encode#forXml(String)}
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
