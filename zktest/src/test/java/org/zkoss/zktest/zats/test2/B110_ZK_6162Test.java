/* B110_ZK_6162Test.java

        Purpose:

        Description:

        History:
                Thu Sep 24 15:18:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6162Test extends WebDriverTestCase {

	private static final String BEGIN = "$drb .z-daterangebox-begin";

	private static final String END = "$drb .z-daterangebox-end";

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
		click(jq(".z-daterangebox-button"));
		waitResponse();
	}

	private void clickCellInPanel(int panelIndex, int day) {
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

		assertEquals("Sep 4, 2025", jq(END).val(),
				"a redraw must repaint the rejected entry, not the synced value");
		assertEquals(REVERSED, errorText(),
				"and must re-raise the server's message with it");
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
	public void testCalendarDoesNotReorderAnEarlierSecondPick() {
		connect();
		waitResponse();

		openPopupViaButton();
		clickCellInPanel(0, 15);
		clickCellInPanel(0, 10);

		assertEquals(REVERSED, errorText(),
				"an earlier second pick must be rejected, not silently swapped");
	}
}
