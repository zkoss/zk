/* B103_ZK_6055Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 22 18:00:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B103_ZK_6055Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B103-ZK-6055.xml");
    private static final String test_page = "/test2/B103-ZK-6055.zul";

    @Test
    public void test() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(getAddress() + test_page).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        try {
            String cspHeader = conn.getHeaderField("Content-Security-Policy");
            assertNotNull(cspHeader);
            assertNull(conn.getHeaderField("Content-Security-Policy-Report-Only"));
            assertTrue(cspHeader.contains("script-src"));
            assertTrue(cspHeader.contains("'self'"));
            assertTrue(cspHeader.contains("'strict-dynamic'"));
            assertTrue(cspHeader.contains("'unsafe-inline'"));
            assertTrue(cspHeader.contains("'unsafe-eval'"));
            assertTrue(cspHeader.contains("'unsafe-hashes'"));
        } finally {
            conn.disconnect();
        }

        connect(test_page);
        click(jq("@nav"));
        waitResponse();
        assertNoJSError();
    }
}
