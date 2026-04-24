/* B110_ZK_6090_SwipeStateTest.java

	Purpose:

	Description:

	History:
		Fri Aug 28 10:20:00 CST 2026, Created by peakerlee

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

/**
 * zk.Swipe keeps the gesture in progress in state shared by every instance, and only
 * the end of a gesture drops it again. Unbinding a widget takes the end handler off,
 * so the state of that gesture must not be able to reach the next one.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6090_SwipeStateTest extends WebDriverTestCase {

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

		assertEquals("true", jq("$touchEnabled").text(), "zk.touchEnabled must be on");
		assertEquals("true", getEval("window.zk6090s.canSynth"),
				"the browser must provide Touch/TouchEvent, otherwise this is not a touch gesture");

		sleep(1500); // the zk.Swipe is made 300ms after the widget is bound
		assertEquals("true", getEval("window.zk6090s.hasSwipe('aborted')"),
				"the first div never got its zk.Swipe, nothing below is tested");
		assertEquals("true", getEval("window.zk6090s.hasSwipe('tapped')"),
				"the second div never got its zk.Swipe, nothing below is tested");

		// control: the simulated gesture really drives zk.Swipe from start to server
		getEval("(window.zk6090s.swipe('aborted'), 1)");
		sleep(500);
		waitResponse();
		assertEquals("1", getEval("window.zk6090s.abortedSwipes"),
				"the simulated swipe never reached the widget, nothing below is tested");
		assertEquals("A", jq("$abortedLog").text(), "the simulated swipe never reached the server");

		// a gesture that is unbound before it ends: its start/stop is never consumed
		int idle = endHandlers();
		getEval("(window.zk6090s.gestureWithoutEnd('aborted'), 1)");
		int inGesture = endHandlers();
		assertTrue(inGesture > idle, "the gesture in progress must have added a touchend handler; "
				+ idle + " -> " + inGesture);
		getEval("(window.zk6090s.unbind('aborted'), 1)");
		assertEquals("false", getEval("window.zk6090s.hasSwipe('aborted')"),
				"unbinding must destroy the zk.Swipe");
		assertTrue(endHandlers() < inGesture,
				"destroy() must take the touchend handler off, that is what strands the gesture");

		// a plain tap has no displacement at all, so it can only look like a swipe if it
		// reads the coordinates the unbound gesture left behind
		getEval("(window.zk6090s.tap('tapped'), 1)");
		sleep(500);
		waitResponse();
		assertEquals("0", getEval("window.zk6090s.tappedSwipes"),
				"a plain tap was reported as a swipe, on the state of the unbound gesture");
		assertEquals("", jq("$tappedLog").text(), "a fake onSwipe reached the server");
		assertEquals("1", getEval("window.zk6090s.abortedSwipes"),
				"the unbound widget must not get a swipe either");

		assertEquals("", getEval("window.zk6090s.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** How many touchend handlers the first div's node carries right now. */
	private int endHandlers() {
		return Integer.parseInt(getEval("window.zk6090s.endHandlers('aborted')"));
	}
}
