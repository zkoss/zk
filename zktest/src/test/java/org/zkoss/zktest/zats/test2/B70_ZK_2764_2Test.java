package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B70_ZK_2764_2Test extends WebDriverTestCase {
	@Test
	public void test2() {
		connect();
		click(jq(".z-treerow .z-tree-icon"));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(1));
		waitResponse();
		assertEquals(
				"node 1 (4)node 1.1 (2)node 1.1.1 (0)node 1.1.2 (1)node 1.2 (3)node 2 (6)node 3 (8)node 4 (10)node 5 (12)",
				jq(".z-treecell-text").text());

		click(jq("@button"));
		waitResponse();
		assertEquals(
				"node 1 (4).node 1.1 (2).node 1.1.1 (0).node 1.1.2 (1).node 1.2 (3).node 2 (6)node 3 (8)node 4 (10)node 5 (12)",
				jq(".z-treecell-text").text());

		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(
				"node 1 (4)..node 1.1 (2)..node 1.1.1 (0)..node 1.1.2 (1)..node 1.2 (3)..node 2 (6)node 3 (8)node 4 (10)node 5 (12)",
				jq(".z-treecell-text").text());
	}
}