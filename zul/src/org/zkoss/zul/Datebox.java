/* Datebox.java

	Purpose:

	Description:

	History:
		Tue Jun 28 13:41:01     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;

import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.WaitLock;
import org.zkoss.util.logging.Log;
import org.zkoss.text.DateFormats;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Blockable;
import org.zkoss.zk.ui.http.Utils;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zul.impl.FormatInputElement;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding a date.
 * 
 * <p>
 * The default format ({@link #getFormat}) depends on {@link DateFormats#getDateFormat}
 * and the current user's locale (unless {@link #setLocale} is assigned.
 * Please refer to {@link #setFormat} for more details.
 * <p>
 * Default {@link #getZclass}: z-datebox.(since 3.5.0)
 * 
 * @author tomyeh
 */
public class Datebox extends FormatInputElement {
	
	private static final Log log = Log.lookup(Datebox.class);
	private static final String DEFAULT_FORMAT = "yyyy/MM/dd";

	private TimeZone _tzone;
	private List<TimeZone> _dtzones;
	/** The locale associated with this datebox. */
	private Locale _locale;
	private boolean _btnVisible = true, _lenient = true, _dtzonesReadonly = false;
	private static Map<Locale, Object> _symbols = new HashMap<Locale, Object>(8);
	
	static {
		addClientEvent(Datebox.class, "onTimeZoneChange", CE_IMPORTANT|CE_DUPLICATE_IGNORE);
	}

	public Datebox() {
		setFormat("");
		setCols(11);
	}

