/* Timebox.java


 Purpose:
 
 Description:
 
 History:
 	Jul 9, 2007 10:03:38 AM , Created by Dennis Chen


 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 2.1 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.text.DateFormats;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.BooleanPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.impl.DateTimeFormatInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An input box for holding a time (a java.util.Date Object , but only Hour & Minute are used.
 *
 * <p>Default {@link #getZclass}: z-timebox. (since 3.5.0)
 * 
 * <p>timebox supports below key events.
 * <ul>
 *  <li>0-9 : set the time digit depend on the position on the inner text box.
 * 	<li>up : increase time digit depend on the position on the inner text box.
 *  <li>down : decrease time digit depend on the position on the inner text box.
 * 	<li>delete : clear the time to empty (null)
 * </ul>
 *
 * <p>Like {@link Combobox} and {@link Datebox},
 * the value of a read-only time box ({@link #isReadonly}) can be changed
 * by clicking the up or down button (though users cannot type anything
 * in the input box).
 *
 * @author Dennis Chen
 * @since 3.0.0
 */
public class Timebox extends DateTimeFormatInputElement {
	/*package*/ static final String DEFAULT_FORMAT = "HH:mm";
	private boolean _btnVisible = true;
	private static Date _dummyDate = new Date();

	public Timebox() {
		setCols(5);
		setFormat("");
	}

