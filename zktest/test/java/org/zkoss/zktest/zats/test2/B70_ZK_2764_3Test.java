package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B70_ZK_2764_3Test extends WebDriverTestCase {
	@Test
	public void test3() {
		connect();
		click(jq(".z-treerow .z-tree-icon").eq(2));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(4));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(3));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(0));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(2));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(1));
		waitResponse();
		JQuery origTreeCells = jq(".z-treecell-text");
		List<String> origList = new ArrayList<String>();
		for (int i = 0; i <= 17; i++) {
			origList.add(origTreeCells.eq(i).text());
		}
		assertEquals(" node 1 (6) node 1.1 (2) node 1.1.1 (0) node 1.1.2 (1) node 1.2 (5) node 1.1.1 (3) node 1.1.2 (4) node 2 (10) node 3 (17) node 3.1 (13) node 3.1.1 (11) node 3.1.2 (12) node 3.2 (16) node 3.2.1 (14) node 3.2.2 (15) node 4 (21) node 5 (25)",
				jq(".z-treecell-text").text());

		click(jq("@button"));
		waitResponse();
		assertEquals(" node 1 (6). node 1.1 (2). node 1.1.1 (0). node 1.1.2 (1). node 1.2 (5). node 1.1.1 (3). node 1.1.2 (4). node 2 (10) node 3 (17) node 3.1 (13) node 3.1.1 (11) node 3.1.2 (12) node 3.2 (16) node 3.2.1 (14) node 3.2.2 (15) node 4 (21) node 5 (25)",
				jq(".z-treecell-text").text());

		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(" node 1 (6).. node 1.1 (2).. node 1.1.1 (0).. node 1.1.2 (1).. node 1.2 (5).. node 1.1.1 (3).. node 1.1.2 (4).. node 2 (10) node 3 (17) node 3.1 (13) node 3.1.1 (11) node 3.1.2 (12) node 3.2 (16) node 3.2.1 (14) node 3.2.2 (15) node 4 (21) node 5 (25)",
				jq(".z-treecell-text").text());
		click(jq("@button").eq(2));
		waitResponse();
		JQuery treeCells = jq(".z-treecell-text");
		for (int i = 0; i <= 17; i++) {
			JQuery treeCell = treeCells.eq(i);
			assertTrue(treeCell.text().contains(origList.get(i)));
		}
	}
}