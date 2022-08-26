/* B50_3049192Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 15:22:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3049192Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();

		Assertions.assertTrue(jq("@label:contains(Hi!)").exists());
		Assertions.assertTrue(jq("@textbox[value=\"Hi!\"]").exists());
	}

	@Test
	public void test2() {
		connect();

		click(jq("@button:eq(1)"));
		waitResponse();

		Assertions.assertTrue(jq("@label:contains(Hi!)").exists());
		Assertions.assertTrue(jq("@textbox[value=\"Hi!\"]").exists());
	}
}
