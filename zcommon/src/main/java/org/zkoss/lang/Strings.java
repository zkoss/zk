/* Strings.java


	Purpose: String utilities and constants
	Description:
	History:
	 2001/4/17, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.zkoss.mesg.MCommon;
import org.zkoss.util.IllegalSyntaxException;

/**
 * String utilities and constants
 *
 * @author tomyeh
 */
public class Strings {
	/** Used with {@link #escape} to escape a string in
	 * JavaScript. It assumes the string will be enclosed with a single quote.
	 */
	public static final String ESCAPE_JAVASCRIPT = "'\n\r\t\f\\/!";
	public static final String EMPTY = "";
	/**
	 * Returns true if the string is null or empty.
	 */
	public static final boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	/**
	 * Returns true if the string is null or empty or pure blank.
	 */
	public static final boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}
	/** Trims the string buffer by removing the leading and trailing
	 * whitespaces.
	 * Note: unlike String.trim(), you can specify the index of
	 * the first character to start trimming.
	 *
	 * <p>Like String.trim(), a whitespace is a character that has
	 * a code not great than <code>'&#92;u0020'</code> (the space character)
	 *
	 * @param buf the string buffer to trim the leading and trailing.
	 * @param index the index of the first character to trim
	 * (i.e., consider as the leading character).
	 * If 0, it starts from the beginning.
	 * @return the same string buffer
	 * @since 3.0.4
	 */
	public static final StringBuffer trim(StringBuffer buf,
	final int index) {
		for (int j = index, len = buf.length();; ++j) {
			if (j >= len) {
				buf.delete(index, len);
				break; //done
			}

			char cc = buf.charAt(j);
			if (cc > ' ') { //same as String.trim()
				buf.delete(index, j);

				for (len = j = buf.length(); --j >= index;) {
					cc = buf.charAt(j);
					if (cc > ' ') { //same as String.trim()
						buf.delete(j + 1, len);
						break;
					}
				}
				break; //done
			}
		}
		return buf;
	}
	/** Returns an encoded string buffer, faster and shorter than
	 * Integer.toHexString. It uses numbers and lower-case letters only.
	 * Thus it is a valid variable name if prefix with an alphabet.
	 * At least one character is generated.
	 *
	 * <p>It works even in system that is case-insensitive, such as IE.
	 *
	 * <p>It is useful to generate a string to represent a number.
	 */
	public static final StringBuffer encode(StringBuffer sb, int val) {
		if (val < 0) {
			sb.append('z');
			val = -val;
		}

		do {
			int v = val & 31;
			if (v < 10) {
				sb.append((char)('0' + v));
			} else {
				sb.append((char)(v + ((int)'a' - 10)));
			}
		} while ((val >>>= 5) != 0);
		return sb;
	}
	/** Returns an encoded string buffer, faster and shorter than
	 * Long.toHexString. It uses numbers and lower-case letters only.
	 * Thus it is a valid variable name if prefix with an alphabet.
	 * At least one character is generated.
	 *
	 * <p>It works even in system that is case-insensitive, such as IE.
	 *
	 * <p>It is useful to generate a string to represent a number.
	 */
	public static final StringBuffer encode(StringBuffer sb, long val) {
		if (val < 0) {
			sb.append('z');
			val = -val;
		}

		do {
			int v = ((int)val) & 31;
			if (v < 10) {
				sb.append((char)('0' + v));
			} else {
				sb.append((char)(v + ((int)'a' - 10)));
			}
		} while ((val >>>= 5) != 0);
		return sb;
	}
	/** Returns an encoded string, faster and shorter than
	 * Long.toHexString.
	 */
	public static final String encode(int val) {
		return encode(new StringBuffer(12), val).toString();
	}
	/** Returns an encoded string, faster and shorter than
	 * Long.toHexString.
	 */
	public static final String encode(long val) {
		return encode(new StringBuffer(20), val).toString();
	}

	/** Returns the index of the give character in the given string buffer,
	 * or -1 if not found.
	 * It is equivalent to <code>sb.indexOf(""+cc, j);</code>, but faster.
	 * @since 5.0.3
	 */
	public static final int indexOf(StringBuffer sb, char cc, int j) {
		for (int len = sb.length(); j < len; ++j)
			if (sb.charAt(j) == cc)
				return j;
		return -1;
	}
	/** Returns the last index of the give character in the given string buffer,
	 * or -1 if not found.
	 * It is equivalent to <code>sb.lastIndexOf(""+cc, j);</code>, but faster.
	 * @since 5.0.3
	 */
	public static final int lastIndexOf(StringBuffer sb, char cc, int j) {
		if (j >= sb.length())
			j = sb.length() - 1;
		for (; j >= 0; --j)
			if (sb.charAt(j) == cc)
				return j;
		return -1;
	}

	/**
	 * Returns the index that is one of delimiters, or the length if none
	 * of delimiter is found.
	 *
	 * <p>Unlike String.indexOf(String, int), this method returns the first
	 * occurrence of <i>any</i> character in the delimiters.
	 *
	 * <p>This method is optimized to use String.indexOf(char, int)
	 * if it found the length of delimiter is 1.
	 *
	 * @param src the source string to search
	 * @param from the index to start the search from
	 * @param delimiters the set of characters to search for
	 *
	 * @return the index that is one of delimiters.
	 * If return &gt;= src.length(), it means no such delimiters
	 * @see #lastAnyOf
	 */
	public static final int anyOf(String src, String delimiters, int from) {
		switch (delimiters.length()) {
		case 0:
			return src.length();
		case 1:
			final int j = src.indexOf(delimiters.charAt(0), from);
			return j >= 0 ? j: src.length();
		}

		for (int len = src.length();
		from < len && delimiters.indexOf(src.charAt(from)) < 0; ++from)
			;
		return from;
	}
	/**
	 * The backward version of {@link #anyOf}.
	 *
	 * <p>This method is optimized to use String.indexOf(char, int)
	 * if it found the length of delimiter is 1.
	 *
	 * @return the previous index that is one of delimiter.
	 * If it is negative, it means no delimiter in front of
	 * <code>from</code>
	 * @see #anyOf
	 */
	public static final int lastAnyOf(String src, String delimiters, int from) {
		switch (delimiters.length()) {
		case 0:
			return -1;
		case 1:
			return src.lastIndexOf(delimiters.charAt(0), from);
		}

		int len = src.length();
		if (from >= len)
			from = len - 1;
		for (; from >= 0 && delimiters.indexOf(src.charAt(from)) < 0; --from)
			;
		return from;
	}
	/**
	 * Returns the next index after skipping whitespaces.
	 */
	public static final int skipWhitespaces(CharSequence src, int from) {
		for (final int len = src.length();
		from < len && Character.isWhitespace(src.charAt(from)); ++from)
			;
		return from;
	}
	/**
	 * The backward version of {@link #skipWhitespaces}.
	 *
	 * @return the next index that is not a whitespace.
	 * If it is negative, it means no whitespace in front of it.
	 */
	public static final int skipWhitespacesBackward(CharSequence src, int from) {
		final int len = src.length();
		if (from >= len)
			from = len - 1;
		for (; from >= 0 && Character.isWhitespace(src.charAt(from)); --from)
			;
		return from;
	}
	/** Returns the next whitespace.
	 */
	public static final int nextWhitespace(CharSequence src, int from) {
		for (final int len = src.length();
		from < len && !Character.isWhitespace(src.charAt(from)); ++from)
			;
		return from;
	}

	/** Escapes (a.k.a, quote) the special characters with backslash.
	 * It prefix a backslash to any characters specified in the specials
	 * argument.
	 *
	 * <p>Note: specials usually contains '\\'.
	 *
	 * <p>For example, {@link org.zkoss.util.Maps#parse} will un-quote
	 * backspace. Thus, if you want to preserve backslash, you have
	 * invoke escape(s, "\\") before calling Maps.parse().
	 *
	 * @param src the string to process. If null, null is returned.
	 * @param specials a string of characters that shall be escaped/quoted
	 * To escape a string in JavaScript code snippet, you can use {@link #ESCAPE_JAVASCRIPT}.
	 * @see #unescape
	 */
	public static final String escape(String src, String specials) {
		if (src == null) return null; // Return null if src is null
		final char[] chars = src.toCharArray(); // Convert source string to char array
		StringBuilder sb = new StringBuilder(src.length()); // Use StringBuilder for efficiency

		int k = 0;
		for (char c : chars) {
			if (shallEncodeUnicode(c, specials)) { // Check if it should be Unicode encoded
				String encoded = encodeUnicode(c);
				sb.append('\\').append(encoded); // Append encoded form with a backslash
			} else if (specials.indexOf(c) >= 0) { // Check if char is a special character to escape
				char escaped = escapeSpecial(src, c, k, specials);
				if (escaped != (char) 0) {
					sb.append('\\').append(escaped); // Append escaped character with a backslash
				} else {
					sb.append(c); // Append character as is
				}
			} else {
				sb.append(c); // Append character as is
			}
			k++;
		}

		return sb.toString(); // Return the string from StringBuilder
	}

	private static char escapeSpecial(CharSequence src,
	char cc, int k, String specials) {
		switch (cc) {
		case '\n': return 'n';
		case '\t': return 't';
		case '\r': return 'r';
		case '\f': return 'f';
		case '/':
			String key;
			//escape </script>
			if (ESCAPE_JAVASCRIPT.equals(specials) // handle it specially
					&& (k <= 0 || src.charAt(k - 1) != '<'
							|| k + 8 > src.length() || !("script>"
							.equalsIgnoreCase((key = src.subSequence(k + 1,
									k + 8).toString())) || "script "
							.equalsIgnoreCase(key)))) {
				return (char) 0; // don't escape
			}
			break;
		case '!':
			//escape <!-- (ZK-676: it causes problem if used with <script>)
			if (ESCAPE_JAVASCRIPT.equals(specials) //handle it specially
			&& (k <= 0 || src.charAt(k - 1) != '<' || k + 3 > src.length()
				|| !"--".equals(src.subSequence(k+1, k+3)))) {
				return (char)0; //don't escape
			}
			break;
		}
		return cc;
	}
	/** Escapes (a.k.a. quote) the special characters with backslash
	 * and appends it the specified string buffer.
	 *
	 * @param dst the destination buffer to append to.
	 * @param src the source to escape from.
	 * @param specials a string of characters that shall be escaped/quoted
	 * To escape a string in JavaScript code snippet, you can use {@link #ESCAPE_JAVASCRIPT}.
	 * @since 5.0.0
	 */
	public static final
	StringBuffer escape(StringBuffer dst, CharSequence src, String specials) {
		if (src == null)
			return dst;

		for (int j = 0, j2 = 0, len = src.length();;) {
			String enc = null;
			char cc;
			int k = j2;
			for (;; ++k) {
				if (k >= len)
					return dst.append((Object)src.subSequence(j, src.length()));

				cc = src.charAt(k);
				if (shallEncodeUnicode(cc, specials)) {
					enc = encodeUnicode(cc);
					break;
				}
				if (specials.indexOf(cc) >= 0)
					break;
			}

			if (enc == null
			&& (cc = escapeSpecial(src, cc, k, specials)) == (char)0) {
				j2 = k + 1;
				continue;
			}

			dst.append((Object)src.subSequence(j, k)).append('\\');
			if (enc != null) dst.append(enc);
			else dst.append(cc);
			j2 = j = k + 1;
		}
	}

	/** Escapes (a.k.a. quote) the special characters with backslash
	 * and appends it the specified string buffer.
	 *
	 * @param dst the destination buffer to append to.
	 * @param src the source to escape from.
	 * @param specials a string of characters that shall be escaped/quoted
	 * To escape a string in JavaScript code snippet, you can use {@link #ESCAPE_JAVASCRIPT}.
	 * @since 8.0.0
	 */
	public static final
	StringBuilder escape(StringBuilder dst, CharSequence src, String specials) {
		if (src == null)
			return dst;
		String str = src.toString();

		char[] chars = str.toCharArray();
		for (int j = 0, j2 = 0, len = chars.length;;) {
			String enc = null;
			char cc;
			int k = j2;
			for (;; ++k) {
				if (k >= len) {
					return dst.append(Arrays.copyOfRange(chars, j, len));
				}

				cc = chars[k];
				if (shallEncodeUnicode(cc, specials)) {
					enc = encodeUnicode(cc);
					break;
				}
				if (specials.indexOf(cc) >= 0)
					break;
			}

			if (enc == null
					&& (cc = escapeSpecial(src, cc, k, specials)) == (char)0) {
				j2 = k + 1;
				continue;
			}

			dst.append(Arrays.copyOfRange(chars, j, k)).append('\\');
			if (enc != null) dst.append(enc);
			else dst.append(cc);
			j2 = j = k + 1;
		}
	}
	/** Escapes (a.k.a. quote) the special characters with backslash.
	 * <p>Note: this implementation is referred from <a href="https://github.com/unbescape/unbescape">unbescape</a></p>
	 * @since 8.0.0
	 */
	public static final String escapeJavaScript(String text) {
		// We utilize the unbescape project's implementation to do the escape for Javascript value
		// which license is under Apache License 2.0 - https://github.com/unbescape/unbescape
		return JavaScriptEscape.escapeJavaScript(text);
	}

	private static final boolean shallEncodeUnicode(char cc, String specials) {
		return ESCAPE_JAVASCRIPT.equals(specials) && cc > (char)255
			&& !Character.isLetterOrDigit(cc);
			//don't check isSpaceChar since \u2028 will return true and it
			//is not recognized by Firefox
	}
	/** Return "u????". */
	private static final String encodeUnicode(int cc) {
		final StringBuilder sb = new StringBuilder(6)
			.append('u').append(Integer.toHexString(cc));
		while (sb.length() < 5)
			sb.insert(1, '0');
		return sb.toString();
	}


	/** Un-escape the quoted string.
	 * @see #escape
	 */
	public static final String unescape(String s) {
		if (s == null)
			return null;
		StringBuilder sb = null;
		int j = 0;
		for (int k; (k = s.indexOf('\\', j)) >= 0;) {
			if (sb == null)
				sb = new StringBuilder(s.length());

			char cc = s.charAt(k + 1);
			switch (cc) {
			case 'n': cc = '\n'; break;
			case 't': cc = '\t'; break;
			case 'r': cc = '\r'; break;
			case 'f': cc = '\f'; break;
			}
			sb.append(s.substring(j, k)).append(cc);
			j = k + 2;
		}
		if (sb == null)
			return s; //nothing changed
		return sb.append(s.substring(j)).toString();
	}

	/**
	 * Returns the substring from the <code>from</code> index up to the
	 * <code>until</code> character or end-of-string.
	 * Unlike String.subsring, it converts \f, \n, \t and \r. It doesn't
	 * handle u and x yet.
	 *
	 * @return the result (never null). Result.next is the position of
	 * the <code>until</code> character if found, or
	 * a number larger than length() if no such character.
	 */
	public static final Result substring(String src, int from, char until) {
		return substring(src, from, until, true);
	}
	/**
	 * Returns the substring from the <code>from</code> index up to the
	 * <code>until</code> character or end-of-string.
	 *
	 * @param escBackslash whether to treat '\\' specially (as escape char)
	 * It doesn't handle u and x yet.
	 * @return the result (never null). Result.next is the position of
	 * the <code>until</code> character if found, or
	 * a number larger than length() if no such character.
	 * You can tell which case it is by examining {@link Result#separator}.
	 */
	public static final
	Result substring(String src, int from, char until, boolean escBackslash) {
		final int len = src.length();
		final StringBuilder sb = new StringBuilder(len);
		for (boolean quoted = false; from < len; ++from) {
			char cc = src.charAt(from);
			if (quoted) {
				quoted = false;
				switch (cc) {
				case 'f': cc = '\f'; break;
				case 'n': cc = '\n'; break;
				case 'r': cc = '\r'; break;
				case 't': cc = '\t'; break;
				}
			} else if (cc == until) {
				break;
			} else if (escBackslash && cc == '\\') {
				quoted = true;
				continue; //skip it
			}
			sb.append(cc);
		}
		return new Result(from, sb.toString(), from < len ? until: (char)0);
	}

	/** Returns the next token with unescape.
	 * <ul>
	 * <li>It trims whitespaces before and after the token.</li>
	 * <li>It handles both '\'' and '"'. All characters between them are
	 * considered as a token.</li>
	 * <li>If nothing found before end-of-string, null is returned</li>
	 * </ul>
	 *
	 * If a separator is found, it is returned in
	 * {@link Strings.Result#separator}.
	 *
	 * @exception IllegalSyntaxException if the quoted string is unclosed.
	 */
	public static final
	Result nextToken(String src, int from, char[] separators)
	throws IllegalSyntaxException {
		return nextToken(src, from, separators, true, true, false);
	}
	/** Returns the next token with additional options.
	 * It is the same as nextToken(src, from, separators, escBackslash, quotAsToken, false);
	 * Refer to {@link #nextToken(String, int, char[], boolean, boolean, boolean)}
	 * for more information.
	 *
	 * @exception IllegalSyntaxException if the quoted string is unclosed.
	 * @see #nextToken(String, int, char[], boolean, boolean, boolean)
	 */
	public static final Result nextToken(String src, int from,
	char[] separators, boolean escBackslash, boolean quotAsToken)
	throws IllegalSyntaxException {
		return nextToken(src, from, separators, escBackslash, quotAsToken, false);
	}
	/** Returns the next token with additional options.
	 *
	 * <ul>
	 * <li>It trims whitespaces before and after the token.</li>
	 * <li>If quotAsToken is true, all characters between quotations
	 * ('\'' or '"') are considered as a token.</li>
	 * <li>If parenthesis is true, the separators and quotes inside
	 * a pair of parenthesis won't be treated specially.
	 * It is useful if EL expressions might be contained.</li>
	 * <li>Consider '\\' as the escape char if escBackslash is true.</li>
	 * <li>If nothing found before end-of-string, null is returned</li>
	 * </ul>
	 *
	 * If a separator is found, it is returned in
	 * {@link Strings.Result#separator}.
	 *
	 * @param escBackslash whether to treat '\\' specially (as escape char)
	 * It doesn't handle u and x yet.
	 * @param quotAsToken whether to treat characters inside '\'' or '"'
	 * as a token. Note: the quotes are excluded from the token.
	 * @param parenthesis whether to ignore separators and quotes inside
	 * a pair of parentheses. Recognized parentheses include
	 * {}, [] or ().
	 * @exception IllegalSyntaxException if the quoted string is unclosed.
	 * @since 3.0.6
	 */
	public static final Result nextToken(String src, int from,
	char[] separators, boolean escBackslash, boolean quotAsToken,
	boolean parenthesis)
	throws IllegalSyntaxException {
		final int len = src.length();
		from = skipWhitespaces(src, from);
		if (from >= len)
			return null; //end-of-string

		//1. handle quoted
		final char cc = src.charAt(from);
		if (quotAsToken && (cc == '\'' || cc == '"')) {
			final Result res = substring(src, from + 1, cc, escBackslash);
			if (res.separator != cc)
				throw new IllegalSyntaxException(MCommon.QUOTE_UNMATCHED, src);

			res.next = skipWhitespaces(src, res.next + 1);
			if (res.next < len && isSeparator(src.charAt(res.next), separators))
				++res.next;
			return res;
		}

		//2. handle not-quoted
		final int j = nextSeparator(src, from, separators,
			escBackslash, false, quotAsToken, parenthesis);
		int next = j;
		if (j < len) {
			if (quotAsToken) {
				final char c = src.charAt(j);
				if (c != '\'' && c != '"')
					++next;
			} else {
				++next;
			}
		}

		if (j == from) //nothing but separator
			return new Result(next, "", src.charAt(j));

		int k = 1 + skipWhitespacesBackward(src, j - 1);
		return new Result(next,
			k > from ? escBackslash ?
				unescape(src.substring(from, k)) : src.substring(from, k): "",
			j < len ? src.charAt(j): (char)0);
			//if the token is nothing but spaces, k < from
	}

	/** Returns the next separator index in the src string.
	 *
	 * @param escQuot whether to escape characters inside quotations
	 * ('\'' or '"'). In other words, ignore separators inside quotations
	 * @param quotAsSeparator whether to consider quotations as one of
	 * the separators
	 * @since 2.4.0
	 */
	public static int nextSeparator(String src, int from, char[] separators,
	boolean escBackslash, boolean escQuot, boolean quotAsSeparator) {
		return nextSeparator(src, from, separators, escBackslash, escQuot,
			quotAsSeparator, false);
	}
	/** Returns the next separator index in the src string.
	 *
	 * @param escQuot whether to escape characters inside quotations
	 * ('\'' or '"'). In other words, it specifies whether to ignore
	 * separators inside quotations
	 * @param quotAsSeparator whether to consider quotations as one of
	 * the separators.
	 * If escQuot is true, quotAsSeparator is ignored.
	 * @param parenthesis whether to ignore separators and quotes in side
	 * a pair of parentheses. Recognized parentheses include
	 * {}, [] or ().
	 * @since 3.0.6
	 */
	public static int nextSeparator(String src, int from, char[] separators,
	boolean escBackslash, boolean escQuot, boolean quotAsSeparator,
	boolean parenthesis) {
		boolean esc = false;
		char quot = (char)0, endparen;
		for (final int len = src.length(); from < len; ++from) {
			if (esc) {
				esc = false;
				continue;
			}

			final char cc = src.charAt(from);
			if (escBackslash && cc == '\\') {
				esc = true;
			} else if (quot != (char)0) {
				if (cc == quot)
					quot = (char)0;
			} else if (escQuot && (cc == '\'' || cc == '"')) {
				quot = cc;
			} else if ((quotAsSeparator && (cc == '\'' || cc == '"'))
			|| isSeparator(cc, separators)) {
				break;
			} else if (parenthesis
			&& (endparen = getEndingParenthesis(cc)) != (char)0) {
				from = skipParenthesis(src, from, cc, endparen);
				if (from >= len) break; //don't increase
			}
		}
		return from;
	}

	/**
	 * Joins the elements of the array into a single String separated by a comma.
	 * @param array
	 * @since 8.0.0
	 */
	public static final <T> String join(T[] array) {
		return join(array, ',');
	}
	/**
	 * Joins the elements of the array into a single String separated by the given separator.
	 * @param array
	 * @param separator
	 * @since 8.0.0
	 */
	public static final <T> String join(T[] array, char separator) {
		if (array == null) return null;
		return join(array, separator, 0, array.length);
	}
	/**
	 * Joins the elements of the array into a single String separated by the given separator
	 * from the startIndex to the endIndex.
	 * @param array
	 * @param separator
	 * @param startIndex
	 * @param endIndex
	 * @since 8.0.0
	 */
	public static final <T> String join(T[] array, char separator,
			int startIndex, int endIndex) {
		if (array == null)
			return null;
		final int items = endIndex - startIndex;
		if (items <= 0)
			return EMPTY;
		final StringBuilder buf = new StringBuilder(items * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * Converts the given byte[] to UTF-8 string, if possible. Or return with the default charset.
	 * <p>This is a method helper for JDK 5</p>
	 * @since 8.0.2
	 */
	public static final String toString(byte[] data) {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(data);
		}
	}

	/**
	 * Returns the passed value, or the default value if it is empty.
	 * @param value the value
	 * @param defaultValue the default value
	 * @return {@code value} if not empty. {@code defaultValue} if empty.
	 * @since 9.5.0
	 */
	public static final String defaultIfEmpty(String value, String defaultValue) {
		return isEmpty(value) ? defaultValue : value;
	}

	/** Returns the ending parenthesis (such as }),
	 * or (char)0 if cc is not the beginning parenthesis (such as {).
	 */
	private static final char getEndingParenthesis(char cc) {
		return cc == '{' ? '}': cc == '(' ? ')': cc == '[' ? ']': (char)0;
	}
	/** Skip the string enclosed by a pair of parenthesis and
	 * return index after the ending parenthesis.
	 * @param j the index of the starting parenthesis
	 */
	private static int skipParenthesis(String src, int j, char beg, char end) {
		for (int len = src.length(), depth = 0; ++j < len;) {
			final char cc = src.charAt(j);
			if (cc == '\\') ++j; //skip next
			else if (cc == beg) ++depth;
			else if (cc == end && --depth < 0)
				break;
		}
		return j;
	}
	private static final boolean isSeparator(char cc, char[] separators) {
		for (int j = 0; j < separators.length; ++j) {
			if (cc == separators[j]
			|| (separators[j] == ' ' && Character.isWhitespace(cc)))
				return true;
		}
		return false;
	}

	/** The result of {@link #substring}.
	 */
	public static class Result {
		/** The next index. */
		public int next;
		/** The converted string. */
		public String token;
		/** The separator found. If no separator but end-of-line found,
		 * ((char)0) is returned.
		 */
		public char separator;

		protected Result(int next, String token, char separator) {
			this.next = next;
			this.token = token;
			this.separator = separator;
		}
		protected Result(int next, char separator) {
			this.next = next;
			this.separator = separator;
		}
		//-- Object --//
		public String toString() {
			return "[next="+next+", token="+token+" separator="+separator+']';
		}
	}
}
