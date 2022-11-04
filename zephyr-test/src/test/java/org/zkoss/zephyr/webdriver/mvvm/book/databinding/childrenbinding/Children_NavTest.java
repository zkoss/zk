/* Children_NavTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 16:05:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.childrenbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Children_NavTest extends WebDriverTestCase {
	@Test
	public void structureTest() {
		connect("/mvvm/book/databinding/childrenbinding/children-nav.zul");

		assertEquals(0, jq(".z-nav:contains(Item A)").length());
		assertEquals(1, jq(".z-navitem:contains(Item A)").length());

		assertEquals(1, jq(".z-nav:contains(Item B)").length());
		assertEquals(1, jq(".z-navitem:contains(Item B)").length());

		assertEquals(2, jq(".z-nav:contains(Item C)").length());
		assertEquals(3, jq(".z-navitem:contains(Item C)").length());

		assertEquals(7, jq(".z-nav:contains(Item D)").length());
		assertEquals(15, jq(".z-navitem:contains(Item D)").length());

		click(jq(".z-nav:contains(Item C)"));
		waitResponse();
		click(jq(".z-nav:contains(Item C_0)"));
		waitResponse();
		click(jq(".z-navitem:contains(Item C_0_1)"));
		waitResponse();
		assertEquals("clicked " + jq(".z-navitem:contains(Item C_0_1)").text().trim(), jq("$msg").text());
	}
}
