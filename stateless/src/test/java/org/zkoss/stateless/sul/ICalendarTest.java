/* ICalendarTest.java

	Purpose:

	Description:

	History:
		Thu Nov 18 13:30:34 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Test for {@link Calendar}
 *
 * @author katherine
 */
public class ICalendarTest extends StatelessTestBase {
	private static Date date = new Date();
	@Test
	public void withCalendar() {
		// check Richlet API case
		assertEquals(richlet(() -> ICalendar.of(date)),
				zul(ICalendarTest::newCalendar));
		// check Stateless file case
		assertEquals(composer(ICalendarTest::newCalendar), zul(ICalendarTest::newCalendar));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Date date2 = new Date();
					return new Calendar(date2);
				}, (ICalendar ICalendar) -> ICalendar.withValue(date)),
				zul(ICalendarTest::newCalendar));
	}

	@Test
	public void withZonedDateTime() {
		assertEquals(richlet(() -> ICalendar.of(ZonedDateTime.of(LocalDateTime.of(1, 1, 1, 1, 1, 1),
				ZoneId.of("Asia/Taipei")))),
				zul(ICalendarTest::newCalendarWithZonedDateTime));
		// check Stateless file case
		assertEquals(composer(ICalendarTest::newCalendarWithZonedDateTime), zul(ICalendarTest::newCalendarWithZonedDateTime));
	}

	private static Calendar newCalendar() {
		return new Calendar(date);
	}

	private static Calendar newCalendarWithZonedDateTime() {
		return new Calendar(LocalDateTime.of(1, 1, 1, 1, 1, 1));
	}
}