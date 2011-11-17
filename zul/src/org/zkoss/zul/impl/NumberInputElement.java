/* NumberInputElement.java

	Purpose:
		
	Description:
		
	History:
		Fri May  4 11:39:46     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.zkoss.json.JSONValue;
import org.zkoss.lang.JVMs;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.math.RoundingModes;

/**
 * A skeletal implementation for number-type input box.
 *
 * @author tomyeh
 */
abstract public class NumberInputElement extends FormatInputElement {
	/** The rounding mode. */
	private int _rounding = BigDecimal.ROUND_HALF_EVEN;
	private Locale _locale;
	
	/** Sets the rounding mode.
	 * Note: You cannot change the rounding mode unless you are
	 * using Java 6 or later.
	 *
	 * @param mode the rounding mode. Allowed value:
	 * {@link BigDecimal#ROUND_CEILING}, {@link BigDecimal#ROUND_DOWN},
	 * {@link BigDecimal#ROUND_FLOOR}, {@link BigDecimal#ROUND_HALF_DOWN},
	 * {@link BigDecimal#ROUND_HALF_UP}, {@link BigDecimal#ROUND_HALF_EVEN},
	 * {@link BigDecimal#ROUND_UNNECESSARY} and {@link BigDecimal#ROUND_UP}
	 *
	 * @exception UnsupportedOperationException if Java 5 or below
	 */
	public void setRoundingMode(int mode) {
		if (_rounding != mode) {
			if (!JVMs.isJava6())
				throw new UnsupportedOperationException("Java 6 or above is required");
			_rounding = mode;
			smartUpdate("rounding", mode);
		}
	}
	/** Sets the rounding mode by the name.
	 * Note: You cannot change the rounding mode unless you are
	 * using Java 6 or later.
	 *
	 * @param name the rounding mode's name. Allowed value:
<dl>
<dt>CEILING</dt>
	<dd>Rounding mode to round towards positive infinity.</dd>
<dt>DOWN</dt>
	<dd>Rounding mode to round towards zero.</dd>
<dt>FLOOR</dt>
	<dd>Rounding mode to round towards negative infinity.</dd>
<dt>HALF_DOWN</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round down.</dd>
<dt>HALF_EVEN</dt>
	<dd>Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant, in which case, round towards the even neighbor.</dd>
<dt>HALF_UP</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round up.</dd>
<dt>UNNECESSARY</dt>
	<dd>Rounding mode to assert that the requested operation has an exact result, hence no rounding is necessary.</dd>
<dt>UP</dt>
	<dd>Rounding mode to round away from zero.</dd>
</dl>
	 * @exception UnsupportedOperationException if Java 5 or below
	 * @see RoundingModes
	 */
	public void setRoundingMode(String name) {
		setRoundingMode(RoundingModes.valueOf(name));
	}
	/** Returns the rounding mode.
	 * <p>Default: {@link BigDecimal#ROUND_HALF_EVEN}.
	 */
	public int getRoundingMode() {
		return _rounding;
	}


	/** Returns the locale associated with this number input element,
	 * or null if {@link Locales#getCurrent} is preferred.
	 * @since 5.0.8
	 */
	public Locale getLocale() {
		return _locale;
	}
	/** Sets the locale used to identify the symbols of this number input element.
	 * <p>Default: null (i.e., {@link Locales#getCurrent}, the current locale
	 * is assumed)
	 * <p> If the format of {@link #getFormat()} is null, the format is assumed from
	 * {@link #getDefaultFormat()}. (since 5.0.9)
	 * @since 5.0.8
	 */
	public void setLocale(Locale locale) {
		if (!Objects.equals(_locale, locale)) {
			_locale = locale;
			
			if (getFormat() == null)
				setFormat(getDefaultFormat());
			
			invalidate();
		}
	}
	/** Sets the locale used to identify the symbols of this number input element.
	 * <p>Default: null (i.e., {@link Locales#getCurrent}, the current locale
	 * is assumed)
	 * @since 5.0.8
	 */
	public void setLocale(String locale) {
		setLocale(locale != null && locale.length() > 0 ?
			Locales.getLocale(locale): null);
	}
	
