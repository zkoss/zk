/* B30_1898094Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 04 15:08:13 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@ForkJVMTestOnly
public class B30_1898094Test extends WebDriverTestCase {

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

		sleep(1000);

		//Look for a textbox focused
		JQuery b = jq("@textbox:focus");

		//Look for a button focused
		JQuery r = jq("@button:focus");

		//Textbox focused Exists?
		boolean bb = b.exists();

		//Button focused Exists?
		boolean br = r.exists();

		//Assert
		assertTrue(bb);
		assertFalse(br);
	}
}