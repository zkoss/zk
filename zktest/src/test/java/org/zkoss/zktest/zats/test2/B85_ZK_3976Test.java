/* B85_ZK_3976Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 29 15:38:05 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3976Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testTree(jq("@tree:eq(0)"));
		testTree(jq("@tree:eq(1)"));
	}

	private void testTree(JQuery tree) {
		Assert.assertEquals(3, tree.find("@treerow").length());

		click(tree.find(".z-tree-icon:eq(0)"));
		waitResponse();
		Assert.assertEquals("tree didn't close", 1, tree.find("@treerow").length());

		click(tree.find(".z-tree-icon:eq(0)"));
		waitResponse();
		Assert.assertEquals("tree didn't open", 3, tree.find("@treerow").length());
	}
}
