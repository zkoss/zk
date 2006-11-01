/* Maps.java

{{IS_NOTE

	Purpose: Utilities for Map
	Description: 
	History:
	 2001/4/25, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.MCommon;
import org.zkoss.util.logging.Log;

/**
 * Utilities for process Map.
 *
 * @author tomyeh
 */
public class Maps {
	private static final Log log = Log.lookup(Maps.class);

	/**
	 * Reads a property list (key and element pairs) from the input stream,
	 * by specifying the charset.
	 * <p>Like java.util.Properties, it translates \\u, \n, \r, \t and \f.
	 * However, it enhanced Properties as follows.
	 *
	 * <ul>
	 *  <li>It accepts any charset, not just 8859-1.</li>
	 *  <li>It uses a different syntax to let value spread over multiple
	 *  lines, descrubed below.</li>
	 *  <li>Whitespace is trimmed around '=' and at the beginning of
	 * the key and the ending of the value.</li>
	 *  <li>Illegal lines are ignored (Properties.load considers it
	 * as a key with an empty value).</li>
	 *  <li>Only '=' is accepted as the separator of key and value.</li>
	 *  <li>Only '#' is accepted as comment lines.</li>
	 * </ul>
	 *
	 * <p>To spead a value over multiple lines, you could,
	 * unlike java.util.Properties.load, append '{' to the end of a line.
	 * Then, all the following lines are considerred as part of a value,
	 * unless encountering a line containing only one '}'.<br>
	 * Example:
	 * <pre><code>abc = {
	 *line 1
	 *line 2
	 *}
	 *xyz = {
	 *line 1
	 *line 2
	 *}</code></pre>
	 *
	 * <p>Moreover, you could prefix a group of keys with certain prefix:
	 * <pre><code>org.zkoss.some. {
	 * a = aaa
	 * b = bbb
	 *}</code></pre>
	 *
	 * It actually defines two keys: "org.zkoss.some.a" and "org.zkoss.some.b".
	 *
	 * <p>Note: (1) whitespace in the {...} block are all preserved.<br>
	 * (2) if only whitespaces is between '=' and '{', they are ignored.
	 *
	 * @param charset the charset; if null, it detects UTF-16 BOM (0xfe 0xff
	 * or 0xff 0xfe). If no UTF-16 BOM, UTF-8 is always assumed.
	 * Note 1: UTF-8's BOM (0xef 0xbb 0xbf) is optional, so we don't count on it.
	 * Note 2: ISO-8859-1 is not used because we cannot tell its difference
	 * from UTF-8 (while some of our properties files are in UTF-8).
	 *
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 */
	public final static void load(Map map, InputStream sm, String charset,
	boolean caseInsensitive) throws IOException {
		final PushbackInputStream pis = new PushbackInputStream(sm, 3);
		if (charset == null || charset.startsWith("UTF")) {
			final byte[] ahead = new byte[3];
			int n = pis.read(ahead);
			if (n >= 2
			&& ((ahead[0] == (byte)0xfe && ahead[1] == (byte)0xff)
				|| (ahead[0] == (byte)0xff && ahead[1] == (byte)0xfe))) {
				charset = "UTF-16";
				//don't eat UTF-16 BOM, since Java use it to know endian
			} else if (n == 3 && ahead[0] == (byte)0xef
			&& ahead[1] == (byte)0xbb && ahead[2] == (byte)0xbf) {
				charset = "UTF-8";
				n = 0; //eat UTF-8 BOM since Java won't handle it
			} else if (charset == null) {
				charset = "UTF-8";
			}
			if (n > 0)
				pis.unread(ahead, 0, n);
		}

		final BufferedReader in =
			new BufferedReader(new InputStreamReader(pis, charset));

		final List prefixes = new LinkedList();
		String prefix = null;
		String line;
		for (int lno = 1; (line = in.readLine()) != null; ++lno) {
			int len = line.length();
			if (len == 0)
				continue;

			final Strings.Result res =
				Strings.nextToken(line, 0, new char[] {'=', '{', '}'}, true, false);
			if (res == null || res.token.startsWith("#"))
				continue; //nothing found
			if (res.separator == (char)0) {
				if (res.token.length() > 0)
					log.warning(">>Igored: a key, "+res.token+", without value, line "+lno);
				continue;
			}
			if (res.separator == '{') {
				//res.token.lenth() could be zero
				if (Strings.skipWhitespaces(line, res.next) < len) //non-space following '{'
					throw new IllegalSyntaxException("Invalid nest: '{' must be the last character, line "+lno);
				prefixes.add(new Integer(res.token.length()));
				prefix = prefix != null ? prefix + res.token: res.token;
				continue;
			}
			if (res.separator == '}' ) {
				if (Strings.skipWhitespaces(line, res.next) < len) //non-space following '}'
					throw new IllegalSyntaxException("Invalid nesting: '}' must be the last character, line "+lno);
				if (prefixes.isEmpty())
					throw new IllegalSyntaxException("Invalid nesting: '}' does have any preceding '{', line "+lno);
				final Integer i = (Integer)prefixes.remove(prefixes.size() - 1); //pop
				prefix = prefixes.isEmpty() ?
					null: prefix.substring(0, prefix.length() - i.intValue());
				continue;
			}
			if (res.token.length() == 0) {
				log.warning(">>Ignored: wihout key, line "+lno);
				continue;
			}

			assert res.separator == '=': "Wrong separator: "+res.separator;
			final String val;
			String key = caseInsensitive ? res.token.toLowerCase(): res.token;
			int j = Strings.skipWhitespaces(line, res.next);
			int k = Strings.skipWhitespacesBackward(line, len - 1);
			if (j == k && line.charAt(k) == '{') { //pack multiple lines
				final StringBuffer sb = new StringBuffer();
				for (int lnoFrom = lno;;) {
					line = in.readLine();
					++lno;
					if (line == null){
						log.warning(
							">>Ignored: invalid multiple-line format: '={' does not have following '}', "+lnoFrom);
						break;
					}

					len = line.length();
					if (len > 0) {
						j = Strings.skipWhitespacesBackward(line, len - 1);
						if (j >= 0 && line.charAt(j) == '}') {
							if (j > 0)
								j = 1 + Strings.skipWhitespacesBackward(line, j - 1);
							if (j == 0) //no non-space before }
								break;
						}
					}
					if (sb.length() > 0)
						sb.append('\n');
					sb.append(line);
				}
				val = sb.toString();
			} else {
				val = j <= k ? line.substring(j, k + 1): "";
			}
			map.put(prefix != null ? prefix + key: key, val);
		}

		if (!prefixes.isEmpty())
			log.warning(">>Ignored: unclosed nesting '{': "+prefixes.size());
	}

