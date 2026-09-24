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

	private static final String[] WIDE_TIGHT = {"wTopRight", "wTopLeft", "wBottomRight", "wBottomLeft",
			"nTopRight", "nTopLeft", "nBottomRight", "nBottomLeft"};

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

	/** Paint-level check: is every edge of the indicator drawn, on top of the child it overlaps? */
	private boolean edgesPainted(String badgeId) {
		return (Boolean) js().executeScript(
				"var el = zk.Widget.$('$" + badgeId + "').$n()"
				+ "  .querySelector('.z-badge-indicator');"
				+ "el.scrollIntoView({block: 'center'});"
				+ "var r = el.getBoundingClientRect(), cx = r.left + r.width / 2, cy = r.top + r.height / 2;"
				+ "return [[cx, r.top + 2], [cx, r.bottom - 2], [r.left + 2, cy], [r.right - 2, cy]]"
				+ "  .every(function (pt) { return document.elementFromPoint(pt[0], pt[1]) === el; });");
	}

	private void assertWhole(String badgeId, String what) {
		double cut = clippedBy(badgeId);
		assertTrue(cut < 0.5, what + " must not be clipped; " + cut + "px is outside its container");
		assertTrue(edgesPainted(badgeId), what + "'s edges must all actually be painted");
	}

	/** How far the indicator's centre sits from the wrapped child's placement corner, in px. */
	private double cornerMiss(String badgeId) {
		return ((Number) js().executeScript(
				"var w = zk.Widget.$('$" + badgeId + "'), p = w.getPlacement(),"
				+ "  i = w.$n().querySelector('.z-badge-indicator').getBoundingClientRect(),"
				+ "  c = w.firstChild.$n().getBoundingClientRect();"
				+ "return Math.max("
				+ "  Math.abs(i.left + i.width / 2 - (p.indexOf('left') >= 0 ? c.left : c.right)),"
				+ "  Math.abs(i.top + i.height / 2 - (p.indexOf('top') === 0 ? c.top : c.bottom)));")).doubleValue();
	}

	private double buttonTop(String badgeId) {
		return ((Number) js().executeScript(
				"return zk.Widget.$('$" + badgeId + "').firstChild.$n().getBoundingClientRect().top;")).doubleValue();
	}

	private String positions(String... ids) {
		StringBuilder sb = new StringBuilder();
		for (String id : ids)
			sb.append(js().executeScript(
					"var r = zk.Widget.$('$" + id + "').$n().getBoundingClientRect();"
					+ "return (r.left + scrollX) + ',' + (r.top + scrollY);")).append(' ');
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

	/** A pill wider than 20px makes the badge reserve half its real width, on every side it overhangs. */
	@Test
	public void testWideIndicatorSurvivesATightClip() {
		connect();
		waitResponse();

		for (String id : WIDE_TIGHT)
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

	@Test
	public void testWideIndicatorIsCentredOnTheCorner() {
		connect();
		waitResponse();

		assertEquals(0, cornerMiss("bAlerts"), 0.5, "99+ must be centred on the top-right corner");
		assertEquals(0, cornerMiss("bNew"), 0.5, "NEW must be centred on the top-left corner");
		for (String id : WIDE_TIGHT)
			assertEquals(0, cornerMiss(id), 0.5, id + " must be centred on its corner");
	}

	/** A child sized to its badge still fills it, beside the half-width kept for the indicator. */
	@Test
	public void testFullWidthChildFillsTheBadge() {
		connect();
		waitResponse();

		double badge = ((Number) js().executeScript(
				"return zk.Widget.$('$bFull').$n().getBoundingClientRect().width;")).doubleValue();
		double button = ((Number) js().executeScript(
				"return zk.Widget.$('$bFull').firstChild.$n().getBoundingClientRect().width;")).doubleValue();
		assertEquals(badge - 10, button, 0.5, "a width=100% child must fill its badge");
		assertEquals(0, cornerMiss("bFull"), 0.5, "the indicator must stay on the full-width child's corner");
	}

	/** A child sized to its badge's height still fills it, below the half-height kept for the indicator. */
	@Test
	public void testFullHeightChildFillsTheBadge() {
		connect();
		waitResponse();

		double badge = ((Number) js().executeScript(
				"return zk.Widget.$('$bTall').$n().getBoundingClientRect().height;")).doubleValue();
		double button = ((Number) js().executeScript(
				"return zk.Widget.$('$bTall').firstChild.$n().getBoundingClientRect().height;")).doubleValue();
		assertEquals(badge - 10, button, 0.5, "a height=100% child must fill its badge");
		assertEquals(0, cornerMiss("bTall"), 0.5, "the indicator must stay on the full-height child's corner");
	}

	/** The wrapped child, never the indicator, gives the badge its baseline, whatever the placement. */
	@Test
	public void testBaselineFollowsTheWrappedChild() {
		connect();
		waitResponse();

		double top = buttonTop("blTopRight");
		for (String id : new String[] {"blTopLeft", "blBottomRight", "blBottomLeft"})
			assertEquals(top, buttonTop(id), 0.5, id + "'s button must sit on the same baseline as blTopRight's");
	}
}
