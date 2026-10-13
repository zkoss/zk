/* F110_ZK_6084_ChosenboxDismissTest.java

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
 * WCAG 2.5.2 for Chosenbox: closing the list, and the blur a press elsewhere causes 200 ms later,
 * clear the typed text. With za11y both wait for the release, and a release back on the list
 * keeps the text.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ChosenboxDismissTest extends WebDriverTestCase {
	private static final String POPUP = ".z-chosenbox-popup";
	private static final String INPUT = "$cb .z-chosenbox-input";

	private void type() {
		getActions().sendKeys(toElement(jq(INPUT)), "Ap").perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "precondition: typing opens the list");
	}

	@Test
	public void pressOutsideKeepsTheTypedTextUntilRelease() {
		connect();
		type();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse(); // outlasts the 200 ms delay of Chosenbox's blur handling
		assertTrue(jq(POPUP).isVisible(), "a press outside must not close the list");
		assertEquals("Ap", jq(INPUT).val(), "a press outside must not clear the typed text");
		getActions().release().perform();
		waitResponse();
		assertFalse(jq(POPUP).isVisible(), "releasing outside closes the list");
		assertEquals("", jq(INPUT).val(), "and clears the typed text");
	}

	@Test
	public void releaseBackOnTheListKeepsTheTypedText() {
		connect();
		type();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		getActions().moveToElement(toElement(jq(POPUP))).release().perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a release back inside aborts the dismissal");
		assertEquals("Ap", jq(INPUT).val(), "and keeps the typed text");
	}
}
