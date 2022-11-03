/* IGridRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 30 17:28:57 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IGrid;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IGrid} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Grid">Grid</a>,
 * if any.
 *
 * @author katherine
 * @see IGrid
 */
public class IGridRichletTest extends WebDriverTestCase {
	@Test
	public void defaultMold() {
		connect("/data/igrid/mold/default");
		assertEquals(2, jq(".z-columns").children(".z-column").length());
		assertEquals(3, jq(".z-rows").children(".z-row").length());
		assertTrue(jq(".z-column-content:eq(0)").text().equals("Type"));
		assertTrue(jq(".z-row-content:eq(0)").text().equals("File:"));
		assertEquals(6, jq(".z-row").children().length());
		assertFalse(jq(".z-grid-paging-bottom").exists());
	}

	@Test
	public void pagingMold() {
		connect("/data/igrid/mold/paging");
		assertTrue(jq(".z-grid-paging-bottom").exists());
		assertEquals(2, jq(".z-row").length());
		click(jq(".z-paging-next"));
		waitResponse();
		assertEquals(1, jq(".z-row").length());
		click(jq(".z-icon-angle-double-left"));
		waitResponse();
		assertEquals(2, jq(".z-row").length());
	}

	@Test
	public void autoSort() {
		connect("/data/igrid/autosort");
		Iterator<JQuery> iterator = jq(".z-rows tr").iterator();
		for (int i = 0; ++i <= 3 && iterator.hasNext();) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
	}

	@Test
	public void children() {
		connect("/data/igrid/children");
		assertEquals("h1", jq(".z-auxheader:eq(0)").text());
		assertEquals("h2", jq(".z-auxheader:eq(1)").text());
		assertEquals("2", jq(".z-auxheader:eq(1)").attr("colspan"));
		assertEquals(4, jq(".z-columns").children(".z-column").length());
		assertEquals("Type", jq(".z-column:eq(0)").text());
		assertEquals(jq(".z-frozen-body").width(), jq(".z-auxheader:eq(0)").width(), 2);
		assertEquals(2, jq(".z-foot").children(".z-footer").length());
		assertEquals("f1", jq(".z-footer:eq(0)").text());
	}

	@Test
	public void emptyMessage() {
		connect("/data/igrid/emptyMessage");
		assertEquals("empty", jq(".z-grid-emptybody-content").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("empty message", jq(".z-grid-emptybody-content").text());
	}

	@Test
	public void innerWidth() {
		connect("/data/igrid/innerWidth");
		assertEquals(400, jq(".z-grid-body table").width());
		click(jq("@button"));
		waitResponse();
		assertEquals(450, jq(".z-grid-body table").width());
	}

	@Test
	public void oddRowSclass() {
		connect("/data/igrid/oddRowSclass");
		assertTrue(jq(".z-row.odd-row").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-row.odd").exists());
	}

	@Test
	public void visibleRows() {
		connect("/data/igrid/visibleRows");
		int height = jq(".z-grid-body").height() * 2;
		click(jq("@button"));
		waitResponse();
		assertEquals(height ,jq(".z-grid-body").height(), 1);
	}
}