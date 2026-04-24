/* B110_ZK_6090_TimerTest.java

	Purpose:

	Description:

	History:
		Fri Aug 28 10:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A repeating zul.utl.Timer registers a zAu error handler when it plays and has to
 * unregister the very same one when it stops, otherwise every play/stop cycle piles
 * one more up in zAu.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_TimerTest extends WebDriverTestCase {

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
		waitResponse();

		assertEquals("true", getEval("window.zk6090tm.patched"),
				"the counting hook must be in place before the timer ever plays");
		assertEquals("true/false", getEval("window.zk6090tm.state()"),
				"a repeating timer that has not played yet");
		assertEquals("0", getEval("window.zk6090tm.probe()"),
				"a timer that never played must not have registered an error handler");

		getEval("(window.zk6090tm.cycle(3), 1)");
		assertEquals("true/true", getEval("window.zk6090tm.state()"),
				"the timer must end up running, otherwise the last play never happened");
		assertEquals("1", getEval("window.zk6090tm.probe()"),
				"stopping the timer must unregister the handler it registered, otherwise "
						+ "every play/stop cycle leaves one more behind in zAu");

		assertEquals("", getEval("window.zk6090tm.errors.join('|')"), "the page threw");
		assertNoJSError();
	}
}
