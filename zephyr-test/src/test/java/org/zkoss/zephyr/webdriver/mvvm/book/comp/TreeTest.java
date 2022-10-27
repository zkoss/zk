/* TreeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 10:51:56 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class TreeTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery treeitem = jq("@treeitem");
		JQuery selectedLabel = jq("$selectedLabel");

		click(treeitem.eq(1));
		waitResponse();
		assertEquals("Root.1", selectedLabel.text());
		click(treeitem.eq(0));
		waitResponse();
		assertEquals("Root.0", selectedLabel.text());

		assertEquals(2, jq("@treeitem").length());
		click(treeitem.eq(0).find(".z-tree-icon"));
		waitResponse();
		assertEquals(6, jq("@treeitem").length());
	}
}
