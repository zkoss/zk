/* B110_ZK_6090_ContentHandlerTest.java

	Purpose:

	Description:

	History:
		Thu Aug 21 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The content handler of a menu registers the menu for the onFloatUp and onHide watches and puts
 * three listeners on the content popup node. Unbinding the menu has to give all of that back.
 *
 * <p>onFloatUp is not one of the visibility watches, so zWatch does not filter out an unbound
 * listener: a menu left in the watch list keeps being called after it is gone.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_ContentHandlerTest extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// assertNoJSError() reads the browser console, which Chrome only exposes when asked for
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	/** The onFloatUp/onHide watch registration must not survive the menu it was made for. */
	@Test
	public void testWatchList() {
		connect();
		waitResponse();
		// the colour content handler lives in zkex and is fetched on demand
		waitFor("String(!!zk.Widget.$('$m')._contentHandler)", "the zkex content handler never arrived");

		assertEquals("true", getEval("zk6090Spy('$m')"), "the menu should own a content handler");
		getEval("(zk6090FloatUp(), 1)");
		assertEquals("1", getEval("zk6090Hits('$m')"),
				"a bound menu with a content is called on onFloatUp");

		getEval("(zk6090Unbind('$m'), 1)");
		getEval("(zk6090FloatUp(), 1)");
		// ZK-6090: unbind() never gave the onFloatUp/onHide watches back, so the menu was still
		// called after it had been unbound
		assertEquals("1", getEval("zk6090Hits('$m')"),
				"an unbound menu should no longer be called on onFloatUp");

		getEval("(zk6090Bind('$m'), 1)");
		getEval("(zk6090FloatUp(), 1)");
		assertEquals("2", getEval("zk6090Hits('$m')"),
				"binding the menu again should put it back in the watch list");
		assertNoJSError();
	}

	/**
	 * The three listeners bind() puts on the content popup node are added whatever the menu
	 * carries, so unbind() has to take them off the same way. A menu that gains a menupopup while
	 * it is bound (what Menu.onChildAdded_ does) is unbound with menupopup already set.
	 */
	@Test
	public void testContentPopupListeners() {
		connect();
		waitResponse();
		waitFor("String(!!zk.Widget.$('$m2')._contentHandler)", "the zkex content handler never arrived");

		assertEquals("true", getEval("zk6090GainMenupopup()"),
				"the second menu should have a content popup node to start with");
		// ZK-6090: the whole removal block sat inside if (!wgt.menupopup), so the listeners were
		// left on a node the handler no longer even points at
		assertEquals("0,0,0", getEval("zk6090Pp2Listeners()"),
				"unbinding should take the content popup listeners off even when a menupopup arrived");
		assertNoJSError();
	}

	/** Polls a JS expression that evaluates to a boolean until it turns true. */
	private void waitFor(String jsBooleanExpr, String message) {
		for (int i = 0; i < 50; i++) {
			if ("true".equals(getEval(jsBooleanExpr)))
				return;
			sleep(100);
		}
		fail(message);
	}
}
