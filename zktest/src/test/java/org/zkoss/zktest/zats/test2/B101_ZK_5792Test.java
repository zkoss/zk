/* B101_ZK_5792Test.java

	Purpose:

	Description:

	History:
		3:30 PM 2024/9/25, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5792Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 1; i < 6; i++) {
			click(jq("@button:eq(0)"));
			waitResponse();
			assertEquals(1 + i , jq(".z-div").length());
			click(jq("@button:eq(2)"));
			waitResponse();
			assertEquals(i, jq(".z-label:contains(itemLabel)").length());
		}
	}
}
