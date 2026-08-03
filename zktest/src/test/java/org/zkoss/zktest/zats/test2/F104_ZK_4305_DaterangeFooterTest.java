/* F104_ZK_4305_DaterangeFooterTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:41 CST 2026, Created by peakerlee

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
 * Footer structure and behaviour tests for Daterangebox (ZK-4305).
 * Covers: footer element rendering, Clear and Cancel click behaviours.
 */
public class F104_ZK_4305_DaterangeFooterTest extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void openPopup(String boxId) {
		click(jq(boxId + " .z-daterangebox-button"));
		waitResponse();
	}

	private void clickCell(String boxId, int panelIndex, int day) {
		js().executeScript(
				"var box = zk.Widget.$(jq('" + boxId + "')[0]);"
				+ "var popup = box._rangePopup;"
				+ "if (!popup || !popup.$n()) return;"
				+ "var panes = popup.$n().querySelectorAll('.z-calendar');"
				+ "var pane = panes[arguments[0]];"
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

	// ===== Footer structure =====

	/** Footer must render with Clear and Cancel buttons. */
	@Test
	public void testFooterElementsExist() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");

		assertTrue(jq(".z-daterangebox-popup-footer").exists(),
				"Footer container must be rendered");
		assertTrue(jq("button.z-daterangebox-popup-clear").exists(),
				"Clear button must be rendered inside footer");
		assertTrue(jq("button.z-daterangebox-popup-cancel").exists(),
				"Cancel button must be rendered inside footer");
	}

	/** Footer must be a child of the popup, not a sibling rendered outside it. */
	@Test
	public void testFooterInsidePopup() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");

		Boolean footerInsidePopup = (Boolean) js().executeScript(
				"var popup = document.querySelector('.z-daterangebox-popup');"
				+ "var footer = document.querySelector('.z-daterangebox-popup-footer');"
				+ "return popup != null && footer != null && popup.contains(footer);");
		assertTrue(footerInsidePopup,
				"Footer must be a descendant of the popup element");
	}

	/** Layout order: Clear is left of Cancel. */
	@Test
	public void testFooterElementOrder() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");

		// getBoundingClientRect().left returns Long (integer pixels) or Double
		// depending on the browser; use Number to avoid ClassCastException.
		double clearX = ((Number) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup-clear').getBoundingClientRect().left;")).doubleValue();
		double cancelX = ((Number) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup-cancel').getBoundingClientRect().left;")).doubleValue();
		assertTrue(clearX < cancelX,
				"Clear must appear to the left of Cancel (clear=" + clearX + ", cancel=" + cancelX + ")");
	}

	// ===== Clear button behaviour =====

	/** Clicking Clear while only begin is staged resets the range-begin highlight. */
	@Test
	public void testClearResetsStageBegin() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");
		clickCell("$drFooter", 0, 10);

		// Verify begin is staged.
		assertEquals(1, jq(".z-cell-range-begin").length(),
				"Pre-condition: begin must be staged before clicking Clear");

		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();

		assertEquals(0, jq(".z-cell-range-begin").length(),
				"Clear must remove the staged range-begin highlight");
		assertEquals(0, jq(".z-cell-range-end").length(),
				"Clear must remove any range-end highlight");
	}

	/** Clicking Clear after a full range selection resets all range highlights. */
	@Test
	public void testClearResetsFullRange() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");
		clickCell("$drFooter", 0, 10);
		clickCell("$drFooter", 0, 20);
		// Wait for auto-apply to fire and close the popup.
		sleep(400);
		waitResponse();

		// Reopen and clear.
		openPopup("$drFooter");
		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();

		assertEquals(0, jq(".z-cell-range-begin").length(),
				"Clear must drop range-begin after a committed range");
		assertEquals(0, jq(".z-cell-range-end").length(),
				"Clear must drop range-end after a committed range");

		// Inputs must be emptied. ZK components render with uuid, not user id —
		// use jq('$id') (ZK jQuery extension) to resolve the component root.
		String beginVal = (String) js().executeScript(
				"return jq('$drFooter .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drFooter .z-daterangebox-end')[0].value;");
		assertTrue(beginVal == null || beginVal.isEmpty(),
				"Clear must empty the begin input, was '" + beginVal + "'");
		assertTrue(endVal == null || endVal.isEmpty(),
				"Clear must empty the end input, was '" + endVal + "'");
	}

	/** Clear on a pre-committed box (`drPreset`, value already set in ZUL) must
	 *  also erase the input fields so the box visually resets. */
	@Test
	public void testClearErasesPrecommittedValue() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		// Verify preset value is visible. Use jq('$id') — ZK renders with uuid, not user id.
		String beginBefore = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-begin')[0].value;");
		assertFalse(beginBefore == null || beginBefore.isEmpty(),
				"Pre-condition: drPreset must have a committed begin value");

		openPopup("$drPreset");
		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();

		String beginAfter = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-begin')[0].value;");
		String endAfter = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-end')[0].value;");
		assertTrue(beginAfter == null || beginAfter.isEmpty(),
				"Clear must erase the pre-committed begin value");
		assertTrue(endAfter == null || endAfter.isEmpty(),
				"Clear must erase the pre-committed end value");
	}

	/** After Clear, the popup remains open so the user can select a new range. */
	@Test
	public void testClearKeepsPopupOpen() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");
		clickCell("$drFooter", 0, 10);

		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertFalse("none".equals(display),
				"Popup must remain open after clicking Clear");
	}

	// ===== Cancel button behaviour =====

	/** Cancel with only begin staged discards staging and closes the popup. */
	@Test
	public void testCancelDiscardsBeginStage() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");
		clickCell("$drFooter", 0, 10);

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertEquals("none", display,
				"Popup must close after clicking Cancel");

		String beginVal = (String) js().executeScript(
				"return jq('$drFooter .z-daterangebox-begin')[0].value;");
		assertTrue(beginVal == null || beginVal.isEmpty(),
				"Cancel must not commit staged begin; input must stay empty");
	}

	/** Cancel discards a partial range and does NOT change the committed value
	 *  if the box already had one (pre-committed box stays unchanged). */
	@Test
	public void testCancelPreservesPrecommittedValue() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		String beginBefore = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-begin')[0].value;");
		String endBefore = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-end')[0].value;");
		assertFalse(beginBefore == null || beginBefore.isEmpty(),
				"Pre-condition: drPreset must have a committed value");

		openPopup("$drPreset");
		// Stage a different begin.
		clickCell("$drPreset", 0, 5);

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String beginAfter = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-begin')[0].value;");
		String endAfter = (String) js().executeScript(
				"return jq('$drPreset .z-daterangebox-end')[0].value;");
		assertEquals(beginBefore, beginAfter,
				"Cancel must NOT overwrite the previously committed begin value");
		assertEquals(endBefore, endAfter,
				"Cancel must NOT overwrite the previously committed end value");
	}

	/** Cancel closes the popup (display:none). */
	@Test
	public void testCancelClosesPopup() {
		connect("/test2/F104-ZK-4305-footer.zul");
		waitResponse();

		openPopup("$drFooter");

		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Pre-condition: popup must be visible");

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertEquals("none", display,
				"Cancel must hide the popup");
	}

}
