/* B110_ZK_6134Test.java

        Purpose:

        Description:

        History:
                Wed Jul 22 15:36:50 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6134Test extends WebDriverTestCase {

	private static final String POPUP = ".z-daterangebox-popup";

	/** A panel's month anchor: the popup sets each Calendar to the 1st to choose
	 *  the displayed month, and CE marks that day z-calendar-selected. */
	private static final String ANCHOR = POPUP
			+ " .z-calendar-cell.z-calendar-selected:not([class*=z-cell-range-])";

	private long count(String selector) {
		return Long.parseLong(getEval(
				"document.querySelectorAll(\"" + selector + "\").length"));
	}

	private String backgroundOf(String selector) {
		return getEval("(function () {"
				+ "var e = document.querySelector(\"" + selector + "\");"
				+ "return e ? getComputedStyle(e).backgroundColor : 'no-such-cell';"
				+ "})()");
	}

	private boolean za11yActive() {
		return "true".equals(getEval("!!window.za11y"));
	}

	private void openPopup(int boxIndex) {
		click(jq(".z-daterangebox-button:eq(" + boxIndex + ")"));
		waitResponse();
		assertTrue(jq(POPUP + "-panels .z-calendar").exists());
	}

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/** Day-view cells keep their day in jQuery's data cache, not a DOM attribute. */
	private void onCellInPanel(int panelIndex, int day, String action) {
		js().executeScript(
				"var pane = document.querySelectorAll('" + POPUP + "-panels .z-calendar')[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++) {"
				+ "  if (jq(cells[i]).data('value') === arguments[1] && (cells[i]._monofs || 0) === 0) {"
				+ "    if (arguments[2] === 'click')"
				+ "      zk.Widget.$(pane)._clickDate({target: cells[i], domTarget: cells[i], stop: function () {}});"
				+ "    else"
				+ "      cells[i].dispatchEvent(new MouseEvent('mouseover', {bubbles: true}));"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day, action);
		waitResponse();
	}

	/** Real pointer move: a synthetic mouseover produces no CSS `:hover` state. */
	private String backgroundOnHover(String selector) {
		WebElement el = driver.findElement(By.cssSelector(selector));
		getActions().moveToElement(el).perform();
		return backgroundOf(selector);
	}

	/** Empty box: nothing in the popup may be announced as selected. */
	@Test
	public void testEmptyBoxAnnouncesNoSelection() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();
		if (!za11yActive()) return; // NO_A11Y variant emits no aria state

		openPopup(0);
		assertEquals(0L, count(POPUP + " [aria-selected]"));
		assertEquals(0L, count(POPUP + " [aria-current]"));
	}

	/** Seeded box (2025-01-10 .. 2025-01-15): the announcement must cover every
	 *  painted day and not the month anchor. */
	@Test
	public void testSeededBoxAnnouncesRangeOnly() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();
		if (!za11yActive()) return; // NO_A11Y variant emits no aria state

		openPopup(1);
		long ranged = count(POPUP + " [class*=z-cell-range-]");
		assertTrue(ranged > 0, "the seeded range must paint some cells");
		assertEquals(ranged,
				count(POPUP + " [class*=z-cell-range-][aria-selected=true]"),
				"every painted range cell must be announced as selected");
		assertEquals(ranged, count(POPUP + " [aria-selected]"),
				"only range cells may be announced as selected");
		assertEquals(0L, count(ANCHOR + "[aria-selected]"),
				"the month anchor must not be announced as selected");
		assertEquals(0L, count(ANCHOR + "[aria-current]"),
				"the month anchor must not be announced as the current date");
	}

	/** A hover preview band is a candidate, not a choice: it stays painted but
	 *  must not be announced as selected. */
	@Test
	public void testHoverPreviewIsNotAnnouncedAsSelected() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();
		if (!za11yActive()) return; // NO_A11Y variant emits no aria state

		openPopup(0);
		onCellInPanel(0, 5, "click");    // begin committed, end still unset
		onCellInPanel(1, 10, "hover");   // band runs from panel 0 into panel 1

		String preview = POPUP + " [class*=z-cell-range-preview-]";
		assertTrue(count(preview) > 0,
				"precondition: the hover must paint a preview band");
		assertEquals(0L, count(preview + "[aria-selected]"),
				"a hover preview is not a selection and must not be announced as one");
		assertEquals(1L, count(POPUP + " .z-cell-range-begin[aria-selected=true]"),
				"the committed begin cell must still be announced");
		assertEquals(1L, count(POPUP + " [aria-selected]"),
				"while previewing, only the committed begin cell may be announced");
	}

	/** GUARD, not evidence: the popup LESS already neutralises the anchor's paint,
	 *  so this passes either way. */
	@Test
	public void testAnchorCellPaintsLikePlainCell() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();

		openPopup(1);
		String plain = backgroundOf(POPUP
				+ " .z-calendar-cell:not(.z-calendar-selected):not(.z-calendar-outside)"
				+ ":not([class*=z-cell-range-])");
		assertEquals(plain, backgroundOf(ANCHOR),
				"the 1st of the month must not paint as selected");
		assertNotEquals(plain, backgroundOf(POPUP + " .z-cell-range-begin"),
				"the range begin must still paint as highlighted");
	}

	/** The anchor inside a preview band must paint like the band: the suppression
	 *  rule outranks the band's own background. */
	@Test
	public void testAnchorInsidePreviewBandPaintsLikeTheBand() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();

		openPopup(0);
		onCellInPanel(0, 5, "click");    // begin only, so a hover previews the end
		onCellInPanel(1, 10, "hover");   // band reaches across the next panel's 1st
		String anchorInBand = POPUP
				+ " .z-calendar-cell.z-calendar-selected.z-cell-range-preview-mid";
		assertEquals(1L, count(anchorInBand),
				"precondition: the preview band must cover the next panel's 1st");
		assertEquals(backgroundOf(POPUP + " .z-cell-range-preview-mid:not(.z-calendar-selected)"),
				backgroundOf(anchorInBand),
				"the anchor must paint like the rest of the preview band");
	}

	/** The suppression rule outranks `:hover` too, so the anchor must still react. */
	@Test
	public void testAnchorCellRespondsToHover() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();

		openPopup(0);
		String hoveredAnchor = backgroundOnHover(ANCHOR);
		String hoveredPlain = backgroundOnHover(POPUP
				+ " .z-calendar-cell:not(.z-calendar-selected):not(.z-calendar-outside)");
		assertNotEquals("rgba(0, 0, 0, 0)", hoveredPlain,
				"control: a plain day cell must paint a hover background");
		assertEquals(hoveredPlain, hoveredAnchor,
				"the month anchor must hover like a plain day cell");
	}
}