	/** Returns the real symbols according to the current locale.
	 * @since 5.0.8
	 */
	private String getRealSymbols() {
		if (_locale != null) {
			final String localeName = _locale.toString();
			if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(
					getDesktop(), "org.zkoss.zul.impl.NumberInputElement" + localeName)) {
				final DecimalFormatSymbols symbols = new DecimalFormatSymbols(
						_locale);
				Map<String, String> map = new HashMap<String, String>();
				map.put("GROUPING",
						String.valueOf(symbols.getGroupingSeparator()));
				map.put("DECIMAL",
						String.valueOf(symbols.getDecimalSeparator()));
				map.put("PERCENT", String.valueOf(symbols.getPercent()));
				map.put("PER_MILL", String.valueOf(symbols.getPerMill()));
				map.put("MINUS", String.valueOf(symbols.getMinusSign()));
				return JSONValue.toJSONString(new Object[] { localeName, map });
			} else return JSONValue.toJSONString(new Object[] { localeName,
					null });
		}
		return null;
	}
	
	private Locale getDefaultLocale() {
		return _locale != null ? _locale : Locales.getCurrent(); 
	}
	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_rounding != BigDecimal.ROUND_HALF_EVEN)
			renderer.render("_rounding", _rounding);
		if (_locale != null)
			renderer.render("localizedSymbols", getRealSymbols());
	}
	
	/**
	 * Return a default format for the number input element when the locale is specified.
	 * <p> Default: <code>##,##0.##</code>, you can overwrite this by specifying
	 * the following setting in zk.xml
	 * <pre><code>
	 *	<library-property>
	 *		<name>org.zkoss.zul.numberFormat</name>
	 *		<value>##,##0.##</value>
	 *	</library-property>
	 * </code></pre>
	 * @since 5.0.9
	 * @see #setLocale(Locale)
	 */
	protected String getDefaultFormat() {
		return Library.getProperty("org.zkoss.zul.numberFormat", "##,##0.##");
	}
	//utilities//
	/** Formats a number (Integer, BigDecimal...) into a string.
	 * If null, an empty string is returned.
	 *
	 * <p>A utility to assist the handling of numeric data.
	 *
	 * @see #toNumberOnly
	 * @param defaultFormat used if {@link #getFormat} returns null.
	 * If defaultFormat and {@link #getFormat} are both null,
	 * the system's default format is used.
	 */
	protected String formatNumber(Object value, String defaultFormat) {
		if (value == null) return "";

		final DecimalFormat df = (DecimalFormat)
			NumberFormat.getInstance(getDefaultLocale());
		if (_rounding != BigDecimal.ROUND_HALF_EVEN)
			df.setRoundingMode(RoundingMode.valueOf(_rounding));

		String fmt = getFormat();
		if (fmt == null) fmt = defaultFormat;
		if (fmt != null) df.applyPattern(fmt);
		return df.format(value);
	}
	/** Filters out non digit characters, such comma and whitespace,
	 * from the specified value.
	 * It is designed to let user enter data in more free style.
	 * They may or may not enter data in the specified format.
	 *
	 * @return a two element array. The first element is the string to
	 * parse with, say, Double.parseDouble. The second element is
	 * an integer to indicate how many digits the result shall be scaled.
	 * For example, if the second element is 2. Then, the result shall be
	 * divided with 10 ^ 2.
	 *
	 * @see #formatNumber
	 */
	protected Object[] toNumberOnly(String val) {
		if (val == null) return new Object[] {null, null};

		final DecimalFormatSymbols symbols =
			new DecimalFormatSymbols(getDefaultLocale());
		final char GROUPING = symbols.getGroupingSeparator(),
			DECIMAL = symbols.getDecimalSeparator(),
			PERCENT = symbols.getPercent(),
			PER_MILL = symbols.getPerMill(), //1/1000
			//not support yet: INFINITY = symbols.getInfinity(), NAN = symbols.getNaN(),
			MINUS = symbols.getMinusSign();
		final String fmt = getFormat();
		StringBuffer sb = null;
		int divscale = 0; //the second element
		boolean minus = false;
		for (int j = 0, len = val.length(); j < len; ++j) {
			final char cc = val.charAt(j);

			boolean ignore = false;
			//We handle percent and (nnn) specially
			if (cc == PERCENT) {
				divscale += 2;
				ignore = true;
			} else if (cc == PER_MILL) {
				divscale += 3;
				ignore = true;
			} else if (cc == '(') {
				minus = true;
				ignore = true;
			} else if (cc == '+') {
				ignore = true;
			}

			//We don't add if cc shall be ignored (not alphanum but in fmt)
			if (!ignore)
				ignore = (cc < '0' || cc > '9')
				&& cc != DECIMAL && cc != MINUS && cc != '+'
				&& (Character.isWhitespace(cc) || cc == GROUPING || cc == ')'
					|| (fmt != null && fmt.indexOf(cc) >= 0));
			if (ignore) {
				if (sb == null)
					sb = new StringBuffer(len).append(val.substring(0, j));
			} else {
				final char c2 = cc == MINUS ? '-':
					cc == DECIMAL ? '.':  cc;
				if (cc != c2) {
					if (sb == null)
						sb = new StringBuffer(len).append(val.substring(0, j));
					sb.append(c2);
				} else if (sb != null) {
					sb.append(c2);
				}
			}
		}
		if (minus) {
			if (sb == null)
				sb = new StringBuffer(val.length() + 1).append(val);
			if (sb.length() > 0) {
				if (sb.charAt(0) == '-') {
					sb.deleteCharAt(0);
				} else {
					sb.insert(0, '-');
				}
			}
		}

		//handle '%'
		if (fmt != null && divscale > 0) {
		l_out:
			for (int j = 0, k, len = fmt.length(); (k = fmt.indexOf('\'', j)) >= 0;) {
				while (++k < len){
					final char cc = fmt.charAt(k);
					if (cc == '%') divscale -= 2;
					else if (cc == '\u2030') divscale -= 3;
					else if (cc == '\'') {
						++k;
						break;
					}
					if (divscale <= 0) {
						divscale = 0;
						break l_out;
					}
				}
				j = k;
			}
		}

		return new Object[] {
			(sb != null ? sb.toString(): val), new Integer(divscale)};
	}
}
