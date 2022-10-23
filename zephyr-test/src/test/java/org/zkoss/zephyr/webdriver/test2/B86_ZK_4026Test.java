/* B86_ZK_4026Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 15 10:48:23 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4026Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery btn = jq("$notifyBtn");
		JQuery lbl = jq("$testLbl");
		String uuid1 = lbl.get(0).get("id");
		click(btn);
		waitResponse();
		lbl = jq("$testLbl");
		String uuid2 = lbl.get(0).get("id");
		Assertions.assertEquals(uuid1, uuid2, "The two uuid should be the same:");
	}

}
