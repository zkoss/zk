/* B110_ZK_6090_FrozenTest.java

	Purpose:

	Description:

	History:
		Thu Aug 20 17:30:00 CST 2026, Created by peakerlee

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
 * Frozen.onSize() finishes its work in a timer, so a frozen that is unbound before the timer fires
 * has to take the timer away with it.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_FrozenTest extends WebDriverTestCase {
	/** the sizing timer is 0ms away; half a second is a wide margin */
	private static final long SETTLE_MS = 500;

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
		sleep(SETTLE_MS);
		assertNoError("mounting the page");
		// onSize only reaches the timer when the grid draws a native scrollbar
		assertEquals("true", getEval("String(!!zk.Widget.$('$g')._nativebar)"),
				"the grid should draw a native scrollbar");
		assertEquals("2", getEval("String(zk.Widget.$('$fz')._columns)"),
				"onSize() gives up unless the frozen has columns");
		assertEquals("false", getEval("String(!!zk.Widget.$('$fz')._smooth)"),
				"the smooth mold replaces onSize, so this page turns it off");

		// the widget is unreachable by id once it is unbound, so hold on to it
		eval("window.zk6090Fz = zk.Widget.$('$fz')");
		// the page sizes the frozen and drops it in the same task, leaving the timer behind
		click(jq("$drop"));
		waitResponse();
		assertEquals("false", getEval("String(!!window.zk6090Fz.desktop)"),
				"the frozen should have been dropped right after onSize");
		sleep(SETTLE_MS);
		// ZK-6090 threw "Node with cave is not found!" out of the leftover sizing timer
		assertNoError("dropping the frozen");

		// the control still sizes its frozen the ordinary way
		click(jq("$dropCtrl"));
		waitResponse();
		sleep(SETTLE_MS);
		assertEquals("true", getEval("String(!!zk.Widget.$('$fzCtrl').desktop)"),
				"the control frozen should still be bound");
		assertNoError("resizing the control");
	}

	/** The throw happens in a timer callback, so the page collects it through window.onerror. */
	private void assertNoError(String action) {
		assertEquals("0", getEval("window.zk6090Errors.length"),
				action + " threw: " + getEval("window.zk6090Errors.join(' | ')"));
		assertNoAnyError();
	}
}
