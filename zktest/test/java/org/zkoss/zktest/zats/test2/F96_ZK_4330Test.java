/* F96_ZK_4330Test.java

	Purpose:

	Description:

	History:
		Tue Feb 09 20:30:19 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;


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
		click(tree1.find(".z-treerow-checkbox:eq(0)"));
		click(tree1.find(".z-treerow-checkbox:eq(1)"));
		click(tree3.find(".z-treerow-checkbox:eq(0)"));
		jq(".z-tree-body:eq(1)").scrollTop(500);
		waitResponse();
		for (int i = 0; i < 30; i++) {
			click(tree2.find(".z-treerow-checkbox").eq(i));
		}
		waitResponse();
		Assert.assertFalse(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertFalse(tree2.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertFalse(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));

		click(jq("@button:eq(6)"));
		waitResponse();
		for (int i = 1; i < 4; i+=2) {
			click(tree2.find(".z-treerow-checkbox").eq(i));
		}
		jq(".z-tree-body:eq(1)").scrollTop(3000);
		waitResponse();
		click(jq("@button:eq(6)"));
		click(tree1.find(".z-treerow-checkbox:eq(1)"));
		click(tree1.find(".z-treerow-checkbox:eq(3)"));
		click(tree3.find(".z-treerow-checkbox:eq(1)"));
		click(tree3.find(".z-treerow-checkbox:eq(2)"));
		click(tree3.find(".z-treerow-checkbox:eq(3)"));
		for (int i = 3; i < 58; i+=2) {
			click(tree2.find(".z-treerow-checkbox").eq(i));
		}
		waitResponse();
		click(selCountBtn);
		Assert.assertTrue(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertTrue(tree2.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertTrue(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));

		click(deSelectAll);
		click(selAllBtn);
		click(selCountBtn);
		waitResponse();
		Assert.assertTrue(tree1.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertTrue(tree3.find(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Assert.assertEquals("4, 60, 4\n4, 60, 4", getZKLog());
	}
}
