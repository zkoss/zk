/* B85_ZK_3881Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Feb 26 3:05 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3881Test extends ZephyrClientMVVMTestCase {

	@Test
	public void testNestedIf() {
		try {
			connect("/test2/B85-ZK-3881-if.zul");
			sleep(2000);
			JQuery buttons = jq("@button");
			assertEquals(buttons.length(), 5);
			JQuery chgBtn = buttons.eq(0);
			JQuery bckBtn = buttons.eq(1);

			click(chgBtn);
			waitResponse();
			buttons = jq("@button");
			assertEquals(buttons.length(), 7);

			click(bckBtn);
			waitResponse();
			buttons = jq("@button");
			assertEquals(buttons.length(), 5);

		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}
}
