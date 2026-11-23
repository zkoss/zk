/* SimpleLocalDateConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 17:04:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A simple {@link LocalTime} constraint.
 * The time format is HHmmss (24-hour clock system, ISO 8601).
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class SimpleLocalTimeConstraint extends AbstractSimpleDateTimeConstraint<LocalTime> {
	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public SimpleLocalTimeConstraint(int flags) {
		super(flags);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleLocalTimeConstraint(int flags, String errmsg) {
		super(flags, errmsg);
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleLocalTimeConstraint(Pattern regex, String errmsg) {
		super(regex, errmsg);
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleLocalTimeConstraint(int flags, Pattern regex, String errmsg) {
		super(flags, regex, errmsg);
	}

	/** Constructs a constraint with beginning and ending date.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param begin the beginning date, or null if no constraint at the beginning
	 * date.
	 * @param end the ending date, or null if no constraint at the ending
	 * date.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleLocalTimeConstraint(int flags, LocalTime begin, LocalTime end, String errmsg) {
		super(flags, errmsg);
		_beg = begin;
		_end = end;
		fixConstraint();
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: between 093000 and 183000, before 210000
	 */
	public SimpleLocalTimeConstraint(String constraint) {
		super(constraint);
	}

	protected void fixConstraint() {
		if ((_flags & NO_FUTURE) != 0 && _end == null)
			_end = LocalTime.now();
		if ((_flags & NO_PAST) != 0 && _beg == null)
			_beg = LocalTime.now();
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		super.validate(comp, value);
		if (value instanceof Date) {
			final Instant instant = ((Date) value).toInstant();
			validate0(comp,
				LocalDateTime.ofInstant(instant, getZoneId(_tzone)).toLocalTime()
			);
		}
	}

	@Override
	protected LocalTime parseFrom(String val) throws UiException {
		try {
			return LocalTime.from(getDateFormat(_tzone).parse(val.trim()));
		} catch (DateTimeException ex) {
			throw new UiException("Not a time: " + val + ". Format: HHmmss", ex);
		}
	}

	private static DateTimeFormatter getDateFormat(TimeZone tzone) {
		return DateTimeFormatter.ofPattern("HHmmss", Locales.getCurrent())
				.withZone(getZoneId(tzone));
	}

	private static ZoneId getZoneId(TimeZone tzone) {
		return Optional.ofNullable(tzone).orElse(TimeZones.getCurrent()).toZoneId();
	}

	@Override
	protected String valueToString(Component comp, LocalTime value) {
		if (value == null)
			return "";
		if (comp instanceof Datebox) {
			final ZoneId zoneId = getZoneId(_tzone);
			return ((Datebox) comp).coerceToString(
				Date.from(ZonedDateTime.of(LocalDate.now(zoneId), value, zoneId).toInstant())
			);
		}
		return getDateFormat(_tzone).format(value);
	}
}
