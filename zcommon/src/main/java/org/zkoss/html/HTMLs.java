/* HTMLs.java

	Purpose:

	Description:

	History:
		Sat Dec 31 12:46:27     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.html;

import java.util.HashSet;
import java.util.Set;

import org.owasp.encoder.Encode;

import org.zkoss.xml.XMLs;

/**
 * Utilities for HTML attributes and styles.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class HTMLs {
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 * If val is null or empty (if String), nothing is generated.
	 *
	 * <p>Note: {@link XMLs#encodeAttribute} is called automatically
	 * to encode val.
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, String val) {
		if (val != null && val.length() != 0)
			sb.append(' ').append(name).append("=\"")
				.append(Encode.forHtmlAttribute(val)).append('"');
	}
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 * If emptyIgnored is true and val is null or empty (if String),
	 * nothing is generated.
	 *
	 * <p>Note: {@link XMLs#encodeAttribute} is called automatically
	 * to encode val.
	 *
	 * @param emptyIgnored whether to ignore a null or empty string.
	 * If false, it is always generated (null is generated as "null").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, String val,
	boolean emptyIgnored) {
		if (!emptyIgnored || (val != null && val.length() != 0))
			sb.append(' ').append(name).append("=\"")
				.append(val != null ? Encode.forHtmlAttribute(val) : null)
				.append('"');
	}
	/** Appends an attribute with a int value to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, int val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends an attribute with a long value to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, long val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends an attribute with a long value to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, double val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends an attribute with a short value to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, short val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, boolean val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends a style value to the string buffer for HTML/XML (name:"val";).
	 * If val is null or empty (if String), nothing is generated.
	 */
	public static final
	void appendStyle(StringBuffer sb, String name, String val) {
		if (val != null && val.length() != 0)
			sb.append(name).append(':').append(val).append(';');
	}

	/** Returns the position of the specified substyle, or -1 if not found.
	 *
	 * @param style the style
	 * @param substyle the sub-style, e.g., display.
	 * @exception IllegalArgumentException if style is null, or substyle is null
	 * or empty.
	 */
	public static final int getSubstyleIndex(String style, String substyle) {
		if (style == null || substyle == null)
			throw new IllegalArgumentException("null");
		if (substyle.length() == 0)
			throw new IllegalArgumentException("empty substyle");

		for (int j = 0, len = style.length();;) {
			int k = -1, l = j;
			for (; l < len; ++l) {
				final char cc = style.charAt(l);
				if (k < 0  && cc == ':') k = l; //colon found
				else if (cc == ';') break; //done
			}

			final String nm =
				(k >= 0 ? style.substring(j, k): style.substring(j, l)).trim();
			if (nm.equals(substyle)) return j;
			if (l >= len) return -1;
			j = l + 1;
		}
	}
	/** Returns the value starting at the specified index (never null).
	 *
	 * <p>Note: the index is usually the returned vale of {@link #getSubstyleIndex}.
	 *
	 * @param style the style
	 * @param j the index that the substyle starts at (including the style's name)
	 */
	public static final String getSubstyleValue(final String style, int j) {
		final int len = style.length();
		int k = -1, l = j;
		for (; l < len; ++l) {
			final char cc = style.charAt(l);
			if (k < 0  && cc == ':') k = l; //colon found
			else if (cc == ';') break; //done
		}

		return k < 0 ? "": style.substring(k + 1, l).trim();
	}

	/** Retrieves text relevant CSS styles.
	 *
	 * <p>For example, if style is
	 * "border: 1px solid blue; font-size: 10px; padding: 3px; color: black;",
	 * then "font-size: 10px;color: black;" is returned.
	 *
	 * @return null if style is null. Otherwise, it never returns null.
	 */
	public static final String getTextRelevantStyle(final String style) {
		if (style == null) return null;
		if (style.length() == 0) return "";

		final StringBuffer sb = new StringBuffer(64);
		for (int j = 0, len = style.length();;) {
			int k = -1, l = j;
			for (; l < len; ++l) {
				final char cc = style.charAt(l);
				if (k < 0  && cc == ':') k = l; //colon found
				else if (cc == ';') break; //done
			}

			final String nm =
				(k >= 0 ? style.substring(j, k): style.substring(j, l)).trim();
			if (nm.startsWith("font")  || nm.startsWith("text")
			|| _txtstyles.contains(nm))
				sb.append(
					l < len ? style.substring(j, l + 1): style.substring(j));

			if (l >= len) return sb.toString();
			j = l + 1;
		}
	}
	private final static Set<String> _txtstyles;
	static {
		final String[] txts = {
			"color", "background-color", "background", "white-space"
		};
		_txtstyles = new HashSet<String>();
		for (int j = txts.length; --j >=0;)
			_txtstyles.add(txts[j]);
	}

	/** Returns whether the specified tag is an 'orphan' tag.
	 * By orphan we mean it doesn't support the format of
	 * &lt;xx&gt; &lt;/xx&gt;.
	 *
	 * <p>For example, br and img are orphan tags.
	 *
	 * @param tagname the tag name, e.g., br and tr.
	 */
	public static final boolean isOrphanTag(String tagname) {
		return _orphans.contains(tagname.toLowerCase(java.util.Locale.ENGLISH));
	}
	/** A set of tags that don't have child. */
	private static final Set<String> _orphans = new HashSet<String>(29);
	static {
		final String[] orphans = {
			"area", "base", "basefont", "bgsound", "br",
			"col", "embed", "hr", "img", "input",
			"isindex", "keygen", "link", "meta", "plaintext",
			"spacer", "wbr"
		};
		for (int j = orphans.length; --j >= 0;)
			_orphans.add(orphans[j]);
	}

	// Since 7.0.2
	// The following implementation for encoding JavaScript is referred from
	// https://code.google.com/p/owasp-esapi-java/
	// which is licensed under New BSD License - http://opensource.org/licenses/BSD-3-Clause
	private static char[] IMMUNE_JAVASCRIPT = { ',', '.', '_' };
	private static final String[] hex = new String[256];

	private static boolean containsCharacter(char c, char[] array) {
		for (char ch : array) {
			if (c == ch)
				return true;
		}
		return false;
	}

	static {
		for (char c = 0; c < 0xFF; c++) {
			if (c >= 0x30 && c <= 0x39 || c >= 0x41 && c <= 0x5A || c >= 0x61
					&& c <= 0x7A) {
				hex[c] = null;
			} else {
				hex[c] = toHex(c).intern();
			}
		}
	}

	private static String toHex(char c) {
		return Integer.toHexString(c);
	}

	private static String getHexForNonAlphanumeric(char c) {
		if (c < 0xFF)
			return hex[c];
		return toHex(c);
	}

	/**
	 * Encodes the JavaScript content for <a href=
	 * "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet#RULE_.233_-_JavaScript_Escape_Before_Inserting_Untrusted_Data_into_JavaScript_Data_Values"
	 * > XSS vulnerabilities</a>, the implementation is referred from <a
	 * href="https://code.google.com/p/owasp-esapi-java/">owasp-esapi-java</a>
	 * <p>
	 * Returns backslash encoded numeric format. Does not use backslash
	 * character escapes such as, \" or \' as these may cause parsing problems.
	 * For example, if a javascript attribute, such as onmouseover, contains a
	 * \" that will close the entire attribute and allow an attacker to inject
	 * another script attribute.
	 *
	 * @since 7.0.2
	 * @deprecated as of release 10.1.0, replaced by {@link org.owasp.encoder.Encode#forJavaScript(String)}
	 */
	public static String encodeJavaScript(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			sb.append(encodeCharacter(IMMUNE_JAVASCRIPT, c));
		}
		return sb.toString();
	}

	/**
	 * Encodes the JavaScript content for <a href=
	 * "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet#RULE_.233_-_JavaScript_Escape_Before_Inserting_Untrusted_Data_into_JavaScript_Data_Values"
	 * > XSS vulnerabilities</a>, the implementation is referred from <a
	 * href="https://code.google.com/p/owasp-esapi-java/">owasp-esapi-java</a>
	 * <p>
	 * Returns backslash encoded numeric format. Does not use backslash
	 * character escapes such as, \" or \' as these may cause parsing problems.
	 * For example, if a javascript attribute, such as onmouseover, contains a
	 * \" that will close the entire attribute and allow an attacker to inject
	 * another script attribute.
	 *
	 * @since 7.0.2
	 * @deprecated as of release 10.1.0, replaced by {@link org.owasp.encoder.Encode#forJavaScript(String)}
	 */
	public static String encodeCharacter(char[] immune, Character c) {

		// check for immune characters
		if (containsCharacter(c, immune)) {
			return "" + c;
		}

		// check for alphanumeric characters
		String hex = getHexForNonAlphanumeric(c);
		if (hex == null) {
			return "" + c;
		}

		// Do not use these shortcuts as they can be used to break out of a
		// context
		// if ( ch == 0x00 ) return "\\0";
		// if ( ch == 0x08 ) return "\\b";
		// if ( ch == 0x09 ) return "\\t";
		// if ( ch == 0x0a ) return "\\n";
		// if ( ch == 0x0b ) return "\\v";
		// if ( ch == 0x0c ) return "\\f";
		// if ( ch == 0x0d ) return "\\r";
		// if ( ch == 0x22 ) return "\\\"";
		// if ( ch == 0x27 ) return "\\'";
		// if ( ch == 0x5c ) return "\\\\";

		// encode up to 256 with \\xHH
		String temp = Integer.toHexString(c);
		if (c < 256) {
			String pad = "00".substring(temp.length());
			return "\\x" + pad + temp.toUpperCase();
		}

		// otherwise encode with \\uHHHH
		String pad = "0000".substring(temp.length());
		return "\\u" + pad + temp.toUpperCase();
	}
}
