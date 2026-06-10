/* F104_ZK_4305_SimpleDateConstraintRangeTest.java

	Purpose:

	Description:

	History:
		Thu May 29 14:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zkmax.zul.Daterangebox;
import org.zkoss.zul.DateRange;
import org.zkoss.zul.SimpleDateConstraint;

/**
 * Pure JUnit boundary tests for the two-layer reverse-range rule introduced
 * with ZK-4305:
 *
 * <ul>
 *   <li>{@link SimpleDateConstraint#validateRange} is <b>day-strict</b>:
 *       both endpoints are normalised to calendar-day start before the
 *       reverse check, so a same-day time-reversed pair
 *       ({@code 23:00 -> 09:00}) does <em>not</em> throw.</li>
 *   <li>{@link Daterangebox#setValue(DateRange)} adds a <b>wall-clock</b>
 *       layer on top when {@code showTime=true}: a same-day time-reversed
 *       pair is rejected because the user explicitly cares about the
 *       time-of-day component. When {@code showTime=false} the outer
 *       reverse check falls back to day-strict and the same input is
 *       accepted.</li>
 * </ul>
 *
 * <p>The two layers fire together at commit time; the combined contract
 * is exercised end-to-end by {@code F104_ZK_4305_DaterangeAttributeTest},
 * but the boundary itself is pinned here so a refactor that flips either
 * layer fails fast without spinning up a browser.
 */
public class F104_ZK_4305_SimpleDateConstraintRangeTest {

	private static Date date(int y, int m, int d) {
		return new GregorianCalendar(y, m, d, 0, 0, 0).getTime();
	}

	private static Date dateTime(int y, int m, int d, int hr, int min) {
		return new GregorianCalendar(y, m, d, hr, min, 0).getTime();
	}

	// ===== Layer 1: SimpleDateConstraint.validateRange (day-strict) =====

