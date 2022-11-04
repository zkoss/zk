/* B00682Test.java
	Purpose:

	Description:

	History:
		Tue Apr 27 16:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B00682Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("", jq("$inp11").val());
		assertEquals("", jq("$inp21").val());
		assertEquals("", jq("$inp12").val());
		assertEquals("", jq("$inp22").val());
		assertEquals("", jq("$inp13").val());
		assertEquals("", jq("$inp23").val());
		assertEquals("", jq("$inp14").val());
		assertEquals("", jq("$inp24").val());
		assertEquals("", jq("$inp15").val());
		assertEquals("", jq("$inp25").val());
		assertEquals("-1.0", jq("$inp16").val());
		assertEquals("-1.0", jq("$inp26").val());
		assertEquals("-2", jq("$inp17").val());
		assertEquals("-2", jq("$inp27").val());
		assertEquals("-2.0", jq("$inp18").find("input").val());
		assertEquals("-2.0", jq("$inp28").find("input").val());
		assertEquals("-3", jq("$inp19").val());
		assertEquals("-3", jq("$inp29").val());
		assertEquals("-3", jq("$inp1a").find("input").val());
		assertEquals("-3", jq("$inp2a").find("input").val());
		assertEquals("-4", jq("$inp1b").val());
		assertEquals("-4", jq("$inp2b").val());
	}
}