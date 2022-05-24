/* Maps.java


	Purpose: Utilities for Map
	Description: 
	History:
	 2001/4/25, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.MCommon;

/**
 * Utilities for process Map.
 *
 * @author tomyeh
 */
public class Maps {
	private static final Logger log = LoggerFactory.getLogger(Maps.class);

	/**
	 * Reads a property list (key and element pairs) from the input stream,
	 * by specifying the charset.
	 * <p>Like java.util.Properties, it translates \\u, \n, \r, \t and \f.
	 * However, it enhanced Properties as follows.
	 *
	 * <ul>
	 *  <li>It accepts any charset, not just 8859-1.</li>
	 *  <li>It uses a different syntax to let value spread over multiple
	 *  lines, described below.</li>
	 *  <li>Whitespace is trimmed around '=' and at the beginning of
	 * the key and the ending of the value.</li>
	 *  <li>Illegal lines are ignored (Properties.load considers it
	 * as a key with an empty value).</li>
	 *  <li>Only '=' is accepted as the separator of key and value.</li>
	 *  <li>Only '#' is accepted as comment lines.</li>
	 * </ul>
	 *
	 * <p>To spread a value over multiple lines, you could,
	 * unlike java.util.Properties.load, append '{' to the end of a line.
	 * Then, all the following lines are considered as part of a value,
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
	public final static void load(Map<? super String, ? super String> map, InputStream sm, String charset,
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
		final List<Integer> prefixes = new LinkedList<Integer>();
		try {
			String prefix = null;
			String line;
			for (int lno = 1; (line = in.readLine()) != null; ++lno) {
				int len = line.length();
				if (len == 0)
					continue;

				final int from = Strings.skipWhitespaces(line, 0);
				if (from < len && line.charAt(from) == '#')
					continue; //comment, ignore rest of line

				final Strings.Result res =
					Strings.nextToken(line, from, new char[] {'=', '{', '}'}, true, false);
				if (res == null)
					continue; //nothing found
				if (res.separator == (char)0) {
					if (res.token.length() > 0)
						log.warn(">>Igored: a key, "+res.token+", without value, line "+lno);
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
					final Integer i = prefixes.remove(prefixes.size() - 1); //pop
					prefix = prefixes.isEmpty() ?
						null: prefix.substring(0, prefix.length() - i.intValue());
					continue;
				}
				if (res.token.length() == 0) {
					log.warn(">>Ignored: wihout key, line "+lno);
					continue;
				}

	//			assert res.separator == '=': "Wrong separator: "+res.separator;
				final String val;
				String key = caseInsensitive ? res.token.toLowerCase(java.util.Locale.ENGLISH): res.token;
				int j = Strings.skipWhitespaces(line, res.next);
				int k = Strings.skipWhitespacesBackward(line, len - 1);
				if (j == k && line.charAt(k) == '{') { //pack multiple lines
					final StringBuffer sb = new StringBuffer();
					for (int lnoFrom = lno;;) {
						line = in.readLine();
						++lno;
						if (line == null){
							log.warn(
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
		} finally {
			in.close();
		}

		if (!prefixes.isEmpty())
			log.warn(">>Ignored: unclosed nesting '{': "+prefixes.size());
	}

	/**
	 * Reads a property list (key and element pairs) from the input stream,
	 * by specifying the charset.
	 */
	public final static void load(Map<? super String, ? super String> map, InputStream sm, String charset)
	throws IOException {
		load(map, sm, charset, false);
	}

	/** Reads a property list (key and element pairs) from the input stream,
	 * by detecting correct charset.
	 *
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 */
	public final static void load(Map<? super String, ? super String> map, InputStream sm,
	boolean caseInsensitive) throws IOException {
		load(map, sm, null, caseInsensitive);
	}
	/** Reads a property list (key and element pairs) from the input stream,
	 * by detecting correct charset.
	 */
	public final static void load(Map<? super String, ? super String> map, InputStream sm) throws IOException {
		load(map, sm, null, false);
	}

