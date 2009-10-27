/* Datebox.java

	Purpose:

	Description:

	History:
		Tue Jun 28 13:41:01     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.impl.FormatInputElement;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding a date.
 * 
 * <p>
 * The default format ({@link #getFormat}) depends on JVM's setting and the
 * current user's locale. That is,
 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
 * You might override {@link #getDefaultFormat} to provide your own default
 * format.
 * <p>
 * Default {@link #getZclass}: z-datebox.(since 3.5.0)
 * 
 * @author tomyeh
 */
public class Datebox extends FormatInputElement implements
		org.zkoss.zul.api.Datebox {
	private TimeZone _tzone;
	private List _dtzones;
	private boolean _btnVisible = true, _lenient = true, _open = false, _dtzonesReadonly = false;
	static {
		addClientEvent(Datebox.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Datebox.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
		addClientEvent(Datebox.class, Events.ON_CHANGE, CE_IMPORTANT|CE_REPEAT_IGNORE);
		addClientEvent(Datebox.class, "onTimeZoneChange", CE_IMPORTANT|CE_DUPLICATE_IGNORE);
	}

	public Datebox() {
		setFormat(getDefaultFormat());
		setCols(11);
	}

	public Datebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}

	/**
	 * Returns the default format, which is used when contructing a datebox.
	 * <p>
	 * The default format ({@link #getFormat}) depends on JVM's setting and the
	 * current user's locale. That is,
	 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
	 * 
	 * <p>
	 * You might override this method to provide your own default format.
	 */
	protected String getDefaultFormat() {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
				Locales.getCurrent());
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat) df).toPattern();
			if (fmt != null && !"M/d/yy h:mm a".equals(fmt))
				return fmt; // note: JVM use "M/d/yy h:mm a" if not found!
		}
		return "yyyy/MM/dd";
	}

	/**
	 * Returns whether or not date/time parsing is to be lenient.
	 * 
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 */
	public boolean isLenient() {
		return _lenient;
	}

	/**
	 * Returns whether or not date/time parsing is to be lenient.
	 * <p>
	 * Default: true.
	 * 
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 */
	public void setLenient(boolean lenient) {
		if (_lenient != lenient) {
			_lenient = lenient;
			smartUpdate("lenient", _lenient);
		}
	}

	/** @deprecated As of release 5.0.0, it is no longer supported.
	 */
	public boolean isCompact() {
		return false;
	}
	/** @deprecated As of release 5.0.0, it is no longer supported.
	 */
	public void setCompact(boolean compact) {
	}

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 * 
	 * @since 2.4.1
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 * 
	 * @since 2.4.1
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("buttonVisible", visible);
		}
	}

	/**
	 * Returns the value (in Date), might be null unless a constraint stops it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException {
		return (Date) getTargetValue();
	}

	/**
	 * Sets the value (in Date).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException {
		if (value == null) value = Dates.today();
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
			getDateFormat(format); // make sure the format is correct
		super.setFormat(format);
	}

	/**
	 * Returns the time zone that this date box belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}

	/** Sets the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 *
	 * <p>Notice that if {@link #getDisplayedTimeZones} was called with
	 * a non-empty list, the time zone must be one of it.
	 * Otherwise (including <code>tzone</tt> is null),
	 * the first timezone is selected.
	 */
	public void setTimeZone(TimeZone tzone) {
		if (_tzone != tzone) {
			if (_dtzones != null) {
				_tzone = _dtzones.contains(tzone) ? tzone : (TimeZone) _dtzones.get(0);
			} else {
				_tzone = tzone;
			}
			invalidate();
		}
	}
	/** Sets the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 * @param id the time zone's ID, such as GMT+12.
	 * The time zone will be retrieved by calling TimeZone.getTimeZone(id).
	 */
	public void setTimeZone(String id) {
		TimeZone tzone = TimeZone.getTimeZone(id);
		setTimeZone(tzone);
	}
	/**
	 * Returns a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>Default: null
	 * @since 3.6.3
	 */
	public List getDisplayedTimeZones() {
		return _dtzones;
	}
	/**
	 * Sets a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>If the {@link #getTimeZone()} is null, 
	 * the first time zone in the list is assumed.
	 * @param dtzones a list of the time zones to display.
	 * If empty, it assumed to be null.
	 * @since 3.6.3
	 */
	public void setDisplayedTimeZones(List dtzones) {
		if (dtzones != null && dtzones.isEmpty())
			dtzones = null;
		if (_dtzones != dtzones) {
			_dtzones = dtzones;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < _dtzones.size(); i++) {
				if(i != 0) sb.append(",");
				TimeZone tz = (TimeZone)_dtzones.get(i);
				sb.append(tz.getID());
			}
			smartUpdate("displayedTimeZones", sb.toString());
			if (_tzone == null && _dtzones != null && _dtzones.get(0) != null)
				_tzone = (TimeZone)_dtzones.get(0);
		}
	}
	/**
	 * Sets a catenation of a list of the time zones' ID, separated by comma,
	 * that will be displayed at the client and allow user to select.
	 * <p>The time zone is retrieved by calling TimeZone.getTimeZone().
	 * @param dtzones a catenation of a list of the timezones' ID, such as
	 * <code>"GMT+12,GMT+8"</code>
	 * @see #setDisplayedTimeZones(List)
	 * @since 3.6.3
	 */
	public void setDisplayedTimeZones(String dtzones) {
		if (dtzones == null || dtzones.length() == 0) {
			setDisplayedTimeZones((List)null);
			return;
		}
		
		LinkedList list = new LinkedList();
		String[] ids = dtzones.split(",");
		for (int i = 0; i < ids.length; i++) {
			TimeZone tzone = TimeZone.getTimeZone(ids[i].trim());
			if (tzone != null)
				list.add(tzone);
		}
		setDisplayedTimeZones(list);
	}
	/**
	 * Returns whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * @since 3.6.3
	 */
	public boolean isTimeZonesReadonly() {
		return _dtzonesReadonly;
	}
	/**
	 * Returns whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * @since 3.6.3
	 */
	public void setTimeZonesReadonly(boolean readonly) {
		if (readonly != _dtzonesReadonly) {
			_dtzonesReadonly = readonly;
			smartUpdate("timeZonesReadonly", _dtzonesReadonly);
		}
	}

	/**
	 * Drops down or closes the calendar to select a date.
	 * 
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (open)
			open();
		else
			close();
	}

	/**
	 * Drops down the calendar to select a date. The same as setOpen(true).
	 * 
	 * @since 3.0.1
	 */
	public void open() {
		smartUpdate("open", true);
	}

	/**
	 * Closes the calendar if it was dropped down. The same as setOpen(false).
	 * 
	 * @since 3.0.1
	 */
	public void close() {
		smartUpdate("open", false);
	}

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onTimeZoneChange, onChange, onChanging and onError.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("onTimeZoneChange")) {
			final Map data = request.getData();
			String timezone = (String)data.get("timezone");
			setTimeZone(timezone);
		} else 
			super.service(request, everError);
	}
	
	// -- super --//
	public void setConstraint(String constr) {
		setConstraint(constr != null ? new SimpleDateConstraint(constr) : null); // Bug
																					// 2564298
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
			throw showCustomError(new WrongValueException(this,
					MZul.DATE_REQUIRED, new Object[] { value, fmt }));
		}
		return date;
	}

	protected String coerceToString(Object value) {
		final DateFormat df = getDateFormat(getFormat());
		return value != null ? df.format((Date) value) : "";
	}

	/**
	 * Returns the date format of the specified format
	 * 
	 * <p>
	 * Default: it uses SimpleDateFormat to format the date.
	 * 
	 * @param fmt
	 *            the pattern.
	 */
	protected DateFormat getDateFormat(String fmt) {
		final DateFormat df = new SimpleDateFormat(fmt, Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone : TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}

	public String getZclass() {
		return _zclass == null ? "z-datebox" : _zclass;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (!_btnVisible)
			renderer.render("buttonVisible", false);
		if (!_lenient)
			renderer.render("lenient", false);
		if (_dtzonesReadonly)
			renderer.render("timeZonesReadonly", true);
		if (_dtzones != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < _dtzones.size(); i++) {
				if(i != 0) sb.append(",");
				TimeZone tz = (TimeZone)_dtzones.get(i);
				sb.append(tz.getID());
			}
			renderer.render("displayedTimeZones", sb.toString());
		}

		if (_tzone != null)
			renderer.render("timeZone", _tzone.getID());
	}
}
