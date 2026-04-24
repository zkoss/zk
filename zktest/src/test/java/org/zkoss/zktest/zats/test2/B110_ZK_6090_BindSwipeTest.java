/* B110_ZK_6090_BindSwipeTest.java

        Purpose:

        Description:

        History:
                Thu Aug 20 10:12:38 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B110_ZK_6090_BindSwipeTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		// touch capability without a mobile user agent, so zk.touchEnabled is on
		// while zk.tabletUIEnabled (and the EE tablet molds) stay off
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 1920);
		deviceMetrics.put("height", 1080);
		deviceMetrics.put("pixelRatio", 1.0);
		deviceMetrics.put("touch", true);
		deviceMetrics.put("mobile", false);
		Map<String, Object> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceMetrics", deviceMetrics);
		ChromeOptions options = super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", mobileEmulation);
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		// touch gestures must be on, tablet molds must stay off (CE-only path)
		assertEquals("true", jq("$touchEnabled").text(),
				"zk.touchEnabled, navigator.maxTouchPoints=" + jq("$maxTouch").text());
		assertEquals("false", jq("$tabletUI").text());

		// wait past the 300ms lazy gesture-init timer opened by Widget.bind_
		sleep(1500);

		assertEquals("true", getEval("!!window.zk6090.boundBeforeDetach"),
				"the div must have been bound before it was detached");
		assertEquals("true", getEval("!!window.zk6090.unboundAfterDetach"),
				"the div must have been unbound by the detach");

		// the ordering is observed, not inferred from the sleep above: the keeper's
		// bindSwipe_ marks when the 300ms timers fired, and the detach must precede it
		int keeperSeq = Integer.parseInt(getEval("window.zk6090.keeperSeq"));
		int detachSeq = Integer.parseInt(getEval("window.zk6090.detachSeq"));
		assertTrue(keeperSeq > 0, "the 300ms lazy gesture-init timer never fired, nothing was tested");
		assertTrue(detachSeq > 0 && detachSeq < keeperSeq,
				"the detach must land inside the 300ms window; detachSeq=" + detachSeq
						+ " keeperSeq=" + keeperSeq);

		// the core assertion: the timer must not enter bindSwipe_ on the unbound widget
		assertEquals("0", getEval("window.zk6090.targetSeq"),
				"lazy gesture init ran on the detached widget, desktop="
						+ getEval("window.zk6090.targetDesktopAtCall"));
		assertEquals("", getEval("window.zk6090.errors.join('|')"),
				"the lazy gesture init must not run on an unbound widget");

		// and the guard must not simply disable the feature
		assertEquals("true", getEval("!!zk.Widget.$('$keeper')._swipe"),
				"a widget that stays bound must still get its zk.Swipe");
		assertEquals("true", getEval("!!zk.Widget.$('$knob')._swipe"),
				"the knob slider must still get its zk.Swipe");

		assertNoJSError();
	}
}
