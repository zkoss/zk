/* LocalDateRange.java

        Purpose:

        Description:

        History:
                Fri May 08 15:16:18 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * An immutable {@link LocalDate}-based range, the {@code java.time} counterpart
 * to {@link DateRange}. See {@link DateRange} for full semantics — same null
 * handling, same non-swapping policy, same comparison contract.
 *
 * @author peaker
 * @since 10.4.0
 */
public final class LocalDateRange implements Serializable, Comparable<LocalDateRange> {

	private static final long serialVersionUID = 9165410618134924839L;

	private final LocalDate _begin;
	private final LocalDate _end;

	/**
	 * Constructs a new range. Either end may be {@code null}.
	 *
	 * @param begin the begin date (inclusive), or {@code null}
	 * @param end the end date (inclusive), or {@code null}
	 */
	public LocalDateRange(LocalDate begin, LocalDate end) {
		// LocalDate is itself immutable, so no defensive copy needed.
		this._begin = begin;
		this._end = end;
	}

	/**
	 * Static factory equivalent to {@link #LocalDateRange(LocalDate, LocalDate)}.
	 *
	 * @param begin the begin date, or {@code null}
	 * @param end the end date, or {@code null}
	 * @return a new {@code LocalDateRange}
	 */
	public static LocalDateRange of(LocalDate begin, LocalDate end) {
		return new LocalDateRange(begin, end);
	}

	/**
	 * @return the begin date, or {@code null}
	 */
	public LocalDate getBegin() {
		return _begin;
	}

	/**
	 * @return the end date, or {@code null}
	 */
	public LocalDate getEnd() {
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
	 * Inclusive containment test, null-safe. {@code null} ends are unbounded.
	 *
	 * @param value the date to test, may be {@code null}
	 * @return {@code true} if {@code value} is in the range
	 */
	public boolean contains(LocalDate value) {
		if (value == null || isEmpty())
			return false;
		if (_begin != null && value.isBefore(_begin))
			return false;
		if (_end != null && value.isAfter(_end))
			return false;
		return true;
	}

	/**
	 * Returns the inclusive day count wrapped in an {@link OptionalLong}.
	 * Same-day closed range returns {@code OptionalLong.of(1)}; inverted
	 * closed ranges return a non-positive value (this class does not auto-
	 * swap). Returns {@link OptionalLong#empty()} for non-closed ranges so
	 * callers must explicitly handle the open-ended case (no more sentinel
	 * value collisions with legitimate inverted-range counts).
	 *
	 * @return the inclusive day count for closed ranges, or
	 *         {@link OptionalLong#empty()} when either endpoint is null
	 */
	public OptionalLong getDays() {
		if (!isClosed())
			return OptionalLong.empty();
		long diff = ChronoUnit.DAYS.between(_begin, _end);
		return OptionalLong.of(diff >= 0 ? diff + 1 : diff - 1);
	}

	/**
	 * Null-safe overlap test, treating {@code null} ends as unbounded.
	 *
	 * @param other the other range
	 * @return {@code true} if the two share at least one day
	 */
	public boolean overlaps(LocalDateRange other) {
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
		LocalDateRange other = (LocalDateRange) o;
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
	public int compareTo(LocalDateRange o) {
		Objects.requireNonNull(o, "other");
		int c = compareNullable(this._begin, o._begin);
		if (c != 0)
			return c;
		return compareNullable(this._end, o._end);
	}

	private static int compareNullable(LocalDate a, LocalDate b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}
