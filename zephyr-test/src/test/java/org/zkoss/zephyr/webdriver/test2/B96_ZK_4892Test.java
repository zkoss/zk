/* B96_ZK_4892Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jul 14 11:30:11 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_4892Test extends ZephyrClientMVVMTestCase {

	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");
		JQuery l4 = jq("$l4");
		JQuery l5 = jq("$l5");
		JQuery l6 = jq("$l6");
		String l1Val = l1.text();
		String l2Val = l2.text();
		String l3Val = l3.text();
		String l4Val = l4.text();
		String l5Val = l5.text();

		click(jq("$cmd1"));
		waitResponse();
		assertEquals(l1Val, l1.text());
		assertEquals(l2Val, l2.text());
		assertEquals(l3Val, l3.text());
		assertEquals(l4Val, l4.text());
		assertEquals(l5Val, l5.text());
		assertEquals("info - cmd1", l6.text());

		click(jq("$cmd2"));
		waitResponse();
		assertEquals(l1Val, l1.text());
		assertEquals(l2Val, l2.text());
		assertEquals(l3Val, l3.text());
		assertEquals("B-option", l4.text());
		assertEquals("B-option", l5.text());
		assertEquals("info - cmd2", l6.text());

		click(jq("$cmd3"));
		waitResponse();
		assertEquals("info - cmd3", l6.text());
	}
}
