package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3230Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-treerow").eq(0));
		waitResponse();
		JQuery tree = jq("@tree");
		clickAt(tree, (tree.width() / 2) - 5, tree.height() / 3);
		waitResponse();
		assertNotEquals(0, jq(".z-tree-body").scrollTop());
		int scrollTop = jq(".z-tree-body").scrollTop();
		clickAt(tree, (tree.width() / 2) - 5, tree.height() / 3);
		waitResponse();
		assertTrue(scrollTop < jq(".z-tree-body").scrollTop());
		scrollTop = jq(".z-tree-body").scrollTop();
		clickAt(tree, (tree.width() / 2) - 5, tree.height() / 3);
		waitResponse();
		assertTrue(scrollTop < jq(".z-tree-body").scrollTop());
	}
}
