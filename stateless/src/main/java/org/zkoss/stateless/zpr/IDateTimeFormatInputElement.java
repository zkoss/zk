/* IDateTimeFormatInputElement.java

	Purpose:

	Description:

	History:
		Tue Nov 09 09:51:55 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.DateTimeFormatInputElement;

/**
 * Immutable {@link DateTimeFormatInputElement} component.
 *
 * <p>A skeletal implementation for date/time type input box.</p>
 *
 * @author katherine
 */
public interface IDateTimeFormatInputElement<I extends IDateTimeFormatInputElement> extends IFormatInputElement<I, Date> {

	/** Returns the locale associated with this number input component
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred</p>
	 */
	@Nullable
	Locale getLocale();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code locale}.
	 *
	 * <p>Sets the locale used to identify the symbols of this number input component.
	 *
	 * @param locale The preferred locale
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withLocale(@Nullable Locale locale);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code locale}.
	 *
	 * <p>Sets the locale used to identify the symbols of this number input component.
	 *
	 * @param locale The preferred locale
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withLocale(String locale) {
		return withLocale(locale != null && locale.length() > 0 ? Locales.getLocale(locale) : null);
	}

	// override super's method for generate Javadoc in Updater
	I withValue(@Nullable Date value);

	/**
	 * Returns the time zone that this component belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	default TimeZone getTimeZone() {
		return TimeZones.getCurrent();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code timeZone}.
	 *
	 * <p>Sets the time zone that this component belongs to, or null if
	 * 	the default time zone is used.
	 *
	 * @param timeZone The preferred timezone
	 * <p>Default: {@link TimeZones#getCurrent}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTimeZone(TimeZone timeZone);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code timeZone}.
	 *
	 * <p>Sets the time zone that this component belongs to, or null if
	 * 	the default time zone is used.
	 *
	 * @param timeZone The preferred timezone
	 * <p>Default: {@link TimeZones#getCurrent}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withTimeZone(String timeZone) {
		return withTimeZone(TimeZone.getTimeZone(timeZone));
	}

	/**
	 * Returns the value (in ZonedDateTime).
	 */
	@Nullable
	default ZonedDateTime getValueInZonedDateTime() {
		return toZonedDateTime(getValue());
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code timeZone}.
	 *
	 * <p>Sets the value (in ZonedDateTime).
	 *
	 * @param timeZone The date to be assigned to this component.<br>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @return A modified copy of the {@code this} object
	 */
	I withValueInZonedDateTime(@Nullable ZonedDateTime timeZone);

	/**
	 * Returns the value (in LocalDateTime), might be null unless a constraint stops it.
	 *
	 */
	@Nullable
	default LocalDateTime getValueInLocalDateTime() {
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalDateTime();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code dateTime}.
	 *
	 * <p>Sets the value (in LocalDateTime).
	 *
	 * @param dateTime The date to be assigned to this component.<br>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @return A modified copy of the {@code this} object
	 */
	I withValueInLocalDateTime(@Nullable LocalDateTime dateTime);

	/**
	 * Returns the value (in LocalDate), might be null unless a constraint stops it.
	 */
	@Nullable
	default LocalDate getValueInLocalDate() {
		//convert to rawdate
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalDate();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code date}.
	 *
	 * <p>Sets the value (in LocalDate).
	 *
	 * @param date The date to be assigned to this component.<br>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @return A modified copy of the {@code this} object
	 */
	I withValueInLocalDate(@Nullable LocalDate date);

	/**
	 * Returns the value (in LocalTime), might be null unless a constraint stops it.
	 */
	@Nullable
	default LocalTime getValueInLocalTime() {
		final ZonedDateTime value = getValueInZonedDateTime();
		return value == null ? null : value.toLocalTime();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code time}.
	 *
	 * <p>Sets the value (in LocalTime).
	 *
	 * @param time The date to be assigned to this component.<br>
	 * Notice that, if this component does not allow users to select the time
	 * (i.e., the format limited to year, month and day), the date specified here
	 * is better to set hour, minutes, seconds and milliseconds to zero
	 * (for the current timezone, {@link TimeZones#getCurrent}), so it is easier
	 * to work with other libraries, such as SQL.
	 * {@link org.zkoss.util.Dates} has a set of utilities to simplify the task.
	 * @return A modified copy of the {@code this} object
	 */
	I withValueInLocalTime(@Nullable LocalTime time);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	@Nullable
	default ZoneId getZoneId() {
		return Optional.ofNullable(getTimeZone())
				.map(TimeZone::toZoneId)
				.orElse(ZoneId.systemDefault());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Date toDate(ZonedDateTime value) {
		return value == null ? null : Date.from(value.toInstant());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Date toDate(LocalDateTime value) {
		return value == null ? null : Date.from(value.atZone(getZoneId()).toInstant());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Date toDate(LocalDate value) {
		return value == null ? null : toDate(value.atStartOfDay());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Date toDate(LocalTime value) {
		final Date rawValue = getValue();
		return value == null ? null : toDate(
				value.atDate(rawValue == null ? LocalDate.now(getZoneId()) : toZonedDateTime(rawValue).toLocalDate())
		);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default int toStyle(String format) {
		if ("short".equals(format = format.trim().toLowerCase(java.util.Locale.ENGLISH)))
			return DateFormat.SHORT;
		if ("medium".equals(format))
			return DateFormat.MEDIUM;
		if ("long".equals(format))
			return DateFormat.LONG;
		if ("full".equals(format))
			return DateFormat.FULL;
		return -111; //not found
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default ZonedDateTime toZonedDateTime(Date value) {
		return value == null ? null : ZonedDateTime.ofInstant(value.toInstant(), getZoneId());
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IFormatInputElement.super.renderProperties(renderer);
		TimeZone _tzone = getTimeZone();
		if (_tzone != null)
			renderer.render("timeZone", _tzone.getID());
	}
}