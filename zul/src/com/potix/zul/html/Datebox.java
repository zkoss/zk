/* Datebox.java

{{IS_NOTE
	$Id: Datebox.java,v 1.19 2006/05/15 13:22:09 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:41:01     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.potix.lang.Objects;
import com.potix.util.prefs.Apps;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.mesg.MZul;
import com.potix.zul.html.impl.FormatInputElement;

/**
 * An edit box for holding a date.
 *
 * <p>Default {@link #getSclass}: datebox.
 *
 * <p>The default format ({@link #getFormat}) depends on JVM's setting
 * and the current user's locale. That is,
 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Apps.getCurrentLocale).</code>
 * You might override {@link #getDefaultFormat} to provide your own default
 * format.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.19 $ $Date: 2006/05/15 13:22:09 $
 */
public class Datebox extends FormatInputElement {
	private boolean _lenient = true;
	private boolean _compact;

	public Datebox() {
		setFormat(getDefaultFormat());
		setSclass("datebox");
		setCols(11);
		_compact = "zh".equals(Apps.getCurrentLocale().getLanguage());
	}
	public Datebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}
	/** Returns the default format, which is used when contructing
	 * a datebox.
	 * <p>The default format ({@link #getFormat}) depends on JVM's setting
	 * and the current user's locale. That is,
	 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Apps.getCurrentLocale).</code>
	 *
	 * <p>You might override this method to provide your own default format.
	 */
	protected String getDefaultFormat() {
		final DateFormat df = DateFormat.getDateInstance(
			DateFormat.DEFAULT, Apps.getCurrentLocale());
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat)df).toPattern();
			if (fmt != null && !"M/d/yy h:mm a".equals(fmt))
				return fmt; //note: JVM use "M/d/yy h:mm a" if not found!
		}
		return "yyyy/MM/dd";
	}

	/** Returns whether or not date/time parsing is to be lenient.
	 *
	 * <p>With lenient parsing, the parser may use heuristics to interpret
	 * inputs that do not precisely match this object's format.
	 * With strict parsing, inputs must match this object's format.
	 */
	public boolean isLenient() {
		return _lenient;
	}
	/** Returns whether or not date/time parsing is to be lenient.
	 * <p>Default: true.
	 *
	 * <p>With lenient parsing, the parser may use heuristics to interpret
	 * inputs that do not precisely match this object's format.
	 * With strict parsing, inputs must match this object's format.
	 */
	public void setLenient(boolean lenient) {
		if (_lenient != lenient) {
			_lenient = lenient;
			smartUpdate("zk_lenient", _lenient);
		}
	}
	/** Returns whether to use a compact layout.
	 * <p>Default: true if zh_TW or zh_CN; false otherwise.
	 */
	public boolean isCompact() {
		return _compact;
	}
	/** Sets whether to use a compact layout.
	 */
	public void setCompact(boolean compact) {
		if (_compact != compact) {
			_compact = compact;
			invalidate(OUTER);
		}
	}

	/** Returns the value (in Date), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException {
		return (Date)getRawValue();
	}
	/** Sets the value (in Date).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException {
		validate(value);
		if (setRawValue(value))
			smartUpdate("value", getText());
	}

	public void setFormat(String format) throws WrongValueException {
		if (format == null || format.length() == 0)
			format = getDefaultFormat();

		if (!Objects.equals(getFormat(), format)) {
			super.setFormat(format);
			smartUpdate("zk_fmt", getFormat());
		}
	}

	//-- super --//
	protected Object coerceFromString(String value) throws WrongValueException {
		if (value == null || value.length() == 0)
			return null;

		final String fmt = getFormat();
		final DateFormat df = getDateFormat(fmt);
		df.setLenient(_lenient);
		final Date date;
		try {
			date = df.parse(value);
		} catch (ParseException ex) {
			throw new WrongValueException(this, MZul.DATE_REQUIRED,
				new Object[] {value, fmt});
		}
/*
		if (date.compareTo(_min) < 0 || date.compareTo(_max) > 0)
			throw new WrongValueException(
				MZul.DATE_OUT_OF_RANGE,
				new Object[] {value, df.format(_min), df.format(_max), fmt});
*/
		return date;
	}
	protected String coerceToString(Object value) {
		final DateFormat df = getDateFormat(getFormat());
		return value != null ? df.format((Date)value): "";
	}
	/** Returns the date format of the specified format
	 *
	 * <p>Default: it uses SimpleDateFormat to format the date.
	 *
	 * @param fmt the pattern.
	 */
	protected DateFormat getDateFormat(String fmt) {
		final DateFormat df = new SimpleDateFormat(fmt, Apps.getCurrentLocale());
		final TimeZone tz = Apps.getCurrentTimeZone();
		df.setTimeZone(tz);
		return df;
	}

	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "zk_fmt", getFormat());
		if (!_lenient) sb.append(" zk_lenient=\"false\"");
		if (_compact) sb.append(" zk_compact=\"true\"");
		return sb.toString();
	}
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final String style = getInnerStyle();
		return style.length() > 0 ? attrs+" style=\""+style+'"': attrs;
	}
	private String getInnerStyle() {
		final StringBuffer sb = new StringBuffer(32)
			.append(HTMLs.getTextRelevantStyle(getRealStyle()));
		HTMLs.appendStyle(sb, "width", getWidth());
		HTMLs.appendStyle(sb, "height", getHeight());
		return sb.toString();
	}
	/** Returns RS_NO_WIDTH|RS_NO_HEIGHT.
	 */
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags()|RS_NO_WIDTH|RS_NO_HEIGHT;
	}
}
