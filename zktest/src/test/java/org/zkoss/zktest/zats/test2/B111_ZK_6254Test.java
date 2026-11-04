/* B111_ZK_6254Test.java

		Purpose:
				
		Description:
				
		History:
				Wed Nov 04 15:06:25 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B111_ZK_6254Test extends WebDriverTestCase {
	@Test
	public void testInvalidDate() {
		connect();
		type(jq("$strict").find("input"), "2567/02/30");
		waitResponse();
		assertNoJSError();
		assertEquals(1, jq(".z-errorbox").length());
	}

	@Test
	public void testLeapDay() {
		connect();
		type(jq("$leap").find("input"), "2567/02/29");
		waitResponse();
		assertNoJSError();
		assertEquals("2024-02-29", jq("$lb").text());
		assertEquals("2567/02/29", jq("$leap").find("input").val());
	}

	@Test
	public void testCustomCalendarLeapDay() {
		connect();
		assertNoJSError();
		assertEquals("民國113/02/29", jq("$roc").find("input").val());
	}
}
