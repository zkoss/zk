/* F110_ZK_6084_DateboxDismissTest.java

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
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * WCAG 2.5.2 for Datebox: closing the calendar commits the date being typed, so with za11y an
 * outside press closes it only on release, and a release back on the calendar keeps it open,
 * including on the time box that a format with a time adds to the calendar popup.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_DateboxDismissTest extends WebDriverTestCase {
	// An open calendar popup is moved to the end of <body>, so find it by id, not under its datebox.
	private JQuery popup(String datebox) {
		return jq("#" + jq("$" + datebox).toWidget().uuid() + "-pp");
	}

	// Typing closes an open calendar, so type first and then open it from the keyboard.
	private void typeThenOpen(String datebox, String text, String events) {
		getActions().click(toElement(jq("$" + datebox + " .z-datebox-input"))).sendKeys(text).perform();
		getActions().keyDown(Keys.ALT).sendKeys(Keys.ARROW_DOWN).keyUp(Keys.ALT).perform();
		waitResponse();
		assertTrue(popup(datebox).isVisible(), "precondition: the calendar is open");
		assertEquals(text, jq("$" + datebox + " .z-datebox-input").val(), "precondition: opening keeps the typed date");
		assertEquals("", jq("$" + events).text(), "precondition: typing does not commit yet");
	}

	@Test
	public void pressOutsideCommitsOnlyOnRelease() {
		connect();
		typeThenOpen("db", "2026/01/15", "events");
		getActions().clickAndHold(toElement(jq("body"))).perform();
		waitResponse();
		assertTrue(popup("db").isVisible(), "a press outside must not close it");
		assertEquals("", jq("$events").text(), "nothing is committed on the down-event");
		getActions().release().perform();
		waitResponse();
		assertFalse(popup("db").isVisible(), "releasing outside closes it");
		assertEquals("C", jq("$events").text(), "and commits once");
	}

	@Test
	public void releaseBackOnTheCalendarKeepsItOpen() {
		connect();
		typeThenOpen("db", "2026/01/15", "events");
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(popup("db"))).release().perform();
		waitResponse();
		assertTrue(popup("db").isVisible(), "a release back inside aborts the dismissal");
		assertEquals("", jq("$events").text());
	}

	// The time box is a sibling of the calendar inside the popup, not part of it.
	@Test
	public void releaseBackOnTheTimeBoxKeepsItOpen() {
		connect();
		typeThenOpen("dbt", "2026/01/15 10:30", "timeEvents");
		JQuery timebox = popup("dbt").find(".z-timebox");
		assertTrue(timebox.isVisible(), "precondition: the calendar shows its time box");
		getActions().clickAndHold(toElement(jq("body"))).perform();
		getActions().moveToElement(toElement(timebox)).release().perform();
		waitResponse();
		assertTrue(popup("dbt").isVisible(), "a release back on the time box aborts the dismissal");
		assertEquals("", jq("$timeEvents").text());
	}
}
