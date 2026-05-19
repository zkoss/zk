/* F104_ZK_5409_ResponsiveGridEventTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:15 CST 2026, Created by peakerlee

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

public class F104_ZK_5409_ResponsiveGridEventTest extends WebDriverTestCase {

	/**
	 * Test 13.1: onClick on Row fires in table mode, and stacking mode renders correctly.
	 */
	@Test
	public void testRowClickStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-event.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Start in table mode (wide) — verify row click events work
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertFalse(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid should be in table mode at wide width");

		// Click first row in table mode
		click(jq("$grid1 .z-row:eq(0)"));
		waitResponse();

		assertEquals("clicked: row0", jq("$clickedRow").text(),
				"onClick should fire for row 0 in table mode");

		// Click second row in table mode
		click(jq("$grid1 .z-row:eq(1)"));
		waitResponse();

		assertEquals("clicked: row1", jq("$clickedRow").text(),
				"onClick should fire for row 1 in table mode");

		// Now switch to stacking mode — verify stacking class is applied
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode at narrow width");

		// Verify stacking zone has rows rendered
		assertTrue(jq("$grid1").find(".z-grid-body tbody > tr.z-row").length() >= 3,
				"Stacking zone should contain row divs");
	}

	/**
	 * Test 13.4: onSort in stacking mode.
	 * Sort should work even with header hidden; rows should reorder; stacking maintained.
	 */
	@Test
	public void testSortStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-event.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// First sort in table mode to verify sorting works
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		// Trigger sort via server-side button (calls colName.sort(true) in zscript)
		click(jq("$btnSort"));
		waitResponse();
		waitResponse();

		// Verify rows are reordered in table mode first (Charlie→Alice, Alice→Bob, Bob→Charlie after ascending sort by name)
		// The first row's first cell should now be Alice (sorted ascending)
		String firstNameInTable = jq("$grid1 .z-row:eq(0) .z-label:eq(0)").text();
		assertEquals("Alice", firstNameInTable,
				"After ascending sort in table mode, first row name should be Alice (got: '" + firstNameInTable + "')");

		// Now switch to stacking mode — stacking zone should rebuild from sorted rows
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();
		waitResponse();

		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid should be in stacking mode after sort");

		// Verify stacking zone rebuilt with sorted rows (Alice first)
		JQuery stackingRows = jq("$grid1").find(".z-grid-body tbody > tr.z-row");
		assertEquals(3, stackingRows.length(),
				"Stacking zone should contain 3 rows after sort");
		String firstNameInStacking = stackingRows.eq(0).find("td:eq(0)").text();
		assertEquals("Alice", firstNameInStacking,
				"Stacking zone should mirror sorted order — first row name should be Alice (got: '" + firstNameInStacking + "')");
	}

	/**
	 * Test 13.5: onPaging in stacking mode.
	 * Page navigation should preserve stacking mode.
	 */
	@Test
	public void testPagingStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-paging.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Navigate to page 2
		click(jq("$grid1 .z-paging-next"));
		waitResponse();

		// Grid should still be in stacking mode
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after paging");

		// Paging event should have fired
		assertEquals("page: 1", jq("$pagingStatus").text(),
				"onPaging event should fire with activePage=1");
	}

	/**
	 * Test 14.1: Frozen + responsive=stacking.
	 * In table mode (wide), frozen should work normally.
	 * In stacking mode (narrow), frozen should be automatically disabled/ignored.
	 */
	@Test
	public void testFrozenWithStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-exclusion.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide: table mode — frozen should work
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		JQuery gridFrozen = jq("$gridFrozen");
		assertFalse(gridFrozen.hasClass("z-grid--stacking"),
				"Frozen grid should be in table mode at wide width");

		// Frozen div should exist in table mode
		JQuery frozen = jq("$gridFrozen .z-frozen");
		assertTrue(frozen.exists(),
				"Frozen component should exist in table mode");

		// Narrow: stacking mode — frozen should be disabled/hidden
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(gridFrozen.hasClass("z-grid--stacking"),
				"Frozen grid should enter stacking mode");

		// In stacking mode, frozen scrollbar should not be active
		// (The grid enters stacking, frozen concept doesn't apply)
		// No JavaScript errors should occur
		assertNoJSError();
	}

	/**
	 * Test 14.2: Auxhead + responsive=stacking.
	 * In table mode the auxhead row is visible. In stacking mode the
	 * entire grid header (including auxhead) is hidden via `display:none`;
	 * auxhead content is not rendered in stacking mode.
	 */
	@Test
	public void testAuxheadWithStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-exclusion.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide: table mode — auxheader cells visible
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		JQuery gridAuxhead = jq("$gridAuxhead");
		assertFalse(gridAuxhead.hasClass("z-grid--stacking"),
				"Auxhead grid should be in table mode at wide width");
		assertTrue(jq("$gridAuxhead .z-auxheader").isVisible(),
				"Auxheader cells should be visible in table mode");

		// Narrow: stacking mode — auxhead hidden along with the grid header
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(gridAuxhead.hasClass("z-grid--stacking"),
				"Auxhead grid should enter stacking mode");
		assertFalse(jq("$gridAuxhead .z-grid-header").isVisible(),
				"Grid header (containing auxhead) should be hidden in stacking");
		assertFalse(jq("$gridAuxhead .z-auxheader").isVisible(),
				"Auxheader cells should be hidden in stacking");

		assertNoJSError();

		// Widen: back to table — auxhead reappears
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertTrue(jq("$gridAuxhead .z-auxheader").isVisible(),
				"Auxheader cells should reappear when returning to table mode");
	}
}
