/* B110_ZK_6039Test.java

	Purpose:

	Description:

	History:
		Mon Feb 09 15:26:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6039Test extends WebDriverTestCase {

	/** maximizable=true, maximized=true -- must fill the container. */
	private static final String[] MAXIMIZED = { "winNoneMaximized", "winTitleMaximized", "winCaptionMaximized",
			"pnlNoneMaximized", "pnlTitleMaximized", "pnlCaptionMaximized" };
	/** maximized=false (with or without maximizable) -- must keep its natural height. */
	private static final String[] NOT_MAXIMIZED = { "winNoneOff", "winNoneMaxable", "winTitleMaxable", "pnlNoneOff",
			"pnlNoneMaxable", "pnlTitleMaxable" };
	/** no title and no caption -- the mold renders no maximize button (ZK-6039). */
	private static final String[] WITHOUT_HEADER = { "winNoneOff", "winNoneMaxable", "winNoneMaximized", "pnlNoneOff",
			"pnlNoneMaxable", "pnlNoneMaximized" };
	/** with a title or a caption -- the maximize button is rendered. */
	private static final String[] WITH_HEADER = { "winTitleMaxable", "winTitleMaximized", "winCaptionMaximized",
			"pnlTitleMaxable", "pnlTitleMaximized", "pnlCaptionMaximized" };

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
		// ZK-6039 threw "Node with max is not found!" and aborted the whole mount
		assertNoAnyError();
		waitResponse();

		for (String id : MAXIMIZED)
			assertMaximized(id, true);
		for (String id : NOT_MAXIMIZED)
			assertMaximized(id, false);

		for (String id : WITHOUT_HEADER)
			assertFalse(maximizeButton(id).exists(), id + " should render no maximize button");
		for (String id : WITH_HEADER) {
			assertTrue(maximizeButton(id).exists(), id + " should render a maximize button");
			// where the button exists, it reflects the maximized state
			assertEquals(Arrays.asList(MAXIMIZED).contains(id), isButtonMaximized(id),
					id + " maximize button state");
		}

		// EE za11y augments the same setter, so keep its labelling covered here too
		if (Boolean.parseBoolean(getEval("!!window.za11y"))) {
			String restore = maximizeButton("winTitleMaximized").attr("aria-label");
			String maximize = maximizeButton("winTitleMaxable").attr("aria-label");
			assertFalse(restore.isEmpty(), "za11y should label the maximize button");
			assertNotEquals(restore, maximize, "the label should follow the maximized state");
			assertEquals(restore, maximizeButton("pnlTitleMaximized").attr("aria-label"));
			assertEquals(maximize, maximizeButton("pnlTitleMaxable").attr("aria-label"));
		}

		// maximizable=false + maximized=true is rejected by the server
		assertEquals("rejected/rejected", jq("$illegalCombo").text());

		// restoring must survive a missing maximize button too
		click(jq("$restoreWin"));
		waitResponse();
		click(jq("$restorePnl"));
		waitResponse();
		assertMaximized("winNoneMaximized", false);
		assertMaximized("pnlNoneMaximized", false);

		assertNoAnyError();
	}

	private void assertMaximized(String id, boolean maximized) {
		int box = jq("$" + id).parent().height();
		int actual = jq("$" + id).outerHeight();
		String inlineHeight = getEval("jq('$" + id + "')[0].style.height");
		if (maximized) {
			assertEquals(box + "px", inlineHeight, id + " should be sized by setMaximized");
			assertEquals(box, actual, id + " should fill its container");
		} else {
			assertEquals("", inlineHeight, id + " should not be sized by setMaximized");
			assertNotEquals(box, actual, id + " should keep its natural height");
		}
	}

	private JQuery maximizeButton(String id) {
		return jq("$" + id).find("." + sclass(id, "maximize"));
	}

	private boolean isButtonMaximized(String id) {
		return maximizeButton(id).hasClass(sclass(id, "maximized"));
	}

	private String sclass(String id, String name) {
		return (id.startsWith("win") ? "z-window-" : "z-panel-") + name;
	}
}
