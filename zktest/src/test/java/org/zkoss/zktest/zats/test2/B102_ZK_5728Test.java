/* B102_ZK_5728Test.java

	Purpose:

	Description:

	History:
		Tue Mar 25 10:22:40 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B102_ZK_5728Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(true, jq("$RBMenusLateral").toWidget().is("selected"));
		assertEquals(true, jq("$r1").toWidget().is("selected"));
		assertEquals(true, jq("$r2").toWidget().is("selected"));
	}
}
