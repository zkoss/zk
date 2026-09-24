/* F110_ZK_6084_FloatDismissMobileTest.java

	Purpose:
		Verify pointer dismissal through ZK's normalized touch events.

	Description:
		WCAG 2.5.2 on touch: a tap outside an open float must dismiss it on
		touchend, not touchstart, and a scroll gesture must cancel the dismissal.

	History:
		Thu Sep 24 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class F110_ZK_6084_FloatDismissMobileTest extends WebDriverTestCase {
	private static final String POPUP = ".z-combobox-popup";

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad Mini"));
	}

	private Actions finger() {
		return getActions().setActivePointer(PointerInput.Kind.TOUCH, "finger");
	}

	private void openCombo() {
		connect("/test2/F110-ZK-6084-FloatDismiss.zul");
		finger().moveToElement(toElement(jq("$cb .z-combobox-button"))).click().perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible());
	}

	@Test
	public void outsideTapDismissesOnlyOnRelease() {
		openCombo();
		// A touch press cannot span two perform() calls - the release never reaches the
		// browser - so record the mid-gesture state here and tap in one gesture.
		getEval("(function(){window.__visibleAtPress = null;"
				+ "jq(document).on('zmousedown', function() {"
				+ "  window.__visibleAtPress = jq('" + POPUP + "').is(':visible');});"
				+ "return 'ok';})()");
		finger().moveToElement(toElement(jq("$empty"))).clickAndHold()
				.pause(Duration.ofMillis(300)).release().perform();
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("String(window.__visibleAtPress)")),
				"touchstart must not dismiss the popup");
		assertFalse(jq(POPUP).isVisible(), "touchend must dismiss the popup");
	}

	@Test
	public void movingTouchCancelsDismissalAndNextTapStillWorks() {
		openCombo();
		finger().moveToElement(toElement(jq("$empty"))).clickAndHold()
				.moveByOffset(0, 50).release().perform();
		waitResponse();
		assertTrue(jq(POPUP).isVisible(), "a scroll gesture must not dismiss the popup");
		finger().moveToElement(toElement(jq("$empty"))).click().perform();
		waitResponse();
		assertFalse(jq(POPUP).isVisible(), "a cancelled gesture must not affect the next tap");
	}
}
