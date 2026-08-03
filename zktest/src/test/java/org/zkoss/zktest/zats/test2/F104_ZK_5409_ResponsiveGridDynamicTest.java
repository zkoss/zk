/* F104_ZK_5409_ResponsiveGridDynamicTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:12 CST 2026, Created by peakerlee

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

public class F104_ZK_5409_ResponsiveGridDynamicTest extends WebDriverTestCase {

	/**
	 * Test 12.1: Dynamic add Row while in stacking mode.
	 * New row should automatically enter stacking format.
	 */
	@Test
	public void testDynamicAddRow() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode");

		// Initial: 3 rows
		assertEquals(3, jq("$grid1").find(".z-grid-body tbody > tr.z-row").length(),
				"Should start with 3 rows");

		// Add row
		click(jq("$btnAdd"));
		waitResponse();

		// Should now have 4 rows, all in stacking format
		assertEquals(4, jq("$grid1").find(".z-grid-body tbody > tr.z-row").length(),
				"Should have 4 rows after adding");
		assertEquals("rows: 4", jq("$rowCount").text(),
				"Row count label should update");

		// New row should also have data-label cells (last data row, not the empty-body placeholder)
		JQuery lastRow = jq("$grid1").find(".z-grid-body tbody > tr.z-row:last");
		assertTrue(lastRow.find("td[data-label]").length() > 0,
				"Newly added row should have data-label cells in stacking mode");
	}

	/**
	 * Test 12.2: Dynamic remove Row while in stacking mode.
	 * Layout should re-arrange properly.
	 */
	@Test
	public void testDynamicRemoveRow() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// Initial: 3 rows
		assertEquals(3, jq("$grid1").find(".z-grid-body tbody > tr.z-row").length(),
				"Should start with 3 rows");

		// Remove row
		click(jq("$btnRemove"));
		waitResponse();

		assertEquals(2, jq("$grid1").find(".z-grid-body tbody > tr.z-row").length(),
				"Should have 2 rows after removing");
		assertEquals("rows: 2", jq("$rowCount").text(),
				"Row count label should update");

		// Grid should still be in stacking mode
		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after removing row");
	}

	/**
	 * Test 12.5: Dynamic toggle responsive via Java API.
	 * Switching between "none" and "stacking" at runtime.
	 */
	@Test
	public void testDynamicToggleResponsive() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode initially");

		// Toggle to none
		click(jq("$btnToggle"));
		waitResponse();

		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should exit stacking after toggle to none");
		assertTrue(jq("$grid1 .z-columns").isVisible(),
				"Columns header should reappear after toggle to none");

		// Toggle back to stacking
		click(jq("$btnToggle"));
		waitResponse();

		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should return to stacking after toggle back");
	}

	/**
	 * Test 12.9: invalidate() should preserve stacking state.
	 */
	@Test
	public void testInvalidatePreservesStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should be in stacking mode before invalidate");

		// Trigger invalidate
		click(jq("$btnInvalidate"));
		waitResponse();

		// After invalidate and re-render, grid should still be in stacking mode
		// (ResizeObserver should re-evaluate after DOM rebuild)
		assertTrue(jq("$grid1").hasClass("z-grid--stacking"),
				"Grid should remain in stacking mode after invalidate()");

		// Cell labels should still be present
		assertTrue(jq("$grid1").find(".z-grid-body tbody > tr > td[data-label]").length() > 0,
				"Cell labels should be present after invalidate()");
	}

	/**
	 * Test 12.10: Parent container resize triggers responsive re-evaluation.
	 * Resize window from narrow to wide and back.
	 */
	@Test
	public void testParentContainerResize() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Start wide — table mode
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should be in table mode at wide width");

		// Narrow — stacking mode
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should enter stacking after narrowing");

		// Wide again — table mode
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should return to table after widening");

		// Narrow once more — should still work
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should enter stacking again after second narrow");
	}

	/**
	 * Test 1.17: onResponsiveModeChange event fires on mode switch.
	 */
	@Test
	public void testOnResponsiveModeChangeEvent() {
		connect("/test2/F104-ZK-5409-responsive-grid-dynamic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Start wide
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		// Narrow to trigger stacking
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// The onResponsiveModeChange event should have fired
		assertEquals("mode: stacking", jq("$modeStatus").text(),
				"onResponsiveModeChange should fire with mode=stacking");

		// Widen to trigger table
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertEquals("mode: table", jq("$modeStatus").text(),
				"onResponsiveModeChange should fire with mode=table");
	}

	/**
	 * When the effective mode stays {@code stacking} but the column count
	 * shifts across a breakpoint (cross-bp resize), the implementation only
	 * updates the {@code --zk-resp-cols} CSS variable on the grid root.
	 * CSS Grid reflows the existing card layout; no DOM rebuild happens
	 * because the same {@code <table>} renders both modes.
	 *
	 * <p>This test guards two contracts:
	 * <ol>
	 *   <li>Resolved {@code _responsiveCols} matches the new breakpoint tier.</li>
	 *   <li>The first row's DOM node is preserved across the resize (we tag it
	 *       beforehand and check the tag survives) — i.e. no rebuild path
	 *       silently sneaks in.</li>
	 *   <li>The {@code --zk-resp-cols} variable on the grid root reflects the
	 *       new cols count (read via {@code getComputedStyle}; the variable
	 *       inherits down through the cascade to the {@code <tbody>}).</li>
	 * </ol>
	 */
	@Test
	public void testColsOnlyChangeNoRebuild() {
		connect("/test2/F104-ZK-5409-responsive-grid-parsing.zul");
		waitResponse();

		// Switch to a multi-bp token so cols differs between sm and md (both stacking).
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"zk.Widget.$('$container').$n().style.width = '600px';"
				+ "void zk.Widget.$('$container').$n().offsetWidth;");
		try { Thread.sleep(80); } catch (InterruptedException e) { /* ignore */ }
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"zk.Widget.$('$testGrid').setResponsiveColumns('sm-1 md-2 lg-none');");

		// Tag the first data row so we can detect rebuild.
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"var first = jq('$testGrid .z-row')[0];"
				+ "if (first) first.setAttribute('data-rebuild-tag', 'before');");

		// Resize across sm→md boundary while keeping stacking active.
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"zk.Widget.$('$container').$n().style.width = '800px';"
				+ "void zk.Widget.$('$container').$n().offsetWidth;");
		try { Thread.sleep(120); } catch (InterruptedException e) { /* ignore */ }

		// Cols updated to 2 (md tier).
		Object cols = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"return zk.Widget.$('$testGrid')._responsiveCols;");
		assertEquals(2L, ((Number) cols).longValue(),
				"Crossing into md (sm-1 → md-2) should resolve to 2 cols");

		// Same row node still present with the tag = no rebuild.
		Object tag = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"var first = jq('$testGrid .z-row')[0];"
				+ "return first ? first.getAttribute('data-rebuild-tag') : null;");
		assertEquals("before", tag,
				"First row should retain its data-rebuild-tag (no DOM rebuild on cols-only change)");

		// CSS variable updated. _updateStackingColumns sets it on the grid root;
		// the variable inherits through the cascade, so getComputedStyle on tbody
		// (or any descendant) sees it. We read the root for clarity.
		Object cssCols = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
				"var n = zk.Widget.$('$testGrid').$n();"
				+ "return n ? getComputedStyle(n).getPropertyValue('--zk-resp-cols') : null;");
		assertEquals("2", ((String) cssCols).trim(),
				"--zk-resp-cols CSS variable should reflect new cols count");
	}
}
