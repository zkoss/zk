/* B111_ZK_6251Test.java

	Purpose:

	Description:

	History:
		Wed Nov 04 09:16:59 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B111_ZK_6251Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$notify"));
		waitResponse();
		assertNoAnyError();
		assertEquals("foo 1", jq("$lb").text());
	}
}
