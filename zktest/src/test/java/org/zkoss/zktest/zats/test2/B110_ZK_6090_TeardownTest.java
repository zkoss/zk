/* B110_ZK_6090_TeardownTest.java

	Purpose:

	Description:

	History:
		Fri Aug 21 14:50:00 CST 2026, Created by peakerlee

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
 * Two teardowns that used to leave something behind on a node they do not own: the fake
 * scrollbars of a biglistbox put their wheel handlers on the body node, and the golden layout
 * tab dropdown is moved into document.body while it is open.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_TeardownTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	/**
	 * WScroll registers its wheel handler on the widget's body node, which survives
	 * WScroll.destroy() — only the bar itself is removed.
	 */
	@Test
	public void testWScrollWheelHandlers() {
		connect();
		waitResponse();
		assertEquals("true", getEval("'' + !!window.zk6090.bodyNode"), "the biglistbox body should exist");
		String bound = getEval("window.zk6090Wheel()");
		assertNotEquals("0", bound,
				"the fake scrollbars should have put wheel handlers on the body, events were "
						+ getEval("window.zk6090Events(window.zk6090.bodyNode)"));

		getEval("(window.zk6090Unbind('blb'), 1)");
		assertEquals("0", getEval("window.zk6090Wheel()"),
				"destroying the fake scrollbars must take their wheel handlers off the body,"
						+ " they were bound as " + bound);
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}

	/** The tab dropdown container is vparented into document.body while the dropdown is open. */
	@Test
	public void testGoldenLayoutDropdownVParent() {
		connect();
		waitResponse();
		assertEquals("0", getEval("window.zk6090DropdownOrphans()"),
				"nothing should sit in document.body before the dropdown is opened");

		// the dropdown button only appears once the tabs overflow their stack header
		assertEquals("true", getEval("'' + (jq('.lm_tabdropdown').length > 0)"),
				"the tabs did not overflow, the dropdown button never appeared");
		getEval("(jq('.lm_tabdropdown')[0].click(), 1)");
		sleep(500);
		assertEquals("1", getEval("window.zk6090DropdownOrphans()"),
				"the open dropdown should have been moved into document.body");

		getEval("(window.zk6090Unbind('gl'), 1)");
		assertEquals("0", getEval("window.zk6090DropdownOrphans()"),
				"unbinding the layout must take the dropdown back out of document.body");
		assertEquals("", getEval("window.zk6090.errors.join('|')"), "unbinding threw");
		assertNoJSError();
	}
}
