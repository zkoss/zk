/* F103_ZK_5265ReportTest.java

	Purpose:

	Description:

	History:
		Tue Nov 25 12:57:09 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
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
public class F103_ZK_5265ReportTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F103-ZK-5265Report.xml");
	private static final String test_page = "/test2/F103-ZK-5265Report.zul";

	@Test
	public void testNonce() {
		connect(test_page);
		String scriptNonce = (String) getEval("document.getElementsByTagName('script')[0].nonce");
		String elNonceValue = jq("$elNonce").text();
		assertNotNull(scriptNonce);
		assertNotNull(elNonceValue);
		assertEquals(elNonceValue, scriptNonce);
		assertTrue(elNonceValue.matches("^[A-Za-z0-9+/]+=*$"));
		assertNoAnyError();
	}

	@Test
	public void testHeader() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(getAddress() + test_page).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		try {
			String cspReportOnly = conn.getHeaderField("Content-Security-Policy-Report-Only");
			assertNull(conn.getHeaderField("Content-Security-Policy"));
			assertNotNull(cspReportOnly);
			assertTrue(cspReportOnly.contains("script-src"));
			assertTrue(cspReportOnly.contains("'self'"));
			assertTrue(cspReportOnly.contains("'strict-dynamic'"));
			assertTrue(cspReportOnly.contains("'unsafe-inline'"));
			assertTrue(cspReportOnly.contains("'unsafe-eval'"));
			assertTrue(cspReportOnly.contains("'nonce-"));
		} finally {
			conn.disconnect();
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
