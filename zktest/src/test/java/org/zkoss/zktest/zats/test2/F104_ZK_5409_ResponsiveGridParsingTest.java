/* F104_ZK_5409_ResponsiveGridParsingTest.java

        Purpose:

        Description:

        History:
                Mon May 04 14:07:06 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Targets the parsing-tolerance table in spec §2.1.1 and the cascade rules
 * (cascade-up + cascade-down-to-xs) at boundary widths.
 *
 * <p>Strategy: load a ZUL with a fixed-width parent {@code <div id="container">}
 * wrapping the grid. Each test sets the container width via JS (avoids
 * scrollbar imprecision) and changes the grid's {@code responsiveColumns}
 * via the client setter, then reads {@code _responsiveMode} /
 * {@code _responsiveCols} from the widget instance.
 */
public class F104_ZK_5409_ResponsiveGridParsingTest extends WebDriverTestCase {

	private static final String ZUL = "/test2/F104-ZK-5409-responsive-grid-parsing.zul";

	@BeforeEach
	public void setUp() {
		connect(ZUL);
		waitResponse();
	}

	// ── helpers ───────────────────────────────────────────────

	/** Set the parent div's pixel width and force a layout flush so the
	 *  ResizeObserver fires synchronously. The ZUL {@code id="container"}
	 *  is mapped to a uuid at render-time, so we resolve via the widget. */
	private void setContainerWidth(int px) {
		js("var n = zk.Widget.$('$container').$n();"
				+ "n.style.width = '" + px + "px';"
				+ "void n.offsetWidth;"); // force layout
		// Tiny pause to let ResizeObserver microtask settle
		try { Thread.sleep(80); } catch (InterruptedException e) { /* ignore */ }
	}

	/** Set responsiveColumns via the client setter. Triggers immediate
	 *  re-evaluation (no AU round-trip). */
	private void setTokens(String tokens) {
		js("zk.Widget.$('$testGrid').setResponsiveColumns(" + jsString(tokens) + ");");
		// _evalResponsiveMode runs synchronously inside the setter
	}

	private long cols() {
		Object o = jsReturn("return zk.Widget.$('$testGrid')._responsiveCols || 0;");
		return ((Number) o).longValue();
	}

	private String mode() {
		Object o = jsReturn("return zk.Widget.$('$testGrid')._responsiveMode || 'undefined';");
		return (String) o;
	}

	private void js(String script) {
		((JavascriptExecutor) driver).executeScript(script);
	}

	private Object jsReturn(String script) {
		return ((JavascriptExecutor) driver).executeScript(script);
	}

	private static String jsString(String s) {
		return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
	}

	// ── G1: parsing tolerance ─────────────────────────────────

	/** Empty string → fallback to default token list "sm-1 md-none".
	 *  At sm width (≥576, &lt;768) → 1 col stacking. */
	@Test
	public void testEmptyTokens() {
		setContainerWidth(600);
		setTokens("");
		assertEquals("stacking", mode(), "Empty token string should fall back to default 'sm-1 md-none'");
		assertEquals(1L, cols(), "Default at sm width = 1 col");
	}

	/** Pure whitespace → same as empty. */
	@Test
	public void testWhitespaceTokens() {
		setContainerWidth(600);
		setTokens("   \t  ");
		assertEquals("stacking", mode());
		assertEquals(1L, cols());
	}

	/** All tokens invalid → fallback to default. */
	@Test
	public void testAllInvalidTokens() {
		setContainerWidth(600);
		setTokens("foo-1 bar-2 xxxl-3");
		assertEquals("stacking", mode(), "All-invalid input falls back to default");
		assertEquals(1L, cols());
	}

	/** Partially invalid → invalid tokens dropped, valid ones honoured. */
	@Test
	public void testPartialInvalidTokens() {
		setContainerWidth(600);
		setTokens("sm-2 foo-9 md-3");
		// At sm width (600), largest defined ≤ sm = sm-2 → 2 cols
		assertEquals("stacking", mode());
		assertEquals(2L, cols(), "Invalid 'foo-9' dropped; sm-2 wins at sm width");
	}

	/** Duplicate breakpoint → first occurrence wins (left-to-right). */
	@Test
	public void testDuplicateBreakpoint() {
		setContainerWidth(600);
		setTokens("sm-1 sm-2 sm-3");
		assertEquals(1L, cols(), "First 'sm-1' wins over later sm-2/sm-3");
	}

	/** Token matching is case-insensitive. */
	@Test
	public void testCaseInsensitive() {
		setContainerWidth(600);
		setTokens("SM-2 MD-NONE");
		assertEquals("stacking", mode());
		assertEquals(2L, cols(), "Uppercase tokens parsed equivalent to lowercase");
	}

	/** Multiple whitespace separators (tabs, multiple spaces). */
	@Test
	public void testMultiSpaceSplit() {
		setContainerWidth(600);
		setTokens("sm-2  \t  md-3");
		assertEquals(2L, cols(), "Tabs and double spaces should split correctly");
	}

	/** Zero value rejected → token dropped. */
	@Test
	public void testZeroValue() {
		setContainerWidth(600);
		// "sm-0" rejected (n < 1); only "md-3" survives
		setTokens("sm-0 md-3");
		// At sm width: no token ≤ sm (sm-0 dropped). Cascade-down:
		// smallest token (md-3) is numeric → inherit value 3.
		assertEquals(3L, cols(), "sm-0 dropped; cascade-down inherits md-3 → 3 cols");
	}

