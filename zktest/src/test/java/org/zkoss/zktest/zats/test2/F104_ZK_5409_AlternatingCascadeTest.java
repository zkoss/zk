/* F104_ZK_5409_AlternatingCascadeTest.java

	Purpose:

	Description:

	History:
		Thu May 21 11:23:50 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Exercises a non-monotonic responsiveColumns cascade
 * {@code "sm-2 md-none lg-3 xl-none"} — alternating stacking/table
 * tiers force the cascade-up resolver to traverse mixed numeric and
 * {@code none} tokens rather than a simple monotonic sequence.
 *
 * <p>Expected resolution by tier (cascade-up; cascade-down for xs):
 * <pre>
 *   xs (&lt;576)       sm-2 (cascade-down)  -&gt; stacking, 2 cols
 *   sm (576..767)    sm-2                 -&gt; stacking, 2 cols
 *   md (768..991)    md-none              -&gt; table
 *   lg (992..1199)   lg-3                 -&gt; stacking, 3 cols
 *   xl (1200..1399)  xl-none              -&gt; table
 *   xxl (&gt;=1400)     xl-none (cascade-up) -&gt; table
 * </pre>
 *
 * <p>Container width tracks window width minus the browser chrome
 * (~15 px), so the test resizes the window to land each tier
 * comfortably inside its window.
 */
public class F104_ZK_5409_AlternatingCascadeTest extends WebDriverTestCase {

	private static final String PATH = "/test2/F104-ZK-5409-responsive-grid-alternating.zul";
	private static final String GRID = "gridAlt";

	private void resize(int width) {
		Dimension cur = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(width, cur.height));
		waitResponse();
	}

	private void assertStacking(boolean expectStacking, int width) {
		boolean actual = jq("$" + GRID).hasClass("z-grid--stacking");
		assertEquals(expectStacking, actual,
				"At window width=" + width + "px, " + GRID + " expected stacking="
						+ expectStacking + " but was " + actual);
	}

	/** Reads the {@code --zk-resp-cols} CSS variable set by
	 *  {@code _updateStackingColumns} on the grid root. */
	private void assertCols(int expected, int width) {
		Object v = ((JavascriptExecutor) driver).executeScript(
				"var n = zk.Widget.$('$" + GRID + "').$n();"
				+ "return n ? getComputedStyle(n).getPropertyValue('--zk-resp-cols') : null;");
		assertEquals(String.valueOf(expected), ((String) v).trim(),
				"At window width=" + width + "px, --zk-resp-cols expected=" + expected);
	}

	/** xs tier (~365 px container at 380 window): cascades down to {@code sm-2}. */
	@Test
	public void testXsStacks2Cols() {
		connect(PATH);
		waitResponse();
		resize(380);
		assertStacking(true, 380);
		assertCols(2, 380);
	}

	/** sm tier: {@code sm-2} wins directly. */
	@Test
	public void testSmStacks2Cols() {
		connect(PATH);
		waitResponse();
		resize(700);
		assertStacking(true, 700);
		assertCols(2, 700);
	}

	/** md tier: {@code md-none} explicitly disables stacking — a gap in
	 *  the cascade between two stacking tiers. */
	@Test
	public void testMdTable() {
		connect(PATH);
		waitResponse();
		resize(900);
		assertStacking(false, 900);
	}

	/** lg tier: {@code lg-3} re-enables stacking with a different cols count. */
	@Test
	public void testLgStacks3Cols() {
		connect(PATH);
		waitResponse();
		resize(1100);
		assertStacking(true, 1100);
		assertCols(3, 1100);
	}

	/** xl tier: {@code xl-none} explicitly disables again. */
	@Test
	public void testXlTable() {
		connect(PATH);
		waitResponse();
		resize(1300);
		assertStacking(false, 1300);
	}

	/** xxl tier: no xxl token → cascade-up picks {@code xl-none}. */
	@Test
	public void testXxlTable() {
		connect(PATH);
		waitResponse();
		resize(1500);
		assertStacking(false, 1500);
	}

	/** Sweep every tier in a single test to verify mode flips and
	 *  cols-count transitions are clean in both directions — the
	 *  state-machine integrity test for the alternating cascade. */
	@Test
	public void testFullSweep() {
		connect(PATH);
		waitResponse();

		resize(1500); assertStacking(false, 1500);                       // xxl
		resize(1300); assertStacking(false, 1300);                       // xl
		resize(1100); assertStacking(true, 1100);  assertCols(3, 1100);  // lg
		resize(900);  assertStacking(false, 900);                        // md
		resize(700);  assertStacking(true, 700);   assertCols(2, 700);   // sm
		resize(380);  assertStacking(true, 380);   assertCols(2, 380);   // xs

		// Grow back through the same tiers.
		resize(1100); assertStacking(true, 1100);  assertCols(3, 1100);  // lg
		resize(1500); assertStacking(false, 1500);                       // xxl
	}
}
