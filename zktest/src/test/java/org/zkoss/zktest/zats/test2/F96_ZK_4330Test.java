/* F96_ZK_4330Test.java

	Purpose:

	Description:

	History:
		Tue Feb 09 20:30:19 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;


public class F96_ZK_4330Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tree1 = jq("@tree:eq(0)");
		JQuery tree2 = jq("@tree:eq(1)");
		JQuery tree3 = jq("@tree:eq(2)");
		JQuery selAllBtn = jq("@button:eq(0)");
		JQuery deSelectAll = jq("@button:eq(1)");
		JQuery selCountBtn = jq("@button:eq(2)");

		checkVisibleUncheckedTreeItems(tree1);
		checkVisibleUncheckedTreeItems(tree2);
		checkVisibleUncheckedTreeItems(tree3);
		Assertions.assertFalse(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assertions.assertFalse(tree2.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		// tree3 has no model, so it's considered as "select all"
		Assertions.assertTrue(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));

		click(jq("@button:eq(6)"));
		waitResponse();
		checkVisibleUncheckedTreeItems(tree1);
		checkVisibleUncheckedTreeItems(tree2);
		checkVisibleUncheckedTreeItems(tree3);
		click(selCountBtn);
		waitResponse();
		Assertions.assertTrue(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assertions.assertTrue(tree2.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assertions.assertTrue(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));

		click(deSelectAll);
		waitResponse();
		click(selAllBtn);
		waitResponse();
		click(selCountBtn);
		waitResponse();
		Assertions.assertTrue(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assertions.assertTrue(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assertions.assertEquals("4, 60, 4\n4, 60, 4", getZKLog());
	}
	@Test
	public void testNestedItem() {
		connect();
		JQuery tree1 = jq("@tree:eq(0)");
		click(tree1.find(".z-tree-icon"));
		waitResponse();
		click(tree1.find(".z-treerow-checkbox:eq(1)"));
		waitResponse();
		click(tree1.find(".z-treecol-checkable"));
		waitResponse();
		click(tree1.find(".z-treecol-checkable"));
		waitResponse();
		click(tree1.find(".z-treerow-checkbox:eq(1)"));
		waitResponse();
		Assertions.assertTrue(tree1.find(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
	}

	private void checkVisibleUncheckedTreeItems(JQuery tree) {
		final JQuery treeitems = tree.find(".z-treerow:not(.z-treerow-selected):visible");
		// Reverse click since the treeitems is dynamic
		for (int i = treeitems.length() - 1; i >= 0; i--) {
			click(treeitems.get(i));
			waitResponse();
		}
		waitResponse();
	}
}
