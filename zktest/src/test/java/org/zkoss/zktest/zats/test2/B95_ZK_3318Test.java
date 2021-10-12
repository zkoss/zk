/* B95_ZK_3318Test.java

	Purpose:

	Description:

	History:
		Wed Dec 16 16:15:20 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class B95_ZK_3318Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqBar = jq(".z-treecols-bar");
		JQuery tree1 = jq("$tree1");
		JQuery tree2 = jq("$tree2");
		JQuery tree3 = jq("$tree3");
		assertEquals(tree1.find(".z-treerow:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(), tree1.width(), 1);
		tree1.scrollTop(100);
		waitResponse();
		assertEquals(tree1.find(".z-treerow:eq(0)").outerWidth() + jqBar.eq(0).outerWidth(), tree1.width(), 1);
		assertEquals(tree2.find(".z-treerow:eq(0)").outerWidth(), tree2.width(), 1);
		assertNotEquals(tree3.find(".z-treechildren").height(), tree3.find(".z-tree-body").height(), 1);
	}
}
