/* B95_ZK_4760Test.java

	Purpose:

	Description:

	History:
		Tue Jan 26 17:25:05 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;


public class B95_ZK_4760Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(Boolean.valueOf(zk(jq(".z-messagebox-viewport")).eval("hasHScroll()")));
		click(jq("@button:eq(6)"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertFalse(Boolean.valueOf(zk(jq(".z-messagebox-viewport")).eval("hasHScroll()")));
		click(jq("@button:eq(6)"));

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertFalse(Boolean.valueOf(zk(jq(".z-messagebox-viewport")).eval("hasHScroll()")));
		click(jq(".z-messagebox-button"));

		click(jq("@button:eq(3)"));
		waitResponse();
		Assertions.assertTrue(Boolean.valueOf(zk(jq(".z-messagebox-viewport")).eval("hasHScroll()")));
		click(jq(".z-messagebox-button"));

		click(jq("@button:eq(4)"));
		waitResponse();
		Assertions.assertEquals(600, jq(".z-messagebox-window").outerWidth());
		click(jq(".z-messagebox-button"));

		click(jq("@button:eq(5)"));
		waitResponse();
		Assertions.assertTrue(Boolean.valueOf(zk(jq(".z-messagebox-viewport")).eval("hasHScroll()")));
		click(jq(".z-messagebox-button"));
	}
}
