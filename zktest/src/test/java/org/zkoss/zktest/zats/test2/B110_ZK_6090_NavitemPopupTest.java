/* B110_ZK_6090_NavitemPopupTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 14:10:00 CST 2026, Created by peakerlee

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
 * A navitem of a collapsed navbar pops its label out into document.body on hover and schedules
 * the close 100ms after the pointer leaves. Unbinding inside that window has to cancel the timer
 * and bring the label back, the same way the sibling Nav does.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_NavitemPopupTest extends WebDriverTestCase {

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
		assertEquals("true", getEval("'' + !!window.zk6090.textNode"), "the navitem label should exist");

		// hover: the label is vparented into document.body
		getEval("(jq(window.zk6090.ni.$n()).trigger('mouseenter'), 1)");
		assertEquals("true", getEval("'' + !!window.zk6090.ni._isPopup"),
				"hovering a topmost item of a collapsed navbar should pop its label out");
		assertEquals("true", getEval("window.zk6090Orphaned()"),
				"the popped-out label should sit in document.body");

		// leave, then unbind inside the 100ms the close is delayed by
		getEval("(jq(window.zk6090.ni.$n()).trigger('mouseleave'), 1)");
		getEval("(window.zk6090Unbind(), 1)");

		// the unbind must close the popup itself instead of leaving it to the timer
		assertEquals("false", getEval("window.zk6090Orphaned()"),
				"unbinding must bring the popped-out label back out of document.body");
		assertEquals("false", getEval("'' + !!window.zk6090.ni._isPopup"),
				"unbinding must clear the popup flag");

		// and the timer must not fire on the unbound widget afterwards
		sleep(600); // the delayed close is 100ms
		assertEquals("", getEval("window.zk6090.errors.join('|')"),
				"the delayed close ran on an unbound navitem");
		assertNoJSError();
	}
}
