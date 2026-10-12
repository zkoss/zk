/* F110_ZK_6084_ConfirmpopupDismissMobileTest.java

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

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.2 for Confirmpopup on touch: a tap outside cancels on touchend, not touchstart, and a
 * touch that scrolls cancels nothing.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_ConfirmpopupDismissMobileTest extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad Mini"));
	}

	private Actions finger() {
		return getActions().setActivePointer(PointerInput.Kind.TOUCH, "finger");
	}

	private void openConfirm() {
		connect("/test2/F110-ZK-6084-ConfirmpopupDismiss.zul");
		finger().moveToElement(toElement(jq("$open"))).click().perform();
		waitResponse();
		assertTrue(jq("$cp").isVisible(), "precondition: the confirmation is open");
	}

	@Test
	public void tapOutsideCancelsOnlyOnRelease() {
		openConfirm();
		// A touch press cannot span two perform() calls, so record the state at touchstart here.
		getEval("(function(){window.__openAtPress = null;"
				+ "jq(document).on('zmousedown', function() {"
				+ "  window.__openAtPress = zk.Widget.$('$cp').isVisible();});"
				+ "return 'ok';})()");
		finger().moveToElement(toElement(jq("body"))).clickAndHold()
				.pause(Duration.ofMillis(300)).release().perform();
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("String(window.__openAtPress)")),
				"touchstart must not answer the confirmation");
		assertFalse(jq("$cp").isVisible(), "touchend cancels it");
		assertEquals("C", jq("$events").text());
	}

	@Test
	public void aScrollingTouchCancelsNothing() {
		openConfirm();
		finger().moveToElement(toElement(jq("body"))).clickAndHold()
				.moveByOffset(0, 50).release().perform();
		waitResponse();
		assertTrue(jq("$cp").isVisible(), "a scroll is not a tap outside");
		assertEquals("", jq("$events").text());
	}
}
