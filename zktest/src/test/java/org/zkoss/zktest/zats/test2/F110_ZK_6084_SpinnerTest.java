/* F110_ZK_6084_SpinnerTest.java

	Purpose:
		WCAG 2.5.2 Pointer Cancellation (Level A) for Spinner / Doublespinner
		and Timebox.

	Description:
		The za11y module overrides Spinner.shallStepOnRelease_() to true, so
		_btnDown only records which arrow was pressed and _btnUp performs the
		step.

	History:
		Tue Aug 19 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class F110_ZK_6084_SpinnerTest extends WebDriverTestCase {

	private static final String SPINNER = "z-spinner";
	private static final String DOUBLESPINNER = "z-doublespinner";
	private static final String TIMEBOX = "z-timebox";

	/** Longer than the 200ms interval _startAutoIncProc used to repeat at. */
	private static final long HOLD = 900;

	/** Presses the up-arrow of the given spinner and keeps holding it. */
	private void pressAndHoldUpArrow(String id, String zcls, long holdMillis) {
		getActions().moveToElement(toElement(jq("$" + id + " ." + zcls + "-up")))
				.clickAndHold()
				.pause(Duration.ofMillis(holdMillis))
				.perform();
	}

	private String valueOf(String id, String zcls) {
		return jq("$" + id + " ." + zcls + "-input").val();
	}

	/**
	 * Puts the caret at {@code pos} in the timebox input, then presses and holds its
	 * up-arrow. getTimeHandler() reads the caret to choose hours/minutes/seconds, so the
	 * step is only predictable once the caret is placed.
	 */
	private void caretThenPressAndHold(String id, int pos, long holdMillis) {
		final String sel = "jq('$" + id + " ." + TIMEBOX + "-input')[0]";
		((JavascriptExecutor) driver).executeScript(
				"var n = " + sel + "; n.focus(); zk(n).setSelectionRange(" + pos + "," + pos + ");");
		waitResponse();
		pressAndHoldUpArrow(id, TIMEBOX, holdMillis);
	}

	// ----- one gesture per widget: inert while held, one step on release -----

	@Test
	public void spinnerInertWhileHeldThenStepsOnceOnRelease() {
		connect();
		waitResponse();
		assertEquals("5", valueOf("sp", SPINNER), "precondition");

		pressAndHoldUpArrow("sp", SPINNER, HOLD);
		assertEquals("5", valueOf("sp", SPINNER),
				"WCAG 2.5.2: holding the arrow must neither step nor auto-repeat");

		getActions().release().perform();
		waitResponse();
		assertEquals("6", valueOf("sp", SPINNER),
				"release over the arrow steps exactly once, however long it was held");
	}

	@Test
	public void doublespinnerInertWhileHeldThenStepsOnceOnRelease() {
		connect();
		waitResponse();
		// compared numerically: the exact rendering of a Doublespinner value
		// depends on its format / fixed digits, which is not what this asserts
		assertEquals(1.5, doubleValueOf("dsp"), 0.0001, "precondition");

		pressAndHoldUpArrow("dsp", DOUBLESPINNER, HOLD);
		assertEquals(1.5, doubleValueOf("dsp"), 0.0001,
				"WCAG 2.5.2: holding the arrow must neither step nor auto-repeat");

		getActions().release().perform();
		waitResponse();
		assertEquals(2.0, doubleValueOf("dsp"), 0.0001, "one step of 0.5 on release");
	}

	private double doubleValueOf(String id) {
		return Double.parseDouble(valueOf(id, DOUBLESPINNER));
	}

	@Test
	public void timeboxInertWhileHeldThenStepsOnceOnRelease() {
		connect();
		waitResponse();
		assertEquals("10:20:30", valueOf("tb", TIMEBOX), "precondition");

		caretThenPressAndHold("tb", 0, HOLD);
		assertEquals("10:20:30", valueOf("tb", TIMEBOX),
				"WCAG 2.5.2: holding the arrow must neither step nor auto-repeat");

		getActions().release().perform();
		waitResponse();
		assertEquals("11:20:30", valueOf("tb", TIMEBOX),
				"release over the arrow steps the caret's field exactly once");
	}

	// ----- pressing the wrong arrow can be abandoned by releasing elsewhere -----

	@Test
	public void releaseOffButtonDoesNotStep() {
		connect();
		waitResponse();

		getActions().moveToElement(toElement(jq("$sp ." + SPINNER + "-up")))
				.clickAndHold()
				.moveByOffset(0, 150)
				.release()
				.perform();
		waitResponse();
		assertEquals("5", valueOf("sp", SPINNER),
				"pressing the arrow then releasing elsewhere must leave the value untouched");
	}

}
