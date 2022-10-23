/* B85_ZK_3871Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Feb 13 3:03 PM:09 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3871Test extends ZephyrClientMVVMTestCase {

	@Test
	public void testNestedChoose() {
		try {
			connect("/test2/B85-ZK-3871-choose.zul");
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
			buttons = jq("button");
			assertEquals(buttons.length(), 5);
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}

	@Test
	public void testNestedIf() {
		try {
			connect("/test2/B85-ZK-3871-if.zul");
			waitResponse();

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

	@Test
	public void testNestedChooseIf() {
		try {
			connect("/test2/B85-ZK-3871-chooseif.zul");
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

