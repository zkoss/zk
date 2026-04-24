/* B110_ZK_6090_DomTouchTest.java

	Purpose:

	Description:

	History:
		Thu Aug 20 16:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The touch listeners a widget gets on a touch device are added to its own node, so unbinding it
 * has to take them off that very node instead of off the one it no longer has.
 *
 * <p>The widget is unbound and bound again from the client, which is what a container doing
 * render-on-demand does to its children: the node stays where it is, so the listeners left on it
 * are counted instead of being thrown away with the node.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_DomTouchTest extends WebDriverTestCase {

	/** A plain desktop user agent: only the touch support is emulated, not a mobile device. */
	private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
			+ " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		// --touch-events=enabled only defines ontouchstart, it leaves maxTouchPoints at 0
		return options.setExperimentalOption("mobileEmulation", Map.of(
				"userAgent", DESKTOP_USER_AGENT,
				"deviceMetrics", Map.of("width", 1280, "height", 900, "pixelRatio", 1, "touch", true)));
	}

	@Test
	public void test() {
		connect();
		assertEquals("true", getEval("String(navigator.maxTouchPoints > 0)"), "touch should be emulated");
		assertEquals("true", getEval("String(!!zk.touchEnabled)"), "zk.touchEnabled should be on");
		assertEquals("false", getEval("String(!!zk.tabletUIEnabled)"),
				"the tablet UI should stay off, this is a desktop user agent");

		// the touch listeners are added 300ms after the widget is bound
		sleep(1000);
		assertEquals("true", getEval("(window.__w = zk.Widget.$('$d'), window.__n = window.__w.$n(), !!window.__n)"));
		assertEquals("true", getEval("String(!!window.__w._swipe)"), "onSwipe should be bound");
		String bound = touchListeners();
		assertNotEquals("0,0,0", bound, "the touch listeners should be bound by now");

		getEval("(window.__w.unbind(), 1)");
		// ZK-6090: the unbind hooks looked the node up again after the widget had lost it, so
		// jq(undefined).off(...) removed nothing and the dead handlers stayed on the node
		assertEquals("0,0,0", touchListeners(),
				"unbinding should take the touch listeners away, they were bound as " + bound);

		// a dead handler still on the node throws as soon as it runs again, because unbindTapHold_
		// already cleared the state it needs
		getEval("(function () {var t = new Touch({identifier: 1, target: window.__n, clientX: 1, clientY: 1});"
				+ "window.__n.dispatchEvent(new TouchEvent('touchstart',"
				+ " {touches: [t], changedTouches: [t], bubbles: true})); return 1})()");
		assertEquals("0", getEval("window.zk6090Errors.length"),
				"a leftover touch listener threw: " + getEval("window.zk6090Errors.join(' | ')"));

		// binding again must not stack a second set of listeners on the same node
		getEval("(window.__w.bind(), 1)");
		sleep(1000);
		assertEquals(bound, touchListeners(), "binding again should not stack more listeners");

		click(jq("$detach"));
		waitResponse();
		assertNoAnyError();
	}

	private String touchListeners() {
		return getEval("(function () {var e = jq._data(window.__n, 'events') || {};"
				+ "return [(e.touchstart || []).length, (e.touchend || []).length,"
				+ " (e.touchmove || []).length].join(',')})()");
	}
}
