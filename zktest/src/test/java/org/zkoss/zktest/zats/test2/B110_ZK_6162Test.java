/* B110_ZK_6162Test.java

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

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6162Test extends WebDriverTestCase {

	private static final String BEGIN = "$drb .z-daterangebox-begin";

	private static final String END = "$drb .z-daterangebox-end";

	private static final String JIRA_BEGIN = "$drbJira .z-daterangebox-begin";

	private static final String JIRA_END = "$drbJira .z-daterangebox-end";

	private static final String REVERSED = "Begin date must not be later than end date";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void typeAndCommit(String selector, String text) {
		// type()'s Cmd+A selects nothing under macOS ChromeDriver, so the text would append.
		js().executeScript("jq('" + selector + "').val('');");
		type(jq(selector), text);
		waitResponse();
	}

	private String errorText() {
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> jq(".z-errorbox-content").exists());
		return jq(".z-errorbox-content").text();
	}

	private void openPopupViaButton() {
		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
	}

	private void clickCellInPanel(int panelIndex, int day) {
		js().executeScript(
				"var panels = zk.Widget.$('$drb')._rangePopup.$n().querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
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

	@Test
	public void testAppThrownErrorIsTrackedAndClearable() {
		connect();
		waitResponse();

		click(jq("$btnAppReject"));
		waitResponse();
		assertEquals("Booked", errorText(), "precondition: the app's WrongValueException reaches the box");

		click(jq("$btnErrmsg"));
		waitResponse();
		assertEquals("Booked", jq("$errmsg").text(), "the server must record an error the app raised");

		click(jq("$btnClearErr"));
		waitResponse();
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"clearErrorMessage must clear that error at the client");
	}

	@Test
	public void testPopupClearAfterRejectionClearsTheServerError() {
		connect();
		waitResponse();

		click(jq("$btnAppReject"));
		waitResponse();
		assertEquals("Booked", errorText(), "precondition: the box is in error");

		openPopupViaButton();
		((WebElement) js().executeScript(
				"return zk.Widget.$('$drb')._rangePopup.$n().querySelector('.z-daterangebox-popup-clear');")).click();
		waitResponse();
		assertEquals("", jq("$committedEnd").text(),
				"re-sending the unchanged range must not post onChange to the app");
		click(jq("$btnErrmsg"));
		waitResponse();
		assertEquals("null", jq("$errmsg").text(),
				"clearing must reach the server even though the committed range did not change");
	}

	@Test
	public void testJiraConfigKeepsBothEntriesThroughRequiredAndReversed() {
		connect();
		waitResponse();

		typeAndCommit(JIRA_BEGIN, "Sep 2, 2026");
		errorText();
		typeAndCommit(JIRA_END, "Sep 4, 2025");
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> REVERSED.equals(jq(".z-errorbox-content").text()));

		assertEquals("Sep 2, 2026", jq(JIRA_BEGIN).val(), "begin must survive both rejections");
		assertEquals("Sep 4, 2025", jq(JIRA_END).val(), "the typed end must not be rewritten");

		typeAndCommit(JIRA_END, "Sep 4, 2026");
		assertFalse(jq("$drbJira").hasClass("z-daterangebox-invalid"), "a valid pair clears the mark");
		assertEquals("committed", jq("$jiraCommitted").text(), "and commits");
	}

	@Test
	public void testAClearQueuedBeforeTheNextRejectionKeepsItsEntry() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		js().executeScript("var w = zk.Widget.$('$drb'), b = w._beginValue, e = w._endValue;"
				+ "zAu.cmd0.clearWrongValue(w.uuid);"
				+ "w._syncValues(b ? b.getTime() : undefined, e ? e.getTime() : undefined);"
				+ "zAu.cmd0.wrongValue(w.uuid, arguments[0]);", REVERSED);
		sleep(500);

		assertEquals("true", getEval("zk.Widget.$('$drb')._sideHasInvalidText('end')"),
				"a clear queued by an earlier response must not erase the later rejection's entry");
		js().executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();
		assertEquals("Sep 4, 2025", jq(END).val(), "so a redraw still repaints the rejected entry");
	}

	@Test
	public void testARepaintBeforeTheRejectionIsShownKeepsTheEntry() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		js().executeScript("jq('" + END + "').val('Sep 4, 2025');");
		js().executeScript("var w = zk.Widget.$('$drb'), b = w._beginValue;"
				+ "w._syncValues(b ? b.getTime() : undefined, undefined);"
				+ "w.setConstraint('');");

		assertEquals("Sep 4, 2025", jq(END).val(),
				"a repaint between the server sync and its error must not paint the old value over the entry");
	}

	@Test
	public void testRejectionKeepsWhatTheUserTyped() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		assertEquals("Sep 2, 2026", jq(BEGIN).val(),
				"begin must survive the rejection");
		assertEquals("Sep 4, 2025", jq(END).val(),
				"end must survive the rejection — the user has to see the year to fix it");
	}

	@Test
	public void testCorrectingOneSideCommitsThePairOnScreen() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		typeAndCommit(BEGIN, "Sep 2, 2025");

		assertEquals(String.valueOf(js().executeScript("return new Date(2025, 8, 4).getTime();")),
				jq("$committedEnd").text(),
				"the end still on screen must be committed with the corrected begin");
	}

	@Test
	public void testRedrawKeepsTheRejectedEntry() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		js().executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();
		focus(jq(END));

		assertEquals("Sep 4, 2025", jq(END).val(),
				"a redraw must repaint the rejected entry, not the synced value");
		assertEquals(REVERSED, errorText(),
				"and must re-raise the server's message with it");
	}

	@Test
	public void testConstraintChangeKeepsTheRejectedEntry() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		js().executeScript("zk.Widget.$('$drb').setConstraint('');");
		js().executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();
		focus(jq(END));

		assertEquals("Sep 4, 2025", jq(END).val(),
				"a constraint change must not drop the rejected entry a later redraw repaints");
		assertEquals(REVERSED, errorText(),
				"and the errorbox must still name that entry");
	}

	@Test
	public void testServerResetToTheSameRangeRepaintsIt() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		click(jq("$btnReset"));
		waitResponse();

		assertEquals("", jq(END).val(),
				"setting the range the server already holds must still replace the rejected entry");
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"and drop the invalid mark");
	}

	@Test
	public void testServerHoldsTheRejectionUntilItSetsTheRange() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		click(jq("$btnErrmsg"));
		waitResponse();
		assertEquals(REVERSED, jq("$errmsg").text(), "getErrorMessage must report the rejection");

		click(jq("$btnReset"));
		waitResponse();
		click(jq("$btnErrmsg"));
		waitResponse();
		assertEquals("null", jq("$errmsg").text(), "setting the range must clear it");
	}

	@Test
	public void testServerBeginAfterRejectionKeepsTheEndMarked() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		click(jq("$btnSetBegin"));
		waitResponse();

		assertEquals("Sep 1, 2025", jq(BEGIN).val(),
				"the pushed value must replace only the begin side");
		assertEquals("Sep 4, 2025", jq(END).val(),
				"the end side's rejected entry must be left alone");
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"the mark must stay while the end input still shows the rejected entry");
	}

	@Test
	public void testRejectedPairDoesNotCommit() {
		connect();
		waitResponse();

		typeAndCommit(BEGIN, "Sep 2, 2026");
		typeAndCommit(END, "Sep 4, 2025");
		errorText();

		Object endValue = js().executeScript(
				"var v = zk.Widget.$('$drb')._endValue; return v ? v.getTime() : null;");
		assertEquals(null, endValue,
				"the rejected end must not reach the committed value, only the input text");
	}

	@Test
	public void testCalendarOrdersAnEarlierSecondPick() {
		connect();
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 15);
		clickCellInPanel(0, 10);
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(d -> !jq("$committedEnd").text().isEmpty());

		assertEquals("10", getEval("zk.Widget.$('$drb')._beginValue.getDate()"),
				"a calendar pick carries no order, so the earlier day becomes the begin");
		assertEquals("15", getEval("zk.Widget.$('$drb')._endValue.getDate()"));
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"), "and nothing is rejected");
	}
}
