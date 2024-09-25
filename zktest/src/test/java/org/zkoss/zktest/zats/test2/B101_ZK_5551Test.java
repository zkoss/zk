/* B101_ZK_5551Test.java

	Purpose:

	Description:

	History:
		5:41 PM 2024/9/24, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5551Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 3; i++) {
			click(jq("@button:eq(0)"));
			waitResponse();
		}
		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(2)"));
		waitResponse();
		String log = getZKLog();
		assertEquals(0, log.split("executing composer for ").length - 1);

	}
}
