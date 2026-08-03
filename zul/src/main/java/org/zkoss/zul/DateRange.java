/* DateRange.java

        Purpose:

        Description:

        History:
                Fri May 08 15:15:49 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * An immutable value object representing a {@link Date}-based range, used as
 * the {@code value} object of {@code Daterangebox}.
 *
 * <p>Either end may be {@code null} to denote an open-ended range:
 * <ul>
 *   <li>{@code begin == null && end == null} — empty range.</li>
 *   <li>{@code begin == null && end != null} — open start ("before {@code end}").</li>
 *   <li>{@code begin != null && end == null} — open end ("after {@code begin}").</li>
 *   <li>both non-null — closed range.</li>
 * </ul>
 *
 * <p>The class intentionally does <strong>not</strong> auto-swap when
 * {@code begin > end}; the raw input is preserved so the UI layer can decide
 * how to surface the inversion.
 *
 * <p>Instances are {@link Serializable} and {@link Comparable}: ordering compares
 * {@code begin} first then {@code end}, treating {@code null} as the smallest value.
 *
 * @author peaker
 * @since 10.4.0
 */
public final class DateRange implements Serializable, Comparable<DateRange> {

	private static final long serialVersionUID = 1730482921338451430L;

	private final Date _begin;
	private final Date _end;

	/**
	 * Constructs a new range. Either {@code begin} or {@code end} may be
	 * {@code null} to denote an open-ended range.
	 *
	 * @param begin the begin date (inclusive), or {@code null} for open start
	 * @param end the end date (inclusive), or {@code null} for open end
	 */
	public DateRange(Date begin, Date end) {
		// Defensive copy so the value object remains truly immutable even
		// when callers retain a reference to the original mutable Date.
		this._begin = (begin == null) ? null : new Date(begin.getTime());
		this._end = (end == null) ? null : new Date(end.getTime());
	}

	/**
	 * Static factory equivalent to {@link #DateRange(Date, Date)}.
	 *
	 * @param begin the begin date (inclusive), or {@code null}
	 * @param end the end date (inclusive), or {@code null}
	 * @return a new {@code DateRange}
	 */
	public static DateRange of(Date begin, Date end) {
		return new DateRange(begin, end);
	}

	/**
	 * Returns the begin date, or {@code null} if the range has an open start.
	 *
	 * @return the begin date (defensive copy), or {@code null}
	 */
	public Date getBegin() {
		return (_begin == null) ? null : new Date(_begin.getTime());
	}

	/**
	 * Returns the end date, or {@code null} if the range has an open end.
	 *
	 * @return the end date (defensive copy), or {@code null}
	 */
	public Date getEnd() {
		return (_end == null) ? null : new Date(_end.getTime());
	}

	/**
	 * @return {@code true} if both ends are {@code null}.
	 */
	public boolean isEmpty() {
		return _begin == null && _end == null;
	}

	/**
	 * @return {@code true} if {@code begin} is {@code null} but {@code end} is not.
	 */
	public boolean isOpenStart() {
		return _begin == null && _end != null;
	}

	/**
	 * @return {@code true} if {@code end} is {@code null} but {@code begin} is not.
	 */
	public boolean isOpenEnd() {
		return _begin != null && _end == null;
	}

	/**
	 * @return {@code true} if both ends are non-{@code null}.
	 */
	public boolean isClosed() {
		return _begin != null && _end != null;
	}

	/**
	 * Tests whether the supplied date falls within this range. The comparison
	 * is null-safe and inclusive on both ends. {@code null} ends are treated
	 * as unbounded (negative or positive infinity respectively).
	 *
	 * @param value the date to test, may be {@code null}
	 * @return {@code true} if {@code value} is within the range
	 */
	public boolean contains(Date value) {
		if (value == null || isEmpty())
			return false;
		if (_begin != null && value.before(_begin))
			return false;
		if (_end != null && value.after(_end))
			return false;
		return true;
	}

