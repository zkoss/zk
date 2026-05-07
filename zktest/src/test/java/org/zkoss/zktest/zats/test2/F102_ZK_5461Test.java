/* F102_ZK_5461Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 11 12:16:39 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F102_ZK_5461Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		options.setCapability("webSocketUrl", true);
		return options;
	}

	@Test
	public void test() {
		connect();
		Map<String, String> headers = new ConcurrentHashMap<>();
		try (Network network = new Network(driver)) {
			network.onBeforeRequestSent(args ->
					args.getRequest().getHeaders().forEach(h ->
							headers.put(h.getName(), h.getValue().getValue())));
			click(jq("@button"));
			waitResponse();
		}
		assertEquals("myValue", headers.get("myKey"));
	}
}
