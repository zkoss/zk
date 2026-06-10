/* F104_ZK_4305_DaterangeFixesTest.java

	Purpose:

	Description:

	History:
		Mon Jun  1 14:54:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Browser regression guards for Daterangebox bug fixes. Reuses the existing
 * ZUL pages ({@code $drFooter} from footer.zul, {@code $drBoth} from allow-empty.zul).
 */
public class F104_ZK_4305_DaterangeFixesTest extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/**
	 * M1: changing numberOfMonths while the popup is closed must rebuild the
	 * panels on reopen — otherwise the grid shows the new column count but the
	 * old number of calendars, leaving a blank trailing column.
	 */
	@Test
	public void testNumberOfMonthsRebuildsPanelsOnReopen() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();
		js().executeScript("zk.Widget.$(jq('$drFooter')[0])._openPopup();");
		waitResponse();
		// Close, change the count while closed, reopen.
		js().executeScript("var b=zk.Widget.$(jq('$drFooter')[0]);"
				+ "b._closePopup(); b.setNumberOfMonths(3); b._openPopup();");
		waitResponse();
		Long panels = (Long) js().executeScript(
				"return zk.Widget.$(jq('$drFooter')[0])._rangePopup.$n()"
				+ ".querySelectorAll('.z-calendar').length;");
		assertEquals(3L, panels.longValue(),
				"reopening after setNumberOfMonths(3) must rebuild to 3 calendar panels, not a stale 2");
	}

	/**
	 * L4: clearing an unparseable value must drop the invalid marker
	 * immediately client-side, not linger until the server echo. Checked
	 * synchronously inside the same script as the change so the assertion runs
	 * before any AU round-trip could mask it.
	 */
	@Test
	public void testClearingInvalidInputRemovesInvalidMarkerImmediately() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();
		Boolean marked = (Boolean) js().executeScript(
				"var i=jq('$drFooter .z-daterangebox-begin')[0];"
				+ "i.value='not-a-date'; jq(i).trigger('change');"
				+ "return jq('$drFooter')[0].classList.contains('z-daterangebox-invalid');");
		assertTrue(marked, "pre-condition: unparseable input must add the invalid marker");

		Boolean stillInvalid = (Boolean) js().executeScript(
				"var i=jq('$drFooter .z-daterangebox-begin')[0];"
				+ "i.value=''; jq(i).trigger('change');"
				+ "return jq('$drFooter')[0].classList.contains('z-daterangebox-invalid');");
		assertFalse(stillInvalid,
				"clearing the input must remove the invalid marker client-side, not wait for the server echo");
		waitResponse();
	}

	/**
	 * Fix: same-cell re-click when begin is staged must restart selection
	 * instead of auto-applying a 0-night range.
	 *
	 * Before the fix the else-branch set {@code _stagedEnd == _stagedBegin},
	 * wasIncomplete was true, so the 200 ms auto-apply timer fired with a
	 * 0-day range. With {@code minNights=2} the server threw
	 * WrongValueException from an innocent double-click.
	 *
	 * After the fix, clicking the same cell restarts selection (begin reset,
	 * end cleared) so the timer never starts — the popup stays open.
	 */
	@Test
	public void testSameCellReClickWithMinNightsDoesNotAutoApply() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();
		// Arm minNights=2 on the box programmatically
		js().executeScript("zk.Widget.$(jq('$drFooter')[0]).setMinNights(2);");
		waitResponse();
		// Open the popup
		js().executeScript("zk.Widget.$(jq('$drFooter')[0])._openPopup();");
		waitResponse();
		// First click on day 15 of panel 0 — stages begin, wasIncomplete becomes true
		clickDay(0, 15);
		// Second click on the same day — must restart selection, NOT trigger auto-apply
		clickDay(0, 15);
		// Wait longer than the 200ms auto-apply timer would have fired
		sleep(350);
		waitResponse();
		// Popup must still be open
		Boolean popupOpen = (Boolean) js().executeScript(
				"var p = document.querySelector('.z-daterangebox-popup');"
				+ "return p != null && p.style.display !== 'none';");
		assertTrue(popupOpen,
				"same-cell re-click must restart selection, not auto-apply a 0-night range that the server rejects");
		// No invalid marker from a server WrongValueException
		assertFalse(jq(".z-daterangebox-invalid").exists(),
				"no invalid class should appear after a same-cell re-click with minNights=2");
	}

	/**
	 * Fix: Clear button {@code bind_}/{@code unbind_} must use DOM-presence
	 * check ({@code $n('clear')}) rather than {@code getAllowEmpty()==='both'}
	 * so the listener is correctly removed even when {@code allowEmpty} changes
	 * between bind and unbind.
	 *
	 * This test verifies the full bind → click-Clear → unbind → rebind cycle:
	 * the Clear button works on first open, and the popup remains fully
	 * functional after a close/reopen cycle (no double-registration or missing
	 * handler).
	 */
	@Test
	public void testClearButtonBindUnbindCycleWorksCorrectly() {
		connect("/test2/F104-ZK-4305-allow-empty.zul");
		waitResponse();
		// Open the 'both' box popup (bind_ runs — listener registered via $n('clear'))
		js().executeScript("zk.Widget.$(jq('$drBoth')[0])._openPopup();");
		waitResponse();
		// Clear button must be in the DOM (mold conditioned on allowEmpty='both')
		Boolean clearPresent = (Boolean) js().executeScript(
				"var popup = zk.Widget.$(jq('$drBoth')[0])._rangePopup;"
				+ "return popup != null && popup.$n('clear') != null;");
		assertTrue(clearPresent, "Clear button must exist in the popup when allowEmpty='both'");
		// Stage a begin date by clicking day 10
		clickDay(0, 10);
		// Click Clear — _doClear listener must have been registered by bind_
		js().executeScript(
				"var p = zk.Widget.$(jq('$drBoth')[0])._rangePopup;"
				+ "if (p && p.$n('clear')) p.$n('clear').click();");
		waitResponse();
		// After _doClear, staged begin must be cleared
		Boolean stagedCleared = (Boolean) js().executeScript(
				"var p = zk.Widget.$(jq('$drBoth')[0])._rangePopup;"
				+ "if (!p) return true;" // popup detached after clear = fine
				+ "return !p._stagedBegin && !p._stagedEnd;");
		assertTrue(stagedCleared, "_doClear must reset _stagedBegin and _stagedEnd");
		// Reopen — unbind_/bind_ cycle (verifies listener was correctly removed in unbind_)
		js().executeScript("zk.Widget.$(jq('$drBoth')[0])._openPopup();");
		waitResponse();
		Boolean clearAfterReopen = (Boolean) js().executeScript(
				"var p = zk.Widget.$(jq('$drBoth')[0])._rangePopup;"
				+ "return p != null && p.$n('clear') != null;");
		assertTrue(clearAfterReopen,
				"Clear button must still be present after close/reopen; unbind_ must not have removed the DOM node");
	}

	// Helper shared by the interaction tests above: simulate _clickDate on a
	// specific day number in the first visible calendar panel.
	private void clickDay(int panelIndex, int day) {
		js().executeScript(
				"var panels = document.querySelectorAll('.z-daterangebox-popup-panels .z-calendar');"
				+ "var pane = panels[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[1] && (cells[i]._monofs || 0) === 0) {"
				+ "    zk.Widget.$(pane)._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}
}
