/* B110_ZK_6145Test.java

        Purpose:

        Description:

        History:
                Tue Aug 18 14:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6145Test extends WebDriverTestCase {

	private static final String PANELS = ".z-daterangebox-popup-panels .z-calendar";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void openPopup(String boxId) {
		click(jq("$" + boxId + " .z-daterangebox-button"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup-panels .z-calendar").exists(),
				"precondition: the popup must have panels");
	}

	/** Switch a panel's view via its title spans ('tm' month, 'ty' year, 'tyd' decade). */
	private void changeView(int panelIndex, String titleNode) {
		js().executeScript(
				"var pane = document.querySelectorAll('" + PANELS + "')[arguments[0]];"
				+ "var w = zk.Widget.$(pane);"
				+ "w._changeView({domTarget: w.$n(arguments[1]), stop: function () {}});",
				panelIndex, titleNode);
		sleep(400); // the view swap animates its new grid in
		waitResponse();
	}

	/** Range-marked cells as `<data-value>:<roles>` — month index in month view, year in year view. */
	private String rolesInPanel(int panelIndex) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var out = [];"
				+ "pane.querySelectorAll('td.z-calendar-cell').forEach(function (c) {"
				+ "  var roles = ['begin', 'end', 'mid'].filter(function (k) {"
				+ "    return c.classList.contains('z-cell-range-' + k);"
				+ "  });"
				+ "  if (roles.length) out.push(c.getAttribute('data-value') + ':' + roles.join('+'));"
				+ "});"
				+ "return out.join(',');"
				+ "})()");
	}

	private static final String PAINTED_CELLS = "td.z-calendar-cell[class*=z-cell-range-]";

	private static final String ANNOUNCED_CELLS = "td.z-calendar-cell[aria-selected=true]";

	/** The panel's display anchor, marked from the Calendar's own value; outside
	 *  the day grid it is also the keyboard cursor. */
	private static final String ANCHOR_CELL = "td.z-calendar-cell.z-calendar-selected";

	/** The NO_A11Y suite variant emits no ARIA, so aria assertions must opt out. */
	private boolean za11yActive() {
		return "true".equals(getEval("!!window.za11y"));
	}

	/** Comma-joined `data-value`s in DOM order — month index in the month view,
	 *  year in the year view, decade start in the decade view. */
	private String cellsInPanel(int panelIndex, String selector) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var out = [];"
				+ "pane.querySelectorAll(\"" + selector + "\").forEach(function (c) {"
				+ "  out.push(c.getAttribute('data-value'));"
				+ "});"
				+ "return out.join(',');"
				+ "})()");
	}

	/** An attribute of the panel's anchor cell, or the literal "null" when unset. */
	private String anchorAttr(int panelIndex, String attr) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var a = pane.querySelector(\"" + ANCHOR_CELL + "\");"
				+ "if (!a) return 'no-anchor';"
				+ "return a.getAttribute('" + attr + "') || 'null';"
				+ "})()");
	}

	/** Whether the panel's aria-activedescendant still resolves to its anchor cell. */
	private String activedescendantIsAnchor(int panelIndex) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var a = pane.querySelector(\"" + ANCHOR_CELL + "\");"
				+ "if (!a) return 'no-anchor';"
				+ "return String(pane.getAttribute('aria-activedescendant') === a.id);"
				+ "})()");
	}

	/** Arrow-key a panel and read the announcement back in the SAME task: the
	 *  repaint defers to the next tick and would mask a missing hook. */
	private String shiftAndReadAnnouncedInSameTask(int panelIndex, int ofs) {
		return getEval("(function () {"
				+ "var sel = '" + PANELS + "';"
				+ "var pane = document.querySelectorAll(sel)[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "zk.Widget.$(pane)._shift(" + ofs + ");"
				+ "var out = [];"
				+ "document.querySelectorAll(sel)[" + panelIndex + "]"
				+ "  .querySelectorAll(\"" + ANNOUNCED_CELLS + "\").forEach(function (c) {"
				+ "  out.push(c.getAttribute('data-value'));"
				+ "});"
				+ "return out.join(',');"
				+ "})()");
	}

	/** Control: a failure here means the harness, not the fix. */
	@Test
	public void testDateboxMarksTheMonthHoldingItsValue() {
		connect();
		waitResponse();

		click(jq("$db .z-datebox-button"));
		waitResponse();
		// The datebox popup holds the only calendar on the page at this point.
		js().executeScript(
				"var w = zk.Widget.$(document.querySelector('.z-calendar'));"
				+ "w._changeView({domTarget: w.$n('tm'), stop: function () {}});");
		sleep(400);
		waitResponse();

		assertTrue(jq(".z-calendar-month .z-calendar-selected").exists(),
				"a datebox must mark the month holding its value");
	}

	/** A single-year range: both ends in one year cell, two months at the ends. */
	@Test
	public void testShortRangeIsSelectedInMonthAndYearViews() {
		connect();
		waitResponse();

		openPopup("drbShort");
		changeView(0, "tm");
		assertEquals("3:begin,4:end", rolesInPanel(0),
				"April and May 2026 must be selected, as the range's two ends");

		changeView(0, "ty");
		assertEquals("2026:begin+end", rolesInPanel(0),
				"2026 holds both ends of the range, so it carries both roles");
	}

	/** A multi-year range fills the months between its ends and marks both years. */
	@Test
	public void testLongRangeIsSelectedInMonthAndYearViews() {
		connect();
		waitResponse();

		openPopup("drbLong");
		changeView(0, "tm");
		assertEquals("3:begin,4:mid,5:mid,6:mid,7:mid,8:mid,9:mid,10:mid,11:mid",
				rolesInPanel(0),
				"the 2026 panel must select April onwards, April being the range begin");

		changeView(0, "ty");
		assertEquals("2026:begin,2027:end", rolesInPanel(0),
				"both years the range spans must be selected");
	}

	/** Every panel must render the same range, whatever month it displays. */
	@Test
	public void testEveryPanelRendersTheSameRangeState() {
		connect();
		waitResponse();

		openPopup("drbLong");
		changeView(0, "tm");
		changeView(1, "tm");
		changeView(2, "tm");

		// spelled out, not compared to panel 0: three empty panels would "agree" too
		String expected = "3:begin,4:mid,5:mid,6:mid,7:mid,8:mid,9:mid,10:mid,11:mid";
		assertEquals(expected, rolesInPanel(0),
				"the first panel must select the range's 2026 months");
		assertEquals(expected, rolesInPanel(1),
				"the second panel must render the same range state as the first");
		assertEquals(expected, rolesInPanel(2),
				"the third panel must render the same range state as the first");
	}

	/** Begin only: its own month and year, nothing trailing. */
	@Test
	public void testBeginOnlyRangeSelectsItsOwnMonthAndYear() {
		connect();
		waitResponse();

		openPopup("drbBeginOnly");
		changeView(0, "tm");
		assertEquals("3:begin", rolesInPanel(0),
				"a begin-only range must select just the begin value's month");

		changeView(0, "ty");
		assertEquals("2026:begin", rolesInPanel(0),
				"a begin-only range must select just the begin value's year");
	}

	/** A hover preview must not survive into the month view as a selected month. */
	@Test
	public void testHoverPreviewDoesNotSelectAMonth() {
		connect();
		waitResponse();

		openPopup("drbBeginOnly");
		// preview a candidate end in the second panel (May)
		js().executeScript(
				"var pane = document.querySelectorAll('" + PANELS + "')[1];"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++) {"
				+ "  if (jq(cells[i]).data('value') === 20 && (cells[i]._monofs || 0) === 0) {"
				+ "    cells[i].dispatchEvent(new MouseEvent('mouseover', {bubbles: true}));"
				+ "    return;"
				+ "  }"
				+ "}");
		waitResponse();
		assertTrue(jq(".z-cell-range-preview-mid").length() >= 1
						|| jq(".z-cell-range-preview-end").length() >= 1,
				"precondition: the hover must have produced a day-view preview");

		changeView(0, "tm");
		assertEquals("3:begin", rolesInPanel(0),
				"a hover preview must not add a selected month");
	}

	/** Every panel must announce exactly the months it paints. */
	@Test
	public void testMonthViewAnnouncesExactlyThePaintedRange() {
		connect();
		waitResponse();

		openPopup("drbLong");
		changeView(0, "tm");
		changeView(1, "tm");
		changeView(2, "tm");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		for (int panel = 0; panel < 3; panel++) {
			String painted = cellsInPanel(panel, PAINTED_CELLS);
			assertEquals("3,4,5,6,7,8,9,10,11", painted,
					"precondition: panel " + panel + " must paint the 2026 range months");
			assertEquals(painted, cellsInPanel(panel, ANNOUNCED_CELLS),
					"panel " + panel + " must announce exactly the months it paints");
		}
	}

	/** drbYearEdge's second panel is anchored to 2030/01 while the range sits in
	 *  2029/12, so it paints nothing — and must announce nothing either. */
	@Test
	public void testMonthViewDoesNotAnnounceAnAnchorOutsideTheRange() {
		connect();
		waitResponse();

		openPopup("drbYearEdge");
		changeView(0, "tm");
		changeView(1, "tm");
		assertEquals("11", cellsInPanel(0, PAINTED_CELLS),
				"precondition: the first panel shows 2029, so December is painted");
		assertEquals("", cellsInPanel(1, PAINTED_CELLS),
				"precondition: the second panel shows 2030, which the range never reaches");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		assertEquals("11", cellsInPanel(0, ANNOUNCED_CELLS),
				"the first panel must announce December, the month it paints");
		assertEquals("", cellsInPanel(1, ANNOUNCED_CELLS),
				"a panel painting no range must announce nothing — not its January anchor");
		assertEquals("0", anchorAttr(1, "data-value"),
				"precondition: the second panel's anchor is January");
		assertEquals("null", anchorAttr(1, "aria-selected"),
				"the display anchor must not claim to be selected");
	}

	/** Same in the year view: the grid holds the range's year, not the anchor's. */
	@Test
	public void testYearViewAnnouncesExactlyThePaintedRange() {
		connect();
		waitResponse();

		openPopup("drbYearEdge");
		changeView(1, "tm");
		changeView(1, "ty");
		assertEquals("2029", cellsInPanel(1, PAINTED_CELLS),
				"precondition: only 2029 intersects the range");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		assertEquals("2029", cellsInPanel(1, ANNOUNCED_CELLS),
				"the year view must announce the year the range covers");
		assertEquals("2030", anchorAttr(1, "data-value"),
				"precondition: the panel is anchored to 2030");
		assertEquals("null", anchorAttr(1, "aria-selected"),
				"the anchor year must not be announced as selected");
	}

	/** Same in the decade view, where CE bakes the selected class in at render time. */
	@Test
	public void testDecadeViewAnnouncesExactlyThePaintedRange() {
		connect();
		waitResponse();

		openPopup("drbYearEdge");
		changeView(1, "tm");
		changeView(1, "ty");
		changeView(1, "tyd");
		assertEquals("2020", cellsInPanel(1, PAINTED_CELLS),
				"precondition: the 2020-2029 cell is the decade holding the range");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		assertEquals("2020", cellsInPanel(1, ANNOUNCED_CELLS),
				"the decade view must announce the decade the range covers");
		assertEquals("2030", anchorAttr(1, "data-value"),
				"precondition: the panel's anchor year falls in the 2030s");
		assertEquals("null", anchorAttr(1, "aria-selected"),
				"the anchor decade must not be announced as selected");
	}

	/** A shift keeps the outgoing grid in the DOM under the same `-mid` id, and a
	 *  RIGHT shift puts the dying one first. drbSingle is the only both-ways shape. */
	@Test
	public void testDecadeViewRightShiftPaintsTheGridThatStays() {
		connect();
		waitResponse();

		openPopup("drbSingle");
		changeView(0, "tm");
		changeView(0, "ty");
		changeView(0, "tyd");
		assertEquals("1990,2000", liveGridCells(0, PAINTED_CELLS),
				"precondition: the decade view paints the two decades the range covers");

		clickArrow(0, "right");
		assertTrue(liveGridCells(0, ALL_CELLS).contains("2090"),
				"precondition: the right arrow must have moved the panel a century on");
		assertEquals("1990,2000", liveGridCells(0, PAINTED_CELLS),
				"the century the right arrow slides in must paint the range's decades");
	}

	/** Mirror image: a left shift slides the incoming grid in first. */
	@Test
	public void testDecadeViewLeftShiftPaintsTheGridThatStays() {
		connect();
		waitResponse();

		openPopup("drbSingle");
		changeView(0, "tm");
		changeView(0, "ty");
		changeView(0, "tyd");
		assertEquals("1990,2000", liveGridCells(0, PAINTED_CELLS),
				"precondition: the decade view paints the two decades the range covers");

		// right first: the 1900s are the calendar's first century, so `<` is disabled there
		clickArrow(0, "right");
		assertTrue(liveGridCells(0, ALL_CELLS).contains("2090"),
				"precondition: the panel must have moved on a century to shift back from");

		clickArrow(0, "left");
		assertTrue(liveGridCells(0, ALL_CELLS).contains("1900"),
				"precondition: the left arrow must have moved the panel a century back");
		assertEquals("1990,2000", liveGridCells(0, PAINTED_CELLS),
				"the century the left arrow slides in must paint the range's decades");
	}

	/** The anchor loses only its selection claim; aria-activedescendant must still
	 *  resolve to it. aria-current belongs to the za11y tier, so it is not asserted. */
	@Test
	public void testAnchorLosesOnlyItsSelectionClaim() {
		connect();
		waitResponse();

		openPopup("drbYearEdge");
		changeView(1, "tm");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		assertEquals("null", anchorAttr(1, "aria-selected"),
				"the anchor's selection claim is the part that goes");
		assertEquals("true", activedescendantIsAnchor(1),
				"aria-activedescendant must still resolve to that same anchor cell");
	}

	/** An arrow-key move announces before the deferred repaint, so read synchronously. */
	@Test
	public void testArrowKeyMoveKeepsTheAnnouncementOnTheRange() {
		connect();
		waitResponse();

		openPopup("drbYearEdge");
		changeView(0, "tm");
		assertEquals("11", cellsInPanel(0, PAINTED_CELLS),
				"precondition: December is the painted month");
		assertEquals("11", anchorAttr(0, "data-value"),
				"precondition: the anchor starts on December");
		if (!za11yActive())
			return; // NO_A11Y variant emits no aria state

		// left, December -> November: same year, so the panel re-marks without rerendering
		assertEquals("11", shiftAndReadAnnouncedInSameTask(0, -1),
				"the announcement must follow the paint, not the cursor's November");
		assertEquals("10", anchorAttr(0, "data-value"),
				"precondition: the cursor did move to November");
		assertEquals("null", anchorAttr(0, "aria-selected"),
				"the cursor's new cell must not be announced as selected");
	}

	/** Drilling back into a day view rebuilds the grid, and picking a month fires
	 *  no change event — so the view switch has to repaint the range. */
	@Test
	public void testDrillingBackIntoTheDayViewKeepsTheRange() {
		connect();
		waitResponse();

		openPopup("drbShort");
		// counted, not listed: day cells carry no data-value
		int paintedBefore = countInPanel(0, PAINTED_CELLS);
		assertTrue(paintedBefore > 0,
				"precondition: the day grid must paint the range, painted: " + paintedBefore);

		changeView(0, "tm");
		assertEquals("3:begin,4:end", rolesInPanel(0),
				"precondition: the month view must show the range");

		chooseCell(0, 3); // April — drills back into that month's day view

		assertEquals(paintedBefore, countInPanel(0, PAINTED_CELLS),
				"the day grid must paint the same range after drilling back into it");
	}

	/** How many cells in a panel match {@code selector}. */
	private int countInPanel(int panelIndex, String selector) {
		return Integer.parseInt(getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "return pane ? pane.querySelectorAll(\"" + selector + "\").length : -1;"
				+ "})()"));
	}

	/** Every cell of a grid, painted or not — used to prove a shift really happened. */
	private static final String ALL_CELLS = "td.z-calendar-cell";

	/** Nodes answering to the calendar's `-mid` id — two while a shift animates. */
	private int gridsInPanel(int panelIndex) {
		return Integer.parseInt(getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return -1;"
				+ "return pane.querySelectorAll('[id=\"' + zk.Widget.$(pane).uuid + '-mid\"]').length;"
				+ "})()"));
	}

	/** Cells of the grid the panel is LEFT showing — resolved through the single
	 *  surviving `-mid` node so a discarded grid cannot answer for it. */
	private String liveGridCells(int panelIndex, String selector) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var mids = pane.querySelectorAll('[id=\"' + zk.Widget.$(pane).uuid + '-mid\"]');"
				+ "if (mids.length !== 1) return 'grids:' + mids.length;"
				+ "var out = [];"
				+ "mids[0].querySelectorAll(\"" + selector + "\").forEach(function (c) {"
				+ "  out.push(c.getAttribute('data-value'));"
				+ "});"
				+ "return out.join(',');"
				+ "})()");
	}

	/** Shift a panel with the header's < / > arrow. Waits the animation out on both
	 *  ends: a click while animating is ignored, and an early read hits a doomed grid. */
	private void clickArrow(int panelIndex, String arrow) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(d -> "false".equals(getEval("!!zk.animating()")));
		click(jq(PANELS + ":eq(" + panelIndex + ") .z-calendar-" + arrow));
		wait.until(d -> gridsInPanel(panelIndex) == 1 && "false".equals(getEval("!!zk.animating()")));
		waitResponse();
	}

	/** The exact instant the client holds as the begin endpoint, or "none". Read
	 *  back because the whole premise is that this millisecond survives the trip:
	 *  a truncated one would sit nowhere near a month boundary. */
	private String beginEpoch(String boxId) {
		return getEval("(function () {"
				+ "var w = zk.Widget.$('$" + boxId + "');"
				+ "if (!w) return 'no-such-box';"
				+ "var d = w.getBeginValueAsDate();"
				+ "return d ? String(d.getTime()) : 'none';"
				+ "})()");
	}

	/** Range roles on the day cell for {@code dayOfMonth}, or "none" / "no-such-day".
	 *  Day cells carry no `data-value` attribute — the day number lives in jQuery
	 *  data — and every grid also renders the neighbouring months' days, which the
	 *  popup hides as `z-calendar-outside`. `_monofs == 0` is what picks the copy
	 *  belonging to the panel's own month, so a hidden namesake cannot answer. */
	private String dayCellRoles(int panelIndex, int dayOfMonth) {
		return getEval("(function () {"
				+ "var pane = document.querySelectorAll('" + PANELS + "')[" + panelIndex + "];"
				+ "if (!pane) return 'no-such-panel';"
				+ "var found = 'no-such-day';"
				+ "pane.querySelectorAll('td.z-calendar-cell').forEach(function (c) {"
				+ "  if (jq(c).data('value') !== " + dayOfMonth + " || (c._monofs || 0) !== 0) return;"
				+ "  var roles = ['begin', 'end', 'mid'].filter(function (k) {"
				+ "    return c.classList.contains('z-cell-range-' + k);"
				+ "  });"
				+ "  found = roles.length ? roles.join('+') : 'none';"
				+ "});"
				+ "return found;"
				+ "})()");
	}

	/** An instant that a server push can put half an hour from a month boundary in
	 *  the box's own time zone. Both passes used to rebuild each month's epoch
	 *  bounds from wall-clock midnight, and an offset change inside the month puts
	 *  those bounds the wrong side of the instant — so the day view dropped the
	 *  whole panel's highlight and the month view selected the wrong cell or none.
	 *  A pick in the popup is box-tz midnight, so only the server route reaches it. */
	private void assertBoundaryInstantMarksItsOwnDayAndMonth(String btnId, String boxId,
			String tz, String epoch, String shownDate, int dayOfMonth, String expectedRoles) {
		connect();
		waitResponse();

		click(jq("$" + btnId));
		waitResponse();
		assertEquals(tz, getEval("zk.Widget.$('$" + boxId + "').getTimeZone()"),
				"precondition: the box must be configured with the boundary time zone");
		assertEquals(epoch, beginEpoch(boxId),
				"precondition: the pushed instant must reach the client to the millisecond");
		assertEquals(shownDate, jq("$" + boxId + " .z-daterangebox-begin").val(),
				"precondition: the box must read that instant as this calendar day");

		openPopup(boxId);
		assertEquals("begin", dayCellRoles(0, dayOfMonth),
				"the day view must select the day the begin instant falls on");

		changeView(0, "tm");
		assertEquals(expectedRoles, rolesInPanel(0),
				"the month view must select the month the begin instant falls in");
	}

	/** Asia/Almaty ends February 2024 with an offset change: 2024-02-29T18:30:00Z
	 *  is Feb 29 23:30 there. The month grid used to select March, and the day grid
	 *  nothing — its panel failed the overlap test and was cleared wholesale. */
	@Test
	public void testInstantNearAnOffsetChangeMarksItsOwnMonth() {
		assertBoundaryInstantMarksItsOwnDayAndMonth("btnAlmaty", "drbAlmaty", "Asia/Almaty",
				"1709231400000", "2024/02/29", 29, "1:begin");
	}

	/** Africa/Cairo ends DST inside Oct 31 2024: 2024-10-31T21:30:00Z is Oct 31
	 *  23:30 there, in the repeated hour. Neither grid used to select anything. */
	@Test
	public void testInstantInADstRepeatedHourMarksItsOwnMonth() {
		assertBoundaryInstantMarksItsOwnDayAndMonth("btnCairo", "drbCairo", "Africa/Cairo",
				"1730410200000", "2024/10/31", 31, "9:begin");
	}

	/** Pick a cell; from a month or year view this drills one level down. */
	private void chooseCell(int panelIndex, int value) {
		click(jq(PANELS + ":eq(" + panelIndex + ") td.z-calendar-cell[data-value=" + value + "]"));
		sleep(400); // the view swap animates its new grid in
		waitResponse();
	}
}
