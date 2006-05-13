/* Strings.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/lang/Strings.java,v 1.20 2006/02/27 03:41:59 tomyeh Exp $
	Purpose: String utilities and constants
	Description:
	History:
	 2001/4/17, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.lang;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.potix.mesg.MCommon;
import com.potix.text.DateFormats;
import com.potix.util.Locales;
import com.potix.util.IllegalSyntaxException;

/**
 * String utilties and constants
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.20 $ $Date: 2006/02/27 03:41:59 $
 */
public class Strings {
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
		if (s == null)
			return true;

		for (int j = s.length(); --j >= 0;)
			if (s.charAt(j) != ' ')
				return false;
		return true;
	}
	/** Returns an encoded string buffer, faster and shorter than
	 * Integer.toHexString. It uses number and lower-case leters.
	 * Thus it is a valid variable name if prefix with an alphabet.
	 * At least one character is generated.
	 *
	 * <p>It works even in system that is case-insensitive, such as IE.
	 *
	 * <p>It is useful to generate a string to represent a number.
	 */
	public static final StringBuffer encode(StringBuffer sb, int val) {
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
	 * Long.toHexString. It uses alpanumeric and '_'.
	 * Thus it is a valid variable name if prefix with an alphabet.
	 * At least one character is generated.
	 *
	 * <p>It works even in system that is case-insensitive, such as IE.
	 *
	 * <p>It is useful to generate a string to represent a number.
	 */
	public static final StringBuffer encode(StringBuffer sb, long val) {
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

	/**
	 * Returns the index that is one of delimiters, or the length if none
	 * of delimiter is found.
	 *
	 * <p>Unlike String.indexOf(String, int), this method returns the first
	 * occurrence of <i>any</i> character in the delimiters.
	 *
	 * <p>This method is optimized to use String.indexOf(char, int)
	 * if it found the length of dilimiter is 1.
	 *
	 * @param src the source string to search
	 * @param from the index to start the search from
	 * @param delimiters the set of characters to search for
	 *
	 * @return the index that is one of delimiters.
	 * If return >= src.length(), it means no such delimiters
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
	 * if it found the length of dilimiter is 1.
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
	public static final int skipWhitespaces(String src, int from) {
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
	public static final int skipWhitespacesBackward(String src, int from) {
		final int len = src.length();
		if (from >= len)
			from = len - 1;
		for (; from >= 0 && Character.isWhitespace(src.charAt(from)); --from)
			;
		return from;
	}
	/** Returns the next whitespace.
	 */
	public static final int nextWhitespace(String src, int from) {
		for (final int len = src.length();
		from < len && !Character.isWhitespace(src.charAt(from)); ++from)
			;
		return from;
	}

	/** Escapes (aka, quote) the special characters with backslash.
	 * It prefix a backslash to any characters specfied in the specials
	 * argument.
	 *
	 * <p>Note: specials usually contains '\\'.	
	 *
	 * <p>For example, {@link com.potix.util.Maps#parse} will un-quote
	 * backspace. Thus, if you want to preserve backslash, you have
	 * invoke escape(s, "\\") before calling Maps.parse().
	 *
	 * @param s the string to process. If null, null is returned.
	 * @param specials a string of characters that shall be escaped/quoted
	 * @see #unescape
	 */
	public static final String escape(String s, String specials) {
		if (s == null)
			return null;

		StringBuffer sb = null;
		int j = 0;
		for (int k, len = s.length(); (k = anyOf(s, specials, j)) < len;) {
			if (sb == null)
				sb = new StringBuffer(len + 4);
			
			char cc = s.charAt(k);
			switch (cc) {
			case '\n': cc = 'n'; break;
			case '\t': cc = 't'; break;
			case '\r': cc = 'r'; break;
			case '\f': cc = 'f'; break;
			}
			sb.append(s.substring(j, k)).append('\\').append(cc);
			j = k + 1;
		}
		if (sb == null)
			return s; //nothing changed
		return sb.append(s.substring(j)).toString();
	}
	/** Escapes (aka. quote) the special characters with backslash
	 * and appends it the specified string buffer.
	 */
	public static final StringBuffer
	appendEscape(StringBuffer sb, String s, String specials) {
		if (s == null)
			return sb;

		for (int j = 0, len = s.length();;) {
			final int k = Strings.anyOf(s, specials, j);
			if (k >= len)
				return sb.append(s.substring(j));

			char cc = s.charAt(k);
			switch (cc) {
			case '\n': cc = 'n'; break;
			case '\t': cc = 't'; break;
			case '\r': cc = 'r'; break;
			case '\f': cc = 'f'; break;
			}
			sb.append(s.substring(j, k)).append('\\').append(cc);
			j = k + 1;
		}
	}
	/** Un-escape the quoted string.
	 * @see #escape
	 * @see #appendEscape
	 */
	public static final String unescape(String s) {
		if (s == null)
			return null;
		StringBuffer sb = null;
		int j = 0;
		for (int k; (k = s.indexOf('\\', j)) >= 0;) {
			if (sb == null)
				sb = new StringBuffer(s.length());

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
	 * Gets the substring from the <code>from</code> index up to the
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
	 * Gets the substring from the <code>from</code> index up to the
	 * <code>until</code> character or end-of-string.
	 *
	 * @param handleBackslash whether to treat '\\' specially (as escape char)
	 * It doesn't handle u and x yet.
	 * @return the result (never null). Result.next is the position of
	 * the <code>until</code> character if found, or
	 * a number larger than length() if no such character.
	 * You can tell which case it is by examining {@link Result#separator}.
	 */
	public static final
	Result substring(String src, int from, char until, boolean handleBackslash) {
		final int len = src.length();
		final StringBuffer sb = new StringBuffer(len);
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
			} else if (handleBackslash && cc == '\\') {
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
		return nextToken(src, from, separators, true, true);
	}
	
	/** Returns the next token with unescape option.
	 *
	 * <ul>
	 * <li>It trims whitespaces before and after the token.</li>
	 * <li>It handles both '\'' and '"' if handleQuotation is true.
	 * If true, all characters between them are considered as a token.</li>
	 * <li>Consider '\\' as the escape char if handleBackslash is true.</li>
	 * <li>If nothing found before end-of-string, null is returned</li>
	 * </ul>
	 *
	 * If a separator is found, it is returned in
	 * {@link Strings.Result#separator}.
	 *
	 * @param handleBackslash whether to treat '\\' specially (as escape char)
	 * It doesn't handle u and x yet.
	 * @param handleQuotation whether to handle '\'' and '"'
	 * @exception IllegalSyntaxException if the quoted string is unclosed.
	 */
	public static final Result nextToken(String src, int from,
	char[] separators, boolean handleBackslash, boolean handleQuotation)
	throws IllegalSyntaxException {
		final int len = src.length();
		from = skipWhitespaces(src, from);
		if (from >= len)
			return null; //end-of-string

		//1. handle quoted
		final char cc = src.charAt(from);
		if (handleQuotation && (cc == '\'' || cc == '"')) {
			final Result res = substring(src, from + 1, cc, handleBackslash);
			if (res.separator != cc)
				throw new IllegalSyntaxException(MCommon.QUOTE_UNMATCHED, src);

			res.next = skipWhitespaces(src, res.next + 1);
			if (res.next < len && isSeparator(src.charAt(res.next), separators))
				++res.next;
			return res;
		}

		//2. handle not-quoted
		final int j = nextSeparator(src, from, separators,
			handleBackslash, handleQuotation);
		int next = j;
		if (j < len) {
			if (handleQuotation) {
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
			k > from ? handleBackslash ?
				unescape(src.substring(from, k)) : src.substring(from, k): "",
			j < len ? src.charAt(j): (char)0);
			//if the token is nothing but spaces, k < from
	}
	
	/** Returns the next seperator index in the src string.
	 */
	public static int nextSeparator(String src, int from, char[] separators,
	boolean handleBackslash, boolean handleQuotation) {
		boolean quoted = false;
		for (final int len = src.length(); from < len; ++from) {
			final char cc = src.charAt(from);
			if (quoted) {
				quoted = false;
				continue;
			} else if (handleBackslash && cc == '\\') {
				quoted = true;
				continue;
			} else if (handleQuotation && (cc == '\'' || cc == '"')) {
				return from;
			}

			if (isSeparator(cc, separators))
				return from;
		}
		return from;
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

	/**
	 * Remove the suffix if a string ends with the specified suffix.
	 * If not found, the original string is returned
	 *
	 * @param s the string to process
	 * @param suffix the suffix to remove
	 * @return the processed string
	 * @see #removePrefix(String, String)
	 */
	public final static String removeSuffix(String s, String suffix) {
		return s.endsWith(suffix) ?
			s.substring(0, s.length() - suffix.length()) : s;
	}

	/**
	 * Remove the prefix if a string starts with the specified prefix.
	 *
	 * @param s the string to process
	 * @param prefix the prefix to remove
	 * @return the processed string
	 * @see #removeSuffix(String, String)
	 */
	public final static String removePrefix(String s, String prefix) {
		return s.startsWith(prefix) ?
			s.substring(prefix.length()) : s;
	}

	/**
	 * Removes the prefix upto the last separator (excluding).
	 * It is the same as
	 * to getSuffix if the string contains the separator. However,
	 * if the separator is not contained, it returns the whole string
	 * while getSuffix returns null.
	 *
	 * <p><code>Strings.removeUptoLast("/home/java/file", '/');</code><br>
	 * returns "file"
	 *
	 * @param s the string to process
	 * @param sep the separator
	 * @return the rest part; s if separator not found
	 * @see #getSuffix
	 * @see #removeSinceLast
	 */
	public final static String removeUptoLast(String s, char sep) {
		int j = s.lastIndexOf(sep);
		return j<0 ? s: s.substring(j+1);
	}
	/**
	 * Remove the suffix since the last separator (excluding).
	 *
	 * <p><code>Strings.removeUptoLast("/home/java/file", '/');</code><br>
	 * returns "/home/java"
	 *
	 * @param s the string to process
	 * @param sep the separator
	 * @return the rest part; s if separator not found
	 * @see #getPrefix
	 * @see #removeUptoLast
	 */
	public final static String removeSinceLast(String s, char sep) {
		int j = s.lastIndexOf(sep);
		return j<0 ? s: s.substring(0, j);
	}

	/**
	 * Gets the suffix after the last separator.
	 * If specifying the separator to '.', caller could get
	 * the last element of a Java class name.
	 *
	 * <p>Strings.getSuffix("abc.def.xyz", '.');<br>
	 * //returns "xyz"
	 *
	 * @param s the string to process
	 * @param sep the separator
	 * @return the suffix; null if separator not found
	 * @see #removeUptoLast
	 */
	public final static String getSuffix(String s, char sep) {
		int j = s.lastIndexOf(sep);
		return j<0 || (j+1) >= s.length() ? null: s.substring(j+1);
	}

	/**
	 * Gets the prefix before (exclude) the last separator.
	 * If specifying the separator to '.', caller could get
	 * the package name of a Java class name.
	 *
	 * <p>Strings.getPrefix("abc.def.xyz", '.');<br>
	 * //returns "abc.def"
	 *
	 * @param s the string to process
	 * @param sep the separator
	 * @return the prefix; the string if separator not found
	 */
	public final static String getPrefix(String s, char sep) {
		int j = s.lastIndexOf(sep);
		return j<0 ? s : s.substring(0, j);
	}

	/**
	 * Gets the prefix before (exclude) the first separator.
	 *
	 * <p>Strings.getShortestPrefix("abc.def.xyz", '.');<br>
	 * //returns "abc"
	 *
	 * @param s the string to process
	 * @param sep the separator
	 * @return the prefix; the string if separator not found
	 */
	public final static String getShortestPrefix(String s, char sep) {
		int j = s.indexOf(sep);
		return j<0 ? s : s.substring(0, j);
	}

	/**
	 * Capitalizes the j-th letter of a string.
	 *
	 * @param s the string to process
	 * @param j the letter to capitalize; 0 for the first
	 * @return the capitalized string
	 * @see #uncapitalize
	 */
	public final static String capitalize(String s, int j) {
		if (s.length() <= j || Character.isUpperCase(s.charAt(j)))
			return s;

		//we don't use substring due to the performance issue

		final char[] buf = s.toCharArray();
		buf[j] = Character.toUpperCase(buf[j]);
		return new String(buf);
	}

	/**
	 * Un-capitalizes the j-th letter of a string.
	 *
	 * @param s the string to process
	 * @param j the letter to capitalize; 0 for the first
	 * @return the uncapitalized string
	 * @see #capitalize
	 */
	public final static String uncapitalize(String s, int j) {
		if (s.length() <= j || Character.isLowerCase(s.charAt(j)))
			return s;

		//we don't use substring due to the performance issue

		final char[] buf = s.toCharArray();
		buf[j] = Character.toLowerCase(buf[j]);
		return new String(buf);
	}

	/**
	 * Gets the number of occurrences of the giving character.
	 * @param s the string; null is acceptable
	 */
	public final static int getOccurrences(String s, char c) {
		int cnt = 0;
		int len = s!=null ? s.length(): 0;
		for (int j=0; j<len; ++j)
			if (s.charAt(j) == c)
				++cnt;
		return cnt;
	}

	/** Normalizes a string. It removes all surrounding whitespace
	 * and merges consecutive spaces to one. Also, '\r' and '\n'
	 * are converted to space.
	 */
	public static final String normalize(String s) {
		final int len = s.length();
		final char[] dst = new char[len];
		boolean white = true;
		boolean modified = false;
		int pos = 0;
		for (int j = 0; j < len; j++) {
			final char cc = s.charAt(j);
			if (" \t\n\r".indexOf(cc) >= 0) {
				if (!white) {
					dst[pos++] = ' ';
					white = true;

					if (cc != ' ')
						modified = true;
				}
			} else {
				dst[pos++] = cc;
				white = false;
			}
		}
		if (white && pos > 0)
			pos--;

		return !modified && pos==s.length() ? s: new String(dst, 0, pos);
	}

	/** Converts a string by processing back-slashes.
	 *
	 * <p>They include \\u, \n, \r, \f, \t.
	 */
	public static final String convertBackslash(String src) {
		char cc;
		int len = src.length();
		StringBuffer sb = new StringBuffer(len);

		for (int j = 0; j < len; ) {
			cc = src.charAt(j++);
			if (cc == '\\') {
				cc = src.charAt(j++);
				if (cc == 'u') {
					int value = 0;
					for (int k = 0; k < 4; k++) {
						cc = src.charAt(j++);
						if (cc >= '0' && cc <= '9')
							value = (value << 4) + cc - '0';
						else if (cc >= 'a' && cc <= 'f')
							 value = (value << 4) + 10 + cc - 'a';
						else if (cc >= 'A' && cc <= 'F')
							 value = (value << 4) + 10 + cc - 'A';
						else
							  throw new IllegalArgumentException(
										   "Malformed \\uxxxx encoding.");
					}
					sb.append((char)value);
				} else {
					if (cc == 't') cc = '\t';
					else if (cc == 'r') cc = '\r';
					else if (cc == 'n') cc = '\n';
					else if (cc == 'f') cc = '\f';
					sb.append(cc);
				}
			} else {
				sb.append(cc);
			}
		}
		return sb.toString();
	}

	/**
	 * Converts a string to an object with the specified type.
	 * It handles primitive types, Date, the enumeration type, and
	 * class with a constructor that accepts String as the argument.
	 *
	 * <p>If str is null and type is not a primitive, null is returned.
	 *
	 * @param type the type of the primitive (can be the primitive or the
	 * wrapper class), or the enumeration type
	 * @param str the String representation of the primitive
	 * @return the object being converted
	 *
	 * @exception NumberFormatException if it failed to convert to a number
	 * @exception ParseException if it failed to convert to a Date
	 * @exception NoSuchMethodException if the class has no constructor
	 * accepting a string argument
	 */
 	public static final Object toObject(Class type, String str)
 	throws NoSuchMethodException, InstantiationException,
 	ParseException, IllegalAccessException, InvocationTargetException {
 		return _objtz.toObject(type, str);
 	}
 	/** Sets the objectizer which is used to convert a string to an object.
 	 *
	 * @param objtz the new objectizer. If null is specified, the default
	 * one is used.
	 * @return the previous objectizer (never null).
 	 */
 	public static final Objectizer setObjectizer(Objectizer objtz) {
 		final Objectizer old = _objtz;
 		_objtz = objtz != null ? objtz: newDefaultObjectizer();
 		return old;
 	}
 	private static Objectizer _objtz = newDefaultObjectizer();
 	/** Defines the method used to convert a string to an object.
 	 */
 	public static interface Objectizer {
 		/** Convers a string to an object of the specified type.
 		 */
 		public Object toObject(Class type, String str)
 		throws NoSuchMethodException, InstantiationException,
		ParseException, IllegalAccessException, InvocationTargetException;
 	}
 	private static Objectizer newDefaultObjectizer() {
 		return new Objectizer() {
	 		public Object toObject(Class type, String str)
	 		throws NoSuchMethodException, InstantiationException,
			ParseException, IllegalAccessException, InvocationTargetException {
 		if (str == null && !type.isPrimitive()) {
 			return null;
		} else if (String.class == type) {
 			return str;
 		} else if (char[].class == type) {
 			return str.toCharArray();
 		} else if (Boolean.class == type || Boolean.TYPE == type) {
 			return Boolean.valueOf(str);
 		} else if (Byte.class == type || Byte.TYPE == type) {
 			return Byte.valueOf(trimForDigits(str));
 		} else if (Character.class == type || Character.TYPE == type) {
 			return new Character(str.charAt(0));
 		} else if (Short.class == type || Short.TYPE == type) {
 			return Short.valueOf(trimForDigits(str));
 		} else if (Integer.class == type || Integer.TYPE == type) {
 			return Integer.valueOf(trimForDigits(str));
 		} else if (Long.class == type || Long.TYPE == type) {
 			return Long.valueOf(trimForDigits(str));
 		} else if (Float.class == type || Float.TYPE == type) {
 			return Float.valueOf(str.trim());
 				//don't use trimForDigit because it cannot handle 1e-1
 		} else if (Double.class == type || Double.TYPE == type) {
 			return Double.valueOf(str.trim());
 				//don't use trimForDigit because it cannot handle 1e-1
 		} else if (Date.class == type) {
			return DateFormats.parse(str);
		} else if (BigDecimal.class == type) {
			return new BigDecimal(trimForDigits(str));
		} else if (BigInteger.class == type) {
			return new BigInteger(trimForDigits(str));
		} else if (Locale.class == type) {
			return Locales.toLocale(str, (char)0);
		} else if (TimeZone.class == type) {
			return TimeZone.getTimeZone(str);
 		} else {
 			return type.getConstructor(new Class[] {String.class})
 				.newInstance(new Object[] {str});
		}
			}
		};
 	}
 
	/**
	 * Trim away the non digits character in the specified string. Generally
	 * used to trim away the thousand seperators or monetary prefix or postfix.
	 * E.g. NT$123,456,789.12 -> 123456789.12
	 */
	private static final String trimForDigits(String str) {
		final int len = str.length();
		final StringBuffer sb = new StringBuffer(len);
		for (int j = 0; j < len; j++) {
			final char cc = str.charAt(j);
			if (Character.isDigit(cc) || cc == '.' || cc == '-')
				sb.append(cc);
		}
		return sb.toString();
	}

 	/**
 	 * Prefix a specified String with a prefix String and delimited with the
 	 * specified delimeter; however, if prefix is empty or null or the given
 	 * string is null, this method would simply return the original String.
 	 */
 	public static final String prefix(String str, String pre, char delimiter) {
 		if (str == null || pre == null || pre.length() == 0)
 			return str;
 		return pre + delimiter + str;
 	}

	/** Advanced ID to the next.
	 * Example, advance("R012", 2) returns "R015".
	 */
	public static final String advance(String id, int step) {
		final int len = id.length();
		int j = len;
		while (--j >= 0) {
			final char cc = id.charAt(j);
			if (cc < '0' || cc > '9')
				break;
		}

		final int val;
		int digits = len - ++j;
		if (digits == 0) {
			val = 0;
			digits = 2;
		} else {
			val = Integer.parseInt(id.substring(j));
		}
		return id.substring(0, j)
			+ Integers.toStringByScale(val + step, digits);
	}
	
	/** 
	 * Make the number of n characters.
	 */
	public static final String nStrings(int n, String str) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++)
			sb.append( str );
		return sb.toString();
	}
}
