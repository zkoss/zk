/* F110_ZK_5409_EffectiveSmartUpdateTest.java

	Purpose:

	Description:

	History:
		Sun Aug 30 11:27:17 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Grid's responsive setters must smartUpdate the effective value, not the raw
 * field: with a page-scope fallback, clearing the component's own value has to
 * push what the fallback resolves to — the value {@code invalidate()} renders.
 * The two {@code WithoutFallback} cases below are not proofs of that fix
 * (effective == raw when nothing resolves); they are client-side guards that an
 * explicit null is honoured rather than parsed as a token string.
 */
public class F110_ZK_5409_EffectiveSmartUpdateTest extends WebDriverTestCase {

	private static final String WGT = "zk.Widget.$('$testGrid')";

	@Test
	public void testClearedResponsiveKeepsPageScopeStacking() {
		connect("/test2/F110-ZK-5409-effective-smartupdate.zul");
		waitResponse();

		assertTrue(jq("$testGrid").hasClass("z-grid--stacking"),
				"Initial render should send the effective responsive (page-scope stacking)");

		click(jq("$btnSetNone"));
		waitResponse();
		assertFalse(jq("$testGrid").hasClass("z-grid--stacking"),
				"responsive=\"none\" should leave stacking");

		click(jq("$btnClearResp"));
		waitResponse();
		String afterUpdate = getEval(WGT + ".getResponsive()");
		boolean stackedAfterUpdate = jq("$testGrid").hasClass("z-grid--stacking");

		click(jq("$btnInvalidate"));
		waitResponse();
		String afterRender = getEval(WGT + ".getResponsive()");

		assertEquals("stacking", afterRender,
				"A full re-render sends the effective responsive");
		assertTrue(stackedAfterUpdate,
				"Clearing responsive must not tear down the stacking the server still resolves to");
		assertEquals(afterRender, afterUpdate,
				"Clearing responsive must smartUpdate the same effective value the render sends");
	}

	@Test
	public void testClearedResponsiveColumnsKeepsPageScopeCascade() {
		connect("/test2/F110-ZK-5409-effective-smartupdate.zul");
		waitResponse();

		assertEquals("2", getEval(WGT + "._responsiveCols"),
				"Initial render should send the effective cascade (page-scope sm-2 md-none)");

		click(jq("$btnSetCols"));
		waitResponse();
		assertEquals("1", getEval(WGT + "._responsiveCols"),
				"Own cascade sm-1 md-none should resolve to 1 card per row");

		click(jq("$btnClearCols"));
		waitResponse();
		String afterUpdate = getEval(WGT + ".getResponsiveColumns()");
		String colsAfterUpdate = getEval(WGT + "._responsiveCols");

		click(jq("$btnInvalidate"));
		waitResponse();
		String afterRender = getEval(WGT + ".getResponsiveColumns()");

		assertEquals("sm-2 md-none", afterRender,
				"A full re-render sends the effective responsiveColumns");
		assertEquals("2", colsAfterUpdate,
				"Clearing responsiveColumns must not drop the client onto its own default cascade");
		assertEquals(afterRender, afterUpdate,
				"Clearing responsiveColumns must smartUpdate the same effective value the render sends");
	}

	/** Client guard, not a fix proof — passes on the pre-fix code. Pins that an
	 * explicit null smartUpdate behaves like an omitted property. */
	@Test
	public void testClearedResponsiveWithoutFallbackMatchesRender() {
		connect("/test2/F110-ZK-5409-effective-smartupdate-nofallback.zul");
		waitResponse();

		assertFalse(jq("$testGrid").hasClass("z-grid--stacking"),
				"Nothing resolves, so the render omits responsive");

		click(jq("$btnSetStacking"));
		waitResponse();
		assertTrue(jq("$testGrid").hasClass("z-grid--stacking"),
				"Own responsive=\"stacking\" should stack at 500px");

		click(jq("$btnClearResp"));
		waitResponse();
		boolean stackedAfterUpdate = jq("$testGrid").hasClass("z-grid--stacking");
		String modeAfterUpdate = getEval(WGT + "._responsiveMode");

		click(jq("$btnInvalidate"));
		waitResponse();

		assertFalse(stackedAfterUpdate,
				"An explicit null smartUpdate must tear stacking down, like the render that omits the property");
		assertFalse(jq("$testGrid").hasClass("z-grid--stacking"),
				"A re-render with nothing to resolve must not stack");
		assertEquals(getEval(WGT + "._responsiveMode"), modeAfterUpdate,
				"Explicit null and an omitted property must leave the same responsive mode");
	}

	/** Client guard, not a fix proof — passes on the pre-fix code. Pins that an
	 * explicit null smartUpdate behaves like an omitted property. */
	@Test
	public void testClearedResponsiveColumnsWithoutFallbackMatchesRender() {
		connect("/test2/F110-ZK-5409-effective-smartupdate-nofallback.zul");
		waitResponse();

		click(jq("$btnSetStacking"));
		waitResponse();
		assertEquals("1", getEval(WGT + "._responsiveCols"),
				"No responsiveColumns anywhere — the client's own sm-1 md-none cascade applies");

		click(jq("$btnSetCols"));
		waitResponse();
		assertEquals("2", getEval(WGT + "._responsiveCols"),
				"Own cascade sm-2 md-none should resolve to 2 cards per row");

		click(jq("$btnClearCols"));
		waitResponse();
		String colsAfterUpdate = getEval(WGT + "._responsiveCols");

		click(jq("$btnInvalidate"));
		waitResponse();

		assertEquals("1", colsAfterUpdate,
				"An explicit null must fall back to the client cascade, not be parsed as a token string");
		assertEquals(getEval(WGT + "._responsiveCols"), colsAfterUpdate,
				"Explicit null and an omitted property must resolve to the same cascade");
	}
}