	/**
	 * Reads a property list (key and element pairs) from the input stream,
	 * by specifying the charset.
	 */
	public final static void load(Map map, InputStream sm, String charset)
	throws IOException {
		load(map, sm, charset, false);
	}

	/** Reads a property list (key and element pairs) from the input stream,
	 * by detecting correct charset.
	 *
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 */
	public final static void load(Map map, InputStream sm,
	boolean caseInsensitive) throws IOException {
		load(map, sm, null, caseInsensitive);
	}
	/** Reads a property list (key and element pairs) from the input stream,
	 * by detecting correct charset.
	 */
	public final static void load(Map map, InputStream sm) throws IOException {
		load(map, sm, null, false);
	}

	/**
	 * Parses a string into a map.
	 *
	 * <p>Example, the following string wil cause ("a12", "12"),
	 * ("b3", null), ("345", null), ("c6", "abc=125"), ("x", "y")
	 * if you specify quote as '\'':<br>
	 * a12=12,b3,345,c6=abc=125,x=y
	 *
	 * <p>Notice: only the first = after separator is meaningful,
	 * so you don't have to escape the following =.
	 * <p>Beside specifying the quote character, you could use back slash
	 * quote a single character (as Java does).
	 *
	 * @param map the map to put parsed results to; null to create a
	 * new hash map
	 * @param src the string to parse
	 * @param separator the separator, e.g., ' ' or ','.
	 * @param quote the quote character to surrounding value, e.g.,
	 * name = 'value'. If (char)0, no quotation is recognized.
	 * Notice: if value is an expression, it is better to specify (char)0
	 * because expression might contain strings.
	 * @return the map being passed in
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see CollectionsX#parse
	 * @see #toString(Map, char, char)
	 */
	public static final Map 
	parse(Map map, String src, char separator, char quote)
	throws IllegalSyntaxException {
		if (separator == (char)0)
			throw new IllegalArgumentException("Separator cannot be 0");
		if (map == null)
			map = new HashMap();
		if (src == null || src.length() == 0)
			return map; //nothing to do

		//prepare delimiters for keys and values.
		final String delimValue, delimKey;
		{	final StringBuffer delimsb =
				new StringBuffer().append(separator);
			if (quote != (char)0)
				delimsb.append(quote);
			delimValue = delimsb.toString();
			delimKey = delimsb.append('=').toString();
		}

		//parase
		for (int j = 0, len = src.length();;) {
			//handle name
			Token tk = next(src, delimKey, j, true);
			if (D.ON && log.finerable()) log.finer("name: "+tk.token+" "+tk.cc);
			j = tk.next;
			final String name = tk.token;
			switch (tk.cc) {
			case '=':
				if (name.length() == 0)
					throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, tk.cc, src);
				++j; //skip =
				break;
			case (char)0:
				assert tk.next >= len;
				if (name.length() > 0)
					map.put(name, null);
				return map;//done
			default:
				//If separator is ' ', tk.cc can be anything; see next()
				if ((separator != ' ' && tk.cc != separator)
				|| name.length() == 0)
					throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, tk.cc, src);

				map.put(name, null);
				if (tk.cc == separator)
					++j; //skip separator
				continue;
			}

