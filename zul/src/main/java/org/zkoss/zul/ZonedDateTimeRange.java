/* ZonedDateTimeRange.java

        Purpose:

        Description:

        History:
                Fri May 08 15:17:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * An immutable {@link ZonedDateTime}-based range, the {@code java.time}
 * counterpart to {@link DateRange} for time-zone-aware scenarios.
 *
 * <p>Equality and ordering compare {@link ZonedDateTime} values directly, which
 * means two ranges with the same instant but different zones are <em>not</em>
 * equal. {@link #contains(ZonedDateTime)} and {@link #overlaps(ZonedDateTimeRange)}
 * compare by absolute instant ({@link ZonedDateTime#isBefore(java.time.chrono.ChronoZonedDateTime)})
 * and therefore work correctly across zones.
 *
 * @author peaker
 * @since 10.4.0
 */
public final class ZonedDateTimeRange implements Serializable, Comparable<ZonedDateTimeRange> {

	private static final long serialVersionUID = -8731635289479276956L;

	private final ZonedDateTime _begin;
	private final ZonedDateTime _end;

	/**
	 * Constructs a new range. Either end may be {@code null}.
	 *
	 * @param begin the begin instant (inclusive), or {@code null}
	 * @param end the end instant (inclusive), or {@code null}
	 */
	public ZonedDateTimeRange(ZonedDateTime begin, ZonedDateTime end) {
		this._begin = begin;
		this._end = end;
	}

	/**
	 * Static factory equivalent to {@link #ZonedDateTimeRange(ZonedDateTime, ZonedDateTime)}.
	 *
	 * @param begin the begin instant, or {@code null}
	 * @param end the end instant, or {@code null}
	 * @return a new {@code ZonedDateTimeRange}
	 */
	public static ZonedDateTimeRange of(ZonedDateTime begin, ZonedDateTime end) {
		return new ZonedDateTimeRange(begin, end);
	}

	/**
	 * @return the begin instant, or {@code null}
	 */
	public ZonedDateTime getBegin() {
		return _begin;
	}

	/**
	 * @return the end instant, or {@code null}
	 */
	public ZonedDateTime getEnd() {
		return _end;
	}

	/**
	 * @return {@code true} if both ends are {@code null}.
	 */
	public boolean isEmpty() {
		return _begin == null && _end == null;
	}

	/**
	 * @return {@code true} if only {@code begin} is {@code null}.
	 */
	public boolean isOpenStart() {
		return _begin == null && _end != null;
	}

	/**
	 * @return {@code true} if only {@code end} is {@code null}.
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
	 * Inclusive, instant-based containment test (null-safe).
	 *
	 * @param value the instant to test
	 * @return {@code true} if {@code value} is in the range
	 */
	public boolean contains(ZonedDateTime value) {
		if (value == null || isEmpty())
			return false;
		if (_begin != null && value.isBefore(_begin))
			return false;
		if (_end != null && value.isAfter(_end))
			return false;
		return true;
	}

	/**
	 * Returns the {@link Duration} between {@code begin} and {@code end} for a
	 * closed range, or {@code null} otherwise.
	 *
	 * @return the duration, or {@code null} for non-closed ranges
	 */
	public Duration getDuration() {
		if (!isClosed())
			return null;
		return Duration.between(_begin, _end);
	}

	/**
	 * Instant-based, null-safe overlap test.
	 *
	 * @param other the other range
	 * @return {@code true} if the two share at least one instant
	 */
	public boolean overlaps(ZonedDateTimeRange other) {
		if (other == null || this.isEmpty() || other.isEmpty())
			return false;
		if (this._begin != null && other._end != null && this._begin.isAfter(other._end))
			return false;
		if (other._begin != null && this._end != null && other._begin.isAfter(this._end))
			return false;
		return true;
	}

	/**
	 * <strong>Zone-identity semantics.</strong> Equality delegates to
	 * {@link ZonedDateTime#equals(Object)}, which compares the local
	 * date-time <em>and</em> the zone — two ranges representing the same
	 * instants in different zones (e.g. {@code 2026-01-01T12:00+09:00}
	 * vs {@code 2026-01-01T03:00Z}) are <em>not</em> equal here, even
	 * though {@link #contains(ZonedDateTime)} and
	 * {@link #overlaps(ZonedDateTimeRange)} would treat them as
	 * equivalent (those use instant-based {@code isBefore}/{@code isAfter}).
	 * Bind-driven dirty checks that re-set the value to an
	 * instant-equivalent but zone-different range will therefore see
	 * the property as changed.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ZonedDateTimeRange other = (ZonedDateTimeRange) o;
		return Objects.equals(_begin, other._begin) && Objects.equals(_end, other._end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_begin, _end);
	}

	@Override
	public String toString() {
		String b = (_begin == null) ? "" : _begin.toString();
		String e = (_end == null) ? "" : _end.toString();
		return b + " ~ " + e;
	}

	@Override
	public int compareTo(ZonedDateTimeRange o) {
		Objects.requireNonNull(o, "other");
		int c = compareNullable(this._begin, o._begin);
		if (c != 0)
			return c;
		return compareNullable(this._end, o._end);
	}

	private static int compareNullable(ZonedDateTime a, ZonedDateTime b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}
