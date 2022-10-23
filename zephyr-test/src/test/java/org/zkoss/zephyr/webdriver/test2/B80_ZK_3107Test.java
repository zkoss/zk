/* B80_ZK_3107Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 2 14:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3107Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		try {
			connect();
			sleep(2000);
			JQuery t = jq("@textbox");
			assertEquals("Peter", t.val());
			JQuery l1 = jq("$l1");
			assertEquals("PETER", l1.text());
			JQuery l2 = jq("$l2");
			assertEquals("5", l2.text());
			JQuery l3 = jq("$l3");
			assertEquals("3", l3.text());
			JQuery l4 = jq("$l4");
			assertEquals("1", l4.text());

			click(jq("@button"));
			waitResponse();
			assertEquals("HI", l1.text());
			assertEquals("2", l2.text());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNoJSError();
	}
}
