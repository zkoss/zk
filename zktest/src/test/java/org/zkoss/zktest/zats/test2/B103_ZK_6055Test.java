/* B103_ZK_6055Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 22 18:00:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

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
public class B103_ZK_6055Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B103-ZK-6055.xml");
    private static final String test_page = "/test2/B103-ZK-6055.zul";

    @Test
    public void test() {
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

        click(jq("@nav"));
        waitResponse();

        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));

            assertNotNull(cspHeaderRef.get());
            assertNull(cspReportOnlyRef.get());

            String cspHeader = cspHeaderRef.get();
            assertTrue(cspHeader.contains("script-src"));
            assertTrue(cspHeader.contains("'self'"));
            assertTrue(cspHeader.contains("'strict-dynamic'"));
            assertTrue(cspHeader.contains("'unsafe-inline'"));
            assertTrue(cspHeader.contains("'unsafe-eval'"));
            assertTrue(cspHeader.contains("'unsafe-hashes'"));
            assertNoJSError();
        } catch (InterruptedException ignored) {
        }
    }
}
