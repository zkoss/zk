/* B90_ZK_4407Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 04 14:27:56 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B90_ZK_4407Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		eval("window.scroll(0, 0)");
		driver.navigate().refresh();
		waitResponse();
		assertNotEquals(0, jq("html").scrollTop());
		sleep(1000);
		click(jq("@button"));
		waitResponse();
		assertEquals("history.scrollRestoration: auto", getZKLog());
	}
}