	/**
	 * Parses a string into a map.
	 * It is the same as<br/>
	 * <pre><code>parse(map, src, '=', separator, quote, false, false, false);</code></pre>
	 *
	 * @see #parse(Map, String, char, char, char, boolean, boolean)
	 * @see #parseMultiple
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, ? super String> 
	parse(Map<? super String, ? super String> map, String src, char separator, char quote)
	throws IllegalSyntaxException {
		return parse0(map, src, '=', separator, quote, false, false, false);
	}
	/**
	 * Parses a string into a map.
	 * It is the same as<br/>
	 * <pre><code>parse(map, src, '=', separator, quote, asValue, false, false);</code></pre>
	 *
	 * @see #parse(Map, String, char, char, char, boolean, boolean)
	 * @see #parseMultiple
	 * @since 2.4.0
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, ? super String> 
	parse(Map<? super String, ? super String> map, String src, char separator, char quote, boolean asValue)
	throws IllegalSyntaxException {
		return parse0(map, src, '=', separator, quote, asValue, false, false);
	}
	/**
	 * Parses a string into a map.
	 * It is the same as<br/>
	 * <pre><code>parse(map, src, pairSeparator, separator, quote, false, false, false);</code></pre>
	 *
	 * @see #parse(Map, String, char, char, char, boolean, boolean)
	 * @see #parseMultiple
	 * @since 7.0.1
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, ? super String> 
	parse(Map<? super String, ? super String> map, String src, char pairSeparator, char separator, char quote)
	throws IllegalSyntaxException {
		return parse0(map, src, pairSeparator, separator, quote, false, false, false);
	}
	/**
	 * Parses a string into a map.
	 * It is the same as<br/>
	 * <pre><code>parse(map, src, '=', separator, quote, asValue, parenthesis, false);</code></pre>
	 *
	 * @see #parse(Map, String, char, char, char, boolean, boolean)
	 * @see #parseMultiple
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, ? super String>
	parse(Map<? super String, ? super String> map,
	String src, char separator, char quote, boolean asValue,
	boolean parenthesis) throws IllegalSyntaxException {
		return parse0(map, src, '=', separator, quote, asValue, parenthesis, false);
	}
	/**
	 * Parse a string into a map.
	 * <p>If = is omitted, whether it is considered as a key with the null
	 * value or a value with the null key depends on
	 * the asValue argument. If true, it is considered as a value with
	 * the null key.
	 *
	 * <p>For example, if the following string is parsed with asValue=false:<br/>
	 * a12=12,b3,c6=abc=125,x=y
	 *
	 * <p>Then, a map with the following content is returned:<br/>
	 * ("a12", "12"), ("b3", null), ("c6", "abc=125"), ("x", "y")
	 *
	 * <p>Notice: only the first = after separator is meaningful,
	 * so you don't have to escape the following =.
	 * <p>Beside specifying the quote character, you could use backslash
	 * quote a single character (as Java does).
	 *
	 * @param map the map to put parsed results to; null to create a
	 * new hash map.
	 * @param src the string to parse
	 * @param pairSeparator the separator of key and value, e.g., '=' or ':'.
	 * @param separator the separator, e.g., ' ' or ','.

	 * @param quote the quote character to surround the value.
	 * Ignored if quote is (char)0.<br/>
	 * Since 3.6.0: if quote is (char)1, then both ' and " are accepted.</br>
	 * For example, a1='b c' will generate a map entry, ("a1", "b c") if
	 * quote is '\''.<br/>
	 * Note: the quote is taken off before storing to the map.<br/>
	 * Note: the quote can <i>not</i> be used to surround the name

	 * @param asValue whether to consider the substring without = as
	 * a value (with the null key), or as a key (with the null value)
	 * For example, a12 is considered as a key if asValue is false.
	 * However, if you surround it with a quote, it is always considered
	 * as a value. For example, 'a12' is considered as a value no matter
	 * asValue is true or not.

	 * @param parenthesis whether to parse parenthesis in the value, {}, () and [].
	 * If true, the separator is ignored inside the parenthesis.
	 * Specify true if the value might contain EL expressions.
	 * Note: it has no effect to the name.

	 * @return the map being generated.
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see CollectionsX#parse
	 * @see #toString(Map, char, char)
	 * @since 7.0.1
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, ? super String>
	parse(Map<? super String, ? super String> map,
	String src,char pairSeparator, char separator, char quote, boolean asValue,
	boolean parenthesis) throws IllegalSyntaxException {
		return parse0(map, src, pairSeparator, separator, quote, asValue, parenthesis, false);
	}
	/**
	 * Parse a string into a map that allows multiple values..
	 * It is the same as<br/>
	 * <pre><code>parseMultiple(map, src, '=', separator, quote, asValue, parenthesis);</code></pre>
	 *
	 * @see #parseMultiple(Map, String, char, char, char, boolean, boolean)
	 * @see CollectionsX#parse
	 * @see #toString(Map, char, char, char)
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, Collection<String>>
	parseMultiple(Map<? super String, Collection<String>> map,
	String src, char separator, char quote, boolean asValue,
	boolean parenthesis) throws IllegalSyntaxException {
		return parse0(map, src, '=', separator, quote, asValue, parenthesis, true);
	}
	/**
	 * Parse a string into a map that allows multiple values.
	 * <p>Unlike {@link #parse}, it assumes there might be multiple values.
	 * Thus, the value of the map is a collection of String instances.
	 * For example, src is a1=x,a1=y, then
	 * the value for a1 is a list with two values, "x" and "y".
	 *
	 * <p>If = is omitted, whether it is considered as a key with the null
	 * value or a value with the null key depends on
	 * the asValue argument. If true, it is considered as a value with
	 * the null key.
	 *
	 * <p>For example, if the following string is parsed with asValue=false:<br/>
	 * a12=12,b3,c6=abc=125,x=y
	 *
	 * <p>Then, a map with the following content is returned:<br/>
	 * ("a12", "12"), ("b3", null), ("c6", "abc=125"), ("x", "y")
	 *
	 * <p>Notice: only the first = after separator is meaningful,
	 * so you don't have to escape the following =.
	 * <p>Beside specifying the quote character, you could use backslash
	 * quote a single character (as Java does).
	 *
	 * @param map the map to put parsed results to; null to create a
	 * new hash map.
	 * @param src the string to parse
	 * @param pairSeparator the separator of key and value, e.g., '=' or ':'.
	 * @param separator the separator, e.g., ' ' or ','.

	 * @param quote the quote character to surround the value.
	 * Ignored if quote is (char)0.<br/>
	 * Since 3.6.0: if quote is (char)1, then both ' and " are accepted.</br>
	 * For example, a1='b c' will generate a map entry, ("a1", "b c") if
	 * quote is '\''.<br/>
	 * Note: the quote is taken off before storing to the map.<br/>
	 * Note: the quote can <i>not</i> be used to surround the name

	 * @param asValue whether to consider the substring without = as
	 * a value (with the null key), or as a key (with the null value)
	 * For example, a12 is considered as a key if asValue is false.
	 * However, if you surround it with a quote, it is always considered
	 * as a value. For example, 'a12' is considered as a value no matter
	 * asValue is true or not.

	 * @param parenthesis whether to parse parenthesis in the value, {}, () and [].
	 * If true, the separator is ignored inside the parenthesis.
	 * Specify true if the value might contain EL expressions.
	 * Note: it has no effect to the name.

	 * @return the map being generated. The value of the map is
	 * a list of String objects (List&lt;String&gt;).
	 *
	 * @exception IllegalSyntaxException if syntax errors
	 * @see CollectionsX#parse
	 * @see #toString(Map, char, char, char)
	 * @since 7.0.1
	 */
	@SuppressWarnings("unchecked")
	public static final Map<? super String, Collection<String>>
	parseMultiple(Map<? super String, Collection<String>> map,
	String src, char pairSeparator, char separator, char quote, boolean asValue,
	boolean parenthesis) throws IllegalSyntaxException {
		return parse0(map, src, pairSeparator, separator, quote, asValue, parenthesis, true);
	}
	/** @deprecated As of release 6.0.0, replaced with
	 * {@link #parse(Map, String, char, char, boolean, boolean)} and
	 * {@link #parseMultiple}
	 */
	public static final Map
	parse(Map map, String src, char separator, char quote, boolean asValue,
	boolean multiple, boolean parenthesis) throws IllegalSyntaxException {
		return parse0(map, src, '=', separator, quote, asValue, parenthesis, multiple);
	}
	
