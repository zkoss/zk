/* ITimeboxTest.java

	Purpose:

	Description:

	History:
		Wed Nov 10 17:38:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Timebox;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ITimebox}
 *
 * @author katherine
 */
public class ITimeboxTest extends StatelessTestBase {
	@Test
	public void withTimebox() {
		// check Richlet API case
		assertEquals(richlet(() -> ITimebox.ofFormat("yyyy/MM/dd").withLocale(Locale.US)),
				zul(ITimeboxTest::newTimebox));
		// check Stateless file case
		assertEquals(composer(ITimeboxTest::newTimebox), zul(ITimeboxTest::newTimebox));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Timebox Timebox = new Timebox();
					Timebox.setFormat("yyyy/MM/dd");
					Timebox.setLocale(Locale.CANADA);
					return Timebox;
				}, (ITimebox ITimebox) -> ITimebox.withFormat("yyyy/MM/dd").withLocale(Locale.US)),
				zul(ITimeboxTest::newTimebox));
	}

	@Test
	public void withZonedDateTime() {
		assertEquals(richlet(() -> ITimebox.of(ZonedDateTime.of(LocalDateTime.of(1, 1, 1, 1, 1, 1),
				ZoneId.of("Asia/Taipei"))).withLocale("en_US")),
				zul(ITimeboxTest::newTimeboxWithZonedDateTime));
		// check Stateless file case
		assertEquals(composer(ITimeboxTest::newTimeboxWithZonedDateTime), zul(ITimeboxTest::newTimeboxWithZonedDateTime));
	}

	@Test
	public void WithLocalDateTime() {
		assertEquals(richlet(() -> ITimebox.of(LocalDateTime.of(1, 1, 1, 1, 1, 1))),
				zul(ITimeboxTest::newTimeboxWithLocalDateTime));
		// check Stateless file case
		assertEquals(composer(ITimeboxTest::newTimeboxWithLocalDateTime), zul(ITimeboxTest::newTimeboxWithLocalDateTime));
	}

	@Test
	public void WithLocalTime() {
		assertEquals(richlet(() -> ITimebox.of(LocalTime.of(1, 1))),
				zul(ITimeboxTest::newTimeboxWithLocalTime));
		assertEquals(composer(ITimeboxTest::newTimeboxWithLocalTime), zul(ITimeboxTest::newTimeboxWithLocalTime));
	}

	private static Timebox newTimebox() {
		Timebox Timebox = new Timebox();
		Timebox.setFormat("yyyy/MM/dd");
		Timebox.setLocale(Locale.US);
		return Timebox;
	}

	private static Timebox newTimeboxWithZonedDateTime() {
		Timebox Timebox = new Timebox(LocalDateTime.of(1, 1, 1, 1, 1, 1));
		Timebox.setLocale("en_US");
		return Timebox;
	}

	private static Timebox newTimeboxWithLocalDateTime() {
		return new Timebox(LocalDateTime.of(1, 1, 1, 1, 1, 1));
	}

	private static Timebox newTimeboxWithLocalTime() {
		return new Timebox(LocalTime.of(1, 1));
	}
}