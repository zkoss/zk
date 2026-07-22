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
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6134Test extends WebDriverTestCase {

	private static final String POPUP = ".z-daterangebox-popup";

	/** A panel's month anchor. DaterangePopup sets each Calendar's value to the
	 *  1st purely to pick the displayed month, and CE marks that day
	 *  z-calendar-selected — so the anchor must neither paint nor announce as a
	 *  selection. The class stays (it is Calendar's keyboard cursor), which is
	 *  why the paint has to be asserted rather than the class. */
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

	/** Empty box: no range is set, so nothing in the popup may be announced as
	 *  selected — in particular not the 1st of either displayed month. */
	@Test
	public void testEmptyBoxAnnouncesNoSelection() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();
		if (!za11yActive()) return; // NO_A11Y variant emits no aria state

		openPopup(0);
		assertEquals(0L, count(POPUP + " [aria-selected]"));
		assertEquals(0L, count(POPUP + " [aria-current]"));
	}

	/** Seeded box (2025-01-10 .. 2025-01-15): the announcement must cover exactly
	 *  the painted range — every highlighted day, not just its two ends — and the
	 *  month anchor must be no part of it. */
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

	/** The ticket's visual claim. The fix keeps z-calendar-selected on the anchor
	 *  and relies on the popup's LESS to neutralise it, so only a computed-style
	 *  comparison can tell "hidden" from "painted". */
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

	/** An anchor that falls inside the hover-preview band must be painted by the
	 *  band, not punched out of it: the suppression rule has to exclude the
	 *  preview roles as well as the committed ones. */
	@Test
	public void testAnchorCellInsidePreviewBandPaintsLikeBand() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();

		openPopup(0);
		// Stage a begin at the end of the first panel, then hover the 10th of the
		// second so the preview band runs across that panel's 1st.
		click(jq(POPUP + "-panels .z-calendar:eq(0) .z-calendar-cell:not(.z-calendar-outside):last"));
		waitResponse();
		getActions().moveToElement(toElement(jq(POPUP
				+ "-panels .z-calendar:eq(1) .z-calendar-cell:not(.z-calendar-outside):eq(9)")))
				.perform();
		waitResponse();

		String band = backgroundOf(POPUP
				+ " .z-calendar-cell.z-cell-range-preview-mid:not(.z-calendar-selected)");
		assertNotEquals("no-such-cell", band, "the hover preview must paint a band");
		assertEquals(band, backgroundOf(POPUP
						+ " .z-calendar-cell.z-calendar-selected.z-cell-range-preview-mid"),
				"the month anchor must not leave a gap in the preview band");
	}

	/** The anchor must give the same hover feedback as any other day; the
	 *  suppression rule out-specifies the plain :hover rule and has to restore it. */
	@Test
	public void testAnchorCellRespondsToHover() {
		connect("/test2/B110-ZK-6134.zul");
		waitResponse();

		openPopup(1);
		String resting = backgroundOf(ANCHOR);
		getActions().moveToElement(toElement(jq(ANCHOR))).perform();
		waitResponse();

		assertEquals(1L, count(ANCHOR + ":hover"), "the hover must land on the anchor");
		assertNotEquals(resting, backgroundOf(ANCHOR),
				"the month anchor must light up under the mouse like any other day");
	}
}
