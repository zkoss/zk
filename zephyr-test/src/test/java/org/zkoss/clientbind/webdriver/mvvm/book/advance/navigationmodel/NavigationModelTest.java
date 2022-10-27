/* NavigationModelTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 17:37:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.navigationmodel;


import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigationModelTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		JQuery jqL1 = jq("$l1");
		assertEquals("AAA", jqL1.text());
		assertEquals("none", jq("$l2").text());
		assertEquals(4, jq("a").length());
		assertEquals(1, jq("$lv2 a").length());

		click(jq("a:contains(BBB)"));
		waitResponse();
		assertEquals("BBB", jqL1.text());
		assertEquals("none", jq("$l2").text());
		assertEquals(4, jq("a").length());
		assertEquals(1, jq("$lv2 a").length());

		click(jq("a:contains(BBB1)"));
		waitResponse();
		assertEquals("BBB", jqL1.text());
		assertEquals("BBB1", jq("$l2").text());
		assertEquals(4, jq("a").length());
		assertEquals(1, jq("$lv2 a").length());

		click(jq("a:contains(CCC)"));
		waitResponse();
		assertEquals("CCC", jqL1.text());
		assertEquals("none", jq("$l2").text());
		assertEquals(3, jq("a").length());
		assertEquals(0, jq("$lv2 a").length());
	}
}
