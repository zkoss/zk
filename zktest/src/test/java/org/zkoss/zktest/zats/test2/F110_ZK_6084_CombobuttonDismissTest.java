/* F110_ZK_6084_CombobuttonDismissTest.java

	Purpose:

	Description:

	History:
		Fri Sep 18 2026, Created by peggypeng

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
 * WCAG 1.4.13 Dismissible: an autodrop Combobutton drops its popup on hover, so Escape has to
 * close it with the pointer still on the button, and it must stay closed until the pointer
 * leaves. za11y only - hovering focuses nothing, so {@code Combobutton.doKeyDown_}, which does
 * handle Escape, never runs for a hover-opened popup.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_CombobuttonDismissTest extends WebDriverTestCase {
	private static final String AUTO_ARROW = "$autoCb .z-combobutton-button";
	private static final String AUTO_POPUP = "$autoPopup";
	private static final String PLAIN_POPUP = "$plainPopup";
	/** Longer than the 200ms autodrop close timer, so anything that reopens has time to. */
	private static final Duration SETTLE = Duration.ofMillis(600);

	private void hoverArrow() {
		getActions().moveToElement(toElement(jq(AUTO_ARROW))).perform();
		waitResponse();
		assertTrue(jq(AUTO_POPUP).isVisible(), "hovering the arrow should drop the popup");
	}

	/** Sends Escape without touching the pointer, so the button stays hovered. */
	private void pressEscape() {
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
	}

	private void movePointerAway() {
		getActions().moveToElement(toElement(jq("$away"))).perform();
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

	@Test
	public void escapeDismissesTheHoverOpenedPopup() {
		connectAndParkPointer();
		hoverArrow();

		pressEscape();

		assertFalse(jq(AUTO_POPUP).isVisible(),
				"Escape should close the popup while the pointer is still on the button");
	}

	@Test
	public void theDismissedPopupStaysClosedWhileTheButtonIsStillHovered() {
		connectAndParkPointer();
		hoverArrow();
		pressEscape();
		assertFalse(jq(AUTO_POPUP).isVisible(), "precondition: dismissed");

		getActions().pause(SETTLE).perform();
		waitResponse();

		assertFalse(jq(AUTO_POPUP).isVisible(),
				"the dismissed popup should not drop again on its own while the arrow is hovered");
	}

	/** 1.4.13 asks for dismissible, not permanently dismissed. */
	@Test
	public void theDismissedPopupDropsAgainAfterLeavingAndReturning() {
		connectAndParkPointer();
		hoverArrow();
		pressEscape();
		assertFalse(jq(AUTO_POPUP).isVisible(), "precondition: dismissed");

		movePointerAway();
		hoverArrow();
	}

	/**
	 * Opened from the server so nothing focuses the Combobutton: a click would let CE's own
	 * Escape handling close it, leaving za11y's listener untested.
	 */
	@Test
	public void escapeLeavesAPopupThatHoverDidNotOpen() {
		connectAndParkPointer();
		click(jq("$openPlain"));
		waitResponse();
		assertTrue(jq(PLAIN_POPUP).isVisible(), "precondition: the click-only popup is open");

		pressEscape();

		assertTrue(jq(PLAIN_POPUP).isVisible(),
				"a popup that was not revealed by hover is not 1.4.13 content and must survive Escape");
	}
}
