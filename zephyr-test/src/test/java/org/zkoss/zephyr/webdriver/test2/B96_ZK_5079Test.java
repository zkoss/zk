/* B96_ZK_5079Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 14:22:36 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5079Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		try {
			click(jq("$btn"));
			waitResponse();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw exception!");
		}
		assertNoJSError();
	}
}
