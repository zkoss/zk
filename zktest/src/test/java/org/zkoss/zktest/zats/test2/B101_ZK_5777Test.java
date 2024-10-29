/* B101_ZK_5777Test.java

	Purpose:

	Description:

	History:
		10:12 AM 2024/10/29, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5777Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertEquals("param1: value1", jq("@label:eq(0)").text());
		assertEquals("param2: value2", jq("@label:eq(1)").text());
	}
}
