package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B70_ZK_2764_1Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();
		click(jq(".z-treerow .z-tree-icon"));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(1));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(4));
		waitResponse();
		assertEquals(
				" node 1 (3) node 1.1 (2) node 1.1.1 (0) node 1.1.2 (1) node 2 (5) node 2.1 (4) node 3 (7) node 4 (9) node 5 (11)",
				jq(".z-treecell-text").text());

		click(jq("@button"));
		waitResponse();
		assertEquals(
				" node 1 (3). node 1.1 (2). node 1.1.1 (0). node 1.1.2 (1). node 2 (5) node 2.1 (4) node 3 (7) node 4 (9) node 5 (11)",
				jq(".z-treecell-text").text());

		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(
				" node 1 (16) node 1.1 (15) node 1.1.1 (13) node 1.1.2 (14) node 2 (18) node 2.1 (17) node 3 (20) node 4 (22) node 5 (24)",
				jq(".z-treecell-text").text());
	}
}