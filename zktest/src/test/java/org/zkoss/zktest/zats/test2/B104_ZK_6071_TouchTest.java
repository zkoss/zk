/* B104_ZK_6071_TouchTest.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 28 11:38:07 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B104_ZK_6071_TouchTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect("/test2/B104-ZK-6071.zul");
		waitResponse();

		click(jq("$view"));
		waitResponse();
		sleep(500);

		click(jq("$inner").find(".z-groupbox-title").eq(0));
		waitResponse();
		sleep(500);

		assertTrue(jq("$db").exists(), "datebox should be rendered after inner groupbox is opened");

		String errorJson = getEval("JSON.stringify(window.__jsErrors || [])");
		assertEquals("[]", errorJson, "Expected no JS errors during open sequence; got: " + errorJson);

		assertNoJSError();
	}
}
