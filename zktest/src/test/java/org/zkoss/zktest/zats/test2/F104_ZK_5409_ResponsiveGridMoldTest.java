/* F104_ZK_5409_ResponsiveGridMoldTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:22:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_ResponsiveGridMoldTest extends WebDriverTestCase {

	/**
	 * Test 2.2: Paging mold + stacking — paging bar should still appear.
	 */
	@Test
	public void testPagingMoldStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-paging.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Paging bar should be visible
		assertTrue(jq("$grid1 .z-paging").exists(),
				"Paging bar should exist in stacking + paging mode");
		assertTrue(jq("$grid1 .z-paging").isVisible(),
				"Paging bar should be visible in stacking + paging mode");

		// Only first page of rows visible (pageSize=3)
		JQuery rows = jq("$grid1").find(".z-grid-body tbody > tr.z-row");
		assertEquals(3, rows.length(),
				"Should show 3 rows on first page");

		// Navigate to page 2
		click(jq("$grid1 .z-paging-next"));
		waitResponse();

		// Should still be in stacking mode after paging
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after paging");

		// Should show remaining rows on page 2
		JQuery rowsPage2 = jq("$grid1").find(".z-grid-body tbody > tr.z-row");
		assertEquals(3, rowsPage2.length(),
				"Should show 3 rows on second page");
	}

	/**
	 * Test 2.3: Paging + multi-column stacking.
	 */
	@Test
	public void testPagingMultiColumnStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-paging.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid2");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// With responsive-columns="3" and pageSize=6, should show 6 items in 2 rows of 3
		JQuery rows = jq("$grid2").find(".z-grid-body tbody > tr.z-row");
		assertEquals(6, rows.length(),
				"Should show 6 rows on first page");

		// Paging should work
		assertTrue(jq("$grid2 .z-paging").isVisible(),
				"Paging bar should be visible");
	}

	/**
	 * Test 5.1: Fixed height grid in stacking mode — body should be scrollable.
	 */
	@Test
	public void testFixedHeightStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$gridFixedH");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Grid should maintain its fixed height (200px)
		int gridHeight = grid.height();
		assertTrue(gridHeight > 0 && gridHeight <= 250,
				"Grid height should be approximately 200px (got " + gridHeight + ")");

		// Stacking zone should contain the rows
		assertTrue(jq("$gridFixedH").find(".z-grid-body tbody > tr.z-row").length() > 0,
				"Stacking zone should contain rows in fixed-height grid");
	}

	/**
	 * Test 5.2: vflex="1" grid in stacking mode — should fill parent container.
	 */
	@Test
	public void testVflexStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$gridVflex");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Grid should fill the parent container height (300px div)
		int gridHeight = grid.height();
		assertTrue(gridHeight >= 250,
				"vflex grid should fill parent height (~300px, got " + gridHeight + ")");
	}

	/**
	 * Test 5.3: No height grid in stacking mode — height expands with content.
	 */
	@Test
	public void testAutoHeightStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$gridAuto");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Grid height should be > 0 (auto-sized to content)
		assertTrue(grid.height() > 0,
				"Auto-height grid should have positive height");
	}

	/**
	 * Test 5.4: Fixed width grid — stacking arranges within the fixed width.
	 */
	@Test
	public void testFixedWidthStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		// Use wide window so the 600px fixed-width grid container < 768 is still relevant
		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		JQuery grid = jq("$gridFixedW");
		// With width=600px and md breakpoint=768px, the container is 600px < 768px
		// So the grid should be in stacking mode even with a wide window
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Fixed width 600px grid should be in stacking mode (600 < 768 md breakpoint)");

		// Grid width should be approximately 600px
		int gridWidth = grid.width();
		assertTrue(gridWidth > 550 && gridWidth <= 650,
				"Grid width should be ~600px (got " + gridWidth + ")");
	}

	/**
	 * Test 5.5: hflex="1" grid — fills parent width, responsive based on that width.
	 */
	@Test
	public void testHflexStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$gridHflex");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"hflex grid should be in stacking mode at 600px");

		// Grid should fill the parent width
		assertTrue(grid.width() > 500,
				"hflex grid should fill parent width");
	}
}
