/* F90_ZK_4372Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 16 18:09:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4372Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq("@notification").exists());

		click(jq("$btnLoseFocus"));
		waitResponse(true);
		Assertions.assertFalse(jq("@notification").exists());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertTrue(jq("@notification").exists());
	}
}
