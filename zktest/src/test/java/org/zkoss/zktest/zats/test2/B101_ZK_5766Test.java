/* B101_ZK_5766Test.java

	Purpose:

	Description:

	History:
		5:06 PM 2024/8/15, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5766Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals(0, jq("div>:contains(\"should display in tooltip\")").length());
	}
}
