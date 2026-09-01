/* B110_ZK_6090_CoachmarkTest.java

	Purpose:

	Description:

	History:
		Thu Aug 21 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The accessibility layer of a coachmark listens for animationend on the coachmark node, and its
 * unbind has to take exactly that listener off again.
 *
 * <p>The widget is unbound and bound again from the client, which is what a container doing
 * render-on-demand does to its children: the node stays where it is, so a listener that was not
 * taken off is counted on it instead of being thrown away with the node.
 *
 * <p>The listener only exists when the accessibility layer is loaded, so the test is a no-op in
 * the NO_A11Y variant.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_CoachmarkTest extends WebDriverTestCase {

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
		if (!Boolean.parseBoolean(getEval("!!window.za11y")))
			return; // the animationend listener belongs to the accessibility layer

		assertEquals("true", getEval("(window.__w = zk.Widget.$('$cm'), window.__n = window.__w.$n(), !!window.__n)"),
				"the coachmark node should be in the page");
		assertEquals("1", animationEndListeners(),
				"binding the coachmark should add one animationend listener");

		getEval("(window.__w.unbind(), 1)");
		// ZK-6090: unbind_ passed a freshly bound function to off(), so nothing was taken off
		assertEquals("0", animationEndListeners(),
				"unbinding the coachmark should take the animationend listener off");
		// the leftover listener used to reach for the -cls node of a widget that no longer has one
		getEval("(jq(window.__n).trigger('animationend'), 1)");

		getEval("(window.__w.bind(), 1)");
		assertEquals("1", animationEndListeners(), "binding again should not stack another listener");
		assertNoJSError();
	}

	/**
	 * While a coachmark highlights its target it puts a click listener on the target's node,
	 * which belongs to another widget and outlives the coachmark. Restoring the target has to
	 * take that very listener off again.
	 */
	@Test
	public void testTargetClickListener() {
		connect();
		waitResponse();
		assertEquals("true", getEval("(window.__cm = zk.Widget.$('$cm'),"
						+ " window.__t = zk.Widget.$('$target').$n(), !!window.__t)"),
				"the target button should be in the page");
		String base = targetClickListeners();

		// the coachmark puts a modal mask over the page, so it is toggled from the client
		// rather than by clicking through the mask
		toggle(true);
		assertNotEquals(base, targetClickListeners(),
				"showing the coachmark should put a click listener on its target");

		toggle(false);
		// ZK-6090: _highlightTarget used bind() and _restoreTarget used proxy(), so the
		// off() matched nothing and every show/hide cycle stacked another handler
		assertEquals(base, targetClickListeners(),
				"hiding the coachmark must take its click listener off the target");

		toggle(true);
		toggle(false);
		assertEquals(base, targetClickListeners(),
				"a second show/hide cycle must not stack another handler either");
		assertNoJSError();
	}

	private String animationEndListeners() {
		return getEval("(function () {var e = jq._data(window.__n, 'events') || {};"
				+ "return String((e.animationend || []).length)})()");
	}

	private void toggle(boolean visible) {
		getEval("(window.__cm.setVisible(" + visible + "), 1)");
		sleep(600); // the coachmark slides in and out
	}

	private String targetClickListeners() {
		return getEval("(function () {var e = jq._data(window.__t, 'events') || {};"
				+ "return String((e.click || []).length)})()");
	}
}
