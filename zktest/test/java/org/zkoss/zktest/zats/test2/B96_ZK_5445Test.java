/* B96_ZK_5445Test.java

	Purpose:
		
	Description:
		
	History:
		4:51 PM 2023/4/26, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import java.util.logging.Level;
import org.openqa.selenium.NoAlertPresentException;

import org.zkoss.json.JSONValue;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5445Test extends WebDriverTestCase {
	/** Distinctive part of the malicious path in B96-ZK-5445.zul. */
	private static final String PAYLOAD_MARKER = "webdzkmy";

	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		LoggingPreferences prefs = new LoggingPreferences();
		prefs.enable(LogType.PERFORMANCE, Level.ALL);
		options.setCapability(ChromeOptions.LOGGING_PREFS, prefs);
		return options;
	}

	@Test
	public void testReflectedXSSAttack() {
		connect();
		try {
			assertNotEquals(driver.switchTo().alert().getText(), "1");
			fail("cannot run into this line, otherwise, the bug exists.");
		} catch (NoAlertPresentException e) {
			// yes, it works here.
		}
		// Since ZK-6083 the malicious path is refused with a plain 404 rather than
		// a zk.error() script, so assert the browser's own request was rejected.
		assertTrue("the malicious resource request should be rejected with 404",
				driver.manage().logs().get(LogType.PERFORMANCE).getAll()
						.stream().anyMatch(B96_ZK_5445Test::isPayloadRejected));
	}

	private static boolean isPayloadRejected(LogEntry entry) {
		final Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) JSONValue.parse(entry.getMessage())).get("message");
		if (!"Network.responseReceived".equals(message.get("method"))) return false;
		final Map<?, ?> response = (Map<?, ?>) ((Map<?, ?>) message.get("params")).get("response");
		return String.valueOf(response.get("url")).contains(PAYLOAD_MARKER)
				&& ((Number) response.get("status")).intValue() == 404;
	}
}