	/** Constructor with a given date.
	 * @param date the date to be assigned to this datebox initially.<br/>
	 * Notice that, if this datebox does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 */
	public Datebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}

	/**
	 * Returns the default format, which is used when constructing a datebox,
	 * or when {@link #setFormat} is called with null or empty.
	 * <p>Default: DateFormats.getDateFormat(DEFAULT, null, "yyyy/MM/dd")
	 * (see {@link DateFormats#getDateFormat}).
	 * 
	 * <p>Though you might override this method to provide your own default format,
	 * it is suggested to specify the format for the current thread
	 * with {@link DateFormats#setLocalFormatInfo}.
	 */
	protected String getDefaultFormat() {
		return DateFormats.getDateFormat(DateFormat.DEFAULT, _locale, DEFAULT_FORMAT);
			//We use yyyy/MM/dd for backward compatibility
	}

	/**
	 * Returns the localized format, which is used when constructing a datebox.
	 * <p>
	 * You might override this method to provide your own localized format.
	 */
	protected String getLocalizedFormat() {
		return new SimpleDateFormat(getRealFormat(),
			_locale != null ? _locale: Locales.getCurrent()).toLocalizedPattern();
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
	 * Sets whether or not date/time parsing is to be lenient.
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
	 * @param value the date to be assigned to this datebox.<br/>
	 * Notice that, if this datebox does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 */
	public void setValue(Date value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

/** Sets the date format.
<p>If null or empty is specified, {@link #getDefaultFormat} is assumed.
Since 5.0.7, you could specify one of the following reserved words,
and {@link DateFormats#getDateFormat} or {@link DateFormats#getDateTimeFormat}
will be used to retrieve the real format.
<table border=0 cellspacing=3 cellpadding=0>
<tr>
<td>short</td>
<td>{@link DateFormats#getDateFormat} with {@link DateFormat#SHORT}</td>
</tr>
<tr>
<td>medium</td>
<td>{@link DateFormats#getDateFormat} with {@link DateFormat#MEDIUM}</td>
</tr>
<tr>
<td>long</td>
<td>{@link DateFormats#getDateFormat} with {@link DateFormat#LONG}</td>
</tr>
<tr>
<td>full</td>
<td>{@link DateFormats#getDateFormat} with {@link DateFormat#FULL}</td>
</tr>
</table>

<p>To specify a date/time format, you could specify two reserved words, separated
by a plus. For example, "medium+short" means 
{@link DateFormats#getDateTimeFormat} with the medium date styling and
the short time styling.

<p>In additions, the format could be a cominbation of the following pattern letters:
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
		if (format == null) {
			format =  "";
		} else if (format.length() != 0) {
			boolean bCustom;
			int j = format.indexOf('+');
			if (j > 0) {
				bCustom = toStyle(format.substring(j + 1)) == -111
					|| toStyle(format.substring(0, j)) == -111;
			} else {
				bCustom = toStyle(format) == -111;
			}
			if (bCustom)
				getDateFormat(format); // make sure the format is correct
		}
		super.setFormat(format);
		smartUpdate("localizedFormat", getLocalizedFormat());
	}
	/** Returns the styling index, or -111 if not matched. */
	/*package*/static int toStyle(String format) {
		if ("short".equals(format = format.trim().toLowerCase()))
			return DateFormat.SHORT;
		if ("medium".equals(format))
			return DateFormat.MEDIUM;
		if ("long".equals(format))
			return DateFormat.LONG;
		if ("full".equals(format))
			return DateFormat.FULL;
		return -111; //not found
	}

	/** Returns the real format, i.e., the combination of the format patterns,
	 * such as yyyy-MM-dd.
	 * <p>As described in {@link #setFormat}, a developer could specify
	 * an abstract name, such as short, or an empty string as the format,
	 * and this method will convert it to a real date/time format.
	 * @since 5.0.7
	 */
	public String getRealFormat() {
		final String format = getFormat();
		if (format == null || format.length() == 0)
			return getDefaultFormat(); //backward compatible

		int ds = format.indexOf('+');
		if (ds > 0) {
			int ts = toStyle(format.substring(ds + 1));
			if (ts != -111) {
				ds = toStyle(format.substring(0, ds));
				if (ds != -111)
					return DateFormats.getDateTimeFormat(ds, ts, _locale, DEFAULT_FORMAT + " " + Timebox.DEFAULT_FORMAT);
			}
		} else {
			ds = toStyle(format);
			if (ds != -111)
				return DateFormats.getDateFormat(ds, _locale, DEFAULT_FORMAT);
		}
		return format;
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
				_tzone = _dtzones.contains(tzone) ? tzone : _dtzones.get(0);
			} else {
				_tzone = tzone;
			}
			smartUpdate("timeZone", _tzone.getID());
			smartUpdate("_value", marshall(_value));
		}
	}
	/** Sets the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 * @param id the time zone's ID, such as "America/Los_Angeles".
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
	public List<TimeZone> getDisplayedTimeZones() {
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
	public void setDisplayedTimeZones(List<TimeZone> dtzones) {
		if (dtzones != null && dtzones.isEmpty())
			dtzones = null;
		if (_dtzones != dtzones) {
			_dtzones = dtzones;
			StringBuffer sb = new StringBuffer();
			int i = 0;
			for (Iterator<TimeZone> it = dtzones.iterator(); it.hasNext(); i++) {
				if(i != 0) sb.append(",");
				sb.append(it.next().getID());
			}
			smartUpdate("displayedTimeZones", sb.toString());
			if (_tzone == null && _dtzones != null && _dtzones.get(0) != null)
				_tzone = _dtzones.get(0);
		}
	}
	/**
	 * Sets a catenation of a list of the time zones' ID, separated by comma,
	 * that will be displayed at the client and allow user to select.
	 * <p>The time zone is retrieved by calling TimeZone.getTimeZone().
	 * @param dtzones a catenation of a list of the timezones' ID, such as
	 * <code>"America/Los_Angeles,GMT+8"</code>
	 * @see #setDisplayedTimeZones(List)
	 * @since 3.6.3
	 */
	public void setDisplayedTimeZones(String dtzones) {
		if (dtzones == null || dtzones.length() == 0) {
			setDisplayedTimeZones((List<TimeZone>)null);
			return;
		}
		
		LinkedList<TimeZone> list = new LinkedList<TimeZone>();
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
	 * Sets whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * @since 3.6.3
	 */
	public void setTimeZonesReadonly(boolean readonly) {
		if (readonly != _dtzonesReadonly) {
			_dtzonesReadonly = readonly;
			smartUpdate("timeZonesReadonly", _dtzonesReadonly);
		}
	}

	/** Returns the locale associated with this datebox,
	 * or null if {@link Locales#getCurrent} is preferred.
	 * @since 5.0.7
	 */
	public Locale getLocale() {
		return _locale;
	}
	/** Sets the locale used to identify the format of this datebox.
	 * <p>Default: null (i.e., {@link Locales#getCurrent}, the current locale
	 * is assumed)
	 * @since 5.0.7
	 */
	public void setLocale(Locale locale) {
		if (!Objects.equals(_locale, locale)) {
			_locale = locale;
			invalidate();
		}
	}
	
	private static Map loadSymbols(Locale locale) {
		WaitLock lock = null;
		for (;;) {
			final Object o;
			synchronized (_symbols) {	
				o = _symbols.get(locale);
				if (o == null)
					_symbols.put(locale, lock = new WaitLock()); //lock it
			}

			if (o instanceof Map)
				return (Map)o;
			if (o == null)
				break; //go to load the symbols

			//wait because some one is creating the servlet
			if (!((WaitLock)o).waitUntilUnlock(5*60*1000))
				log.warning("Take too long to wait loading localized symbol: "+locale
					+"\nTry to load again automatically...");
		} //for(;;)
		
		try {
			
			// the following implementation is referred to 
			// org.zkoss.zk.ui.http.Wpds#getDateJavaScript()
			final Map<String, Object> map = new HashMap<String, Object>();
			final Calendar cal = Calendar.getInstance(locale);
			int firstDayOfWeek = Utils.getFirstDayOfWeek();
			cal.clear();
	
			if (firstDayOfWeek < 0)
				firstDayOfWeek = cal.getFirstDayOfWeek();
			map.put("DOW_1ST",
					Integer.valueOf(firstDayOfWeek - Calendar.SUNDAY));
	
			final boolean zhlang = locale.getLanguage().equals("zh");
			SimpleDateFormat df = new SimpleDateFormat("E", locale);
			final String[] sdow = new String[7], s2dow = new String[7];
			for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
				cal.set(Calendar.DAY_OF_WEEK, j);
				sdow[k] = df.format(cal.getTime());
				if (++j > Calendar.SATURDAY)
					j = Calendar.SUNDAY;
	
				if (zhlang) {
					s2dow[k] = sdow[k].length() >= 3 ? sdow[k].substring(2)
							: sdow[k];
				} else {
					final int len = sdow[k].length();
					final char cc = sdow[k].charAt(len - 1);
					s2dow[k] = cc == '.' || cc == ',' ? sdow[k].substring(
							0, len - 1) : sdow[k];
				}
			}
			df = new SimpleDateFormat("G", locale);
			map.put("ERA", df.format(new java.util.Date()));
	
			Calendar ec = Calendar.getInstance(Locale.ENGLISH);
			Calendar lc = Calendar.getInstance(locale);
			map.put("YDELTA",
					Integer.valueOf(lc.get(Calendar.YEAR)
							- ec.get(Calendar.YEAR)));
	
			df = new SimpleDateFormat("EEEE", locale);
			final String[] fdow = new String[7];
			for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
				cal.set(Calendar.DAY_OF_WEEK, j);
				fdow[k] = df.format(cal.getTime());
				if (++j > Calendar.SATURDAY)
					j = Calendar.SUNDAY;
			}
	
			df = new SimpleDateFormat("MMM", locale);
			final String[] smon = new String[12], s2mon = new String[12];
			for (int j = 0; j < 12; ++j) {
				cal.set(Calendar.MONTH, j);
				smon[j] = df.format(cal.getTime());
	
				if (zhlang) {
					s2mon[j] = smon[0].length() >= 2 ? // remove the last
														// char
					smon[j].substring(0, smon[j].length() - 1)
							: smon[j];
				} else {
					final int len = smon[j].length();
					final char cc = smon[j].charAt(len - 1);
					s2mon[j] = cc == '.' || cc == ',' ? smon[j].substring(
							0, len - 1) : smon[j];
				}
			}
	
			df = new SimpleDateFormat("MMMM", locale);
			final String[] fmon = new String[12];
			for (int j = 0; j < 12; ++j) {
				cal.set(Calendar.MONTH, j);
				fmon[j] = df.format(cal.getTime());
			}
	
			map.put("SDOW", sdow);
			if (Objects.equals(s2dow, sdow))
				map.put("S2DOW", sdow);
			else map.put("S2DOW", s2dow);
			if (Objects.equals(fdow, sdow))
				map.put("FDOW", sdow);
			else map.put("FDOW", fdow);
	
			map.put("SMON", smon);
			if (Objects.equals(s2mon, smon))
				map.put("S2MON", smon);
			else map.put("S2MON", s2mon);
	
			if (Objects.equals(fmon, smon))
				map.put("FMON", smon);
			else map.put("FMON", fmon);
	
			// AM/PM available since ZK 3.0
			df = new SimpleDateFormat("a", locale);
			cal.set(Calendar.HOUR_OF_DAY, 3);
			final String[] ampm = new String[2];
			ampm[0] = df.format(cal.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 15);
			ampm[1] = df.format(cal.getTime());
	
			map.put("APM", ampm);
			
			synchronized (_symbols) {
				_symbols.put(locale, map);
				cloneSymbols();
			}
			
			return map;
		} finally {
			lock.unlock();
		}
	}
	
	private static void cloneSymbols() {
		final Map<Locale, Object> symbols = new HashMap<Locale, Object>();
		for (Map.Entry<Locale, Object> me: _symbols.entrySet()) {
			final Object value = me.getValue();
			if (value instanceof Map)
				symbols.put(me.getKey(), value);
		}
		_symbols = symbols;
	}
	
	private static Object[] getRealSymbols(Locale locale, Datebox box) {
		if (locale != null) {
			final String localeName = locale.toString();
			if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(
					box.getDesktop(),
					box.getClass().getName() + localeName)) {
				Map symbols = (Map)_symbols.get(locale);
				if (symbols == null)
					symbols = loadSymbols(locale);
				return new Object[] {localeName, symbols };
			}
			return new Object[] {localeName, null };
		}
		return null;
	}
	
	/** Sets the locale used to identify the format of this datebox.
	 * <p>Default: null (i.e., {@link Locales#getCurrent}, the current locale
	 * is assumed)
	 * @since 5.0.7
	 */
	public void setLocale(String locale) {
		setLocale(locale != null && locale.length() > 0 ?
			Locales.getLocale(locale): null);
	}

	/**
	 * Drops down or closes the calendar to select a date.
	 * only works while visible
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (isVisible()) {
			if (open)
				open();
			else
				close();
		}
	}

	/**
	 * Drops down the calendar to select a date. The same as setOpen(true).
	 * 
	 * @since 3.0.1
	 */
	public void open() {
		response("open", new AuInvoke(this, "setOpen", true)); //don't use smartUpdate
	}

	/**
	 * Closes the calendar if it was dropped down. The same as setOpen(false).
	 * 
	 * @since 3.0.1
	 */
	public void close() {
		response("open", new AuInvoke(this, "setOpen", false));//don't use smartUpdate
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
			final Map<String, Object> data = request.getData();
			String timezone = (String)data.get("timezone");
			setTimeZone(timezone);
		} else 
			super.service(request, everError);
	}
	
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 *
	 * <p>If a component requires more client controls, it is suggested to
	 * override {@link #getExtraCtrl} to return an instance that extends from
	 * this class.
	 */
	protected class ExtraCtrl extends FormatInputElement.ExtraCtrl implements Blockable {
		public boolean shallBlock(AuRequest request) {
			// B50-3316103: special case of readonly component: do not block onChange and onSelect
			final String cmd = request.getCommand();
			if(Events.ON_OPEN.equals(cmd))
				return false;
			return !Components.isRealVisible(Datebox.this) || isDisabled() || 
				(isReadonly() && Events.ON_CHANGING.equals(cmd));
		}
	}
	
	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: "between 20071012 and 20071223", "before 20080103"
	 */
	// -- super --//
	public void setConstraint(String constr) {
		setConstraint(constr != null ? new SimpleDateConstraint(constr) : null); // Bug 2564298
	}
	protected Object marshall(Object value) {
		if (value == null || _tzone == null) return value;
		Date date = (Date) value;
		return new Date((date).getTime() - Dates.getTimezoneOffset(TimeZones.getCurrent(), date) + Dates.getTimezoneOffset(_tzone, date));
	}
	protected Object unmarshall(Object value) {
		if (value == null || _tzone == null) return value;
		Date date = (Date) value;
		return new Date((date).getTime() + Dates.getTimezoneOffset(TimeZones.getCurrent(), date) - Dates.getTimezoneOffset(_tzone, date));
	}
	protected Object coerceFromString(String value) throws WrongValueException {
		if (value == null || value.length() == 0)
			return null;

		final String fmt = getRealFormat();
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
		if (value == null)
			return "";
		if (value instanceof Date) {
			final DateFormat df = getDateFormat(getRealFormat());
			return df.format((Date) value);
		}
		// ZK-631, will receive the "wrong" string value
		// if set both custom constraint and format
		// for showCustomError
		throw showCustomError(new WrongValueException(this,
				MZul.DATE_REQUIRED, new Object[] { value, getRealFormat() }));
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
		final DateFormat df = new SimpleDateFormat(fmt,
			_locale != null ? _locale: Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone : TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}
	private String getUnformater() {
		if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(
				getDesktop(), "org.zkoss.zul.Datebox.unformater.isSent")) {
			return Library.getProperty("org.zkoss.zul.Datebox.unformater");
		}
		return null;
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
		renderer.render("localizedFormat", getLocalizedFormat());
		
		String unformater = getUnformater();
		if (!Strings.isBlank(unformater))
			renderer.render("unformater", unformater);

		if (_locale != null)
			renderer.render("localizedSymbols", getRealSymbols(_locale, this));
	}
}
