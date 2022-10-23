/* B85_ZK_3698Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 22 15:23:40 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3698Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery buttons = jq("@button");
		try {
			click(buttons.eq(0));
			waitResponse();
			click(buttons.eq(1));
			waitResponse();
			click(buttons.eq(2));
			waitResponse();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNoJSError();
	}
}
