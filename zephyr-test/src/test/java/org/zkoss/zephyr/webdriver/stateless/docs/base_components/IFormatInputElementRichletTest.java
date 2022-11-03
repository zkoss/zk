/* IFormatInputElementRichletTest.java

	Purpose:

	Description:

	History:
		Wed Feb 23 17:45:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IFormatInputElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IFormatInputElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/FormatInputElement">FormatInputElement</a>,
 * if any.
 *
 * @author katherine
 * @see IFormatInputElement
 */
public class IFormatInputElementRichletTest extends WebDriverTestCase {
	@Test
	public void format() {
		connect("/base_components/iFormatInputElement/format");
		assertEquals(LocalDate.of(2000, 1, 1).format(DateTimeFormatter.ofPattern("dd/MMM yyyy")
						.withLocale(Locale.ENGLISH)),jq("input:eq(0)").val());
		assertEquals("@ 12,345.00", jq("input:eq(1)").val());
		assertEquals("12.30", jq("input:eq(2)").val());
		assertEquals("01,234.57", jq("input:eq(3)").val());
		assertEquals("1234,567 XYZ", jq("input:eq(4)").val());
		assertEquals("99,999.0", jq("input:eq(5)").val());
		assertEquals("01", jq("input:eq(6)").val());
		assertEquals("AM 01:01:00", jq("input:eq(7)").val());
	}
}