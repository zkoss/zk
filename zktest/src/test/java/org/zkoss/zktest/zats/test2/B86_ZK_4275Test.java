/* B86_ZK_4275Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 16:01:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4275Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		sleep(1000); // wait for server push
		Assertions.assertEquals(2, jq("@button").length());

		closeZKLog();
		click(jq("@button:last"));
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable());
		Assertions.assertEquals("did a click", getZKLog());
	}
}
