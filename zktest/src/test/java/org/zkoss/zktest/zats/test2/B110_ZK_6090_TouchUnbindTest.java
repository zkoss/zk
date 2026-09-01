/* B110_ZK_6090_TouchUnbindTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 10:20:00 CST 2026, Created by peakerlee

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
 * Unbinding a widget on a touch device has to undo whatever the touch gestures set up,
 * including the parts that were only set up once a finger was already down.
 *
 * <p>The widgets are unbound from the client without touching the DOM, which is what a
 * container doing render-on-demand does to its children: the node stays in the page, so
 * anything left on it is counted instead of being thrown away with the node.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_TouchUnbindTest extends WebDriverTestCase {

	/** A plain desktop user agent: only the touch support is emulated, not a mobile device. */
	private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
			+ " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options.setExperimentalOption("mobileEmulation", Map.of(
				"userAgent", DESKTOP_USER_AGENT,
				"deviceMetrics", Map.of("width", 1280, "height", 900, "pixelRatio", 1, "touch", true)));
	}

	/**
	 * zk.Swipe adds its move and end handlers only once the gesture has started, so
	 * destroying it while a finger is down has to take those off as well.
	 */
	@Test
	public void testUnbindDuringGesture() {
		connectAndWaitForGestures();
		assertEquals("touchstart", getEval("window.zk6090.startEvt"),
				"zk.Swipe listens to mouse events here, the touch emulation did not take");
		assertEquals("true", getEval("'' + !!window.zk6090.swiper._swipe"), "onSwipe should be bound");

		String base = getEval("window.zk6090.swiperBase");
		getEval("(window.zk6090Touch(window.zk6090.swiperNode, 'touchstart'), 1)");
		String started = snap("swiper");
		assertNotEquals(base, started, "the gesture should have added its move and end handlers");

		getEval("(window.zk6090.swiper.unbind(), 1)");
		assertEquals(base, snap("swiper"),
				"destroying the swipe must take the in-flight gesture handlers off, they were " + started);

		// a leftover move handler throws on the next finger move: destroy() already
		// dropped the options it reads
		getEval("(window.zk6090Touch(window.zk6090.swiperNode, 'touchmove'), 1)");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "a leftover swipe handler threw");
		assertNoJSError();
	}

	/**
	 * The touch handlers were added because the widget had a listener; they still have to
	 * come off once it no longer has one.
	 */
	@Test
	public void testUnbindAfterListenersRemoved() {
		connectAndWaitForGestures();
		String base = getEval("window.zk6090.tapperBase");
		String bound = snap("tapper");
		assertNotEquals(base, bound, "the tap handlers should be on the node by now");

		getEval("(window.zk6090Drop(), 1)");
		assertEquals("false", getEval("'' + window.zk6090.tapper.isListen('onDoubleClick')"),
				"the onDoubleClick listener should be gone");
		assertEquals("false", getEval("'' + window.zk6090.tapper.isListen('onRightClick')"),
				"the onRightClick listener should be gone");

		getEval("(window.zk6090.tapper.unbind(), 1)");
		assertEquals(base, snap("tapper"),
				"unbinding must take the tap handlers off whether or not the listener is still there,"
						+ " they were bound as " + bound);
		assertNoJSError();
	}

	/**
	 * A tap-hold that has already started arms an 800ms timer; unbinding has to cancel it,
	 * or it delivers onRightClick to a widget that is no longer there.
	 */
	@Test
	public void testUnbindCancelsPendingHold() {
		connectAndWaitForGestures();
		getEval("(window.zk6090Touch(window.zk6090.tapperNode, 'touchstart'), 1)");
		assertEquals("true", getEval("'' + !!window.zk6090.tapper._rightClickPending"),
				"the hold timer never started, nothing was tested");

		getEval("(window.zk6090.tapper.unbind(), 1)");
		sleep(1500); // the hold fires 800ms after the touch started
		assertEquals("0", getEval("'' + window.zk6090.rightClicks"),
				"a hold armed before the unbind fired onRightClick on an unbound widget");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "the pending hold threw");
		assertNoJSError();
	}

	private void connectAndWaitForGestures() {
		connect();
		waitResponse();
		assertEquals("true", getEval("String(navigator.maxTouchPoints > 0)"), "touch should be emulated");
		assertEquals("true", getEval("String(!!zk.touchEnabled)"), "zk.touchEnabled should be on");
		sleep(1000); // the touch handlers are added 300ms after the widget is bound
	}

	private String snap(String which) {
		return getEval("window.zk6090Snap(window.zk6090." + which + "Node)");
	}
}
