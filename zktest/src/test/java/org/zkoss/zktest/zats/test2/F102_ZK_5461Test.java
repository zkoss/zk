/* F102_ZK_5461Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 11 12:16:39 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v142.network.Network;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F102_ZK_5461Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Map<String, Object> headers = new HashMap<>();
		DevTools devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		devTools.addListener(Network.requestWillBeSent(), request -> headers.putAll(request.getRequest().getHeaders()));
		click(jq("@button"));
		waitResponse();
		assertEquals("myValue", headers.get("myKey"));
	}
}
