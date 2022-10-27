/* B85_ZK_3630Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Feb 13 3:03 PM:09 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3630Test extends ClientBindTestCase {
	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
			JQuery buttons = jq("@button");
			assertEquals(8, buttons.length());
			JQuery chgBtn = buttons.eq(0);
			JQuery bckBtn = buttons.eq(1);

			click(chgBtn);
			waitResponse();
			buttons = jq("@button");
			assertEquals(12, buttons.length());

			click(bckBtn);
			waitResponse();
			buttons = jq("@button");
			assertEquals(8, buttons.length());
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}
}