	private static final Map
	parse0(Map map, String src, char pairSeparator, char separator, char quote, boolean asValue,
	boolean parenthesis, boolean multiple)
	throws IllegalSyntaxException {
		if (separator == (char)0)
			throw new IllegalArgumentException("Separator cannot be 0");
		if (map == null)
			map = new HashMap<String, String>();
		if (src == null || src.length() == 0)
			return map; //nothing to do

		//prepare delimiters for keys and values.
		final String delimValue, delimKey;
		final boolean sngldblquote = quote == (char)1;
		{	final StringBuffer delimsb =
				new StringBuffer().append(separator);

			if (sngldblquote) {
				delimsb.append("\"'");
				quote = '"';
			} else if (quote != (char)0) {
				delimsb.append(quote);
			}

			delimValue = delimsb.toString();
			delimKey = delimsb.append(pairSeparator).toString();
		}

		//parse
		for (int j = 0, len = src.length();;) {
			//handle name
			Token tk = next(src, delimKey, j, true, parenthesis);
//			if (log.finerable()) log.finer("name: "+tk.token+" "+tk.cc);
			j = tk.next;
			String name = tk.token;
			if (tk.cc == pairSeparator) {
				if (name.length() == 0)
					throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, tk.cc, src);
				++j; //skip =
			} else if( tk.cc == (char) 0) {
//				assert tk.next >= len;
				if (name.length() > 0)
					if (asValue) put(map, null, name, multiple);
					else put(map, name, null, multiple);
				return map;//done
			} else {
				if (quote != (char)0
				&& (tk.cc == quote || (sngldblquote && tk.cc == '\''))) {
					name = null;
					//value only
				} else {
					//If separator is ' ', tk.cc can be anything; see next()
					if ((separator != ' ' && tk.cc != separator)
					|| name.length() == 0)
						throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, tk.cc, src);
	
					if (asValue) put(map, null, name, multiple);
					else put(map, name, null, multiple);
					if (tk.cc == separator)
						++j; //skip separator
					continue;
				}
			}

