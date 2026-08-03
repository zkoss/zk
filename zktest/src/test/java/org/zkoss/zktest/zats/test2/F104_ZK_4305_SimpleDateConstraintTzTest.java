/* F104_ZK_4305_SimpleDateConstraintTzTest.java

		Purpose:

		Description:

		History:
				Wed May 20 17:10:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.zkoss.zul.SimpleDateConstraint;

/**
 * Regression coverage for {@code SimpleDateConstraint.forTimeZone(...)}:
 * the parsed begin / end bounds must be anchored in the supplied time zone
 * (not the JVM default). {@link SimpleDateConstraint} parses lazily — the
 * date literals are only consumed on the first {@code validate()} call — so
 * the test triggers a no-op validate to force the parse before asserting
 * against {@code getEndDate()}.
 */
public class F104_ZK_4305_SimpleDateConstraintTzTest {

	@Test
	public void testForTimeZoneAnchorsBoundsInSuppliedZone() throws Exception {
		TimeZone original = TimeZone.getDefault();
		TimeZone fakeDefault = TimeZone.getTimeZone("UTC");
		TimeZone tokyo = TimeZone.getTimeZone("Asia/Tokyo"); // +09:00, distinct from UTC

		try {
			TimeZone.setDefault(fakeDefault);

			SimpleDateConstraint c = SimpleDateConstraint.forTimeZone("before 20260101", tokyo);
			// Make the lazy-parse contract self-testing: getEndDate() is a
			// plain field accessor on AbstractSimpleDateTimeConstraint, so it
			// returns null until validate(...) runs the parser. If a future
			// refactor moves the trigger elsewhere, the assertNotNull below
			// fires immediately — the test depends on its observed behaviour,
			// not just on internal contract of SimpleConstraint.
			assertNull(c.getEndDate(), "lazy-parse must not have run before validate()");
			c.validate(null, null);

			Date end = c.getEndDate();
			assertNotNull(end, "validate() must trigger lazy-parse so getEndDate() returns the parsed bound");

			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			fmt.setTimeZone(tokyo);
			Date tokyoMidnight = fmt.parse("20260101");
			fmt.setTimeZone(fakeDefault);
			Date utcMidnight = fmt.parse("20260101");

			// Sanity: if the test runs inside Tokyo time the assertion below
			// loses its signal. Fail loudly rather than silently passing.
			assertNotEquals(utcMidnight.getTime(), tokyoMidnight.getTime(),
					"UTC midnight and Tokyo midnight must differ for this test to be meaningful");

			assertEquals(tokyoMidnight.getTime(), end.getTime(),
					"forTimeZone(constraint, Asia/Tokyo) must anchor bounds in Tokyo, "
							+ "not the JVM default zone");
		} finally {
			TimeZone.setDefault(original);
		}
	}

	@Test
	public void testPlainConstructorAnchorsBoundsInJvmDefault() throws Exception {
		TimeZone original = TimeZone.getDefault();
		TimeZone fakeDefault = TimeZone.getTimeZone("UTC");
		TimeZone tokyo = TimeZone.getTimeZone("Asia/Tokyo");

		try {
			TimeZone.setDefault(fakeDefault);

			SimpleDateConstraint c = new SimpleDateConstraint("before 20260101");
			assertNull(c.getEndDate(), "lazy-parse must not have run before validate()");
			c.validate(null, null);

			Date end = c.getEndDate();
			assertNotNull(end, "validate() must trigger lazy-parse");

			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			fmt.setTimeZone(fakeDefault);
			Date utcMidnight = fmt.parse("20260101");
			fmt.setTimeZone(tokyo);
			Date tokyoMidnight = fmt.parse("20260101");

			assertNotEquals(utcMidnight.getTime(), tokyoMidnight.getTime());
			assertEquals(utcMidnight.getTime(), end.getTime(),
					"plain SimpleDateConstraint must anchor bounds in JVM default zone");
		} finally {
			TimeZone.setDefault(original);
		}
	}

	/**
	 * Regression: {@code SimpleDateConstraint.validateRange} normalises both
	 * endpoints to calendar-day start before the reverse-order check, matching
	 * the per-endpoint {@code validate(...)} which also does
	 * {@code Dates.beginOfDate(...)}. Same-day wall-clock-reversed pairs
	 * (begin=23:00, end=09:00 same date) used to throw
	 * "begin must not be later than end" even though both endpoints
	 * individually pass any day-strict bound check.
	 */
	@Test
	public void testValidateRangeAcceptsSameDayReversedWallClock() throws Exception {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date begin = fmt.parse("2026-06-15 23:00");
		Date end = fmt.parse("2026-06-15 09:00");
		// Should NOT throw — both endpoints fall on 2026-06-15 calendar-wise.
		c.validateRange(null, begin, end);
	}

	@Test
	public void testValidateRangeRejectsCrossDayReversed() throws Exception {
		SimpleDateConstraint c = new SimpleDateConstraint("between 20260101 and 20261231");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date begin = fmt.parse("2026-06-16 09:00");
		Date end = fmt.parse("2026-06-15 09:00");
		try {
			c.validateRange(null, begin, end);
			throw new AssertionError("Expected WrongValueException for cross-day reversed range");
		} catch (org.zkoss.zk.ui.WrongValueException expected) {
			// expected
		}
	}
}
