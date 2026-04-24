/* B110_ZK_6090_SpinnerTest.java

	Purpose:

	Description:

	History:
		Thu Aug 20 16:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A spinner listens to the mouse button going up on the whole document while its arrow is held
 * down, so unbinding it has to take that listener away. Doublespinner and Timebox reuse the same
 * arrows and leak the same way.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_SpinnerTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	@Test
	public void test() {
		connect();
		holdTheArrowUntilItIsRemoved("spinner", "sp", "spCtrl", ".z-spinner-up");
		holdTheArrowUntilItIsRemoved("doublespinner", "dsp", "dspCtrl", ".z-doublespinner-up");
		holdTheArrowUntilItIsRemoved("timebox", "tb", "tbCtrl", ".z-timebox-up");
	}

	private void holdTheArrowUntilItIsRemoved(String name, String id, String controlId, String upArrow) {
		int before = bodyMouseupListeners();

		// the control widget shows the listener really is added on the way down and taken away on
		// the way up, otherwise the run below would prove nothing
		getActions().clickAndHold(toElement(jq("$" + controlId).find(upArrow))).perform();
		assertEquals(before + 1, bodyMouseupListeners(),
				"holding the " + name + " arrow should register the document listener");
		getActions().release().perform();
		assertEquals(before, bodyMouseupListeners(),
				"releasing the " + name + " arrow should take the document listener away");

		getActions().clickAndHold(toElement(jq("$" + id).find(upArrow))).perform();
		// onChanging closes the window while the mouse button is still down
		for (int i = 0; i < 50 && jq("$" + id).exists(); i++)
			sleep(100);
		assertFalse(jq("$" + id).exists(), "the " + name + " window should be closed while the arrow is held");
		int leaked = bodyMouseupListeners();

		getActions().release().perform();
		sleep(200);

		// ZK-6090 threw "Node with btn is not found!" from the leftover document listener
		assertEquals("0", getEval("window.zk6090Errors.length"),
				"releasing the " + name + " arrow threw: " + getEval("window.zk6090Errors.join(' | ')"));
		assertNoAnyError();
		assertEquals(before, leaked, "unbinding the " + name + " should take the document listener away");
	}

	private int bodyMouseupListeners() {
		return Integer.parseInt(getEval(
				"String(((jq._data(document.body, 'events') || {}).mouseup || []).length)"));
	}
}
