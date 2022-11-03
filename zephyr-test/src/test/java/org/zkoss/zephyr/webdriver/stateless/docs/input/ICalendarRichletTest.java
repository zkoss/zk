/* ICalendarRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:17:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ICalendar;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ICalendar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Calendar">Calendar</a>,
 * if any.
 *
 * @author katherine
 * @see ICalendar
 */
public class ICalendarRichletTest extends WebDriverTestCase {
	@Test
	public void constraint() {
		connect("/input/iCalendar/constraint");
		assertFalse(jq(".z-calendar-selected:eq(0)").prev().hasClass("z-calendar-disabled"));
		assertTrue(jq(".z-calendar-selected:eq(1)").prev().hasClass("z-calendar-disabled"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-calendar-selected:eq(1)").prev().hasClass("z-calendar-disabled"));
		click(jq("@button"));
		waitResponse();
	}

	@Test
	public void name() {
		connect("/input/iCalendar/name");
		assertEquals("calendar", jq(".z-calendar").toWidget().get("name"));
		click(jq("@button"));
		waitResponse();
		assertEquals("calendar2", jq(".z-calendar").toWidget().get("name"));
	}

	@Test
	public void showTodayLink() {
		connect("/input/iCalendar/showTodayLink");
		assertFalse(jq(".z-calendar:eq(0) .z-calendar-today").exists());
		assertTrue(jq(".z-calendar:eq(1) .z-calendar-today").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-calendar:eq(1) .z-calendar-today").exists());
	}

	@Test
	public void todayLinkLabel() {
		connect("/input/iCalendar/todayLinkLabel");
		assertEquals("jump to today", jq(".z-calendar-title:eq(1)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("today", jq(".z-calendar-title:eq(1)").text());
	}

	@Test
	public void value() {
		connect("/input/iCalendar/value");
		assertEquals("1", jq(".z-calendar-text:eq(1)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("2000", jq(".z-calendar-text:eq(1)").text());
	}

	@Test
	public void valueInLocalDate() {
		connect("/input/iCalendar/valueInLocalDate");
		assertEquals("2000", jq(".z-calendar-text:eq(1)").text());
	}

	@Test
	public void valueInLocalDateTime() {
		connect("/input/iCalendar/valueInLocalDateTime");
		assertEquals("1", jq(".z-calendar-text:eq(1)").text());
	}

	@Test
	public void valueInZonedDateTime() {
		connect("/input/iCalendar/valueInZonedDateTime");
		assertEquals("1", jq(".z-calendar-text:eq(1)").text());
	}

	@Test
	public void weekOfYear() {
		connect("/input/iCalendar/weekOfYear");
		assertFalse(jq(".z-calendar:eq(0) .z-calendar-weekofyear").exists());
		assertTrue(jq(".z-calendar:eq(1) .z-calendar-weekofyear").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-calendar:eq(1) .z-calendar-weekofyear").exists());
	}
}