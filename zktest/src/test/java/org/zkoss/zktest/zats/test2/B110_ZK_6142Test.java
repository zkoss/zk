/* B110_ZK_6142Test.java

        Purpose:

        Description:

        History:
                Tue Aug 18 10:12:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6142Test extends WebDriverTestCase {

	private static final String POPUP = ".z-daterangebox-popup";

	private boolean popupOpen() {
		return "true".equals(getEval("(function () {"
				+ "var p = document.querySelector('" + POPUP + "');"
				+ "return !!p && getComputedStyle(p).display !== 'none';"
				+ "})()"));
	}

	private String activeClass() {
		return getEval("document.activeElement ? document.activeElement.className : ''");
	}

	private void altArrow(Keys arrow) {
		getActions().keyDown(Keys.ALT).sendKeys(arrow).keyUp(Keys.ALT).perform();
		waitResponse();
	}

	/** Text of the day cell the calendar's keyboard cursor sits on. */
	private String cursorDay() {
		return getEval("(function () {"
				+ "var p = document.querySelector('" + POPUP + "');"
				+ "var c = p && p.querySelector('.z-calendar-selected');"
				+ "return c ? c.textContent.trim() : '';"
				+ "})()");
	}

	/** open() moves focus in from a setTimeout(0), so poll for it. */
	private void waitUntilFocusInsidePopup() {
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d ->
				"true".equals(getEval("(function () {"
						+ "var p = document.querySelector('" + POPUP + "');"
						+ "return !!p && p.contains(document.activeElement);"
						+ "})()")));
	}

	/** Clicking an input focuses that input and does nothing else. */
	@Test
	public void testInputClickKeepsFocusAndLeavesPopupClosed() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();

		assertFalse(popupOpen(), "clicking the begin input must not open the calendar");
		assertTrue(activeClass().contains("z-daterangebox-begin"),
				"focus must stay in the begin input, activeElement was: " + activeClass());
	}

	/** Typing after the first click must reach the input, not the popup's key handler. */
	@Test
	public void testTypingWorksOnTheFirstClick() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		getActions().sendKeys("2026/08/07").perform();
		waitResponse();

		assertEquals("2026/08/07", jq("$drb .z-daterangebox-begin").val(),
				"the typed date must land in the begin input on the first click");
	}

	/** Tabbing in must not push a keyboard user into the dialog either. */
	@Test
	public void testTabIntoInputLeavesPopupClosed() {
		connect();
		waitResponse();

		click(jq("$before"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertTrue(activeClass().contains("z-daterangebox-begin"),
				"Tab must land on the begin input, activeElement was: " + activeClass());
		assertFalse(popupOpen(), "tabbing into the box must not open the calendar");
	}

	/** Either Alt+arrow toggles, so Alt+Down must close an open calendar too. */
	@Test
	public void testAltDownClosesAnOpenPopup() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: the trigger button must open the calendar");

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: clicking an input must not close the calendar");

		altArrow(Keys.ARROW_DOWN);
		assertFalse(popupOpen(), "Alt+Down on an input must close an open calendar");
	}

	/** GUARD, not evidence: without the fix the input click already opened it. */
	@Test
	public void testAltUpOpensAClosedPopup() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		altArrow(Keys.ARROW_UP);

		assertTrue(popupOpen(), "Alt+Up on an input must open a closed calendar");
	}

	/** Tabbing straight out must clear the focus mark. */
	@Test
	public void testBlurClearsTheFocusMark() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-focused"),
				"precondition: focusing an input must mark the box focused");

		click(jq("$before"));
		waitResponse();

		assertFalse(jq("$drb").hasClass("z-daterangebox-focused"),
				"the focus mark must be gone once focus leaves the box");
		assertFalse(popupOpen(), "and no calendar may have been opened along the way");
	}

	/** Moving between the two inputs must keep the box marked focused. */
	@Test
	public void testFocusMarkSurvivesMovingBetweenInputs() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		click(jq("$drb .z-daterangebox-end"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("z-daterangebox-focused"),
				"the box must stay marked focused while focus moves begin -> end");
	}

	/** Alt+Down is the explicit keyboard gesture, and only it moves focus in. */
	@Test
	public void testAltDownOpensPopupAndMovesFocusIn() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		altArrow(Keys.ARROW_DOWN);

		assertTrue(popupOpen(), "Alt+Down on an input must open the calendar");
		waitUntilFocusInsidePopup();
	}

	/** Alt+Up must close the calendar from where focus actually sits once it
	 *  opens — inside the popup. The gesture is bound to the box, and the
	 *  calendar hands the key back up to it. */
	@Test
	public void testAltUpClosesPopup() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: the trigger button must open the calendar");
		waitUntilFocusInsidePopup();

		altArrow(Keys.ARROW_UP);
		assertFalse(popupOpen(),
				"Alt+Up must close the calendar with focus still inside the popup");
	}

	/** The same gesture from an input is a second, separate key route: the
	 *  keydown starts on the input rather than on a calendar panel. */
	@Test
	public void testAltUpFromInputClosesPopup() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: the trigger button must open the calendar");

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertTrue(popupOpen(), "precondition: clicking an input must not close the calendar");

		altArrow(Keys.ARROW_UP);
		assertFalse(popupOpen(), "Alt+Up on an input must close the calendar");
	}

	/** Arrows must move the calendar's day cursor once the popup has focus. */
	@Test
	public void testArrowMovesTheDayCursorInsidePopup() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		altArrow(Keys.ARROW_DOWN);
		assertTrue(popupOpen(), "precondition: Alt+Down must open the calendar");
		waitUntilFocusInsidePopup();
		assertEquals("1", cursorDay(),
				"precondition: the cursor starts on the first day of the panel's month");

		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();

		assertEquals("2", cursorDay(),
				"ArrowRight must move the calendar's day cursor one day on");
	}

	/** Enter must pick the cursor's day, twice over, completing the range. */
	@Test
	public void testEnterInsidePopupPicksTheRange() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		altArrow(Keys.ARROW_DOWN);
		waitUntilFocusInsidePopup();

		// cursor 1 -> 2, pick it as the range begin
		getActions().sendKeys(Keys.ARROW_RIGHT).perform();
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		// cursor 2 -> 4, pick it as the range end (auto-applies)
		getActions().sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT).perform();
		waitResponse();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();

		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d ->
				!jq("$drb .z-daterangebox-end").val().isEmpty());
		String begin = jq("$drb .z-daterangebox-begin").val();
		String end = jq("$drb .z-daterangebox-end").val();
		assertTrue(begin.endsWith("/02"),
				"Enter must commit the cursor's day as the range begin, was: " + begin);
		assertTrue(end.endsWith("/04"),
				"and the second Enter as the range end, was: " + end);
		assertEquals(begin.substring(0, 8), end.substring(0, 8),
				"both ends must sit in the panel's month, was: " + begin + " / " + end);
	}

	/** The trigger icon must not be a tab stop: an &lt;a&gt; without href is
	 *  activated by neither Enter nor Space, so Tab would stop on a dead
	 *  control. Matches zul.inp.ComboWidget's tabindex="-1" trigger. */
	@Test
	public void testTriggerIsNotATabStop() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(activeClass().contains("z-daterangebox-end"),
				"precondition: Tab must move to the end input, activeElement was: " + activeClass());

		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();

		assertFalse(activeClass().contains("z-daterangebox-button"),
				"Tab must not stop on the trigger icon, activeElement was: " + activeClass());
		assertTrue("true".equals(getEval(
						"document.activeElement === jq('$drbNoButton .z-daterangebox-begin')[0]")),
				"Tab must skip the trigger and land on the next box, activeElement was: "
						+ activeClass());
	}

	/** With no trigger button Alt+Down is the only way in, so it must work. */
	@Test
	public void testAltDownOpensBoxWithoutTriggerButton() {
		connect();
		waitResponse();

		click(jq("$drbNoButton .z-daterangebox-begin"));
		waitResponse();
		assertFalse(popupOpen(), "precondition: the click alone must not open the calendar");

		altArrow(Keys.ARROW_DOWN);
		assertTrue(popupOpen(),
				"Alt+Down must open a box that renders no trigger button");
	}

	/** The trigger button keeps working, unchanged. */
	@Test
	public void testTriggerButtonStillOpens() {
		connect();
		waitResponse();

		click(jq("$drb .z-daterangebox-button"));
		waitResponse();

		assertTrue(popupOpen(), "the trigger button must still open the calendar");
		waitUntilFocusInsidePopup();
	}
}
