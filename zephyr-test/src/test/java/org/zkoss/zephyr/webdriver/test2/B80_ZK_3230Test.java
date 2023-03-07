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
		waitResponse();
		click(jq(".z-treerow").eq(0));
		waitResponse();
		JQuery tree = jq("@tree");
		jq(".z-tree-body").scrollTop(tree.height() / 3);
		waitResponse();
		assertNotEquals(0, jq(".z-tree-body").scrollTop());
		int scrollTop = jq(".z-tree-body").scrollTop();
		jq(".z-tree-body").scrollTop(tree.height() / 2);
		waitResponse();
		assertTrue(scrollTop < jq(".z-tree-body").scrollTop());
		scrollTop = jq(".z-tree-body").scrollTop();
		jq(".z-tree-body").scrollTop(tree.height());
		waitResponse();
		assertTrue(scrollTop < jq(".z-tree-body").scrollTop());
	}
}
