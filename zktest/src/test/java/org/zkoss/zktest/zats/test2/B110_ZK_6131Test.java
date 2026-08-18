/* B110_ZK_6131Test.java

        Purpose:

        Description:

        History:
                Tue Aug 18 12:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6131Test extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/** Body-mounted popup: a plain selector cannot tell two boxes apart. */
	private boolean popupVisible(String boxId) {
		return "true".equals(getEval("(function () {"
				+ "var b = zk.Widget.$('$" + boxId + "');"
				+ "var p = b._rangePopup && b._rangePopup.$n();"
				+ "return !!p && getComputedStyle(p).display !== 'none';"
				+ "})()"));
	}

	private void openViaButton(String boxId) {
		click(jq("$" + boxId + " .z-daterangebox-button"));
		waitResponse();
	}

	/** Day-view cells keep their day in jQuery's data cache, not a DOM attribute. */
	private void clickCellInPanel(int panelIndex, int day) {
		js().executeScript(
				"var pane = document.querySelectorAll("
				+ "'.z-daterangebox-popup-panels .z-calendar')[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++) {"
				+ "  if (jq(cells[i]).data('value') === arguments[1] && (cells[i]._monofs || 0) === 0) {"
				+ "    zk.Widget.$(pane)._clickDate({target: cells[i], domTarget: cells[i], stop: function () {}});"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}

	/** Count onClick AU requests per box id. The server discards a disabled box's
	 *  onClick, so only zAu.send can show the wasted round-trip. */
	private void installClickProbe() {
		js().executeScript(
				"window.__clicks = {};"
				+ "if (!zAu.__origSend) {"
				+ "  zAu.__origSend = zAu.send;"
				+ "  zAu.send = function (aureq) {"
				+ "    var t = aureq && aureq.target;"
				+ "    if (aureq && aureq.name === 'onClick' && t && t.id)"
				+ "      window.__clicks[t.id] = (window.__clicks[t.id] || 0) + 1;"
				+ "    return zAu.__origSend.apply(zAu, arguments);"
				+ "  };"
				+ "}");
	}

	private int clickRequests(String boxId) {
		return Integer.parseInt(getEval(
				"String((window.__clicks || {})['" + boxId + "'] || 0)"));
	}

	/** A disabled box must refuse the click outright, not spend a round-trip on
	 *  an onClick the server discards. */
	@Test
	public void testDisabledBoxSendsNoClickRequest() {
		connect();
		waitResponse();
		installClickProbe();

		// control: so a zero below is the guard working, not a probe that never fires
		click(jq("$drbEdit .z-daterangebox-separator"));
		waitResponse();
		assertEquals(1, clickRequests("drbEdit"),
				"control: an editable box must send its onClick");
		assertEquals("drbEdit", jq("$echoClick").text(),
				"control: the editable box's onClick must reach the listener");

		click(jq("$drbDisabled .z-daterangebox-separator"));
		waitResponse();
		assertEquals(0, clickRequests("drbDisabled"),
				"a disabled box must not send an onClick the server only discards");
		assertEquals("drbEdit", jq("$echoClick").text(),
				"and the disabled box's onClick must never reach the listener");
	}

	/** readonly blocks typing, not picking: the popup has to open. */
	@Test
	public void testReadonlyPopupOpensViaTriggerButton() {
		connect();
		waitResponse();

		// control: the readonly datebox in the same page opens on click
		click(jq("$dbRo"));
		waitResponse();
		assertTrue(jq(".z-datebox-popup:visible").exists(),
				"control: a readonly datebox must still open its calendar");

		openViaButton("drbRo");
		assertTrue(popupVisible("drbRo"),
				"a readonly daterangebox must still open its calendar");
	}

	/** A click anywhere in a readonly box is a request to pick. The separator is
	 *  used because it focuses nothing, isolating the click path from focus. */
	@Test
	public void testReadonlyPopupOpensOnBoxClick() {
		connect();
		waitResponse();

		click(jq("$drbRo .z-daterangebox-separator"));
		waitResponse();

		assertTrue(popupVisible("drbRo"),
				"clicking a readonly box must open its calendar");
	}

	/** GUARD, not evidence: the focus handler opens the popup here anyway. */
	@Test
	public void testReadonlyPopupOpensOnInputClick() {
		connect();
		waitResponse();

		click(jq("$drbRo .z-daterangebox-begin"));
		waitResponse();

		assertTrue(popupVisible("drbRo"),
				"clicking a readonly box's input must open its calendar");
	}

	/** The trigger button must stay a toggle — doClick_ has to exclude it. */
	@Test
	public void testReadonlyTriggerButtonStillToggles() {
		connect();
		waitResponse();

		openViaButton("drbRo");
		assertTrue(popupVisible("drbRo"), "precondition: the button must open the calendar");

		openViaButton("drbRo");
		assertFalse(popupVisible("drbRo"),
				"a second click on the trigger must close the calendar, not reopen it");
	}

	/** A pick in a readonly box must still commit and reach the server. */
	@Test
	public void testReadonlyPickCommitsTheRange() {
		connect();
		waitResponse();

		openViaButton("drbRo");
		clickCellInPanel(0, 10);
		clickCellInPanel(0, 15);
		// The completing pick auto-applies after 200ms; let it fire.
		sleep(500);
		waitResponse();

		String begin = jq("$drbRo .z-daterangebox-begin").val();
		String end = jq("$drbRo .z-daterangebox-end").val();
		assertFalse(begin.isEmpty(),
				"picking in a readonly popup must fill the begin input");
		assertFalse(end.isEmpty(),
				"picking in a readonly popup must fill the end input");
		assertEquals(begin + "~" + end, jq("$echo").text(),
				"the server must receive the range picked on a readonly box");
	}

	/** setOpen(true) must open a readonly box and be recorded as open. The echo
	 *  comes from a timer because a click outside the popup would close it. */
	@Test
	public void testSetOpenOpensReadonlyPopup() {
		connect();
		waitResponse();

		click(jq("$btnOpen"));
		waitResponse();
		assertTrue(popupVisible("drbRo"),
				"setOpen(true) must open a readonly box's calendar");

		sleep(900); // let the armed timer report back
		waitResponse();
		assertEquals("true", jq("$echoOpen").text(),
				"the server must record a readonly box's popup as open");
		assertTrue(popupVisible("drbRo"),
				"the timer round-trip must not have closed the calendar");
	}

	/** Turning readonly on must not close an open calendar. Driven on the widget
	 *  because a click on any control outside the popup would close it. */
	@Test
	public void testSetReadonlyKeepsAnOpenPopupOpen() {
		connect();
		waitResponse();

		openViaButton("drbEdit");
		assertTrue(popupVisible("drbEdit"), "precondition: the calendar must be open");

		js().executeScript("zk.Widget.$('$drbEdit').setReadonly(true);");
		waitResponse();

		assertTrue(jq("$drbEdit").hasClass("z-daterangebox-readonly"),
				"precondition: the box must have become readonly");
		assertTrue(popupVisible("drbEdit"),
				"turning readonly on must not close the open calendar");
	}

	/** disabled is still a hard gate. */
	@Test
	public void testDisabledStillBlocksThePopup() {
		connect();
		waitResponse();

		openViaButton("drbDisabled");

		assertFalse(popupVisible("drbDisabled"),
				"a disabled box must still refuse to open its calendar");
	}
}