			//handle value
			tk = next(src, delimValue, j, false);
			if (D.ON && log.finerable()) log.finer("value: "+tk.token+" "+tk.cc);
			j = tk.next;
			final String value = tk.token;
			if (quote != (char)0 && tk.cc == quote) {
				if (value.length() > 0)
					throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, tk.cc, src);

				final StringBuffer valsb = new StringBuffer(32);
				for (;;) {
					if (++j == len)
						throw newIllegalSyntaxException(MCommon.EXPECTING_CHARACTER, tk.cc, src);

					final char cc = src.charAt(j);
					if (cc == tk.cc)
						break;
					valsb.append(cc == '\\' ? escape(src, ++j): cc);
				}
				map.put(name, valsb.toString());
				++j; //skip the closing ' or "
			} else {
				map.put(name, value);
			}

			if (separator != ' ') {
				//If not ' ', ensure the following is a separator
				j = Strings.skipWhitespaces(src, j);
				if (j >= len)
					return map;
				if (src.charAt(j) != separator)
					throw newIllegalSyntaxException(MCommon.EXPECTING_CHARACTER, separator, src);
				++j; //skip separator
			}
		}
	}
	private static final IllegalSyntaxException
	newIllegalSyntaxException(int code, char cc, String src) {
		return new IllegalSyntaxException(code, new Object[] {new Character(cc), src});
	}
	private static class Token {
		/** The next position right after token. */
		private final int next;
		/** The character before next. */
		private final char cc;
		/** The token before next. */
		private final String token;
		private Token(int next, char cc, String token) {
			this.next = next;
			this.cc = cc;
			this.token = token;
		}
	}
	private static final Token
	next(String src, String delimiters, int j, boolean whitespaceAware) {
		final StringBuffer tksb = new StringBuffer(64);
		final int len = src.length();
		j = Strings.skipWhitespaces(src, j);
		for (; j < len; ++j) {
			final char cc = src.charAt(j);
			if (cc == '\\') {
				tksb.append(escape(src, ++j));
			} else if (delimiters.indexOf(cc) >= 0) {
				//note: cc might be a separator which might be a whitespace
				j = Strings.skipWhitespaces(src, j);
				break; //done
			} else if (Character.isWhitespace(cc)) {
				final int k = Strings.skipWhitespaces(src, j);
				//done if the following is nothing but whitespace or...
				if (whitespaceAware || k >= len
				|| delimiters.indexOf(src.charAt(k))>=0) {
					j = k;
					break; //done
				}
				if (j > k - 1) { //more than one whitespaces
					tksb.append(src.substring(j, k));
					j = k - 1; //j will increase by one later
				} else {
					tksb.append(cc);
				}
			} else if (cc == (char)0) {
				throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, (char)0, src);
			} else {
				tksb.append(cc);
			}
		}
		return new Token(j, j < len ? src.charAt(j): (char)0, tksb.toString());
	}
	private static final char escape(String src, int j) {
		if (j >= src.length())
			throw new IllegalSyntaxException(MCommon.ILLEGAL_CHAR, "\\");
		final char cc = src.charAt(j);
		return cc == 'n' ? '\n': cc == 't' ? '\t': cc;
	}
	/**
	 * Converts a map to a string.
	 *
	 * @param map the map to convert from
	 * @param quote the quotation character; 0 means no quotation surrunding
	 * the value
	 * @param separator the separator between two name=value pairs
	 * @see #parse(Map, String, char, char)
	 */
	public static final String toString(Map map, char quote, char separator) {
		return toStringBuffer(new StringBuffer(64), map, quote, separator)
			.toString();
	}
	/** Converts a map to string and append to a string buffer.
	 * @see #toString
	 */
	public static final StringBuffer
	toStringBuffer(StringBuffer sb, Map map, char quote, char separator) {
		if (separator == (char)0)
			throw new IllegalArgumentException("Separator cannot be 0");
		if (map.isEmpty())
			return sb; //nothing to do

		//prepare characters to escape with backslash.
		final String escKey, escValue;
		{	final StringBuffer escsb = new StringBuffer(12)
				.append(",\\'\" \n\t").append(separator);
			if (quote != (char)0 && quote != '\'' && quote != '"')
				escsb.append(quote);
			escValue = escsb.toString();
			escKey = escsb.append('=').toString();
		}

		//convert one-by-one
		for (final Iterator it = map.entrySet().iterator();
		it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();

			final Object key = me.getKey();
			if (key == null)
				throw new IllegalSyntaxException("Unable to encode null key: "+map);
			final String skey = key.toString();
			if (skey == null || skey.length() == 0)
				throw new IllegalSyntaxException(MCommon.EMPTY_NOT_ALLOWED, "key");
			encode(sb, skey, escKey);

			final Object val = me.getValue();
			if (val != null) {
				sb.append('=');
				if (quote != (char)0)
					sb.append(quote);
				encode(sb, val.toString(), escValue);
				if (quote != (char)0)
					sb.append(quote);
			}
			sb.append(separator);
		}
		sb.setLength(sb.length() - 1); //remove the last comma
		return sb;
	}
	private static final void
	encode(StringBuffer sb, String val, String escapes) {
		for (int j = 0, len = val.length();;) {
			final int k = Strings.anyOf(val, escapes, j);
			if (k >= len) {
				sb.append(val.substring(j));
				return;
			}
			sb.append(val.substring(j, k)).append('\\');
			final char cc = val.charAt(k);
			sb.append(cc == '\n' ? 'n': cc == '\t' ? 't': cc);
			j = k + 1;
		}
	}
}
