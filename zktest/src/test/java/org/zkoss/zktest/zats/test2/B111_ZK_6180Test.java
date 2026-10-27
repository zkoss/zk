/* B111_ZK_6180Test.java

	Purpose:

	Description:

	History:
		Sat Oct 24 19:09:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B111_ZK_6180Test extends WebDriverTestCase {
	@Test
	public void rowsOuterDrawsWithCurrentRows() {
		connect();
		for (int push = 1; push <= 3; push++) {
			push("grid");
			assertEquals("true", getEval("zk.$('$grid')._rowsAtRedraw"),
					"push " + push + ": the grid should point at the new rows while they are drawn");
			assertEquals("true", getEval("zk.$('$grid').rows === zk.$('$rows')"));
			assertFirstCells("$grid", push, 2);
			assertNoJSError();
		}
	}

	@Test
	public void pagedRowsOuter() {
		connect();
		for (int push = 1; push <= 3; push++) {
			push("pgrid");
			assertEquals("true", getEval("zk.$('$pgrid')._rowsAtRedraw"),
					"push " + push + ": the paged grid should point at the new rows while they are drawn");
			assertEquals("true", getEval("zk.$('$pgrid').rows === zk.$('$prows')"));
			assertFirstCells("$pgrid", push, 2);
			assertNoJSError();
		}
	}

	@Test
	public void otherChildOuterKeepsPointers() {
		connect();
		click(jq("$others"));
		waitResponse();
		assertEquals("true", getEval("zk.$('$grid').rows === zk.$('$rows')"));
		assertEquals("true", getEval("zk.$('$grid').columns === zk.$('$cols')"));
		assertEquals("true", getEval("zk.$('$grid').foot === zk.$('$foot')"));
		assertEquals("true", getEval("zk.$('$grid').frozen === zk.$('$frozen')"));
		assertEquals("true", getEval("(function (g) { var p = g.paging; return !!p && !!p.desktop"
				+ " && p.parent === g && p.getMeshWidget() === g && jq(p.$n()).closest(g.$n()).length === 1; })(zk.$('$pgrid'))"));
		assertNoJSError();

		push("grid");
		assertFirstCells("$grid", 1, 2);
		assertFirstCells("$pgrid", 1, 2);
		assertNoJSError();
	}

	private void push(String grid) {
		eval("zk.$('$" + grid + "')._rowsAtRedraw = undefined");
		click(jq("$push"));
		waitResponse();
	}

	private void assertFirstCells(String grid, int push, int count) {
		JQuery rows = jq(grid).find(".z-row");
		assertEquals(count, rows.length(), grid + " should show " + count + " rows");
		for (int i = 0; i < count; i++)
			assertEquals("push " + push + ", row " + (i + 1), rows.eq(i).find(".z-label").eq(0).text());
	}
}
