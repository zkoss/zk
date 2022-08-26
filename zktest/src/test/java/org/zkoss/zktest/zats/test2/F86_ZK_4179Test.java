/* F86_ZK_4179Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 24 17:34:48 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4179Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "JavaScript exception");
		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable(), "JavaScript exception");

		driver.navigate().back();
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable(), "ZK onHistoryPopState error?");
		closeZKLog();

		driver.navigate().forward();
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable(), "ZK onHistoryPopState error?");
	}
}
