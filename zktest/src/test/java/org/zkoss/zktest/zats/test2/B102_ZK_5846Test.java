/* B102_5846Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 13 18:42:51 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5846Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(
			B102_ZK_5846Test.class);

	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		options.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
		options.setCapability("goog:loggingPrefs", Collections.singletonMap(LogType.BROWSER, Level.ALL));
		return options;
	}

	@Test
	public void test() {
		connect();
		JQuery datebox = jq(".z-datebox");
		click(datebox.find(".z-datebox-button"));
		waitResponse();

		LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries) {
			if (entry.getLevel().equals(Level.SEVERE)) {
				fail("Found JavaScript error in browser console: " + entry.getMessage());
			}
		}

	}
}


