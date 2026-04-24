/* B110_ZK_6090_SlideRebindTest.java

	Purpose:

	Description:

	History:
		Thu Sep 03 16:00:00 CST 2026, Created by peakerlee

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
 * Sliding a collapsed region down is client state, so a rerender draws the region collapsed again
 * while the widget still believes it is slid down. Unbinding has to drop that state, both flags of
 * it: setSlide() short-circuits on the previous value, so a stale one leaves the region unable to
 * slide at all, or sliding the wrong way.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_SlideRebindTest extends WebDriverTestCase {

	private static final int ANIMA_MS = 800;

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
		assertEquals("false,false", getEval("zk6090SlideFlags()"),
				"a collapsed region should start neither sliding nor slid");

		// the control: the same click on a fresh binding slides the region down, otherwise the
		// run below would prove nothing
		click(jq("$w").find(".z-west-collapsed"));
		sleep(ANIMA_MS);
		assertEquals("true,true", getEval("zk6090SlideFlags()"),
				"clicking the collapsed title should slide the region down");
		assertEquals("true", getEval("String(zk6090SlideShown())"),
				"the region should be shown while it is slid down");

		click(jq("$rerender"));
		sleep(ANIMA_MS);
		assertEquals("false", getEval("String(zk6090SlideShown())"),
				"the rerendered region should be drawn collapsed again");
		// ZK-6090 left both flags set here, so the next setSlide() short-circuited on the stale
		// value and the region either slid up on a collapsed DOM or did nothing at all
		assertEquals("false,false", getEval("zk6090SlideFlags()"),
				"rerendering should leave no slide state behind");

		click(jq("$w").find(".z-west-collapsed"));
		sleep(ANIMA_MS);
		assertEquals("true,true", getEval("zk6090SlideFlags()"),
				"the first click after a rerender should slide the region down again");
		assertEquals("true", getEval("String(zk6090SlideShown())"),
				"one click after a rerender should be enough to show the region");

		assertEquals("0", getEval("window.zk6090Errors.length"),
				"sliding after a rerender threw: " + getEval("window.zk6090Errors.join(' | ')"));
		assertNoJSError();
	}
}
