/* B86_ZK_3990Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 18 09:47:11 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class B86_ZK_3990Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void testOpenByButton() {
		connect();

		click(jq("@datebox > a"));
		waitResponse();

		assertEquals("", jq("$err").text());
	}

	@Test
	public void testOpenByInput() {
		connect();

		click(jq("@datebox > input"));
		waitResponse();

		assertEquals("", jq("$err").text());
	}
}
