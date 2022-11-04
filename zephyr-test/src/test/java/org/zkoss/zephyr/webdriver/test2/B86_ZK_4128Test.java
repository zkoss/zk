/* B86_ZK_4128Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 11 17:45:49 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4128Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);

		click(jq("@button"));
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
