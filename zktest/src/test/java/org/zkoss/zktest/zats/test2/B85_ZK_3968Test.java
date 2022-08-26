/* B85_ZK_3968Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 25 17:58:20 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3968Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		String result1 = getZKLog();
		closeZKLog();
		click(jq("@button:eq(2)"));
		waitResponse();
		String result2 = getZKLog();
		closeZKLog();
		Assertions.assertNotEquals(result1, result2);

		click(jq("@button:eq(1)"));
		waitResponse();
		click(jq("@button:eq(3)"));
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable(),
				"It should print out the results");
	}
}
