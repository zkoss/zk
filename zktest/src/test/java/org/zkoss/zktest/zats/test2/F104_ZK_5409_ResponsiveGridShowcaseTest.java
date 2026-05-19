/* F104_ZK_5409_ResponsiveGridShowcaseTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:22:59 CST 2026, Created by peakerlee

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

public class F104_ZK_5409_ResponsiveGridShowcaseTest extends WebDriverTestCase {

	/**
	 * Real-world grid: production tracking report with 14 columns, footer totals,
	 * mixed responsive-col visibility, and multi-column stacking.
	 * Verifies responsive works on a complex grid without errors.
	 */
	@Test
	public void testRealWorldComplexGrid() {
		connect("/test2/F104-ZK-5409-responsive-grid-realworld.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide viewport (table mode) — all 14 columns should show
		driver.manage().window().setSize(new Dimension(1600, size.height));
		waitResponse();

		assertFalse(jq("$summaryGrid").hasClass("z-grid--stacking"),
				"Real-world grid should be in table mode at 1600px");
		assertTrue(jq("$summaryGrid .z-column").length() >= 14,
				"All 14 columns should be rendered");
		assertTrue(jq("$summaryGrid .z-footer").length() > 0,
				"Footer should be visible");
		assertNoJSError();

		// Narrow viewport (stacking mode) — cells should stack as key-value
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();
		waitResponse();

		// Default tokens are "sm-1 md-none"; this grid uses "sm-1 lg-none"
		// so the table→stacking switch happens below lg (992 px).
		assertTrue(jq("$summaryGrid").hasClass("z-grid--stacking"),
				"Real-world grid should enter stacking at 600px (lg-none switch)");

		// Stacking zone should contain 8 data rows
		JQuery stackingRows = jq("$summaryGrid").find(".z-grid-body tbody > tr.z-row");
		assertEquals(8, stackingRows.length(),
				"Stacking zone should contain 8 data rows");

		// Verify responsive-col hides some columns.
		// Columns marked responsiveCol="none" should be hidden in stacking at < md.
		// Columns marked responsiveCol="none" should always be hidden in stacking.
		JQuery firstRow = stackingRows.eq(0);
		JQuery labels = firstRow.find("td[data-label]");

		// Confirm certain labels that should NOT appear (responsiveCol="none")
		String[] alwaysHidden = { "Rec.", "Arrastre", "Man.", "Ing./T" };
		for (String hiddenLabel : alwaysHidden) {
			for (int i = 0; i < labels.length(); i++) {
				JQuery lbl = labels.eq(i);
				String parentCls = lbl.parent().attr("class");
				boolean hidden = parentCls != null && parentCls.contains("z-cell-hide-stacking");
				if (!hidden && hiddenLabel.equals(lbl.text())) {
					assertFalse(true,
							"Column '" + hiddenLabel + "' with responsiveCol='none' should not appear visible in stacking");
				}
			}
		}

		assertNoJSError();

		// Resize back to widest — should restore table layout cleanly
		driver.manage().window().setSize(new Dimension(1600, size.height));
		waitResponse();

		assertFalse(jq("$summaryGrid").hasClass("z-grid--stacking"),
				"Grid should restore to table mode at wide width");
		assertNoJSError();
	}

	/**
	 * Frozen + responsive limitation: frozen works only in table mode.
	 * In stacking, frozen is gracefully hidden (no broken scrollbar, no errors).
	 */
	@Test
	public void testFrozenGracefulFallback() {
		connect("/test2/F104-ZK-5409-responsive-grid-frozen-limit.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide mode — frozen should be present and functional
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		JQuery frozenGrid = jq("$frozenGrid");
		assertFalse(frozenGrid.hasClass("z-grid--stacking"),
				"frozenGrid should be in table mode at wide width");

		JQuery frozenEl = jq("$frozenGrid .z-frozen");
		assertTrue(frozenEl.exists(),
				"Frozen component should exist in table mode");
		assertTrue(frozenEl.isVisible(),
				"Frozen scrollbar should be visible in table mode");

		// Narrow mode — frozen should be hidden, no errors
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(frozenGrid.hasClass("z-grid--stacking"),
				"frozenGrid should enter stacking at 600px");
		assertFalse(jq("$frozenGrid .z-frozen").isVisible(),
				"Frozen scrollbar should be hidden in stacking mode");

		// No JS errors on the transition — this is the key guarantee
		assertNoJSError();

		// Stacking zone should still be populated
		JQuery rows = jq("$frozenGrid").find(".z-grid-body tbody > tr.z-row");
		assertEquals(4, rows.length(),
				"Stacking zone should contain 4 rows despite frozen");

		// Comparison grid (no frozen) should also be stacking
		assertTrue(jq("$noFrozenGrid").hasClass("z-grid--stacking"),
				"noFrozenGrid (same data, no frozen) should also be in stacking");

		// Restore wide — frozen should reappear
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(frozenGrid.hasClass("z-grid--stacking"),
				"frozenGrid should return to table mode");
		assertTrue(jq("$frozenGrid .z-frozen").isVisible(),
				"Frozen should be visible again after restoring wide width");
	}

	/**
	 * Fixed-height grid in stacking mode must scroll internally.
	 * Regression test for the sizing issue where tall content overflowed grid height.
	 */
	@Test
	public void testFixedHeightStackingScrolls() {
		connect("/test2/F104-ZK-5409-responsive-grid-sizing.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$gridFixedH");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Fixed-height grid should be in stacking mode");

		// Grid height should stay at ~200px (fixed height)
		int gridHeight = grid.height();
		assertTrue(gridHeight > 0 && gridHeight <= 250,
				"Grid height should be ~200px (got " + gridHeight + ")");

		// Stacking zone should exist
		JQuery zone = jq("$gridFixedH").find(".z-grid-body tbody");
		assertTrue(zone.exists(),
				"Stacking zone should exist inside fixed-height grid");

		// Grid root itself has overflow-y:auto in stacking mode (scroll within grid)
		String gridOverflowY = grid.css("overflow-y");
		assertTrue("auto".equals(gridOverflowY) || "scroll".equals(gridOverflowY),
				"Grid in stacking mode should have overflow-y auto/scroll (got '" + gridOverflowY + "')");
	}
}
