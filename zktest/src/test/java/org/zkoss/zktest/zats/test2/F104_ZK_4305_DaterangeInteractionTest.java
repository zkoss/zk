/* F104_ZK_4305_DaterangeInteractionTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeInteractionTest extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void clickCellInPanel(int panelIndex, int day) {
		// Call the Calendar widget's _clickDate directly. Day-view cells
		// store their day value in jQuery's data cache, not in a DOM
		// attribute, so we look it up via jq().data('value').
		js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "var pane = panels[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[1] && (cells[i]._monofs||0) === 0) {"
				+ "    var w = zk.Widget.$(pane);"
				+ "    w._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}

	private void openPopupViaButton() {
		click(jq(".z-daterangebox-button"));
		waitResponse();
	}

	/** Dispatch a bubbling mouseover on the current-month day cell in a panel,
	 *  the way a real pointer hover would, to exercise the cell-hover preview
	 *  listeners installed by DaterangePopup._installCellHover. */
	private void hoverCellInPanel(int panelIndex, int day) {
		js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "var pane = panels[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[1] && (cells[i]._monofs||0) === 0) {"
				+ "    cells[i].dispatchEvent(new MouseEvent('mouseover', {bubbles: true}));"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}

	/** P5: hovering a candidate end date must preview the range. Proves the
	 *  cell-hover listeners survive the panel's deferred setValue rerender on
	 *  open — fails if _installCellHover binds to a node the rerender replaces
	 *  (orphaned listeners). */
	@Test
	public void testHoverPreviewHighlight() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();
		openPopupViaButton();
		clickCellInPanel(0, 6);   // stage begin only (stagedEnd still empty)
		waitResponse();
		// The hover preview uses z-cell-range-preview-{mid,end} (a committed
		// range uses z-cell-range-{mid,end}); none exist before hovering.
		assertEquals(0, jq(".z-cell-range-preview-end").length(),
				"Pre-condition: no preview endpoint before hovering");
		hoverCellInPanel(0, 15);  // hover a later candidate end
		assertTrue(jq(".z-cell-range-preview-end").length() >= 1
						|| jq(".z-cell-range-preview-mid").length() >= 1,
				"Hovering a candidate end date should preview the range (z-cell-range-preview-*)");
	}

	/** Click the < or > arrow on a panel via the Calendar widget. We bypass
	 *  the DOM click event to avoid Selenium's autoscroll. After triggering
	 *  the navigation, sleep briefly so Calendar's animation + DOM swap
	 *  completes before the next assertion runs. */
	private void clickPanelArrow(int panelIndex, String direction) {
		String suffix = "right".equals(direction) ? "right" : "left";
		js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "var pane = panels[arguments[0]];"
				+ "if (!pane) return;"
				+ "var arrow = pane.querySelector('.z-calendar-' + arguments[1]);"
				+ "if (!arrow) return;"
				+ "var w = zk.Widget.$(pane);"
				+ "w._clickArrow({domTarget: arrow, stop: function(){}});",
				panelIndex, suffix);
		// Allow Calendar's animated rerender and our deferred _applyHighlight
		// (queued via setTimeout(0)) to settle.
		sleep(400);
		waitResponse();
	}

	private String panelTitleText(int panelIndex) {
		return (String) js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "return panels[arguments[0]].querySelector('.z-calendar-title').textContent.trim();",
				panelIndex);
	}

	/** TC-L1-02 + spec: clicking the calendar icon opens popup with two
	 *  side-by-side calendar panels. */
	@Test
	public void testIconClickOpensPopup() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();
		assertFalse(jq(".z-daterangebox-popup:visible").exists(),
				"Popup should be hidden before any click");

		openPopupViaButton();

		assertTrue(jq(".z-daterangebox-popup").exists(), "Popup must exist");
		assertEquals(2, jq(".z-daterangebox-popup-panels .z-calendar").length(),
				"Popup must contain exactly two month panels");
	}

	/** Spec extension: clicking the begin input also opens popup. */
	@Test
	public void testInputClickOpensPopup() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		click(jq(".z-daterangebox-begin"));
		waitResponse();

		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup must open when the begin input is focused");
	}

	/**
	 * Regression: opening the popup via input focus, then clicking Cancel,
	 * must leave it closed. Previously the popup re-opened because close()
	 * restores focus to the begin input → focusin → _openPopup → and the
	 * old guard order had already set _open=false so the early-return
	 * trip-wire didn't trigger. Server/client also went out of sync because
	 * the synchronous re-open fired onOpen:true between onOpen:false events.
	 */
	@Test
	public void testCancelAfterInputFocusKeepsPopupClosed() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		// Open via the input field so close()'s back.focus() lands on it.
		click(jq(".z-daterangebox-begin"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup must open from input focus");

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertEquals("none", display,
				"Popup must stay closed after Cancel even when opened via input focus");

		Boolean clientOpen = (Boolean) js().executeScript(
				"return zk.Widget.$('$dr')._open;");
		assertEquals(Boolean.FALSE, clientOpen,
				"Client _open flag must reflect actual closed state (server/client sync)");
	}

	/** TC-L1-07: clicking outside the popup closes it. */
	@Test
	public void testClickOutsideClosesPopup() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		assertTrue(jq(".z-daterangebox-popup").exists());

		click(jq("body"));
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertEquals("none", display,
				"Popup must be display:none after outside click");
	}

	/** TC-L2-17: default popup renders 2 month panels. */
	@Test
	public void testTwoMonthPanelsRendered() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();

		assertEquals(2, jq(".z-daterangebox-popup-panels .z-calendar").length(),
				"Default daterangebox must render 2 month panels");
	}

	/** TC-L1-03: clicking begin then end produces z-cell-range-* classes. */
	@Test
	public void testRangeSelectionHighlight() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 6);
		waitResponse();
		clickCellInPanel(0, 15);
		waitResponse();

		assertEquals(1, jq(".z-cell-range-begin").length(),
				"Exactly one cell should be marked as range begin");
		assertEquals(1, jq(".z-cell-range-end").length(),
				"Exactly one cell should be marked as range end");
		assertTrue(jq(".z-cell-range-mid").length() >= 1,
				"At least one cell should be marked as range mid");
	}

	/** Cancel discards staged selection — committed inputs stay empty. */
	@Test
	public void testCancelDiscardsStaging() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		waitResponse();

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-begin').value;");
		assertTrue(beginVal == null || beginVal.isEmpty(),
				"Begin input must remain empty after cancel");
	}

	/** Apply commits the staged range — inputs reflect chosen begin/end. */
	@Test
	public void testApplyCommitsRange() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		waitResponse();
		clickCellInPanel(0, 15);
		waitResponse();
		sleep(300); // auto-apply fires after the 200ms range-completion delay
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-begin').value;");
		String endVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-end').value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"Begin input must show committed begin date");
		assertFalse(endVal == null || endVal.isEmpty(),
				"End input must show committed end date");
	}

	/** Bug guard for the residual-highlight bug seen in the user's
	 *  screenshot: a third click after a complete range must reset staging
	 *  and leave no z-cell-range-mid / -end classes behind, AND no
	 *  z-calendar-selected cell may render with the strong-coloured
	 *  background. */
	@Test
	public void testThirdClickClearsOldHighlight() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 6);   // begin
		waitResponse();
		clickCellInPanel(0, 15);  // end -> range 6..15 in panel 0
		waitResponse();
		clickCellInPanel(1, 23);  // 3rd click in panel 1 -> reset
		waitResponse();

		assertEquals(1, jq(".z-cell-range-begin").length(),
				"Only the new begin should remain after reset");
		assertEquals(0, jq(".z-cell-range-end").length(),
				"No range-end class should remain after reset");
		assertEquals(0, jq(".z-cell-range-mid").length(),
				"No range-mid class should remain after reset");

		Long stuckBlue = (Long) js().executeScript(
				"var n = 0;"
				+ "document.querySelectorAll('.z-daterangebox-popup .z-calendar-cell.z-calendar-selected').forEach(function(c){"
				+ "  if (!c.classList.contains('z-cell-range-begin') && !c.classList.contains('z-cell-range-end') && !c.classList.contains('z-cell-range-mid')) {"
				+ "    var bg = getComputedStyle(c).backgroundColor;"
				+ "    if (bg && bg !== 'rgba(0, 0, 0, 0)' && bg !== 'transparent') n++;"
				+ "  }"
				+ "});"
				+ "return n;");
		assertEquals(0L, stuckBlue.longValue(),
				"No leftover selected cell should still render the strong-coloured background");
	}

	/** TC-L1-05: Clear button drops the staged highlight. */
	@Test
	public void testClearResetsStaging() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		waitResponse();

		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();

		assertEquals(0, jq(".z-cell-range-begin").length(),
				"Clear must drop the begin highlight");
		assertEquals(0, jq(".z-cell-range-end").length(),
				"Clear must drop the end highlight");
	}

	/** TC-L4-01 / B1-01: cross-month range — begin in panel 0, end in
	 *  panel 1. The shared month boundary should still receive consistent
	 *  begin / mid / end marks across both panels. */
	@Test
	public void testCrossMonthRangeHighlight() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		// Click "28" on panel 0 (current month) and "5" on panel 1
		// (next month) — that's typically a 7- to 9-day cross-month range.
		clickCellInPanel(0, 28);
		clickCellInPanel(1, 5);

		// Begin / end each appear on EXACTLY ONE cell: the in-month cell that
		// owns the date. A boundary date also shows as an adjacent-month
		// "outside" fill cell (visibility:hidden) on the neighbouring panel,
		// but setRangeHighlight only tags the in-month cell (monofs === 0), so
		// the date is never double-tagged. (Regression: previously the outside
		// fill cell received the same class, so this asserted >= 1.)
		assertEquals(1, jq(".z-cell-range-begin").length(),
				"Exactly one begin highlight (in-month cell only) on cross-month range");
		assertEquals(1, jq(".z-cell-range-end").length(),
				"Exactly one end highlight (in-month cell only) on cross-month range");
		assertTrue(jq(".z-cell-range-mid").length() >= 1,
				"At least one mid cell across the two panels");
	}

	/** Bug guard: popup must mount on document.body so it isn't clipped by
	 *  ancestors with overflow:hidden (hlayout / vlayout / window). */
	@Test
	public void testPopupMountedOnBody() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();

		Boolean onBody = (Boolean) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').parentElement === document.body;");
		assertTrue(onBody,
				"Popup root must be a direct child of document.body to escape overflow:hidden ancestors");
	}

	// ===== Batch A: state preservation across panel navigation =====

	/** S1: a complete begin..end range stays visible after navigating
	 *  panel 0 forward and back. Calendar wipes its inner DOM on month
	 *  shift, so the popup must reapply z-cell-range-* on the fresh grid. */
	@Test
	public void testCompletedRangeSurvivesPanelForwardBack() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 6);
		clickCellInPanel(0, 15);
		// Range now: 6..15 in panel 0. Verify pre-navigation baseline.
		int beginBefore = jq(".z-cell-range-begin").length();
		int endBefore = jq(".z-cell-range-end").length();
		int midBefore = jq(".z-cell-range-mid").length();
		assertTrue(beginBefore >= 1 && endBefore >= 1 && midBefore >= 1,
				"baseline highlight must exist before navigation");

		// Forward then back on panel 0.
		clickPanelArrow(0, "right");
		clickPanelArrow(0, "left");

		assertEquals(beginBefore, jq(".z-cell-range-begin").length(),
				"begin highlight count must be restored after forward+back");
		assertEquals(endBefore, jq(".z-cell-range-end").length(),
				"end highlight count must be restored after forward+back");
		assertTrue(jq(".z-cell-range-mid").length() >= 1,
				"mid highlight must exist after forward+back");
	}

	/** S1 extension: navigate two months forward, two months back. */
	@Test
	public void testCompletedRangeSurvivesTwoStepNavigation() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 6);
		clickCellInPanel(0, 15);

		clickPanelArrow(0, "right");
		clickPanelArrow(0, "right");
		clickPanelArrow(0, "left");
		clickPanelArrow(0, "left");

		assertTrue(jq(".z-cell-range-begin").length() >= 1,
				"begin highlight must survive 2-step round trip");
		assertTrue(jq(".z-cell-range-end").length() >= 1,
				"end highlight must survive 2-step round trip");
	}

	/** S2: with only stagedBegin set (no end yet), navigating the panel
	 *  must NOT be misinterpreted as a date pick that overrides
	 *  stagedBegin. ZK Calendar fires onChange with shiftView=true for
	 *  arrow clicks; the popup's handler must filter that out. */
	@Test
	public void testStagedBeginSurvivesPanelNavigation() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		// Sanity: stagedBegin is set, stagedEnd is undefined.
		Long stagedBeginMs = (Long) js().executeScript(
				"var p = zk.Widget.$(document.querySelector('.z-daterangebox'))._rangePopup;"
				+ "return p._stagedBegin ? p._stagedBegin.getTime() : null;");
		Long stagedEndMs = (Long) js().executeScript(
				"var p = zk.Widget.$(document.querySelector('.z-daterangebox'))._rangePopup;"
				+ "return p._stagedEnd ? p._stagedEnd.getTime() : null;");
		assertTrue(stagedBeginMs != null,
				"stagedBegin must be set after first cell click");
		assertTrue(stagedEndMs == null,
				"stagedEnd must NOT be set after only one cell click");

		clickPanelArrow(0, "right");

		Long stagedBeginAfter = (Long) js().executeScript(
				"var p = zk.Widget.$(document.querySelector('.z-daterangebox'))._rangePopup;"
				+ "return p._stagedBegin ? p._stagedBegin.getTime() : null;");
		Long stagedEndAfter = (Long) js().executeScript(
				"var p = zk.Widget.$(document.querySelector('.z-daterangebox'))._rangePopup;"
				+ "return p._stagedEnd ? p._stagedEnd.getTime() : null;");
		assertEquals(stagedBeginMs, stagedBeginAfter,
				"stagedBegin must be unchanged after panel navigation");
		assertTrue(stagedEndAfter == null,
				"stagedEnd must remain unset after panel navigation");
	}

	/** S3: applying a range, closing the popup, and reopening should land
	 *  panel 0 on the same month as the committed begin (so the highlight
	 *  is actually visible) and re-show z-cell-range-* classes. */
	@Test
	public void testReopenAfterApplyAnchorsToBeginMonth() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		clickCellInPanel(0, 15);
		String panelTitleAtApply = panelTitleText(0);
		sleep(300); // auto-apply fires after the 200ms range-completion delay
		waitResponse();
		// Popup is now closed; box has committed value.

		// Reopen.
		openPopupViaButton();

		assertEquals(panelTitleAtApply, panelTitleText(0),
				"Reopened popup must anchor panel 0 to the committed begin month");
		assertTrue(jq(".z-cell-range-begin").length() >= 1,
				"Reopened popup must restore the range-begin highlight");
		assertTrue(jq(".z-cell-range-end").length() >= 1,
				"Reopened popup must restore the range-end highlight");
		assertTrue(jq(".z-cell-range-mid").length() >= 1,
				"Reopened popup must restore the in-range mid highlights");
	}

	/** S5: cancel discards staged range AND, on reopen, panel 0 is still
	 *  anchored to today (no committed value to anchor to). */
	@Test
	public void testCancelDiscardsAndReopenLandsOnToday() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		// Reopen.
		openPopupViaButton();

		String title = panelTitleText(0);
		// We can't easily test "title contains the right month name" across
		// locales; instead, verify there are no leftover range classes from
		// the discarded staged pick.
		assertEquals(0, jq(".z-cell-range-begin").length(),
				"Discarded staged pick must not produce range-begin on reopen");
		assertEquals(0, jq(".z-cell-range-end").length(),
				"No range-end class should exist after cancel + reopen");
		assertTrue(title != null && !title.isEmpty(),
				"Reopened popup must have a panel title (month label)");
	}

	/** ZK-4305: the invalid mark is whole-widget, so a valid edit on ONE input
	 *  must not clear the red border while the OTHER input still holds
	 *  unparseable text — including when the valid edit fires a successful
	 *  onChange whose server response also requests a clear. */
	@Test
	public void testInvalidMarkPersistsWhileOtherInputInvalid() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		// Unparseable text in begin -> whole-widget invalid mark appears.
		typeAndChange(".z-daterangebox-begin", "not-a-date");
		assertTrue(jq(".z-daterangebox-invalid").exists(),
				"Unparseable begin input must mark the widget invalid");

		// A valid value typed into the OTHER (end) input must NOT clear the mark
		// while begin is still unparseable. (Since the X2 fix, this end edit no
		// longer fires onChange at all -- _fireChange is suppressed while a side
		// holds bad text; the mark stays because _setInvalid self-guards on the
		// still-unparseable begin. See testStaleSideNotCommittedWhileOtherInputInvalid.)
		typeAndChange(".z-daterangebox-end", "2026-02-10");
		assertTrue(jq(".z-daterangebox-invalid").exists(),
				"Invalid mark must persist while begin input is still unparseable");

		// Fixing begin to a valid value clears the mark (both inputs now valid).
		typeAndChange(".z-daterangebox-begin", "2026-02-01");
		assertFalse(jq(".z-daterangebox-invalid").exists(),
				"Invalid mark must clear once both inputs parse");
	}

	/** Fire two _clickDate events on the SAME cell within ONE script execution,
	 *  reproducing a native double-click inside the 200ms auto-apply window. */
	private void doubleClickCellInPanel(int panelIndex, int day) {
		js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "var pane = panels[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[1] && (cells[i]._monofs||0) === 0) {"
				+ "    var w = zk.Widget.$(pane);"
				+ "    var ev = {target: cells[i], domTarget: cells[i], stop: function(){}};"
				+ "    w._clickDate(ev);"
				+ "    w._clickDate(ev);"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}

	/** N1: a native double-click on the cell that COMPLETES a range (begin
	 *  already staged, the later day picked twice within 200ms) must still
	 *  commit the range. The duplicate onChange used to cancel the pending
	 *  auto-apply and reset staging, silently discarding the selection. */
	@Test
	public void testDoubleClickCompletingCellCommitsRange() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 10);        // begin
		waitResponse();
		doubleClickCellInPanel(0, 15);  // double-click the completing (later) cell
		sleep(300);                      // auto-apply fires after the 200ms delay
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-begin').value;");
		String endVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-end').value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"Double-click on the completing cell must still commit the begin date");
		assertFalse(endVal == null || endVal.isEmpty(),
				"Double-click on the completing cell must still commit the end date");
	}

	/** N1 (swap path): when the later day is picked first and the EARLIER day
	 *  completes the range, the completing pick lands on stagedBegin (via the
	 *  swap branch). A double-click on that earlier cell must still commit —
	 *  the guard matches either endpoint, not just stagedEnd. */
	@Test
	public void testDoubleClickEarlierCompletingCellCommitsRange() {
		connect("/test2/F104-ZK-4305-basic.zul");
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 15);        // begin = the later day
		waitResponse();
		doubleClickCellInPanel(0, 10);  // double-click the earlier completing cell (swap)
		sleep(300);
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-begin').value;");
		String endVal = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-end').value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"Double-click on the earlier completing cell (swap) must still commit begin");
		assertFalse(endVal == null || endVal.isEmpty(),
				"Double-click on the earlier completing cell (swap) must still commit end");
	}

	/** X2: a valid edit on one input must NOT commit while the OTHER input holds
	 *  unparseable text — otherwise the sibling's stale last-valid value is sent
	 *  to the server silently. Observed via the drEvents onChange handler, which
	 *  records "begin|end" epoch millis into lblChange and so changes ONLY when an
	 *  onChange actually reaches the server. */
	@Test
	public void testStaleSideNotCommittedWhileOtherInputInvalid() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Commit an initial valid range; onChange records "beginMs|endMs".
		typeAndChangeIn("$drEvents", ".z-daterangebox-begin", "2026-01-01");
		typeAndChangeIn("$drEvents", ".z-daterangebox-end", "2026-01-20");
		String committedBefore = jq("$lblChange").text();
		// Guard against a vacuous pass: the initial range must really have committed
		// (two epoch-millis values), or the comparison below proves nothing.
		assertTrue(committedBefore.matches("\\d+\\|\\d+"),
				"pre-condition: a valid range must commit first (got '" + committedBefore + "')");

		// Unparseable text in begin fires onError, NOT onChange — lblChange stays.
		typeAndChangeIn("$drEvents", ".z-daterangebox-begin", "not-a-date");
		// A valid edit on end must be WITHHELD while begin is unparseable: no
		// onChange may fire, so the recorded commit must be byte-for-byte unchanged.
		// Without the fix this commits "<staleBeginMs>|<newEndMs>" and the assert fails.
		typeAndChangeIn("$drEvents", ".z-daterangebox-end", "2026-02-10");
		String committedAfter = jq("$lblChange").text();

		assertEquals(committedBefore, committedAfter,
				"No onChange may commit a stale begin while begin holds unparseable text (got '"
				+ committedAfter + "')");
	}

	/** Sets a scoped input's raw text and fires the bound change event, then
	 *  waits for the AU round-trip. boxSel is a ZK id selector (e.g. "$drValue"). */
	private void typeAndChangeIn(String boxSel, String inputClass, String value) {
		js().executeScript(
				"var input = jq('" + boxSel + " " + inputClass + "')[0];"
				+ "input.value = arguments[0];"
				+ "jq(input).trigger('change');",
				value);
		waitResponse();
	}

	/** Sets an input's raw text and fires the change event the widget binds
	 *  (change.daterangebox), then waits for the AU round-trip. */
	private void typeAndChange(String inputSelector, String value) {
		js().executeScript(
				"var input = jq('$dr " + inputSelector + "')[0];"
				+ "input.value = arguments[0];"
				+ "jq(input).trigger('change');",
				value);
		waitResponse();
	}
}
