/* B110_ZK_6090_BandpopupTest.java

	Purpose:

	Description:

	History:
		Thu Aug 20 16:00:00 CST 2026, Created by peakerlee

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
 * A bandpopup adds focusin/focusout listeners to its own node when it is bound, and its unbind
 * has to take exactly those listeners off again.
 *
 * <p>The widget is unbound and bound again from the client, which is what a container doing
 * render-on-demand does to its children: the node stays where it is, so the listeners left on it
 * are counted instead of being thrown away with the node.
 *
 * @author peakerlee
 */
public class B110_ZK_6090_BandpopupTest extends WebDriverTestCase {

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
		// the popup content is rendered on demand, open the bandbox to get it
		click(jq("$bb").find(".z-bandbox-button"));
		waitResponse();
		assertEquals("true", getEval("(window.__w = zk.Widget.$('$bp'), window.__n = window.__w.$n(), !!window.__n)"),
				"the bandpopup should be rendered once the bandbox is open");
		assertEquals("1,1", focusListeners(),
				"binding the bandpopup should add one focusin and one focusout listener");

		getEval("(window.__w.unbind(), 1)");
		// ZK-6090: unbind_ passed a freshly bound function to off(), so nothing was taken off
		assertEquals("0,0", focusListeners(), "unbinding the bandpopup should take both listeners off");

		getEval("(window.__w.bind(), 1)");
		assertEquals("1,1", focusListeners(), "binding again should not stack more listeners");
		assertNoJSError();
	}

	private String focusListeners() {
		return getEval("(function () {var e = jq._data(window.__n, 'events') || {};"
				+ "return [(e.focusin || []).length, (e.focusout || []).length].join(',')})()");
	}
}
