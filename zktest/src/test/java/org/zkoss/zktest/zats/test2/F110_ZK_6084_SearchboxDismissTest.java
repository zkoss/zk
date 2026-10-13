/* F110_ZK_6084_SearchboxDismissTest.java

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
 * WCAG 2.5.2 for Searchbox: closing the list commits the item picked in it, so with za11y an
 * outside press closes it only on release, and a release back on the list keeps it open.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_SearchboxDismissTest extends WebDriverTestCase {
	private static final String POPUP = ".z-searchbox-popup";

	private String events() {
		return jq("$events").text();
	}

	private void openAndPick() {
		click(jq("$sb"));
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "precondition: the list is open");
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "precondition: picking an item keeps the list open");
		assertEquals("", events(), "precondition: picking does not commit yet");
	}

	@Test
	public void pressOutsideCommitsOnlyOnRelease() {
		connect();
		openAndPick();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a press outside must not close it");
		assertEquals("", events(), "nothing is committed on the down-event");
		getActions().release().perform();
		waitResponse();
		assertFalse(jq(POPUP).isVisible(), "releasing outside closes it");
		assertEquals("S", events(), "and commits once");
	}

	@Test
	public void releaseBackOnTheListKeepsItOpen() {
		connect();
		openAndPick();
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(jq(POPUP))).release().perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a release back inside aborts the dismissal");
		assertEquals("", events());
	}
}
