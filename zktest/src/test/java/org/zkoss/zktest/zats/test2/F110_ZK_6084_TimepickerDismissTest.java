/* F110_ZK_6084_TimepickerDismissTest.java

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
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.2 for Timepicker: with za11y an outside press closes the list only on release, and a
 * release back on the list keeps it open. A browsed time is still committed on the press by the
 * input's own blur, as in any input, so these tests check only the list and that it commits once.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_TimepickerDismissTest extends WebDriverTestCase {
	private static final String POPUP = ".z-timepicker-popup";

	private String events() {
		return jq("$events").text();
	}

	private void openAndBrowse() {
		click(jq("$tp .z-timepicker-button"));
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "precondition: the list is open");
		getActions().sendKeys(toElement(jq("$tp .z-timepicker-input")), Keys.ARROW_DOWN).perform();
		assertFalse(jq("$tp .z-timepicker-input").val().isEmpty(), "precondition: browsing wrote a time into the input");
		assertEquals("", events(), "precondition: browsing does not commit yet");
	}

	@Test
	public void pressOutsideClosesOnlyOnRelease() {
		connect();
		openAndBrowse();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a press outside must not close it");
		getActions().release().perform();
		waitResponse();
		assertFalse(jq(POPUP).isVisible(), "releasing outside closes it");
		assertEquals("C", events(), "and commits once");
	}

	@Test
	public void releaseBackOnTheListKeepsItOpen() {
		connect();
		openAndBrowse();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq(POPUP))).release().perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a release back inside aborts the dismissal");
	}
}
