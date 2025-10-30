/* B101_ZK_5475Test.java

	Purpose:

	Description:

	History:
		2:48 PM 2024/9/18, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5475Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("13 de junho 2024 00:00:00", jq(".z-datebox-input").val());
		type(jq(".z-datebox-input"), "13 de junho 2024 00:00:01");
		blur(jq(".z-datebox-input"));
		waitResponse(true);
		assertEquals(0, jq(".z-errorbox").length());
		assertEquals("13 de junho 2024 00:00:01", jq(".z-datebox-input").val());
	}
}