	/**
	 * Returns the inclusive 24-hour-block count between {@code begin} and
	 * {@code end}. A range whose endpoints fall on the same UTC instant
	 * returns {@code 1}; each additional full 24 h adds {@code 1}.
	 *
	 * <p><b>Wall-clock, not calendar-day, semantics.</b> The result is derived
	 * from {@code end - begin} in milliseconds, divided by 24 h. A range like
	 * {@code 2026-01-01 23:59 → 2026-01-02 00:01} spans two calendar dates
	 * but returns {@code 1} because the wall-clock duration is only two
	 * minutes. Likewise a DST "spring forward" range whose endpoints both
	 * read {@code 02:30} local time can return {@code 0} because the
	 * underlying duration is 23 h. For zone-aware calendar-day counting use
	 * {@link LocalDateRange#getDays()} instead.
	 *
	 * <p>Returns an empty {@link OptionalLong} when the range is not closed
	 * (either end is {@code null}). When the range is closed and inverted
	 * ({@code end} before {@code begin}) a non-positive value is wrapped in
	 * the optional — this class does not auto-swap. The optional return
	 * type is deliberate: it forces callers to acknowledge the "open-ended"
	 * case at compile time, replacing the previous {@code Long.MIN_VALUE}
	 * sentinel which collided with sub-24 h inverted ranges.
	 *
	 * <p><b>Inverted-range semantics.</b> The signed formula is
	 * {@code diffMs >= 0 ? toDays(diffMs) + 1 : toDays(diffMs) - 1}. The
	 * inclusive-count language only applies to forward ranges; for inverted
	 * ranges the returned value is a negative "signed inclusive count"
	 * (e.g. begin=Jan 5, end=Jan 4 → {@code -2}; begin=Jan 5 noon, end=Jan
	 * 5 morning → {@code -1}). Callers reasoning about magnitude should
	 * apply {@code Math.abs(...)}; callers wanting only forward ranges
	 * should swap endpoints before calling, or check {@code compareTo(...)}.
	 *
	 * @return the inclusive 24-h-block count for closed ranges (negative
	 *         when inverted), or {@link OptionalLong#empty()} when either
	 *         endpoint is null
	 */
	public OptionalLong getDays() {
		if (!isClosed())
			return OptionalLong.empty();
		long diffMs = _end.getTime() - _begin.getTime();
		long days = TimeUnit.MILLISECONDS.toDays(diffMs);
		// Branch on diffMs, not days. toDays() truncates toward zero, so
		// a sub-24h inverted range yields days=0 with diffMs<0; checking
		// days>=0 would wrongly classify it as forward and return +1.
		return OptionalLong.of(diffMs >= 0 ? days + 1 : days - 1);
	}

	/**
	 * Tests whether this range overlaps the supplied range. {@code null} ends
	 * are treated as unbounded. An empty range never overlaps.
	 *
	 * @param other the other range
	 * @return {@code true} if the two ranges share at least one instant
	 */
	public boolean overlaps(DateRange other) {
		if (other == null || this.isEmpty() || other.isEmpty())
			return false;
		// this._begin <= other._end (or other._end == null) AND
		// other._begin <= this._end (or this._end == null)
		if (this._begin != null && other._end != null && this._begin.after(other._end))
			return false;
		if (other._begin != null && this._end != null && other._begin.after(this._end))
			return false;
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DateRange other = (DateRange) o;
		return Objects.equals(_begin, other._begin) && Objects.equals(_end, other._end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_begin, _end);
	}

	// Thread-safe formatter shared across instances; replaces the
	// per-toString SimpleDateFormat allocation. UTC-anchored so logs are
	// reproducible regardless of the JVM default zone, matching the
	// LocalDate{,Time}Range / ZonedDateTimeRange siblings whose toString
	// pulls through java.time's own formatters.
	private static final DateTimeFormatter ISO_DATE_UTC = DateTimeFormatter
			.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

	/**
	 * Returns a debug representation in the form {@code "yyyy-MM-dd ~ yyyy-MM-dd"}.
	 * The {@code yyyy-MM-dd} pattern is locale-insensitive and the zone is UTC,
	 * so the same range prints identically across JVMs and locales — this is
	 * meant for logs and diagnostics, not user-facing display. For zone-aware
	 * formatting use the
	 * {@code DateRange} value together with the consumer's own
	 * {@code SimpleDateFormat}/{@code DateTimeFormatter}.
	 */
	@Override
	public String toString() {
		String b = (_begin == null) ? "" : ISO_DATE_UTC.format(Instant.ofEpochMilli(_begin.getTime()));
		String e = (_end == null) ? "" : ISO_DATE_UTC.format(Instant.ofEpochMilli(_end.getTime()));
		return b + " ~ " + e;
	}

	@Override
	public int compareTo(DateRange o) {
		Objects.requireNonNull(o, "other");
		int c = compareNullable(this._begin, o._begin);
		if (c != 0)
			return c;
		return compareNullable(this._end, o._end);
	}

	/**
	 * Null-aware comparison: {@code null} sorts before any non-{@code null} value.
	 */
	private static int compareNullable(Date a, Date b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}
