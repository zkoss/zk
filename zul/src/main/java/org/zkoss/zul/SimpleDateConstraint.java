/* SimpleDateConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 25 12:06:30     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.mesg.Messages;
import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * A simple date constraint.
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class SimpleDateConstraint extends AbstractSimpleDateTimeConstraint<Date> {
	public SimpleDateConstraint(int flags) {
		super(flags);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(int flags, String errmsg) {
		super(flags, errmsg);
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleDateConstraint(Pattern regex, String errmsg) {
		super(regex, errmsg);
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleDateConstraint(int flags, Pattern regex, String errmsg) {
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
	public SimpleDateConstraint(int flags, Date begin, Date end, String errmsg) {
		super(flags, errmsg);
		_beg = begin;
		_end = end;
		fixConstraint();
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: "between 20071012 and 20071223", "before 20080103"
	 */
	public SimpleDateConstraint(String constraint) {
		super(constraint);
		checkValidConstraint(constraint);
	}

	/**
	 * Builds a {@link SimpleDateConstraint} whose date literals are parsed in
	 * {@code tzone} instead of the JVM default. Use this from components that
	 * expose their own {@code timeZone} property so the constraint bounds line
	 * up with the component's interpretation of "midnight" — e.g. a
	 * Daterangebox configured for {@code Asia/Tokyo} parsing
	 * {@code "before 20260101"} should anchor the bound to midnight Tokyo
	 * time, not the server's JVM default.
	 *
	 * <p>This works because {@link SimpleConstraint} parses lazily: the date
	 * literals are only consumed on the first {@link #validate} call. The
	 * {@link AbstractSimpleDateTimeConstraint#setTimeZone(TimeZone)} override
	 * resets the lazy-parse flag immediately after construction, so by the
	 * time {@link #parseFrom} runs the right zone is in place.
	 *
	 * <p><b>Limitation.</b> The supplied {@code tzone} controls how the
	 * constraint's own date literals are parsed and how the candidate value
	 * is normalised in {@link #validate0}, but the {@code NO_PAST} /
	 * {@code NO_FUTURE} / {@code NO_TODAY} flag checks performed by
	 * {@code super.validate} compare against {@code Dates.today()} — i.e.
	 * today in the JVM-default zone — not against today-in-{@code tzone}.
	 * Near the date-line a value that is "today" in {@code tzone} can be
	 * "yesterday" or "tomorrow" in the JVM zone, so the flag verdict may
	 * differ from a user's zone-aware expectation. A fully zone-aware
	 * today-flag check is tracked as a separate enhancement.
	 *
	 * @since 10.4.0
	 */
	public static SimpleDateConstraint forTimeZone(String constraint, TimeZone tzone) {
		SimpleDateConstraint c = new SimpleDateConstraint(constraint);
		if (tzone != null) c.setTimeZone(tzone);
		return c;
	}

	private void checkValidConstraint(String constraint) {
		int constraintFormatLength = "yyyyMMdd".length(); // default constraint format is yyyyMMdd
		String[] constraintParts = constraint.split(",");
		Pattern numberStartWithKeyword = Pattern.compile("((?<=before |after |between |and )(\\d+))");
		for (String part: constraintParts) {
			String partWithoutMessage = part.split(":")[0];
			Matcher matcher = numberStartWithKeyword.matcher(partWithoutMessage);
			while (matcher.find()) {
				if (matcher.group().length() != constraintFormatLength)
					throw new UiException("Invalid constraint: " + part + " (the constraint should be formatted in yyyyMMdd)");
			}
		}
	}

	@Override
	protected void fixConstraint() {
		if ((_flags & NO_FUTURE) != 0 && _end == null)
			_end = Dates.today();
		if ((_flags & NO_PAST) != 0 && _beg == null)
			_beg = Dates.today();
	}

	@Override
	protected Date parseFrom(String val) throws UiException {
		try {
			return getDateFormat(_tzone).parse(val.trim());
		} catch (ParseException ex) {
			throw new UiException("Not a date: " + val + ". Format: yyyyMMdd", ex);
		}
	}

	private static SimpleDateFormat getDateFormat(TimeZone tzone) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locales.getCurrent());
		df.setTimeZone(tzone != null ? tzone : TimeZones.getCurrent());
		return df;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		super.validate(comp, value);
		if (value instanceof Date) {
			validate0(comp, Dates.beginOfDate((Date) value, _tzone));
		}
	}

	/**
	 * Validates a date range against this constraint's begin / end bounds.
	 *
	 * <p><b>Semantics — per-endpoint, not range-as-whole.</b> The
	 * single-value {@link #validate(Component, Object)} is applied to
	 * {@code begin} and to {@code end} independently. A "between 20260101
	 * and 20261231" constraint accepts {@code (2026-03-01, 2026-09-01)}
	 * because each endpoint individually falls within the bound; it does
	 * NOT check whether the (begin, end) pair as a unit satisfies any
	 * cross-bound predicate (e.g. "the range must be wholly inside Q2").
	 * Callers that need cross-bound rules should add them in their own
	 * component (Daterangebox uses {@code minNights}/{@code maxNights}
	 * properties for that purpose).
	 *
	 * <p>Either end may be {@code null} for open-ended ranges. When both
	 * are non-null, the range is also required to be non-reversed
	 * ({@code begin} &le; {@code end}). Asymmetry with single-value
	 * {@link #validate(Component, Object)}: a {@code null} endpoint here
	 * is silently skipped (so {@code NO_EMPTY} does <em>not</em> fire for
	 * it). Emptiness for range-aware components is governed by the
	 * component's own {@code allowEmpty} property, not by base-class flags.
	 *
	 * <p>Note: both ends are normalised via {@link Dates#beginOfDate} before
	 * validation, so the time-of-day component is ignored. A constraint such
	 * as "before 2026-01-01 12:00" will be treated as "before 2026-01-01"
	 * for the end-of-range check — matching the existing single-value
	 * {@link #validate(Component, Object)} behaviour.
	 *
	 * @param comp the component (used for error message context)
	 * @param begin the begin date, or {@code null} if unbounded
	 * @param end the end date, or {@code null} if unbounded
	 * @throws WrongValueException if either bound violates the constraint,
	 *         or if {@code begin} is later than {@code end}
	 * @since 10.4.0
	 */
	public void validateRange(Component comp, Date begin, Date end) throws WrongValueException {
		// Normalise both endpoints to calendar-day start before the reverse
		// check so the semantics match the per-endpoint validate() below
		// (which also uses Dates.beginOfDate). Without this, a same-day pair
		// like (2026-01-01 23:00, 2026-01-01 09:00) would throw "begin later
		// than end" even though every individual day-strict bound check
		// passes — confusing for users whose UX only models calendar days.
		final Date beginDay = begin != null ? Dates.beginOfDate(begin, _tzone) : null;
		final Date endDay = end != null ? Dates.beginOfDate(end, _tzone) : null;
		if (beginDay != null && endDay != null && beginDay.after(endDay))
			throw new WrongValueException(comp,
					Messages.get(MZul.RANGE_BEGIN_AFTER_END));
		// Reuse the already-normalised day values for the bounds check
		// (validate0) instead of letting validate(comp, raw) recompute
		// Dates.beginOfDate internally for a third and fourth time.
		// super.validate still runs on the raw endpoint so flag checks
		// (NO_PAST / NO_FUTURE / NO_TODAY) apply per-endpoint exactly as
		// the single-value validate() would.
		if (begin != null) {
			super.validate(comp, begin);
			validate0(comp, beginDay);
		}
		if (end != null) {
			super.validate(comp, end);
			validate0(comp, endDay);
		}
	}

	@Override
	protected String valueToString(Component comp, Date value) {
		if (value == null)
			return "";
		if (comp instanceof Datebox)
			return ((Datebox) comp).coerceToString(value);
		return getDateFormat(_tzone).format(value);
	}
}
