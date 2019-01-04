/* B86_ZK_4117Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 04 09:47:26 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4117Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// Simulates iPad
		options.addArguments("user-agent=Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");

		// Enable browser log
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, Level.SEVERE);
		options.setCapability(CapabilityType.LOGGING_PREFS, logs);
		return options;
	}

	@Test
	public void test() {
		connect();

		List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.collect(Collectors.toList());
		if (!logs.isEmpty()) {
			Assert.fail(logs.get(0).toString());
		}
	}
}
