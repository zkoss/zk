/* B110_ZK_6112_DateTimeboxColsTest.java

	Purpose:

	Description:

	History:
		Wed Oct 07 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6112_DateTimeboxColsTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertTrue(inputWidth("$d8") < inputWidth("$d30"), "datebox cols=8 must be narrower than cols=30");
		assertTrue(inputWidth("$t8") < inputWidth("$t30"), "timebox cols=8 must be narrower than cols=30");
	}

	private int inputWidth(String widgetSelector) {
		return jq(widgetSelector).find("input").outerWidth();
	}
}
