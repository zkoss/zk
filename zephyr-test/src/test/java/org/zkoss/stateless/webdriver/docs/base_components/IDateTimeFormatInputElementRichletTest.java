/* IDateTimeFormatInputElementRichletTest.java

	Purpose:

	Description:

	History:
		Wed Feb 23 17:46:24 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IDateTimeFormatInputElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IDateTimeFormatInputElement} Java Docs.
 *
 * @author katherine
 * @see IDateTimeFormatInputElement
 */
public class IDateTimeFormatInputElementRichletTest extends WebDriverTestCase {

	@Test
	public void locale() {
		connect("/base_components/iDateTimeFormatInputElement/locale");
		assertEquals("01,Jan 2000", jq("input:eq(0)").val());
		assertEquals("01,1月 2000", jq("input:eq(1)").val());
		assertEquals("01,1月 2000", jq("input:eq(2)").val());
		assertEquals("01,1월 2000", jq("input:eq(3)").val());
		assertEquals("AM 01:01:00", jq("input:eq(4)").val());
		assertEquals("上午 01:01:00", jq("input:eq(5)").val());
		assertEquals("午前 01:01:00", jq("input:eq(6)").val());
		assertEquals("오전 01:01:00", jq("input:eq(7)").val());
	}

	@Test
	public void timeZone() {
		connect("/base_components/iDateTimeFormatInputElement/timeZone");
		assertEquals("31,Dec 1999", jq("input:eq(0)").val());
		assertEquals("01,Jan 2000", jq("input:eq(1)").val());
		assertEquals("31,Dec 1999", jq("input:eq(2)").val());
		assertEquals("AM 01:01:00", jq("input:eq(4)").val());
		assertEquals("PM 17:01:00", jq("input:eq(5)").val());
	}

	@Test
	public void withValue() {
		connect("/base_components/iDateTimeFormatInputElement/withValue");
		assertEquals("01,Jan 2000", jq("input:eq(0)").val());
		assertEquals("03,Jan 0001", jq("input:eq(1)").val());
		assertEquals("AM 01:01:01", jq("input:eq(2)").val());
		assertEquals("AM 00:00:00", jq("input:eq(4)").val());
		assertEquals("01,Jan 2000", jq("input:eq(5)").val());
		assertEquals("AM 01:01:00", jq("input:eq(6)").val());
	}
}