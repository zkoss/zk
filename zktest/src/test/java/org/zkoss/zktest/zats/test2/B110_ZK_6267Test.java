/* B110_ZK_6267Test.java

		Purpose:

		Description:

		History:
				Tue Nov 10 15:48:25 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6267Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		assertEquals(0, getFixCount(), "a closed combobox must not measure its popup on an AU response");

		click(jq("@combobox").eq(0).find("input"));
		getActions().sendKeys("a").perform();
		waitResponse(true);
		assertTrue(jq(".z-combobox-popup.z-combobox-open").exists());
		assertTrue(getFixCount() > 0, "an open combobox must still measure its popup on an AU response");
	}

	private int getFixCount() {
		return Integer.parseInt(getEval("fixCount"));
	}
}
