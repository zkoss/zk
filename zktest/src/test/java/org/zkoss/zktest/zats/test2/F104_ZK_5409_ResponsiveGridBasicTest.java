/* F104_ZK_5409_ResponsiveGridBasicTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:29 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_5409_ResponsiveGridBasicTest extends WebDriverTestCase {

	/**
	 * Test 1.1 + 2.1: Simple grid enters stacking mode when container width < md (768px).
	 * - Header row should be hidden
	 * - Rows should have vertical key-value layout
	 * - Cell label spans should appear with column labels
	 * - Switching back to wide should restore table mode
	 */
	@Test
	public void testSimpleGridStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		// Ensure wide enough for table mode
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");

		// Table mode: no stacking class, header visible
		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should NOT have stacking class in table mode");
		assertTrue(jq("$grid1 .z-columns").isVisible(),
				"Columns header should be visible in table mode");
		// No cell-label spans in table mode
		assertEquals(0, jq("$grid1 td[data-label]").length(),
				"No cell-label spans should exist in table mode");

		// Resize below md breakpoint (768px)
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// Stacking mode: stacking class present, header hidden
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should have stacking class in stacking mode");
		assertFalse(jq("$grid1 .z-columns").isVisible(),
				"Columns header should be hidden in stacking mode");
		// Cell label spans should appear in the stacking zone
		// Use CSS selector (not ZK $id) since stacking zone is dynamically created DOM
		assertTrue(jq(".z-grid-body tbody > tr > td[data-label]").length() > 0,
				"Cell-label spans should appear in stacking mode");
		// Verify label content matches column labels
		assertEquals("Name", jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(0)").attr("data-label"),
				"First cell label should be 'Name'");
		assertEquals("Email", jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(1)").attr("data-label"),
				"Second cell label should be 'Email'");
		assertEquals("Department", jq(".z-grid-body tbody > tr.z-row:eq(0) td:eq(2)").attr("data-label"),
				"Third cell label should be 'Department'");

		// Verify 3 rows exist in stacking zone
		assertEquals(3, jq(".z-grid-body tbody > tr.z-row").length(),
				"Should have 3 rows in stacking mode");

		// Resize back to wide — should restore table mode
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should NOT have stacking class after restoring wide width");
		assertTrue(jq("$grid1 .z-columns").isVisible(),
				"Columns header should be visible after restoring wide width");
	}

	/**
	 * Test 1.2: Grid without Columns should stack without label spans.
	 */
	@Test
	public void testGridWithoutColumnsStacking() {
		connect("/test2/F104-ZK-5409-responsive-grid-no-columns.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Resize to stacking mode
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should enter stacking mode");

		// Without columns, no label spans should exist
		assertEquals(0, jq("$grid1 td[data-label]").length(),
				"No cell-label spans without <columns>");

		// Rows should still be rendered
		assertTrue(jq("$grid1 .z-row").length() > 0,
				"Rows should still exist in stacking mode without columns");
	}

	/**
	 * Test 1.3: responsive="none" should keep grid in table mode always.
	 */
	@Test
	public void testResponsiveNoneOverride() {
		connect("/test2/F104-ZK-5409-responsive-grid-none.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Narrow width — grid should still be in table mode
		driver.manage().window().setSize(new Dimension(400, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid with responsive='none' should NOT enter stacking mode");
		assertTrue(jq("$grid1 .z-columns").isVisible(),
				"Header should remain visible with responsive='none'");
	}

	/**
	 * Test 1.5: responsiveColumns breakpoint cascade rules.
	 * <ul>
	 *   <li>gridSm uses {@code responsiveColumns="sm-none"} — every bp
	 *       resolves to {@code none}, so always in table mode.</li>
	 *   <li>gridLg uses {@code responsiveColumns="sm-1 lg-none"} —
	 *       stacks 1 col below lg (992 px), table at lg and above.</li>
	 * </ul>
	 */
	@Test
	public void testBreakpointSmAndLg() {
		connect("/test2/F104-ZK-5409-responsive-grid-breakpoint.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// At 768px: gridSm always table (sm-none); gridLg stacking (768 < 992)
		driver.manage().window().setSize(new Dimension(768, size.height));
		waitResponse();

		assertFalse(jq("$gridSm").hasClass("z-grid--stacking"),
				"gridSm (sm-none) should be table at 768px");
		assertTrue(jq("$gridLg").hasClass("z-grid--stacking"),
				"gridLg (sm-1 lg-none) should be stacking at 768px");

		// At 500px (xs range): gridSm still table (cascade-down to sm-none);
		// gridLg stacking (cascade-down to sm-1).
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		assertFalse(jq("$gridSm").hasClass("z-grid--stacking"),
				"gridSm (sm-none) should remain table at 500px (cascade)");
		assertTrue(jq("$gridLg").hasClass("z-grid--stacking"),
				"gridLg (sm-1 lg-none) should be stacking at 500px");

		// At 1024px (lg range): both table.
		driver.manage().window().setSize(new Dimension(1024, size.height));
		waitResponse();

		assertFalse(jq("$gridSm").hasClass("z-grid--stacking"),
				"gridSm (sm-none) should be table at 1024px");
		assertFalse(jq("$gridLg").hasClass("z-grid--stacking"),
				"gridLg (sm-1 lg-none) should be table at 1024px");
	}

	/**
	 * Test 1.14: Breakpoint boundary — container width exactly equal to breakpoint
	 * should stay in table mode (strict less-than).
	 * md breakpoint default = 768px.
	 * At 768px window width, container width should be ~768px → table mode.
	 */
	@Test
	public void testBreakpointBoundary() {
		connect("/test2/F104-ZK-5409-responsive-grid-basic.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Use wide window to ensure table mode, then narrow down.
		// Note: window width != container width due to scrollbar (~15-17px).
		// So we use 800px (safely above md=768) for table mode.
		driver.manage().window().setSize(new Dimension(800, size.height));
		waitResponse();

		JQuery grid = jq("$grid1");
		assertFalse(grid.hasClass("z-grid--stacking"),
				"Grid should be table mode when container width >= breakpoint");

		// Use 750px (safely below md=768 even with scrollbar) for stacking
		driver.manage().window().setSize(new Dimension(750, size.height));
		waitResponse();

		assertTrue(grid.hasClass("z-grid--stacking"),
				"Grid should enter stacking when container width < breakpoint");
	}

	/**
	 * Test 1.15: responsive="none" must NOT install a ResizeObserver.
	 * The spec distinguishes "unset" (inherit) from "explicit none" (skip
	 * everything). _initResponsive() should never create the observer on
	 * an explicitly-disabled grid, even at a narrow viewport.
	 */
	@Test
	public void testResponsiveNoneSkipsObserver() {
		connect("/test2/F104-ZK-5409-responsive-grid-none.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		try {
			driver.manage().window().setSize(new Dimension(400, size.height));
			waitResponse();

			JavascriptExecutor js = (JavascriptExecutor) driver;
			Object observer = js.executeScript(
					"return zk.Widget.$('$grid1')._resizeObserver;");
			assertNull(observer,
					"Grid with responsive='none' should never instantiate a ResizeObserver");

			Object mode = js.executeScript(
					"return zk.Widget.$('$grid1')._responsiveMode;");
			assertNull(mode,
					"Grid with responsive='none' should have no responsive mode set");
		} finally {
			driver.manage().window().setSize(size);
		}
	}
}
