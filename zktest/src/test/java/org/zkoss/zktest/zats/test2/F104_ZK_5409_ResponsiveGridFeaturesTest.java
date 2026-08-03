/* F104_ZK_5409_ResponsiveGridFeaturesTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:08 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_ResponsiveGridFeaturesTest extends WebDriverTestCase {

	/**
	 * Test 17.6: Custom sclass on Grid should be preserved across mode changes.
	 */
	@Test
	public void testCustomSclassPreserved() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide — table mode
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertTrue(jq("$gridFeatures").hasClass("my-custom-grid"),
				"Custom sclass 'my-custom-grid' should exist in table mode");

		// Narrow — stacking mode
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridFeatures").hasClass("my-custom-grid"),
				"Custom sclass should be preserved in stacking mode");
		assertTrue(jq("$gridFeatures").hasClass("z-grid--stacking"),
				"Grid should also have stacking class");
	}

	/**
	 * Test 7.4: Column width/hflex/align should not break responsive behavior.
	 */
	@Test
	public void testColumnWidthHflexAlign() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Table mode — column widths should be honored
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(jq("$gridFeatures").hasClass("z-grid--stacking"),
				"Grid should be in table mode");

		// Stacking mode — column widths don't apply; responsive layout takes over
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridFeatures").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Stacking zone should have 2 rows regardless of column widths
		assertEquals(2, jq("$gridFeatures").find(".z-grid-body tbody > tr.z-row").length(),
				"Stacking zone should contain 2 rows");

		// Back to table mode — layout should restore properly
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(jq("$gridFeatures").hasClass("z-grid--stacking"),
				"Grid should return to table mode");
	}

	/**
	 * Test 13.2 + 13.3: onRightClick and onDoubleClick in table mode.
	 * Event handlers wired to rows should fire correctly when grid is in table mode.
	 */
	@Test
	public void testRowRightClickAndDoubleClick() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Table mode — verify events work on original rows
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(jq("$gridFeatures").hasClass("z-grid--stacking"),
				"Grid should be in table mode");

		// Right-click the first row
		getActions().contextClick(toElement(jq("$gridFeatures .z-row:eq(0)"))).perform();
		waitResponse();

		assertEquals("right: row0", jq("$rightClickStatus").text(),
				"onRightClick should fire in table mode");

		// Double-click the first row
		getActions().doubleClick(toElement(jq("$gridFeatures .z-row:eq(0)"))).perform();
		waitResponse();

		assertEquals("double: row0", jq("$dblClickStatus").text(),
				"onDoubleClick should fire in table mode");
	}

	/**
	 * Test 9.5 + 9.10: Image cells and long text content in stacking mode.
	 */
	@Test
	public void testImageAndLongTextStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridImageLong").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Stacking zone should have 2 rows
		JQuery stackingRows = jq("$gridImageLong").find(".z-grid-body tbody > tr.z-row");
		assertEquals(2, stackingRows.length(),
				"Stacking zone should contain 2 rows");

		// Each row should have 2 cells (Icon + Description)
		JQuery firstRowCells = stackingRows.eq(0).find("td");
		assertEquals(2, firstRowCells.length(),
				"Each row should have Icon + Description cells");

		// Image cell should contain an img element (cloned into stacking zone)
		assertTrue(stackingRows.eq(0).find("img").length() > 0,
				"Image should be rendered in stacking zone");

		// Long text should be present in the value span
		String longText = stackingRows.eq(0).find("td:eq(1)").text();
		assertTrue(longText.length() > 100,
				"Long description text should be preserved in stacking (length=" + longText.length() + ")");
	}

	/**
	 * Test 8.2: Row visible=false in both modes, and toggle.
	 */
	@Test
	public void testRowVisibleToggleInStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridRowVisible").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Initially: rowA visible, rowB hidden, rowC visible → 2 visible rows
		JQuery rows = jq("$gridRowVisible").find(".z-grid-body tbody > tr.z-row");
		// 3 rows are rendered but rowB has display:none
		assertEquals(3, rows.length(),
				"Stacking zone should contain 3 row divs");
		// Count visible ones (not display:none)
		int visibleCount = 0;
		for (int i = 0; i < rows.length(); i++) {
			if (!"none".equals(rows.eq(i).css("display")))
				visibleCount++;
		}
		assertEquals(2, visibleCount,
				"Only 2 rows should be visible (rowB is hidden)");

		// Toggle rowB visibility — should now show 3 visible rows
		click(jq("$btnToggleVis"));
		waitResponse();
		waitResponse();

		JQuery rowsAfter = jq("$gridRowVisible").find(".z-grid-body tbody > tr.z-row");
		int visibleAfter = 0;
		for (int i = 0; i < rowsAfter.length(); i++) {
			if (!"none".equals(rowsAfter.eq(i).css("display")))
				visibleAfter++;
		}
		assertEquals(3, visibleAfter,
				"After toggle, all 3 rows should be visible");
	}

	/**
	 * Test 12.3: Dynamic add Column — stacking zone should re-render with new column.
	 */
	@Test
	public void testDynamicAddColumnStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridDynCol").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Initially: 2 columns, 1 row with 2 cells
		JQuery firstRow = jq("$gridDynCol").find(".z-grid-body tbody > tr.z-row:eq(0)");
		assertEquals(2, firstRow.find("td").length(),
				"Initially should have 2 cells in stacking");

		// Add a column
		click(jq("$btnAddCol"));
		waitResponse();
		waitResponse();

		// Now should have 3 cells in stacking
		JQuery firstRowAfter = jq("$gridDynCol").find(".z-grid-body tbody > tr.z-row:eq(0)");
		assertEquals(3, firstRowAfter.find("td").length(),
				"After add column, should have 3 cells in stacking");

		// New column label should appear in a cell's data-label attr
		boolean col3Found = false;
		JQuery labels = firstRowAfter.find("td[data-label]");
		for (int i = 0; i < labels.length(); i++) {
			if ("Col3".equals(labels.eq(i).attr("data-label"))) {
				col3Found = true;
				break;
			}
		}
		assertTrue(col3Found, "New column 'Col3' should appear in stacking");
	}

	/**
	 * Test 12.4: Dynamic remove Column — stacking zone should re-render without the column.
	 */
	@Test
	public void testDynamicRemoveColumnStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-features.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// Initially: 2 cells
		assertEquals(2, jq("$gridDynCol").find(".z-grid-body tbody > tr.z-row:eq(0) td").length(),
				"Initially should have 2 cells");

		// Remove a column
		click(jq("$btnRemoveCol"));
		waitResponse();
		waitResponse();

		// Now should have 1 cell
		assertEquals(1, jq("$gridDynCol").find(".z-grid-body tbody > tr.z-row:eq(0) td").length(),
				"After remove column, should have 1 cell in stacking");
	}
}
