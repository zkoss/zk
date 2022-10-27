/* IDateTimeFormatInputElementRichlet.java

	Purpose:

	Description:

	History:
		Wed Feb 23 17:47:06 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.base_components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.sul.IDateTimeFormatInputElement;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDatebox;
import org.zkoss.stateless.sul.ITimebox;
import org.zkoss.stateless.sul.IVlayout;

/**
 * A set of example for {@link IDateTimeFormatInputElement} Java Docs.
 *
 * @author katherine
 * @see IDateTimeFormatInputElement
 */
@RichletMapping("/base_components/iDateTimeFormatInputElement")
public class IDateTimeFormatInputElementRichlet implements StatelessRichlet {

	private static final LocalDate DATE = LocalDate.of(2000, 1, 1);
	private static final LocalTime TIME = LocalTime.of(1, 1);

	@RichletMapping("/locale")
	public IComponent locale() {
		IDatebox datebox = IDatebox.of(DATE).withFormat("dd,MMM yyyy");
		ITimebox timebox =  ITimebox.of(TIME).withFormat("a HH:mm:ss");
		return IVlayout.of(
				datebox.withLocale(Locale.ENGLISH),
				datebox.withLocale(Locale.CHINESE),
				datebox.withLocale(Locale.JAPANESE),
				datebox.withLocale(Locale.KOREA),
				timebox.withLocale(Locale.ENGLISH),
				timebox.withLocale(Locale.CHINESE),
				timebox.withLocale(Locale.JAPANESE),
				timebox.withLocale(Locale.KOREA)
		);
	}

	@RichletMapping("/timeZone")
	public IComponent timeZone() {
		IDatebox datebox = IDatebox.of(DATE).withFormat("dd,MMM yyyy").withLocale(Locale.ENGLISH);
		ITimebox timebox =  ITimebox.of(TIME).withFormat("a HH:mm:ss").withLocale(Locale.ENGLISH);
		return IVlayout.of(
				datebox.withTimeZone("Europe/Berlin"),
				datebox.withTimeZone("GMT+8"),
				datebox.withTimeZone(TimeZone.getTimeZone("UTC")),
				timebox.withTimeZone("Europe/Berlin"),
				timebox.withTimeZone("GMT+8"),
				timebox.withTimeZone(TimeZone.getTimeZone("UTC"))
		);
	}

	@RichletMapping("/withValue")
	public IComponent withValue() {
		IDatebox datebox = IDatebox.DEFAULT.withFormat("dd,MMM yyyy").withLocale(Locale.ENGLISH);
		ITimebox timebox =  ITimebox.DEFAULT.withFormat("a HH:mm:ss").withLocale(Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.of(DATE, TIME), ZoneId.of("Asia/Taipei"));
		return IVlayout.of(
				datebox.withValueInLocalDate(DATE),
				datebox.withValueInLocalDateTime(localDateTime),
				timebox.withValueInLocalDateTime(localDateTime),
				datebox.withValueInLocalTime(localDateTime.toLocalTime()),
				timebox.withValueInLocalTime(LocalTime.MIN),
				datebox.withValueInZonedDateTime(zonedDateTime),
				timebox.withValueInZonedDateTime(zonedDateTime)
		);
	}
}