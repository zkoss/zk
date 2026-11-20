/* B110_ZK_6287Test.java

		Purpose:

		Description:

		History:
				Fri Nov 20 16:56:17 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6287Test extends WebDriverTestCase {

	// Chrome drops a disallowed unload listener without registering it, so record the calls themselves
	@Override
	protected WebDriver getWebDriver() {
		if (driver == null) {
			super.getWebDriver();
			((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source",
					"window.zk6287Listeners = [];"
					+ "var add = EventTarget.prototype.addEventListener;"
					+ "EventTarget.prototype.addEventListener = function (type) {"
					+ "if (this === window) window.zk6287Listeners.push(type);"
					+ "return add.apply(this, arguments);"
					+ "};"));
		}
		return driver;
	}

	@Test
	public void noUnloadListener() {
		connect();
		List<String> types = Arrays.asList(getEval("zk6287Listeners.join()").split(","));
		assertFalse(types.contains("unload"), "registered window listeners: " + types);
		assertEquals("beforeunload,pagehide", getEval(windowEventsBoundTo("zk.$('$gl')._goldenLayout._unloadFunction")));
	}

	@Test
	public void detachUnbindsWindowListeners() {
		connect();
		eval("window.zk6287Unload = zk.$('$gl')._goldenLayout._unloadFunction");
		click(jq("$detach"));
		waitResponse();
		assertFalse(jq("@goldenlayout").exists());
		assertEquals("", getEval(windowEventsBoundTo("window.zk6287Unload")));
	}

	private static String windowEventsBoundTo(String handler) {
		return "(function (fn) { var events = jq._data(window, 'events') || {};"
				+ " return Object.keys(events).filter(function (type) {"
				+ " return events[type].some(function (h) { return h.handler === fn; }); }).sort().join(); })(" + handler + ")";
	}
}
