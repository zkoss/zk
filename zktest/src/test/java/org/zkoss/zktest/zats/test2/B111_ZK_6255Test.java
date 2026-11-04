/* B111_ZK_6255Test.java

		Purpose:
				
		Description:
				
		History:
				Wed Nov 04 17:52:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B111_ZK_6255Test extends WebDriverTestCase {
	@Test
	public void testStrictLeapDay() {
		connect();
		type(jq("$strict").find("input"), "2567/02/29");
		waitResponse();
		assertNoJSError();
		assertEquals(0, jq(".z-errorbox").length());
		assertEquals("2567/02/29", jq("$strict").find("input").val());
	}

	@Test
	public void testCustomCalendarLeapDayInput() {
		connect();
		type(jq("$roc").find("input"), "民國109/02/29");
		waitResponse();
		assertNoJSError();
		assertEquals("2020-02-29", jq("$lb").text());
		assertEquals("民國109/02/29", jq("$roc").find("input").val());
	}
}