			//handle value
			tk = next(src, delimValue, j, false, parenthesis);
//			if (log.finerable()) log.finer("value: "+tk.token+" "+tk.cc);
			j = tk.next;
			final String value = tk.token;
			if (quote != (char)0
			&& (tk.cc == quote || (sngldblquote && tk.cc == '\''))) {
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
				put(map, name, valsb.toString(), multiple);
				++j; //skip the closing ' or "
			} else {
				put(map, name, value, multiple);
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
	@SuppressWarnings("unchecked")
	private static void put(Map map, String name, String value, boolean multiple) {
		if (multiple) {
			List l = (List)map.get(name);
			if (l == null)
				map.put(name, l = new LinkedList());
			l.add(value);
		} else {
			map.put(name, value);
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
	next(String src, String delimiters, int j, boolean whitespaceAware,
	boolean parenthesis) {
		final StringBuffer tksb = new StringBuffer(64);
		final int len = src.length();
		j = Strings.skipWhitespaces(src, j);
		for (; j < len; ++j) {
			final char cc = src.charAt(j), endparen;
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
			} else if (parenthesis
			&& (endparen = getEndingParenthesis(cc)) != (char)0) {
				int k = skipParenthesis(src, j, cc, endparen);
				if (k >= len) k = len - 1; //endparen not found
				tksb.append(src.substring(j, k + 1));
				j = k;
			} else if (cc == (char)0) {
				throw newIllegalSyntaxException(MCommon.UNEXPECTED_CHARACTER, (char)0, src);
			} else {
				tksb.append(cc);
			}
		}
		return new Token(j, j < len ? src.charAt(j): (char)0, tksb.toString());
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
	private static final char escape(String src, int j) {
		if (j >= src.length())
			throw new IllegalSyntaxException(MCommon.ILLEGAL_CHAR, "\\");
		final char cc = src.charAt(j);
		return cc == 'n' ? '\n': cc == 't' ? '\t': cc;
	}
	/**
	 * Converts a map to a string.
	 *
	 * It is the same as<br/>
	 * <pre><code>toString(map, quote, separator, '=');</code></pre>
	 *
	 * @see #toString(Map, char, char, char)
	 * @see #parse(Map, String, char, char, char)
	 */
	public static final String toString(Map<? super String, ? super String> map, char quote, char separator) {
		return toString(map, quote, separator, '=');
	}
	/**
	 * Converts a map to a string.
	 *
	 * @param map the map to convert from
	 * @param quote the quotation character; 0 means no quotation surrounding
	 * the value
	 * @param separator the separator between two name=value pairs
	 * @param pairSeparator the separator between name and value
	 * @see #parse(Map, String, char, char, char)
	 * @since 7.0.1
	 */
	public static final String toString(Map<? super String, ? super String> map, char quote, char separator, char pairSeparator) {
		return toStringBuffer(new StringBuffer(64), map, quote, separator, pairSeparator)
			.toString();
	}
	/** Converts a map to string and append to a string buffer.
	 * 
	 * It is the same as<br/>
	 * <pre><code>toStringBuffer(sb, map, quote, separator, '=');</code></pre>
	 * 
	 * @see #toStringBuffer(StringBuffer, Map, char, char, char)
	 * @see #toString(Map, char, char, char)
	 */
	public static final StringBuffer
	toStringBuffer(StringBuffer sb, Map<? super String, ? super String> map, char quote, char separator) {
		return 	toStringBuffer(sb, map, quote, separator, '=');
	}
	/** Converts a map to string and append to a string buffer.
	 * @see #toString(Map, char, char, char)
	 * @since 7.0.1
	 */
	public static final StringBuffer
	toStringBuffer(StringBuffer sb, Map<? super String, ? super String> map, char quote, char separator, char pairSeparator) {
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
			escKey = escsb.append(pairSeparator).toString();
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
				sb.append(pairSeparator);
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
	/**
	 * Transfer {@code Set<<Map.Entry<K, V>>} to be serializable
	 * @since 8.0.0
	 */
	public static <K, V> Set<SerializableEntry<K, V>> transferToSerializableEntrySet(Set<Map.Entry<K, V>> entry) {
		Set<SerializableEntry<K, V>> newSet = new LinkedHashSet<SerializableEntry<K, V>>(entry.size());
		for(Map.Entry<K, V> me: entry) {
			newSet.add(new SerializableEntry<K, V>(me));
		}
		return newSet;
	}
	
	/**
	 * A serializable implement of Map.Entry
	 * @since 8.0.0
	 */
	public static class SerializableEntry<K,V> implements Map.Entry<K,V>, java.io.Serializable {
	    private static final long serialVersionUID = 20150121161520L;
	    private final K key;
	    private V val;
	
	    public SerializableEntry(K key, V val) {
	        this.key = key;
	        this.val = val;
	    }
	
	    public SerializableEntry(Map.Entry<K, V> entry) {
	        this.key = entry.getKey();
	        this.val = entry.getValue();
	    }

	    public K getKey() {
	        return key;
	    }
	
	    public V getValue() {
	        return val;
	    }
	
	    public V setValue(V value) {
	        V old = this.val;
	        this.val = value;
	        return old;
	    }
	
		public boolean equals(Map.Entry<K, V> obj) {
	        return eq(key, obj.getKey()) && eq(val, obj.getValue());
	    }
	
	    public int hashCode() {
	        return (key == null ? 0 : key.hashCode()) ^ (val == null ? 0 : val.hashCode());
	    }
	   
	    public String toString() {
	        return key + "=" + val;
	    }
	    
	    private static boolean eq(Object o1, Object o2) {
	        return o1 == null ? o2 == null : o1.equals(o2);
	    }
	}
}
