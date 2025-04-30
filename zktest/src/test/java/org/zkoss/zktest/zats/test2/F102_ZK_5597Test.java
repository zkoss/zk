/* F102_ZK_5597Test.java

	Purpose:

	Description:

	History:
		Tue Apr 22 17:23:55 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F102_ZK_5597Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqSbar1 = jq("$sbar1");
		JQuery jqSbar2 = jq("$sbar2");
		JQuery jqSbar3 = jq("$sbar3");
		JQuery jqSbar4 = jq("$sbar4");
		assertFalse(jqSbar1.hasClass("z-stepbar-vertical"));
		assertFalse(jqSbar2.hasClass("z-stepbar-vertical"));
		assertTrue(jqSbar3.hasClass("z-stepbar-vertical"));
		assertTrue(jqSbar4.hasClass("z-stepbar-vertical"));
		assertEquals(jqSbar1.find("@step:eq(0)").offsetTop(), jqSbar1.find("@step:eq(1)").offsetTop());
		assertEquals(jqSbar2.find("@step:eq(0)").offsetTop(), jqSbar2.find("@step:eq(1)").offsetTop());
		assertNotEquals(jqSbar1.find("@step:eq(0)").offsetLeft(), jqSbar1.find("@step:eq(1)").offsetLeft());
		assertNotEquals(jqSbar2.find("@step:eq(0)").offsetLeft(), jqSbar2.find("@step:eq(1)").offsetLeft());
		assertEquals(jqSbar3.find("@step:eq(0)").offsetLeft(), jqSbar3.find("@step:eq(1)").offsetLeft());
		assertEquals(jqSbar4.find("@step:eq(0)").offsetLeft(), jqSbar4.find("@step:eq(1)").offsetLeft());
		assertNotEquals(jqSbar3.find("@step:eq(0)").offsetTop(), jqSbar3.find("@step:eq(1)").offsetTop());
		assertNotEquals(jqSbar4.find("@step:eq(0)").offsetTop(), jqSbar4.find("@step:eq(1)").offsetTop());
		click(jq("$btn1"));
		click(jq("$btn2"));
		click(jq("$btn3"));
		click(jq("$btn4"));
		waitResponse();
		assertTrue(jqSbar1.hasClass("z-stepbar-vertical"));
		assertTrue(jqSbar2.hasClass("z-stepbar-vertical"));
		assertFalse(jqSbar3.hasClass("z-stepbar-vertical"));
		assertFalse(jqSbar4.hasClass("z-stepbar-vertical"));
		assertEquals(jqSbar1.find("@step:eq(0)").offsetLeft(), jqSbar1.find("@step:eq(1)").offsetLeft());
		assertEquals(jqSbar2.find("@step:eq(0)").offsetLeft(), jqSbar2.find("@step:eq(1)").offsetLeft());
		assertNotEquals(jqSbar1.find("@step:eq(0)").offsetTop(), jqSbar1.find("@step:eq(1)").offsetTop());
		assertNotEquals(jqSbar2.find("@step:eq(0)").offsetTop(), jqSbar2.find("@step:eq(1)").offsetTop());
		assertEquals(jqSbar3.find("@step:eq(0)").offsetTop(), jqSbar3.find("@step:eq(1)").offsetTop());
		assertEquals(jqSbar4.find("@step:eq(0)").offsetTop(), jqSbar4.find("@step:eq(1)").offsetTop());
		assertNotEquals(jqSbar3.find("@step:eq(0)").offsetLeft(), jqSbar3.find("@step:eq(1)").offsetLeft());
		assertNotEquals(jqSbar4.find("@step:eq(0)").offsetLeft(), jqSbar4.find("@step:eq(1)").offsetLeft());
	}
}
