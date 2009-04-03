/* Datebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:41:01     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.au.out.AuInvoke;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.impl.FormatInputElement;
import org.zkoss.zul.impl.Utils;

/**
 * An edit box for holding a date.
 *
 * <p>The default format ({@link #getFormat}) depends on JVM's setting
 * and the current user's locale. That is,
 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
 * You might override {@link #getDefaultFormat} to provide your own default
 * format.
 * <p>Default {@link #getZclass}: z-datebox.(since 3.5.0)
 * @author tomyeh
 */
public class Datebox extends FormatInputElement implements org.zkoss.zul.api.Datebox {
	private TimeZone _tzone;
	private boolean _lenient = true;
	private boolean _compact, _btnVisible = true;

	public Datebox() {
		setFormat(getDefaultFormat());
		setCols(11);
		_compact = "zh".equals(Locales.getCurrent().getLanguage());
	}
	public Datebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}

	/** Returns the default format, which is used when contructing
	 * a datebox.
	 * <p>The default format ({@link #getFormat}) depends on JVM's setting
	 * and the current user's locale. That is,
	 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
	 *
	 * <p>You might override this method to provide your own default format.
	 */
	protected String getDefaultFormat() {
		final DateFormat df = DateFormat.getDateInstance(
			DateFormat.DEFAULT, Locales.getCurrent());
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
			smartUpdate("lenient", _lenient);
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
			smartUpdate("compact", compact);
		}
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 * @since 2.4.1
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}
	/** Sets whether the button (on the right of the textbox) is visible.
	 * @since 2.4.1
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("buttonVisible", visible);
		}
	}

	/** Returns the value (in Date), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException {
		return (Date)getTargetValue();
	}
	/** Sets the value (in Date).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	/** Sets the date format.
<p>The following pattern letters are defined:
<table border=0 cellspacing=3 cellpadding=0>

     <tr bgcolor="#ccccff">
         <th align=left>Letter
         <th align=left>Date or Time Component
         <th align=left>Presentation
         <th align=left>Examples
     <tr>
         <td><code>G</code>
         <td>Era designator
         <td><a href="#text">Text</a>
         <td><code>AD</code>

     <tr bgcolor="#eeeeff">
         <td><code>y</code>
         <td>Year
         <td><a href="#year">Year</a>
         <td><code>1996</code>; <code>96</code>
     <tr>
         <td><code>M</code>

         <td>Month in year
         <td><a href="#month">Month</a>
         <td><code>July</code>; <code>Jul</code>; <code>07</code>
     <tr bgcolor="#eeeeff">
         <td><code>w</code>
         <td>Week in year (starting at 1)
         <td><a href="#number">Number</a>

         <td><code>27</code>
     <tr>
         <td><code>W</code>
         <td>Week in month (starting at 1)
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr bgcolor="#eeeeff">

         <td><code>D</code>
         <td>Day in year (starting at 1)
         <td><a href="#number">Number</a>
         <td><code>189</code>
     <tr>
         <td><code>d</code>
         <td>Day in month (starting at 1)
         <td><a href="#number">Number</a>

         <td><code>10</code>
     <tr bgcolor="#eeeeff">
         <td><code>F</code>
         <td>Day of week in month
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr>

         <td><code>E</code>
         <td>Day in week
         <td><a href="#text">Text</a>
         <td><code>Tuesday</code>; <code>Tue</code>
 </table>
 	 */
	public void setFormat(String format) throws WrongValueException {
		if (format == null || format.length() == 0)
			format = getDefaultFormat();
		else
			getDateFormat(format); //make sure the format is correct
		super.setFormat(format);
	}

	/** Returns the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}
	/** Sets the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone) {
		_tzone = tzone;
	}

	/** Drops down or closes the calendar to select a date.
	 *
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (open) open();
		else close();
	}
	/** Drops down the calendar to select a date.
	 * The same as setOpen(true).
	 *
	 * @since 3.0.1
	 */
	public void open() {
		response("dropdn", new AuInvoke(this, "dropdn", true));
	}
	/** Closes the calendar if it was dropped down.
	 * The same as setOpen(false).
	 *
	 * @since 3.0.1
	 */
	public void close() {
		response("dropdn", new AuInvoke(this, "dropdn", false));
	}

	//-- super --//
	public void setConstraint(String constr) {
		setConstraint(new SimpleDateConstraint(constr));
	}
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
			throw showCustomError(
				new WrongValueException(this, MZul.DATE_REQUIRED,
					new Object[] {value, fmt}));
		}
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
		final DateFormat df = new SimpleDateFormat(fmt, Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone: TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-datebox" : _zclass;
	}
}
