/* F110_ZK_6084_TooltipDismissTest.java

	Purpose:

	Description:

	History:
		Mon Sep 08 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 1.4.13 Dismissible: a Popup shown through the {@code tooltip} attribute appears on hover,
 * so Escape has to close it with the pointer still on the trigger, and it must stay closed until
 * the pointer leaves. za11y only - a hover-shown tooltip never takes focus, so CE's Escape
 * handling, which hangs off the focused widget's doKeyDown_, can never reach it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_TooltipDismissTest extends WebDriverTestCase {

	/** zk.tipDelay is 800ms; allow for the open animation and the onOpen round trip. */
	private static final Duration TOOLTIP_DELAY = Duration.ofMillis(1300);

	private void hoverAndWaitForTooltip(String trigger, String tooltip) {
		getActions().moveToElement(toElement(jq("$" + trigger)))
				.pause(TOOLTIP_DELAY)
				.perform();
		waitResponse();
		assertTrue(jq("$" + tooltip).isVisible(),
				tooltip + " should be shown by hovering " + trigger);
	}

	/** Sends Escape without touching the pointer, so hover is still on the trigger. */
	private void pressEscape() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	private void movePointerAway() {
		getActions().moveToElement(toElement(jq("$blurTarget"))).perform();
		waitResponse();
	}

	/**
	 * The virtual pointer survives page loads, so a test can otherwise start with the pointer
	 * already on its target, and the hover transition it needs never happens.
	 */
	private void connectAndParkPointer() {
		connect();
		waitResponse();
		movePointerAway();
	}

	// ----- Dismissible -----

	@Test
	public void escapeDismissesTooltipWithoutMovingPointer() {
		connectAndParkPointer();
		hoverAndWaitForTooltip("tooltipTrigger", "myTooltip");

		pressEscape();

		assertFalse(jq("$myTooltip").isVisible(),
				"Escape should dismiss the tooltip while the pointer is still on its trigger");
	}

	@Test
	public void dismissedTooltipStaysClosedWhileStillHovered() {
		connectAndParkPointer();
		hoverAndWaitForTooltip("tooltipTrigger", "myTooltip");
		pressEscape();
		assertFalse(jq("$myTooltip").isVisible(), "precondition: dismissed");

		getActions().pause(TOOLTIP_DELAY).perform();
		waitResponse();

		assertFalse(jq("$myTooltip").isVisible(),
				"the dismissed tooltip should not re-open on its own while the trigger is hovered");
	}

	/** 1.4.13 asks for dismissible, not permanently dismissed. */
	@Test
	public void dismissedTooltipReopensAfterLeavingAndReturning() {
		connectAndParkPointer();
		hoverAndWaitForTooltip("tooltipTrigger", "myTooltip");
		pressEscape();
		assertFalse(jq("$myTooltip").isVisible(), "precondition: dismissed");

		movePointerAway();
		hoverAndWaitForTooltip("tooltipTrigger", "myTooltip");
	}

	/** Closing getOpenTooltip() is only enough while ZK can have just one tooltip open. */
	@Test
	public void neverMoreThanOneTooltipOpen() {
		connectAndParkPointer();
		// right to left: a tooltip is drawn at the pointer and extends rightwards, so it
		// covers the next trigger along and the pointer could never reach it
		hoverAndWaitForTooltip("tipTriggerB", "tipB");

		hoverAndWaitForTooltip("tipTriggerA", "tipA");

		assertFalse(jq("$tipB").isVisible(),
				"only one tooltip may be open at a time");
	}

	// ----- controls: neither is hover-triggered content, so Escape must leave them alone -----

	@Test
	public void escapeDoesNotCloseClickOpenedPopup() {
		connectAndParkPointer();
		click(jq("$clickTrigger"));
		waitResponse();
		assertTrue(jq("$clickPopup").isVisible(), "precondition: the popup is open");

		pressEscape();

		assertTrue(jq("$clickPopup").isVisible(),
				"a click-opened popup is not hover-triggered content and must survive Escape");
	}

	@Test
	public void escapeDoesNotCloseErrorbox() {
		connectAndParkPointer();
		type(jq("$constrained"), "not-an-email");
		click(jq("$blurTarget"));
		waitResponse();
		assertTrue(jq(".z-errorbox").isVisible(), "precondition: the errorbox is open");

		pressEscape();

		assertTrue(jq(".z-errorbox").isVisible(),
				"an errorbox communicates an input error, which 1.4.13 exempts from Dismissible");
	}
}
