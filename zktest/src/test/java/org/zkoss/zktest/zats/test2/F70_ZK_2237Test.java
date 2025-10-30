/* F70_ZK_2237Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 16:49:59 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2237Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery ok = jq("@a:first");
		click(ok);
		Assertions.assertTrue(ok.is("[disabled]"));
		sleep(1000);
		Assertions.assertFalse(ok.is("[disabled]"));

		JQuery ok2 = jq("@a:contains(OK 2)");
		JQuery cancel = jq("@a:contains(Cancel)");
		click(ok2);
		Assertions.assertTrue(ok2.is("[disabled]"));
		Assertions.assertTrue(cancel.is("[disabled]"));
		sleep(1000);
		Assertions.assertFalse(ok2.is("[disabled]"));
		Assertions.assertFalse(cancel.is("[disabled]"));

		click(cancel);
		Assertions.assertTrue(ok2.is("[disabled]"));
		Assertions.assertTrue(cancel.is("[disabled]"));
		sleep(1000);
		Assertions.assertFalse(ok2.is("[disabled]"));
		Assertions.assertFalse(cancel.is("[disabled]"));

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(ok2.is("[disabled]"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertFalse(ok2.is("[disabled]"));
	}
}
