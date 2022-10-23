/* B95_ZK_4698Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 15 12:22:50 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4698Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("myParam = my parameter value", getZKLog());
	}
}
