/* B110_ZK_6146Test.java

        Purpose:

        Description:

        History:
                Wed Aug 19 10:12:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6146Test extends WebDriverTestCase {

	private static final String PAGE = "/test2/B110-ZK-6146.zul";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void openPopup(String boxId) {
		click(jq(boxId + " .z-daterangebox-button"));
		waitResponse();
	}

	/** Day-view cells keep their day in jQuery's data cache, not a DOM attribute. */
	private void clickCell(String boxId, int panelIndex, int day) {
		js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "var panes = p.$n('panels').querySelectorAll('.z-calendar');"
				+ "var pane = panes[arguments[1]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[2] && (cells[i]._monofs||0) === 0) {"
				+ "    zk.Widget.$(pane)._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "    return;"
				+ "  }"
				+ "}",
				boxId, panelIndex, day);
		waitResponse();
	}

	/** Complete a range (day 10 then 15) and let the 200ms auto-apply fire. */
	private void completeRange(String boxId) {
		openPopup(boxId);
		clickCell(boxId, 0, 10);
		clickCell(boxId, 0, 15);
		sleep(300); // auto-apply fires 200ms after the completing pick
		waitResponse();
	}

	private String popupDisplay(String boxId) {
		return (String) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "return p && p.$n() ? p.$n().style.display : 'absent';",
				boxId);
	}

	private String inputValue(String boxId, String side) {
		return (String) js().executeScript(
				"return jq(arguments[0] + ' .z-daterangebox-' + arguments[1])[0].value;",
				boxId, side);
	}

	/** Time spinner inputs actually laid out — offsetParent is null in a
	 *  display:none subtree, so this is paint-level, not "the node exists". */
	private long visibleSpinnerCount(String boxId) {
		return (Long) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "if (!p || !p.$n('times')) return 0;"
				+ "var inps = p.$n('times').querySelectorAll('input.z-timebox-input'), n = 0;"
				+ "for (var i=0;i<inps.length;i++) if (inps[i].offsetParent) n++;"
				+ "return n;",
				boxId);
	}

	private String spinnerValue(String boxId, String side) {
		return (String) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "var tb = arguments[1] === 'begin' ? p._beginTime : p._endTime;"
				+ "return tb ? tb.getInputNode().value : 'absent';",
				boxId, side);
	}

	/** The spinner's accessible name. It lives on the `-real` input, the node the
	 *  spinner mold gives role="spinbutton". */
	private String spinnerName(String boxId, String side) {
		return (String) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "var tb = arguments[1] === 'begin' ? p._beginTime : p._endTime;"
				+ "if (!tb) return 'absent';"
				+ "var n = tb.getInputNode();"
				+ "return n.getAttribute('aria-label') || n.getAttribute('aria-labelledby') || '';",
				boxId, side);
	}

	/** Edit a spinner and hop focus to the other so the Timebox's blur commits.
	 *  Returns "ok", or "no-popup" when the popup is already gone. */
	private String editSpinner(String boxId, String side, String time) {
		String result = (String) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "if (!p || !p._isOpen || !p._beginTime || !p._endTime) return 'no-popup';"
				+ "var edited = arguments[1] === 'begin' ? p._beginTime : p._endTime;"
				+ "var other = arguments[1] === 'begin' ? p._endTime : p._beginTime;"
				+ "var inp = edited.getInputNode();"
				+ "inp.focus();"
				+ "inp.value = arguments[2];"
				+ "other.getInputNode().focus();"
				+ "return 'ok';",
				boxId, side, time);
		waitResponse();
		return result;
	}

	/** With showTime on, the completing pick must commit but leave the popup open. */
	@Test
	public void testShowTimeCompletingPickCommitsWithoutClosing() {
		connect(PAGE);
		waitResponse();

		completeRange("$drTime");

		// seeded spinner times are 00:00 begin, 23:59 end
		String begin = inputValue("$drTime", "begin");
		String end = inputValue("$drTime", "end");
		assertTrue(begin != null && begin.endsWith(" 00:00"),
				"Completing pick must commit the begin date with the begin spinner time (got '" + begin + "')");
		assertTrue(end != null && end.endsWith(" 23:59"),
				"Completing pick must commit the end date with the end spinner time (got '" + end + "')");
		assertTrue(jq("$lblTime").text().matches("\\d+\\|\\d+"),
				"The commit must reach the server as an onChange (got '" + jq("$lblTime").text() + "')");

		assertNotEquals("none", popupDisplay("$drTime"),
				"showTime popup must NOT close after the completing pick");
		assertEquals(2L, visibleSpinnerCount("$drTime"),
				"Both time spinners must still be laid out after the completing pick");
	}

	/** The popup builds both spinners itself, so a page has no ca:aria-* handle on
	 *  them: without a za11y default a screen reader reads two unnamed spinbuttons
	 *  and cannot tell begin from end. */
	@Test
	public void testShowTimeSpinnersHaveDistinctAccessibleNames() {
		connect(PAGE);
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return; // the NO_A11Y build emits no ARIA at all

		openPopup("$drTime");
		assertEquals(2L, visibleSpinnerCount("$drTime"),
				"pre-condition: both time spinners must be laid out");

		String begin = spinnerName("$drTime", "begin");
		String end = spinnerName("$drTime", "end");
		assertTrue(begin != null && !begin.isEmpty(),
				"The begin time spinner needs an accessible name (got '" + begin + "')");
		assertTrue(end != null && !end.isEmpty(),
				"The end time spinner needs an accessible name (got '" + end + "')");
		assertFalse(begin.startsWith("Unknown message code"),
				"The begin spinner name must resolve from msgza11y (got '" + begin + "')");
		assertFalse(end.startsWith("Unknown message code"),
				"The end spinner name must resolve from msgza11y (got '" + end + "')");
		assertNotEquals(begin, end,
				"The two spinners must be told apart by name (both read '" + begin + "')");
	}

	/** A begin spinner change commits, survives Cancel, and reappears on reopen. */
	@Test
	public void testBeginSpinnerChangeCommitsAndSurvivesClose() {
		connect(PAGE);
		waitResponse();

		completeRange("$drTime");
		String committedAfterPick = jq("$lblTime").text();
		assertTrue(committedAfterPick.matches("\\d+\\|\\d+"),
				"pre-condition: the completing pick must commit first (got '" + committedAfterPick + "')");

		assertEquals("ok", editSpinner("$drTime", "begin", "08:30"),
				"The begin spinner must still be available after the range is complete");

		String begin = inputValue("$drTime", "begin");
		assertTrue(begin != null && begin.endsWith(" 08:30"),
				"A begin spinner change must commit its new time (got '" + begin + "')");
		String committedAfterEdit = jq("$lblTime").text();
		assertNotEquals(committedAfterPick, committedAfterEdit,
				"The spinner change must reach the server as a new onChange");
		assertTrue(committedAfterEdit.matches("\\d+\\|\\d+"),
				"The re-commit must carry both endpoints (got '" + committedAfterEdit + "')");

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertEquals("none", popupDisplay("$drTime"), "Cancel must close the popup");
		assertEquals(begin, inputValue("$drTime", "begin"),
				"The committed begin time must survive the popup close");
		assertEquals(committedAfterEdit, jq("$lblTime").text(),
				"Closing the popup must not commit anything further");

		openPopup("$drTime");
		assertEquals("08:30", spinnerValue("$drTime", "begin"),
				"Reopened popup must show the committed begin time");
	}

	/** An end spinner change commits its new time too. */
	@Test
	public void testEndSpinnerChangeCommits() {
		connect(PAGE);
		waitResponse();

		completeRange("$drTime");
		String committedAfterPick = jq("$lblTime").text();
		assertTrue(committedAfterPick.matches("\\d+\\|\\d+"),
				"pre-condition: the completing pick must commit first (got '" + committedAfterPick + "')");

		assertEquals("ok", editSpinner("$drTime", "end", "10:15"),
				"The end spinner must still be available after the range is complete");

		String end = inputValue("$drTime", "end");
		assertTrue(end != null && end.endsWith(" 10:15"),
				"An end spinner change must commit its new time (got '" + end + "')");
		assertNotEquals(committedAfterPick, jq("$lblTime").text(),
				"The end spinner change must reach the server as a new onChange");
	}

	/** A spinner's up-arrow. Clicked with a real pointer so the widget's own
	 *  path runs: that gesture fires `onChanging` only, never `onChange`. */
	private WebElement upArrow(String boxId, String side) {
		String uuid = (String) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "var tb = arguments[1] === 'begin' ? p._beginTime : p._endTime;"
				+ "return tb ? tb.uuid : '';",
				boxId, side);
		assertTrue(uuid.length() > 0, "precondition: the " + side + " spinner must exist");
		return driver.findElement(By.id(uuid + "-btn-up"));
	}

	/** An arrow edit is never blurred before dismissal — that runs on mousedown —
	 *  so the close itself has to commit the new time. */
	@Test
	public void testArrowDrivenTimeEditSurvivesOutsideClick() {
		connect(PAGE);
		waitResponse();

		completeRange("$drTime");
		String committedAfterPick = jq("$lblTime").text();
		assertTrue(committedAfterPick.matches("\\d+\\|\\d+"),
				"pre-condition: the completing pick must commit first (got '" + committedAfterPick + "')");
		assertEquals("23:59", spinnerValue("$drTime", "end"),
				"pre-condition: the end spinner shows its seeded 23:59");

		WebElement up = upArrow("$drTime", "end");
		getActions().moveToElement(up).click().perform();
		getActions().moveToElement(up).click().perform();
		waitResponse();

		String edited = spinnerValue("$drTime", "end");
		assertNotEquals("23:59", edited,
				"pre-condition: the up-arrow clicks must change the visible end time");

		// the title label sits ABOVE the box, so the popup cannot overlap it
		click(jq("$lblTitle"));
		waitResponse();

		assertEquals("none", popupDisplay("$drTime"),
				"pre-condition: the outside click must close the popup");
		String end = inputValue("$drTime", "end");
		assertTrue(end != null && end.endsWith(" " + edited),
				"An arrow-driven end time must be committed by the close (spinner showed '"
						+ edited + "', committed '" + end + "')");
		assertNotEquals(committedAfterPick, jq("$lblTime").text(),
				"The arrow edit must reach the server as a new onChange");
	}

	/** With showTime off the auto-apply still commits AND closes. */
	@Test
	public void testDateOnlyStillAutoAppliesAndCloses() {
		connect(PAGE);
		waitResponse();

		completeRange("$drDate");

		assertEquals("none", popupDisplay("$drDate"),
				"Date-only popup must still close after the 200ms auto-apply");
		assertTrue(jq("$lblDate").text().matches("\\d+\\|\\d+"),
				"Date-only auto-apply must still commit the range (got '" + jq("$lblDate").text() + "')");
		assertTrue(inputValue("$drDate", "begin").length() > 0,
				"Date-only begin input must show the committed date");
		assertTrue(inputValue("$drDate", "end").length() > 0,
				"Date-only end input must show the committed date");
	}
}
