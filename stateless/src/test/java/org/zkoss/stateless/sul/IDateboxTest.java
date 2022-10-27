/* IDateboxTest.java

	Purpose:

	Description:

	History:
		Tue Nov 09 16:12:11 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Datebox;

/**
 * Test for {@link IDatebox}
 *
 * @author katherine
 */
public class IDateboxTest extends StatelessTestBase {
	@Test
	public void withDatebox() {
		// check Richlet API case
		assertEquals(richlet(() -> IDatebox.ofLocale(Locale.US).withDisplayedTimeZones(getTimeZones()).withFormat("yyyy/MM/dd")),
				zul(IDateboxTest::newDatebox));
		// check Stateless file case
		assertEquals(composer(IDateboxTest::newDatebox), zul(IDateboxTest::newDatebox));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Datebox datebox = new Datebox();
					datebox.setFormat("yyyy/MM/dd");
					datebox.setDisplayedTimeZones(getTimeZones());
					datebox.setLocale(Locale.CANADA);
					return datebox;
				}, (IDatebox iDatebox) -> iDatebox.withFormat("yyyy/MM/dd")
						.withDisplayedTimeZones(getTimeZones()).withLocale(Locale.US)),
				zul(IDateboxTest::newDatebox));
	}

	@Test
	public void withZonedDateTime() {
		assertEquals(richlet(() -> IDatebox.of(ZonedDateTime.of(LocalDateTime.of(1, 1, 1, 1, 1, 1),
				ZoneId.of("Asia/Taipei"))).withLocale("en_US")),
				zul(IDateboxTest::newDateboxWithZonedDateTime));
		// check Stateless file case
		assertEquals(composer(IDateboxTest::newDateboxWithZonedDateTime), zul(IDateboxTest::newDateboxWithZonedDateTime));
	}

	@Test
	public void WithLocalDateTime() {
		assertEquals(richlet(() -> IDatebox.of(LocalDateTime.of(1, 1, 1, 1, 1, 1))),
				zul(IDateboxTest::newDateboxWithLocalDateTime));
		// check Stateless file case
		assertEquals(composer(IDateboxTest::newDateboxWithLocalDateTime), zul(IDateboxTest::newDateboxWithLocalDateTime));
	}

	@Test
	public void WithLocalDate() {
		assertEquals(richlet(() -> IDatebox.of(LocalDate.of(1, 1, 1))),
				zul(IDateboxTest::newDateboxWithLocalDate));
		// check Stateless file case
		assertEquals(composer(IDateboxTest::newDateboxWithLocalDate), zul(IDateboxTest::newDateboxWithLocalDate));
	}

	@Test
	public void WithLocalTime() {
		assertEquals(richlet(() -> IDatebox.of(LocalTime.of(1, 1))),
				zul(IDateboxTest::newDateboxWithLocalTime));
		assertEquals(composer(IDateboxTest::newDateboxWithLocalTime), zul(IDateboxTest::newDateboxWithLocalTime));
	}

	private static Datebox newDatebox() {
		Datebox datebox = new Datebox();
		datebox.setFormat("yyyy/MM/dd");
		datebox.setDisplayedTimeZones(getTimeZones());
		datebox.setLocale(Locale.US);
		return datebox;
	}

	private static Datebox newDateboxWithZonedDateTime() {
		Datebox datebox = new Datebox(LocalDateTime.of(1, 1, 1, 1, 1, 1));
		datebox.setLocale("en_US");
		return datebox;
	}

	private static Datebox newDateboxWithLocalDateTime() {
		return new Datebox(LocalDateTime.of(1, 1, 1, 1, 1, 1));
	}

	private static Datebox newDateboxWithLocalDate() {
		return new Datebox(LocalDate.of(1, 1, 1));
	}

	private static Datebox newDateboxWithLocalTime() {
		return new Datebox(LocalTime.of(1, 1));
	}

	private static List<TimeZone> getTimeZones() {
		TimeZone tz1 = TimeZone.getTimeZone("GMT+2");
		TimeZone tz2 = TimeZone.getTimeZone("GMT-2");
		return Arrays.asList(tz1, tz2);
	}
}