	@Test
	public void validateRange_forwardRange_passes() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		assertDoesNotThrow(() ->
				c.validateRange(null, date(2026, Calendar.MAY, 1), date(2026, Calendar.MAY, 5)));
	}

	@Test
	public void validateRange_crossDayReversed_throws() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		assertThrows(WrongValueException.class, () ->
				c.validateRange(null, date(2026, Calendar.MAY, 5), date(2026, Calendar.MAY, 1)),
				"calendar-day reversed range must be rejected");
	}

	/** Day-strict means both endpoints are first normalised to day-start
	 *  via {@code Dates.beginOfDate}, so a same-day pair with the time
	 *  component reversed is treated as a zero-night same-day stay rather
	 *  than an inverted range. */
	@Test
	public void validateRange_sameDayTimeReversed_passes() {
		SimpleDateConstraint c = new SimpleDateConstraint(0, null, null, null);
		assertDoesNotThrow(() ->
				c.validateRange(null,
						dateTime(2026, Calendar.JANUARY, 1, 23, 0),
						dateTime(2026, Calendar.JANUARY, 1, 9, 0)),
				"day-strict layer must NOT reject same-day time-reversed pair");
	}

	@Test
	public void validateRange_sameDayEqual_passes() {
		SimpleDateConstraint c = new SimpleDateConstraint(0, null, null, null);
		assertDoesNotThrow(() ->
				c.validateRange(null, date(2026, Calendar.JANUARY, 1), date(2026, Calendar.JANUARY, 1)));
	}

	@Test
	public void validateRange_oneNullEndpoint_passes() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		assertDoesNotThrow(() ->
				c.validateRange(null, null, date(2026, Calendar.MAY, 5)),
				"null begin (open-ended range) must be accepted");
		assertDoesNotThrow(() ->
				c.validateRange(null, date(2026, Calendar.MAY, 5), null),
				"null end (open-ended range) must be accepted");
	}

	@Test
	public void validateRange_bothNull_passes() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		assertDoesNotThrow(() -> c.validateRange(null, null, null));
	}

	@Test
	public void validateRange_beginBeforeLowerBound_throws() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260501 and 20261231");
		assertThrows(WrongValueException.class, () ->
				c.validateRange(null, date(2026, Calendar.APRIL, 1), date(2026, Calendar.MAY, 5)),
				"begin earlier than the lower bound must be rejected per-endpoint");
	}

	@Test
	public void validateRange_endAfterUpperBound_throws() {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260501 and 20261231");
		assertThrows(WrongValueException.class, () ->
				c.validateRange(null, date(2026, Calendar.MAY, 5), date(2027, Calendar.JANUARY, 5)),
				"end later than the upper bound must be rejected per-endpoint");
	}

	// ===== Layer 2: Daterangebox.setValue → validateCandidateOrThrow =====
	// (adds the wall-clock check on top when showTime=true)

	@Test
	public void daterangebox_sameDayTimeReversed_withShowTime_throws() {
		Daterangebox box = new Daterangebox();
		box.setShowTime(true);
		assertThrows(WrongValueException.class, () ->
				box.setValue(new DateRange(
						dateTime(2026, Calendar.JANUARY, 1, 23, 0),
						dateTime(2026, Calendar.JANUARY, 1, 9, 0))),
				"showTime=true: wall-clock layer must reject same-day time-reversed pair");
	}

	@Test
	public void daterangebox_sameDayTimeReversed_withoutShowTime_passes() {
		Daterangebox box = new Daterangebox();
		// showTime defaults to false; declare explicitly for readability.
		box.setShowTime(false);
		assertDoesNotThrow(() ->
				box.setValue(new DateRange(
						dateTime(2026, Calendar.JANUARY, 1, 23, 0),
						dateTime(2026, Calendar.JANUARY, 1, 9, 0))),
				"showTime=false: outer reverse check falls back to day-strict; "
						+ "same-day pair is accepted as a zero-night stay");
	}

	@Test
	public void daterangebox_overnightTimeReversed_withShowTime_passes() {
		Daterangebox box = new Daterangebox();
		box.setShowTime(true);
		assertDoesNotThrow(() ->
				box.setValue(new DateRange(
						dateTime(2026, Calendar.JANUARY, 1, 23, 0),
						dateTime(2026, Calendar.JANUARY, 2, 9, 0))),
				"showTime=true: wall-clock-forward overnight pair must be accepted "
						+ "even though Duration.toDays() truncates to 0");
	}

	@Test
	public void daterangebox_crossDayReversed_withShowTime_throws() {
		Daterangebox box = new Daterangebox();
		box.setShowTime(true);
		assertThrows(WrongValueException.class, () ->
				box.setValue(new DateRange(
						date(2026, Calendar.MAY, 5), date(2026, Calendar.MAY, 1))),
				"calendar-day reversed pair must be rejected regardless of showTime");
	}

	@Test
	public void daterangebox_crossDayReversed_withoutShowTime_throws() {
		Daterangebox box = new Daterangebox();
		box.setShowTime(false);
		assertThrows(WrongValueException.class, () ->
				box.setValue(new DateRange(
						date(2026, Calendar.MAY, 5), date(2026, Calendar.MAY, 1))),
				"calendar-day reversed pair must be rejected regardless of showTime");
	}

	// ===== minNights / maxNights interaction =====

	@Test
	public void daterangebox_minNights_overnightCountsAsOneNight() {
		Daterangebox box = new Daterangebox();
		box.setShowTime(true);
		box.setMinNights(1);
		assertDoesNotThrow(() ->
				box.setValue(new DateRange(
						dateTime(2026, Calendar.JANUARY, 1, 23, 0),
						dateTime(2026, Calendar.JANUARY, 2, 9, 0))),
				"minNights counts calendar days, not 24h blocks: 23:00->09:00 next day = 1 night");
	}

	@Test
	public void daterangebox_minNights_sameDayCountsAsZero_throws() {
		Daterangebox box = new Daterangebox();
		box.setMinNights(1);
		assertThrows(WrongValueException.class, () ->
				box.setValue(new DateRange(
						date(2026, Calendar.JANUARY, 1), date(2026, Calendar.JANUARY, 1))),
				"same-day pair = 0 nights; minNights=1 must reject");
	}

	@Test
	public void daterangebox_maxNights_atLimit_passes() {
		Daterangebox box = new Daterangebox();
		box.setMaxNights(3);
		assertDoesNotThrow(() ->
				box.setValue(new DateRange(
						date(2026, Calendar.JANUARY, 1), date(2026, Calendar.JANUARY, 4))),
				"exactly maxNights worth of nights must be accepted (inclusive bound)");
	}

	@Test
	public void daterangebox_maxNights_oneOver_throws() {
		Daterangebox box = new Daterangebox();
		box.setMaxNights(3);
		assertThrows(WrongValueException.class, () ->
				box.setValue(new DateRange(
						date(2026, Calendar.JANUARY, 1), date(2026, Calendar.JANUARY, 5))),
				"maxNights+1 must be rejected");
	}
}
