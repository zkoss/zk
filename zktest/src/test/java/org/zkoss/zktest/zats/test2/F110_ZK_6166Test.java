/* F110_ZK_6166Test.java

        Purpose:

        Description:

        History:
                Thu Sep 24 15:18:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6166Test extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/**
	 * How far the indicator pokes out of any clipping ancestor, in px, measured
	 * against the padding box, where overflow actually clips. getBoundingClientRect
	 * alone cannot answer this — a clipped box still reports its full rect — so the
	 * caller pairs it with a hit test.
	 */
	private double clippedBy(String badgeId) {
		return ((Number) js().executeScript(
				"var el = zk.Widget.$('$" + badgeId + "').$n()"
				+ "  .querySelector('.z-badge-indicator');"
				+ "var r = el.getBoundingClientRect(), cut = 0;"
				+ "for (var p = el.parentElement; p && p !== document.body; p = p.parentElement) {"
				+ "  var cs = getComputedStyle(p);"
				+ "  if (cs.overflowX === 'visible' && cs.overflowY === 'visible') continue;"
				+ "  var c = p.getBoundingClientRect();"
				+ "  cut = Math.max(cut,"
				+ "      c.top + parseFloat(cs.borderTopWidth) - r.top,"
				+ "      r.bottom - (c.bottom - parseFloat(cs.borderBottomWidth)),"
				+ "      c.left + parseFloat(cs.borderLeftWidth) - r.left,"
				+ "      r.right - (c.right - parseFloat(cs.borderRightWidth)));"
				+ "}"
				+ "return cut;")).doubleValue();
	}

	/** Paint-level check: is every edge of the indicator actually drawn? */
	private boolean edgesPainted(String badgeId) {
		return (Boolean) js().executeScript(
				"var el = zk.Widget.$('$" + badgeId + "').$n()"
				+ "  .querySelector('.z-badge-indicator');"
				+ "var r = el.getBoundingClientRect(), cx = r.left + r.width / 2, cy = r.top + r.height / 2;"
				+ "return [[cx, r.top + 2], [cx, r.bottom - 2], [r.left + 2, cy], [r.right - 2, cy]]"
				+ "  .every(function (pt) { return document.elementsFromPoint(pt[0], pt[1]).indexOf(el) >= 0; });");
	}

	private void assertWhole(String badgeId, String what) {
		double cut = clippedBy(badgeId);
		assertTrue(cut < 0.5, what + " must not be clipped; " + cut + "px is outside its container");
		assertTrue(edgesPainted(badgeId), what + "'s edges must all actually be painted");
	}

	private String positions(String... ids) {
		StringBuilder sb = new StringBuilder();
		for (String id : ids)
			sb.append(js().executeScript(
					"var r = zk.Widget.$('$" + id + "').$n().getBoundingClientRect();"
					+ "return r.left + ',' + r.top;")).append(' ');
		return sb.toString();
	}

	/** The ticket's own repro: a plain groupbox, no author CSS. */
	@Test
	public void testIndicatorSurvivesAGroupbox() {
		connect();
		waitResponse();

		assertWhole("bInbox", "a count badge in a groupbox");
	}

	@Test
	public void testWideIndicatorSurvivesAGroupbox() {
		connect();
		waitResponse();

		assertWhole("bAlerts", "an overflowed count badge in a groupbox");
	}

	/** Dot mode is 6px, so it reserves less — its own arithmetic. */
	@Test
	public void testDotSurvivesAGroupbox() {
		connect();
		waitResponse();

		assertWhole("bDot", "a dot badge in a groupbox");
	}

	/** Groupbox is only the reported case; any overflow: hidden box did it. */
	@Test
	public void testIndicatorSurvivesAPlainClippingDiv() {
		connect();
		waitResponse();

		assertWhole("bTasks", "a count badge in an overflow: hidden div");
	}

	/** Every placement, pill and dot, in a clipping box that hugs the badge on all four sides. */
	@Test
	public void testEveryPlacementSurvivesATightClip() {
		connect();
		waitResponse();

		for (String id : new String[] {"pTopRight", "pTopLeft", "pBottomRight", "pBottomLeft",
				"dTopRight", "dTopLeft", "dBottomRight", "dBottomLeft"})
			assertWhole(id, id);
	}

	@Test
	public void testIndicatorTogglingNeverMovesTheLayout() {
		connect();
		waitResponse();
		String rest = positions("zeroBtn", "zeroNext");
		assertFalse(jq("$bZero").find(".z-badge-indicator").exists());

		click(jq("$toFive"));
		waitResponse();
		assertEquals(rest, positions("zeroBtn", "zeroNext"), "showing the indicator must not move anything");
		assertWhole("bZero", "an indicator shown at runtime inside an hlayout");

		click(jq("$toZero"));
		waitResponse();
		assertEquals(rest, positions("zeroBtn", "zeroNext"), "hiding it must not move anything either");
	}
}
