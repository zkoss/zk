/* F110_ZK_6084_ConfirmpopupDismissTest.java

		Purpose:

		Description:

		History:
				Tue Sep 29 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.2 for Confirmpopup: a press outside answers the confirmation as Cancel, so with za11y
 * the Cancel waits for the release, and a release back inside the confirmation aborts it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ConfirmpopupDismissTest extends WebDriverTestCase {
	private void openConfirm() {
		click(jq("$open"));
		waitResponse();
	}

	private String events() {
		return jq("$events").text();
	}

	@Test
	public void pressOutsideCancelsOnlyOnRelease() {
		connect();
		openConfirm();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		assertTrue(jq("$cp").isVisible(), "a press outside must not answer the confirmation");
		assertEquals("", events(), "no onCancel on the down-event");

		getActions().release().perform();
		waitResponse();
		assertFalse(jq("$cp").isVisible(), "releasing outside cancels it");
		assertEquals("C", events(), "exactly one onCancel");
	}

	@Test
	public void releaseBackInsideKeepsTheConfirmationOpen() {
		connect();
		openConfirm();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq("$cp").find("[id$=message]"))).release().perform();
		waitResponse();
		assertTrue(jq("$cp").isVisible(), "a release back inside aborts the cancel");
		assertEquals("", events());
	}

	// The press moves focus to the textbox, which fires a second onFloatUp from the focus change.
	@Test
	public void pressOnAFocusableTargetOutsideWaitsForRelease() {
		connect();
		openConfirm();
		try {
			getActions().clickAndHold(toElement(jq("$outsideInput"))).perform();
			waitResponse();
			assertTrue(jq("$cp").isVisible(), "moving focus with the press must not answer it either");
			assertEquals("", events());
		} finally {
			getActions().release().perform();
		}
		waitResponse();
		assertFalse(jq("$cp").isVisible());
		assertEquals("C", events());
	}

	@Test
	public void pressInsideDoesNotClose() {
		connect();
		openConfirm();
		click(jq("$cp").find("[id$=message]"));
		waitResponse();
		assertTrue(jq("$cp").isVisible());
		assertEquals("", events());
	}
}