	public Timebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}

	public Timebox(ZonedDateTime value) throws WrongValueException {
		this();
		setValueInZonedDateTime(value);
	}

	public Timebox(LocalDateTime value) throws WrongValueException {
		this();
		setValueInLocalDateTime(value);
	}

	public Timebox(LocalTime value) throws WrongValueException {
		this();
		setValueInLocalTime(value);
	}

	/** Sets the date format.
	<p>If null or empty is specified, {@link #getDefaultFormat} is assumed.
	Since 5.0.7, you could specify one of the following reserved words,
	and {@link DateFormats#getTimeFormat}
	will be used to retrieve the real format.
	<table border=0 cellspacing=3 cellpadding=0>
	<tr>
	<td>short</td>
	<td>{@link DateFormats#getTimeFormat} with {@link DateFormat#SHORT}</td>
	</tr>
	<tr>
	<td>medium</td>
	<td>{@link DateFormats#getTimeFormat} with {@link DateFormat#MEDIUM}</td>
	</tr>
	<tr>
	<td>long</td>
	<td>{@link DateFormats#getTimeFormat} with {@link DateFormat#LONG}</td>
	</tr>
	<tr>
	<td>full</td>
	<td>{@link DateFormats#getTimeFormat} with {@link DateFormat#FULL}</td>
	</tr>
	</table>
	
	<p>In additions, the format could be a combination of the following pattern letters:
	<table border=0 cellspacing=3 cellpadding=0>
	
	 <tr bgcolor="#ccccff">
	     <th align=left>Letter
	     <th align=left>Date or Time Component
	     <th align=left>Presentation
	     <th align=left>Examples
	 <tr>
	*     <tr bgcolor="#eeeeff">
	*         <td><code>a</code>
	*         <td>Am/pm marker
	*         <td><a href="#text">Text</a>
	*         <td><code>PM</code>
	*     <tr>
	*         <td><code>H</code>
	*         <td>Hour in day (0-23)
	*         <td><a href="#number">Number</a>
	*         <td><code>0</code>
	*     <tr bgcolor="#eeeeff">
	*         <td><code>k</code>
	*         <td>Hour in day (1-24)
	*         <td><a href="#number">Number</a>
	*         <td><code>24</code>
	*     <tr>
	*         <td><code>K</code>
	*         <td>Hour in am/pm (0-11)
	*         <td><a href="#number">Number</a>
	*         <td><code>0</code>
	*     <tr bgcolor="#eeeeff">
	*         <td><code>h</code>
	*         <td>Hour in am/pm (1-12)
	*         <td><a href="#number">Number</a>
	*         <td><code>12</code>
	*     <tr>
	*         <td><code>m</code>
	*         <td>Minute in hour
	*         <td><a href="#number">Number</a>
	*         <td><code>30</code>
	*     <tr bgcolor="#eeeeff">
	*         <td><code>s</code>
	*         <td>Second in minute
	*         <td><a href="#number">Number</a>
	*         <td><code>55</code>
	</table>
	@since 5.0.0
	 */
	public void setFormat(String format) throws WrongValueException {
		if (!Objects.equals(getFormat(), format)) {
			String realformat = getRealFormat();
			if (realformat.indexOf("z") != -1) {
				String timezone = getFormattedTimezone();
				smartUpdate("timezone", timezone);
			}

			super.setFormat(format != null ? format : "");
		}
	}

	/** Returns the real format, i.e., the combination of the format patterns,
	 * such as hh:mm.
	 * <p>As described in {@link #setFormat}, a developer could specify
	 * an abstract name, such as short, or an empty string as the format,
	 * and this method will convert it to a real time format.
	 * @since 5.0.7
	 */
	public String getRealFormat() {
		final String format = getFormat();
		if (format == null || format.length() == 0)
			return getDefaultFormat();

		int ts = Datebox.toStyle(format);
		return ts != -111 ? DateFormats.getTimeFormat(ts, _locale, DEFAULT_FORMAT) : format;
	}

	/**
	 * It is meaningless to set only LocalDate in timebox.
	 */
	@Override
	public void setValueInLocalDate(LocalDate value) throws WrongValueException {
		throw new UnsupportedOperationException("need time");
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}

	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("buttonVisible", visible);
		}
	}

	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: between 093000 and 183000, before 210000
	 */
	@Override
	public void setConstraint(String constr) {
		setConstraint(constr != null ? new SimpleLocalTimeConstraint(constr) : null);
	}

	/**
	 * Returns the default format, which is used when constructing a timebox.
	 * <p>Default: DateFormats.getTimeFormat(DEFAULT, null, "HH:mm")
	 * (see {@link DateFormats#getTimeFormat}).
	 * 
	 * <p>Though you might override this method to provide your own default format,
	 * it is suggested to specify the format for the current thread
	 * with {@link DateFormats#setLocalFormatInfo}.
	 * @since 5.0.7
	 */
	protected String getDefaultFormat() {
		return DateFormats.getTimeFormat(DateFormat.DEFAULT, _locale, "HH:mm");
		//We use HH:mm for backward compatibility
	}

	protected Object coerceFromString(String value) throws WrongValueException {
		//null or empty string,
		if (value == null || value.length() == 0) {
			return null;
		}
		final String fmt = getRealFormat();
		final DateFormat df = getDateFormat(fmt);
		final Date date;
		try {
			date = df.parse(value);
		} catch (ParseException ex) {
			throw showCustomError(new WrongValueException(this, MZul.DATE_REQUIRED, new Object[] { value, fmt }));
		}
		return date;
	}

	protected String coerceToString(Object value) {
		final DateFormat df = getDateFormat(getRealFormat());
		return value != null ? df.format((Date) value) : "";
	}

	/** Returns the date format of the time only,
	 *
	 * <p>Default: it uses SimpleDateFormat to format the date.
	 */
	protected DateFormat getDateFormat(String fmt) {
		final DateFormat df = new SimpleDateFormat(fmt, _locale != null ? _locale : Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone : TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}

	private String getUnformater() {
		if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(getDesktop(),
				"org.zkoss.zul.Timebox.unformater.isSent")) {
			return Library.getProperty("org.zkoss.zul.Timebox.unformater");
		}
		return null;
	}

	private Object[] getRealSymbols() {
		if (_locale != null) {
			final String localeName = _locale.toString();
			if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(getDesktop(), getClass().getName() + localeName)) {
				final Map<String, String[]> map = new HashMap<String, String[]>(2);
				final Calendar cal = Calendar.getInstance(_locale);

				SimpleDateFormat df = new SimpleDateFormat("a", _locale);
				cal.set(Calendar.HOUR_OF_DAY, 3);
				final String[] ampm = new String[2];
				ampm[0] = df.format(cal.getTime());
				cal.set(Calendar.HOUR_OF_DAY, 15);
				ampm[1] = df.format(cal.getTime());
				map.put("APM", ampm);
				return new Object[] { localeName, map };
			}
			return new Object[] { localeName, null };
		}
		return null;
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-timebox" : _zclass;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		String realformat = getRealFormat();
		if (realformat.indexOf("z") != -1) {
			String timezone = getFormattedTimezone();
			renderer.render("timezoneAbbr", timezone);
		}

		if (!_btnVisible)
			renderer.render("buttonVisible", _btnVisible);

		String unformater = getUnformater();
		if (!Strings.isBlank(unformater))
			renderer.render("unformater", unformater); // TODO: compress

		if (_locale != null)
			renderer.render("localizedSymbols", getRealSymbols());
	}

	private String getFormattedTimezone() {
		return getDateFormat("z").format(_dummyDate);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);

	static {
		_properties.put("buttonVisible", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((Timebox) cmp).setButtonVisible(value);
			}

			public Boolean getValue(Component cmp) {
				return ((Timebox) cmp).isButtonVisible();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}
}
