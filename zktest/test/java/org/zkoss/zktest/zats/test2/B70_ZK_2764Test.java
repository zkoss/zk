package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.util.ArrayList;
import java.util.List;

public class B70_ZK_2764Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-treerow .z-tree-icon"));
		waitResponse();
		click(jq(".z-treerow .z-tree-icon").eq(1));
		waitResponse();
		JQuery treeCells = jq(".z-treecell-text");
		String cellsString = "";
		for (int i = 0; i <= 7; i++) {
			JQuery cell = treeCells.eq(i);
			cellsString += cell.text();
		}
		assertEquals(
				"node 1 (3)node 1.1 (2)node 1.1.1 (0)node 1.1.2 (1)node 2 (5)node 3 (7)node 4 (9)node 5 (11)",
				cellsString);

		click(jq("@button"));
		waitResponse();
		cellsString = "";
		for (int i = 0; i <= 7; i++) {
			JQuery cell = treeCells.eq(i);
			cellsString += cell.text();
		}
		assertEquals(
				"node 1 (3).node 1.1 (2).node 1.1.1 (0).node 1.1.2 (1).node 2 (5).node 3 (7).node 4 (9).node 5 (11).",
				cellsString);

		click(jq("@button").eq(1));
		waitResponse();
		cellsString = "";
		for (int i = 0; i <= 7; i++) {
			JQuery cell = treeCells.eq(i);
			cellsString += cell.text();
		}
		assertEquals(
				"node 1 (16)node 1.1 (15)node 1.1.1 (13)node 1.1.2 (14)node 2 (18)node 3 (20)node 4 (22)node 5 (24)",
				cellsString);
	}
}