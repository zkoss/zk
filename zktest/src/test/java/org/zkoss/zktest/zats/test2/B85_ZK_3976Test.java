/* B85_ZK_3976Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 29 15:38:05 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

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
		Assertions.assertEquals(3, tree.find("@treerow").length());

		click(tree.find(".z-tree-icon:eq(0)"));
		waitResponse();
		Assertions.assertEquals(1, tree.find("@treerow").length(), "tree didn't close");

		click(tree.find(".z-tree-icon:eq(0)"));
		waitResponse();
		Assertions.assertEquals(3, tree.find("@treerow").length(), "tree didn't open");
	}
}
