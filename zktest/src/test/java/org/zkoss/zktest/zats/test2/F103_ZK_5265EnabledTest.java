/* F103_ZK_5265EnabledTest.java

	Purpose:

	Description:

	History:
		Tue Nov 25 12:57:09 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v142.network.Network;
import org.openqa.selenium.devtools.v142.network.model.Response;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class F103_ZK_5265EnabledTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F103-ZK-5265Enabled.xml");
	private static final String test_page = "/test2/F103-ZK-5265Enabled.zul";

	@Test
	public void testHeader() {
		connect();
		DevTools devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();

		devTools.send(Network.enable(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		));

		AtomicReference<String> cspHeaderRef = new AtomicReference<>();
		AtomicReference<String> cspReportOnlyRef = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);

		devTools.addListener(Network.responseReceived(), response -> {
			Response resp = response.getResponse();

			if (resp.getUrl().contains(test_page)) {
				Map<String, Object> headers = resp.getHeaders().toJson();
				Object csp = headers.get("Content-Security-Policy");
				Object cspReportOnly = headers.get("Content-Security-Policy-Report-Only");

				cspHeaderRef.set(csp != null ? csp.toString() : null);
				cspReportOnlyRef.set(cspReportOnly != null ? cspReportOnly.toString() : null);
				latch.countDown();
			}
		});

		driver.get(getAddress() + test_page);
		waitResponse();

		try {
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertNotNull(cspHeaderRef.get());
			assertNull(cspReportOnlyRef.get());

			String cspHeader = cspHeaderRef.get();
			assertTrue(cspHeader.contains("script-src"));
			assertTrue(cspHeader.contains("'unsafe-eval'"));
			assertTrue(cspHeader.contains("'unsafe-inline'"));
			driver.quit();
		} catch (InterruptedException ignored) {
		}
	}

	@Test
	public void checkApplicationScope() {
		connect(test_page);
		click(jq("$statusCheckbox"));
		waitResponse();
		type(jq("$eventListener"), "listener test");
		waitResponse();
		click(jq("$serverPushBtn"));
		waitResponse();
		assertNoAnyError();
	}
}
