/* F110_ZK_6084_FloatDismissTest.java

		Purpose:

		Description:

		History:
				Tue Aug 25 2026, Created by peggypeng

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
 * WCAG 2.5.2: pressing outside an open float must not dismiss it; the dismissal happens on
 * release, and a release back inside the float aborts it.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_FloatDismissTest extends WebDriverTestCase {
	private static final String COMBO_POPUP = ".z-combobox-popup";
	private static final String MENU_POPUP = "$mp";

	private void openCombo() {
		click(jq("$cb .z-combobox-button"));
		waitResponse();
		assertTrue(jq(COMBO_POPUP).isVisible(), "precondition: the dropdown is open");
	}

	private void pressAndHold(String target) {
		getActions().moveToElement(toElement(jq(target)))
				.clickAndHold().pause(Duration.ofMillis(300)).perform();
	}

	private void pressThenRelease(String target, String popup) {
		pressAndHold(target);
		assertTrue(jq(popup).isVisible(), "a press must not dismiss the float on the down-event");

		getActions().release().perform();
		waitResponse();
		assertFalse(jq(popup).isVisible(), "the dismissal must still happen, on the up-event");
	}

	@Test
	public void comboboxSurvivesPressOnPlainArea() {
		connect();
		waitResponse();
		openCombo();

		pressThenRelease("$empty", COMBO_POPUP);
	}

	@Test
	public void menupopupSurvivesPressOnPlainArea() {
		connect();
		waitResponse();
		click(jq("$mn"));
		waitResponse();
		assertTrue(jq(MENU_POPUP).isVisible(), "precondition: the menu is open");

		pressThenRelease("$empty", MENU_POPUP);
	}

	@Test
	public void releaseInsidePopupDoesNotDismiss() {
		connect();
		waitResponse();
		openCombo();

		pressAndHold("$empty");
		getActions().moveToElement(toElement(jq(COMBO_POPUP + " .z-comboitem"))).release().perform();
		waitResponse();

		assertTrue(jq(COMBO_POPUP).isVisible(),
				"a press released back inside the float must abort the dismissal");
	}

	@Test
	public void pressOnFocusableTargetDoesNotDismiss() {
		connect();
		openCombo();
		pressThenRelease("$outsideInput", COMBO_POPUP);
	}

	@Test
	public void keyboardFocusStillDismisses() {
		connect();
		openCombo();
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertFalse(jq(COMBO_POPUP).isVisible(), "keyboard focus must not need a pointer snapshot");
	}

	@Test
	public void autodropClosesWithoutAnyPress() {
		connect();
		getActions().moveToElement(toElement(jq("$autoFirst"))).perform();
		waitResponse();
		assertTrue(jq("$autoFirstPopup").isVisible());
		getActions().moveToElement(toElement(jq("$empty"))).perform();
		sleep(400);
		assertFalse(jq("$autoFirstPopup").isVisible(), "hover-out must close without a preceding press");
	}

	@Test
	public void autodropSwitchesAfterAnEarlierClick() {
		connect();
		click(jq("$empty"));
		getActions().moveToElement(toElement(jq("$autoFirst"))).perform();
		waitResponse();
		assertTrue(jq("$autoFirstPopup").isVisible());
		getActions().moveToElement(toElement(jq("$autoSecond"))).perform();
		waitResponse();
		assertFalse(jq("$autoFirstPopup").isVisible());
		assertTrue(jq("$autoSecondPopup").isVisible());
	}

	@Test
	public void mouseupDoesNotRequireClick() {
		connect();
		openCombo();
		getEval("(function(){var n = zk.Widget.$('$empty').$n();"
				+ "n.dispatchEvent(new MouseEvent('mousedown', {bubbles: true, button: 0, buttons: 1}));"
				+ "return 'ok';})()");
		assertTrue(jq(COMBO_POPUP).isVisible());
		getEval("(function(){zk.Widget.$('$empty').$n()"
				+ ".dispatchEvent(new MouseEvent('mouseup', {bubbles: true, button: 0}));"
				+ "return 'ok';})()");
		waitResponse();
		assertFalse(jq(COMBO_POPUP).isVisible(), "mouseup without click must complete and clear the pending dismissal");
	}

	@Test
	public void popupOpenedByMouseupIsNotDismissed() {
		connect();
		getEval("(function(){zk.Widget.$('$empty').$n().addEventListener('mouseup', function(){"
				+ "zk.Widget.$('$cbOther').open();}, {once: true}); return 'ok';})()");
		click(jq("$empty"));
		waitResponse();
		assertTrue(jq("$cbOther").toWidget().is("open"),
				"the deferred notification must not close a dropdown opened by this release");
	}

	@Test
	public void disabledGatePreservesDownEventDismissal() {
		connect();
		getEval("(function(){zk.Widget.dismissFloatOnRelease = false; return 'ok';})()");
		openCombo();
		try {
			pressAndHold("$empty");
			assertFalse(jq(COMBO_POPUP).isVisible(), "without the gate, dismissal stays on mousedown");
		} finally {
			getActions().release().perform();
		}
	}
}
