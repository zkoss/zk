/* LocalDateTimeRange.java

        Purpose:

        Description:

        History:
                Fri May 08 15:16:27 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An immutable {@link LocalDateTime}-based range, the {@code java.time}
 * counterpart to {@link DateRange} for date-with-time scenarios. See
 * {@link DateRange} for general semantics; the only difference here is that
 * {@link #getDuration()} returns a {@link Duration} (or {@code null} when not
 * closed) instead of a day count.
 *
 * @author peaker
 * @since 10.4.0
 */
public final class LocalDateTimeRange implements Serializable, Comparable<LocalDateTimeRange> {

	private static final long serialVersionUID = 3676279883778943234L;

	private final LocalDateTime _begin;
	private final LocalDateTime _end;

	/**
	 * Constructs a new range. Either end may be {@code null}.
	 *
	 * @param begin the begin instant (inclusive), or {@code null}
	 * @param end the end instant (inclusive), or {@code null}
	 */
	public LocalDateTimeRange(LocalDateTime begin, LocalDateTime end) {
		this._begin = begin;
		this._end = end;
	}

	/**
	 * Static factory equivalent to {@link #LocalDateTimeRange(LocalDateTime, LocalDateTime)}.
	 *
	 * @param begin the begin instant, or {@code null}
	 * @param end the end instant, or {@code null}
	 * @return a new {@code LocalDateTimeRange}
	 */
	public static LocalDateTimeRange of(LocalDateTime begin, LocalDateTime end) {
		return new LocalDateTimeRange(begin, end);
	}

	/**
	 * @return the begin instant, or {@code null}
	 */
	public LocalDateTime getBegin() {
		return _begin;
	}

	/**
	 * @return the end instant, or {@code null}
	 */
	public LocalDateTime getEnd() {
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
	 * Inclusive containment test, null-safe.
	 *
	 * @param value the instant to test
	 * @return {@code true} if {@code value} is in the range
	 */
	public boolean contains(LocalDateTime value) {
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
	 * closed range, or {@code null} otherwise. The duration is signed (it can
	 * be negative if {@code end} is before {@code begin}, since the class does
	 * not auto-swap).
	 *
	 * @return the duration, or {@code null} for non-closed ranges
	 */
	public Duration getDuration() {
		if (!isClosed())
			return null;
		return Duration.between(_begin, _end);
	}

	/**
	 * Null-safe overlap test, {@code null} ends treated as unbounded.
	 *
	 * @param other the other range
	 * @return {@code true} if the two share at least one instant
	 */
	public boolean overlaps(LocalDateTimeRange other) {
		if (other == null || this.isEmpty() || other.isEmpty())
			return false;
		if (this._begin != null && other._end != null && this._begin.isAfter(other._end))
			return false;
		if (other._begin != null && this._end != null && other._begin.isAfter(this._end))
			return false;
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LocalDateTimeRange other = (LocalDateTimeRange) o;
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
	public int compareTo(LocalDateTimeRange o) {
		Objects.requireNonNull(o, "other");
		int c = compareNullable(this._begin, o._begin);
		if (c != 0)
			return c;
		return compareNullable(this._end, o._end);
	}

	private static int compareNullable(LocalDateTime a, LocalDateTime b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}
