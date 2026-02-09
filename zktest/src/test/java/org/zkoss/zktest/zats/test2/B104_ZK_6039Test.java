/* B104_ZK_6039Test.java

	Purpose:

	Description:

	History:
		Mon Feb 09 15:26:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_6039Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		assertTrue(jq("$result").exists());
		assertTrue(jq("$result").text().contains("Page loaded successfully"));

		// Both widgets must be bound on the client (the bug aborted bind_ during setMaximized)
		assertTrue(jq("$targetWindow").exists(), "Window widget not bound");
		assertTrue(jq("@window @label").exists());
		assertTrue(jq("$targetPanel").exists(), "Panel widget not bound");
		assertTrue(jq("@panel @label").exists());

		// No JS error during page load (original bug threw "Node with max is not found!")
		LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries) {
			if (entry.getLevel().equals(Level.SEVERE)) {
				fail("Found JavaScript error in browser console: " + entry.getMessage());
			}
		}
	}
}
