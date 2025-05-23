/* DateTimeFormatInputElement.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 12 15:00:14 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.AbstractSimpleDateTimeConstraint;
import org.zkoss.zul.Constraint;

/**
 * A skeletal implementation for date/time type input box.
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public abstract class DateTimeFormatInputElement extends FormatInputElement {
	protected Locale _locale;
	protected TimeZone _tzone = TimeZones.getCurrent();

	/** Returns the locale associated with this component,
	 * or null if {@link Locales#getCurrent} is preferred.
	 * @since 5.0.7
	 */
	public Locale getLocale() {
		return _locale;
	}

	/** Sets the locale used to identify the format of this component.
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

	/** Sets the locale used to identify the format of this component.
	 * <p>Default: null (i.e., {@link Locales#getCurrent}, the current locale
	 * is assumed)
	 * @since 5.0.7
	 */
	public void setLocale(String locale) {
		setLocale(locale != null && locale.length() > 0 ? Locales.getLocale(locale) : null);
	}

	/**
	 * Returns the time zone that this component belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}

	/** Sets the time zone that this component belongs to, or null if
	 * the default time zone is used.

	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone) {
		if (_tzone != tzone) {
			_tzone = tzone;
			Constraint cst = getConstraint();
			if (cst instanceof AbstractSimpleDateTimeConstraint)
				((AbstractSimpleDateTimeConstraint) cst).setTimeZone(_tzone);
			smartUpdate("timeZone", getFormattedTimeZone());
			smartUpdate("_value", marshall(_value));
		}
	}

	/** Sets the time zone that this component belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 * @param id the time zone's ID, such as "America/Los_Angeles".
	 * The time zone will be retrieved by calling TimeZone.getTimeZone(id).
	 */
	public void setTimeZone(String id) {
		setTimeZone(TimeZone.getTimeZone(id));
	}

	protected ZoneId getZoneId() {
		return Optional.ofNullable(getTimeZone())
				.map(TimeZone::toZoneId)
				.orElse(ZoneId.systemDefault());
	}

	@Override
	public void setConstraint(Constraint constr) {
		if (constr instanceof AbstractSimpleDateTimeConstraint) {
			((AbstractSimpleDateTimeConstraint) constr).setTimeZone(_tzone);
		}
		super.setConstraint(constr);
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
	 * @param value the date to be assigned to this component.<br/>
	 * Notice that, if this component does not allow users to select the time
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

	/**
	 * Returns the value (in ZonedDateTime), might be null unless a constraint stops it.
	 *
	 * @exception WrongValueException if user entered a wrong value
	 * @since 9.0.0
	 */
	public ZonedDateTime getValueInZonedDateTime() throws WrongValueException {
		return toZonedDateTime((Date) getTargetValue());
	}

	/**
	 * Sets the value (in ZonedDateTime).
	 *
	 * @exception WrongValueException if value is wrong
	 * @param value the date to be assigned to this component.<br/>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @since 9.0.0
	 */
	public void setValueInZonedDateTime(ZonedDateTime value) throws WrongValueException {
		if (value != null) {
			setTimeZone(TimeZone.getTimeZone(value.getZone()));
		}
		setValue(value == null ? null : Date.from(value.toInstant()));
	}

	/**
	 * Returns the value (in LocalDateTime), might be null unless a constraint stops it.
	 *
	 * @exception WrongValueException if user entered a wrong value
	 * @since 9.0.0
	 */
	public LocalDateTime getValueInLocalDateTime() throws WrongValueException {
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalDateTime();
	}

	/**
	 * Sets the value (in LocalDateTime).
	 *
	 * @exception WrongValueException if value is wrong
	 * @param value the date to be assigned to this component.<br/>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @since 9.0.0
	 */
	public void setValueInLocalDateTime(LocalDateTime value) throws WrongValueException {
		setValue(toDate(value));
	}

	/**
	 * Returns the value (in LocalDate), might be null unless a constraint stops it.
	 *
	 * @exception WrongValueException if user entered a wrong value
	 * @since 9.0.0
	 */
	public LocalDate getValueInLocalDate() throws WrongValueException {
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalDate();
	}

	/**
	 * Sets the value (in LocalDate).
	 *
	 * @exception WrongValueException if value is wrong
	 * @param value the date to be assigned to this component.<br/>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @since 9.0.0
	 */
	public void setValueInLocalDate(LocalDate value) throws WrongValueException {
		setValue(toDate(value));
	}

	/**
	 * Returns the value (in LocalTime), might be null unless a constraint stops it.
	 *
	 * @exception WrongValueException if user entered a wrong value
	 * @since 9.0.0
	 */
	public LocalTime getValueInLocalTime() throws WrongValueException {
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalTime();
	}

	/**
	 * Sets the value (in LocalTime).
	 *
	 * @exception WrongValueException if value is wrong
	 * @param value the date to be assigned to this component.<br/>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @since 9.0.0
	 */
	public void setValueInLocalTime(LocalTime value) throws WrongValueException {
		setValue(toDate(value));
	}

	protected ZonedDateTime toZonedDateTime(Date value) {
		return value == null ? null : ZonedDateTime.ofInstant(value.toInstant(), getZoneId());
	}

	protected Date toDate(LocalDateTime value) {
		return value == null ? null : Date.from(value.atZone(getZoneId()).toInstant());
	}

	protected Date toDate(LocalDate value) {
		return value == null ? null : toDate(value.atStartOfDay());
	}

	protected Date toDate(LocalTime value) {
		final Date rawValue = (Date) getRawValue();
		return value == null ? null : toDate(
			value.atDate(rawValue == null ? LocalDate.now(getZoneId()) : toZonedDateTime(rawValue).toLocalDate())
		);
	}

	@Override
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		if (_tzone != null)
			renderer.render("timeZone", getFormattedTimeZone());
	}

	private static Map<String, PropertyAccess> _properties = new HashMap<>(5);

	static {
		_properties.put("value", new PropertyAccess<Date>() {
			@Override
			public void setValue(Component cmp, Date value) {
				((DateTimeFormatInputElement) cmp).setValue(value);
			}

			@Override
			public Class<Date> getType() {
				return Date.class;
			}

			@Override
			public Date getValue(Component cmp) {
				return ((DateTimeFormatInputElement) cmp).getValue();
			}
		});
		_properties.put("valueInZonedDateTime", new PropertyAccess<ZonedDateTime>() {
			@Override
			public void setValue(Component cmp, ZonedDateTime val) {
				((DateTimeFormatInputElement) cmp).setValueInZonedDateTime(val);
			}

			@Override
			public Class<ZonedDateTime> getType() {
				return ZonedDateTime.class;
			}

			@Override
			public ZonedDateTime getValue(Component cmp) {
				return ((DateTimeFormatInputElement) cmp).getValueInZonedDateTime();
			}
		});
		_properties.put("valueInLocalDateTime", new PropertyAccess<LocalDateTime>() {
			@Override
			public void setValue(Component cmp, LocalDateTime val) {
				((DateTimeFormatInputElement) cmp).setValueInLocalDateTime(val);
			}

			@Override
			public Class<LocalDateTime> getType() {
				return LocalDateTime.class;
			}

			@Override
			public LocalDateTime getValue(Component cmp) {
				return ((DateTimeFormatInputElement) cmp).getValueInLocalDateTime();
			}
		});
		_properties.put("valueInLocalDate", new PropertyAccess<LocalDate>() {
			@Override
			public void setValue(Component cmp, LocalDate val) {
				((DateTimeFormatInputElement) cmp).setValueInLocalDate(val);
			}

			@Override
			public Class<LocalDate> getType() {
				return LocalDate.class;
			}

			@Override
			public LocalDate getValue(Component cmp) {
				return ((DateTimeFormatInputElement) cmp).getValueInLocalDate();
			}
		});
		_properties.put("valueInLocalTime", new PropertyAccess<LocalTime>() {
			@Override
			public void setValue(Component cmp, LocalTime val) {
				((DateTimeFormatInputElement) cmp).setValueInLocalTime(val);
			}

			@Override
			public Class<LocalTime> getType() {
				return LocalTime.class;
			}

			@Override
			public LocalTime getValue(Component cmp) {
				return ((DateTimeFormatInputElement) cmp).getValueInLocalTime();
			}
		});
	}

	@Override
	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	private static final Map<String, String> _javaSystemVOldTimeZoneMappings = new HashMap<>(Map.ofEntries(
		Map.entry("SystemV/AST4", "America/Puerto_Rico"),
		Map.entry("SystemV/AST4ADT", "America/Puerto_Rico"),
		Map.entry("SystemV/EST5", "America/New_York"),
		Map.entry("SystemV/EST5EDT", "America/New_York"),
		Map.entry("SystemV/CST6", "America/Chicago"),
		Map.entry("SystemV/CST6CDT", "America/Chicago"),
		Map.entry("SystemV/MST7", "America/Denver"),
		Map.entry("SystemV/MST7MDT", "America/Denver"),
		Map.entry("SystemV/PST8", "America/Los_Angeles"),
		Map.entry("SystemV/PST8PDT", "America/Los_Angeles"),
		Map.entry("SystemV/YST9", "America/Anchorage"),
		Map.entry("SystemV/YST9YDT", "America/Anchorage"),
		Map.entry("SystemV/HST10", "Pacific/Honolulu")
	));
	private static final Pattern _gmtWholeHourPositive = Pattern.compile("^GMT\\+(0\\d|1[0-2]):00$", Pattern.CASE_INSENSITIVE);
	private static final Pattern _gmtWholeHourNegative = Pattern.compile("^GMT-(0\\d|1[0-4]):00$", Pattern.CASE_INSENSITIVE);

	/**
	 * Returns the formatted timezone ID,
	 * convert the timezone IDs for moment.js recognized format.
	 * <p>
	 * There are 3 cases we need to handle here, and 1 case will be handled at client side:
	 * <ol>
	 *     <li>
	 *         Java old mappings (refer from sun.util.calendar.ZoneInfoFile.java#oldMappings).
	 *         <p>
	 *         Instead of TimeZone.getID(), TimeZone.toZoneId().getId() can get the mapped IANA timezone of Java old mappings,
	 *         can solve cases below (refer from sun.util.calendar.ZoneInfoFile.java#oldMappings):
	 *         <pre>
	 *             "ACT"  "AET"  "AGT"  "ART"  "AST"
	 *             "BET"  "BST"  "CAT"  "CNT"  "CST"
	 *             "CTT"  "EAT"  "ECT"  "IET"  "IST"
	 *             "JST"  "MIT"  "NET"  "NST"  "PLT"
	 *             "PNT"  "PRT"  "PST"  "SST"  "VST"
	 *         </pre>
	 *     </li>
	 *     <li>
	 *         Java old timezones "SystemV/*", the old timezone ID is mapped to the IANA timezone ID 
	 *         by {@link DateTimeFormatInputElement#_javaSystemVOldTimeZoneMappings}.
	 *     </li>
	 *     <li>
	 *         Java GMT whole hour offset timezones "GMT+xx:00" and "GMT-xx:00", it should be converted 
	 *         to "Etc/GMT+x" and "Etc/GMT-x".
	 *     </li>
	 *     <li>
	 *         Java GMT non-whole hour offset timezones, will be handled at client side.
	 *     </li>
	 *     <li>
	 *         For other cases, the timezone ID is already recognized by moment.js.
	 *     </li>
	 * </ol>
	 */
	private String getFormattedTimeZone() {
		String zoneID = _tzone.toZoneId().getId();
		if (_javaSystemVOldTimeZoneMappings.containsKey(zoneID)) {
			zoneID = _javaSystemVOldTimeZoneMappings.get(zoneID);
		} else if (_gmtWholeHourPositive.matcher(zoneID).matches()) {
			zoneID = "Etc/GMT-" + Integer.parseInt(zoneID.substring(4, 6));
		} else if (_gmtWholeHourNegative.matcher(zoneID).matches()) {
			zoneID = "Etc/GMT+" + Integer.parseInt(zoneID.substring(4, 6));
		}
		return zoneID;
	}
}
