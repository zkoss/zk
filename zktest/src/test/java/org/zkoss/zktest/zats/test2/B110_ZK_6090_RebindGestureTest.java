/* B110_ZK_6090_RebindGestureTest.java

	Purpose:

	Description:

	History:
		Mon Sep 01 14:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Widget.bind_ defers the swipe/double-tap/tap-hold binding by 300ms, and none of the three bind
 * methods is idempotent. A timer left over from an earlier binding of the same widget therefore
 * registers a second set of gesture handlers, and two double-tap handlers make a single tap report
 * itself as a double click.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6090_RebindGestureTest extends WebDriverTestCase {

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

		// the CE gesture path only exists when touch is on
		assertEquals("true", jq("$touchEnabled").text(), "zk.touchEnabled");

		// the page rerenders the div 50ms after mount, inside the 300ms window
		assertEquals("true", getEval("String(!!window.zk6090RG.rebound)"),
				"the div should have been rerendered while the first timer was pending");

		// wait past both 300ms lazy gesture-init timers
		sleep(1500);

		// one binding pass puts two touchstart handlers on the node (bindDoubleTap_ and the
		// zk.Swipe constructor) and one touchend (bindDoubleTap_); bindTapHold_ registers nothing
		// without onRightClick or a context menu.
		// ZK-6090: unbind_ did not cancel the pending timer, so it ran against the new binding and
		// doubled both counts, which makes a single tap report itself as a double click
		assertEquals("2", getEval("zk6090RG.count('touchstart')"),
				"one binding pass registers exactly two touchstart handlers");
		assertEquals("1", getEval("zk6090RG.count('touchend')"),
				"one binding pass registers exactly one touchend handler");
		assertEquals("", jq("$dblLog").text(), "no gesture was performed, so nothing should be logged");
		assertNoJSError();
	}
}