	/** Negative value rejected. */
	@Test
	public void testNegativeValue() {
		setContainerWidth(600);
		// Regex requires \d+ so "-3" doesn't match — token dropped
		setTokens("sm--3 md-2");
		assertEquals(2L, cols(), "Negative token dropped; cascade-down inherits md-2 → 2 cols");
	}

	/** Float value rejected. */
	@Test
	public void testFloatValue() {
		setContainerWidth(600);
		setTokens("sm-2.5 md-4");
		// "sm-2.5" doesn't match \d+ regex anchor — dropped
		assertEquals(4L, cols(), "Float dropped; cascade-down inherits md-4 → 4 cols");
	}

	/** Non-numeric value rejected (except "none"). */
	@Test
	public void testNonNumericValue() {
		setContainerWidth(600);
		setTokens("sm-abc md-2");
		assertEquals(2L, cols(), "Non-numeric dropped; cascade-down inherits md-2 → 2 cols");
	}

	// ── G2: cascade-up across multiple bp ─────────────────────

	/** Cascade-up: at md width with tokens "sm-1 lg-3", md must inherit
	 *  from sm (largest defined ≤ md), NOT from lg. */
	@Test
	public void testCascadeUpGapJump() {
		// Force container into md range (768~991)
		setContainerWidth(800);
		setTokens("sm-1 lg-3");
		assertEquals("stacking", mode());
		assertEquals(1L, cols(), "At md, cascade-up picks sm-1 (NOT lg-3)");

		// Now at lg width
		setContainerWidth(1000);
		assertEquals("stacking", mode());
		assertEquals(3L, cols(), "At lg, cascade-up picks lg-3");
	}

	// ── G3: zigzag (`{bp}-none` between two stacking bps) ─────

	/** Zigzag: "sm-1 md-none lg-3" — stacking → table → stacking.
	 *  Spec allows but warns. Implementation must honour the literal token
	 *  cascade. */
	@Test
	public void testZigzagBpNone() {
		setTokens("sm-1 md-none lg-3");

		setContainerWidth(600); // sm
		assertEquals("stacking", mode(), "sm: 1 col stacking");
		assertEquals(1L, cols());

		setContainerWidth(800); // md
		assertEquals("table", mode(), "md: table (md-none cascade-up)");

		setContainerWidth(1000); // lg
		assertEquals("stacking", mode(), "lg: 3 col stacking (zigzag back)");
		assertEquals(3L, cols());

		setContainerWidth(1300); // xl — cascade-up from lg-3
		assertEquals("stacking", mode());
		assertEquals(3L, cols());
	}

	// ── G6: cascade-down to xs / smaller-than-smallest ────────

	/** Cascade-down: only "md-2" defined; at xs width (&lt;576) the smallest
	 *  defined numeric token's value is inherited per spec
	 *  ("widths below the smallest defined token inherit it"). */
	@Test
	public void testCascadeDownToXs() {
		setContainerWidth(400); // xs
		setTokens("md-2");
		assertEquals("stacking", mode(), "xs cascades down from md-2 → stacking");
		assertEquals(2L, cols(), "Cascade-down inherits md-2 → 2 cols");
	}

	/** Cascade-down with sm only: "sm-3" at xs inherits to 3 cols. */
	@Test
	public void testCascadeDownSmOnly() {
		setContainerWidth(400); // xs
		setTokens("sm-3");
		assertEquals(3L, cols(), "Cascade-down at xs inherits sm-3 → 3 cols");
	}

	// ── G7: min-width inclusive boundary ──────────────────────

	/** Boundary: at exactly 768 px container width, currentBp must be 'md'
	 *  (min-width inclusive). With token "sm-1 md-none", 768 → table.
	 *
	 *  <p>Grid.offsetWidth is what _evalResponsiveMode reads. We need to set
	 *  the parent so the grid lays out at exactly the test width. Since the
	 *  grid has 1px border (or similar chrome) we read back the actual width
	 *  and use it for the assertion semantics. */
	@Test
	public void testInclusiveBoundaryAtMd() {
		setTokens("sm-1 md-none");

		// Find the offset between requested parent width and grid.offsetWidth.
		setContainerWidth(800);
		long offset = 800 - gridOffsetWidth();

		setContainerWidth(767 + (int) offset);
		assertEquals("stacking", mode(), "Grid width 767 → sm range → stacking");

		setContainerWidth(768 + (int) offset);
		assertEquals("table", mode(), "Grid width 768 (inclusive) → md → table");

		setContainerWidth(769 + (int) offset);
		assertEquals("table", mode(), "Grid width 769 → md → table");
	}

	/** Boundary at lg = 992. */
	@Test
	public void testInclusiveBoundaryAtLg() {
		setTokens("sm-1 lg-none");

		setContainerWidth(800);
		long offset = 800 - gridOffsetWidth();

		setContainerWidth(991 + (int) offset);
		assertEquals("stacking", mode(), "Grid width 991 → md → stacking (cascade-up sm-1)");

		setContainerWidth(992 + (int) offset);
		assertEquals("table", mode(), "Grid width 992 (inclusive) → lg → table");
	}

	private long gridOffsetWidth() {
		Object o = jsReturn("return zk.Widget.$('$testGrid').$n().offsetWidth;");
		return ((Number) o).longValue();
	}
}
