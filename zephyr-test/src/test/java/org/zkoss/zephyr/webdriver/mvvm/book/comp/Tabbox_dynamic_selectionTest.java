/* Tabbox_dynamic_selectionTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 17:58:07 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class Tabbox_dynamic_selectionTest extends ZephyrClientMVVMTestCase {
	@Test
	public void testIndex() {
		connect();
		JQuery buttons = jq("$vbox1 @button");
		JQuery selectedIndex = jq("$selectedIndex");
		click(buttons.eq(0));
		waitResponse();
		assertEquals("1", selectedIndex.text());
		click(buttons.eq(1));
		waitResponse();
		assertEquals("3", selectedIndex.text());

		click(jq("$vbox1 @tab").eq(4));
		waitResponse();
		assertEquals("4", selectedIndex.text());
	}

	@Test
	public void testItem() {
		connect();
		JQuery buttons = jq("$vbox2 @button");
		JQuery selectedItem = jq("$selectedItem");
		click(buttons.eq(0));
		waitResponse();
		assertEquals("Tab 1", selectedItem.text());
		click(buttons.eq(1));
		waitResponse();
		assertEquals("Tab 3", selectedItem.text());

		click(jq("$vbox2 @tab").eq(4));
		waitResponse();
		assertEquals("Tab 4", selectedItem.text());
	}
}
