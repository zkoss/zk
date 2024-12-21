/* B36_2807475Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 04 15:14:53 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
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
public class B36_2807475Test extends WebDriverTestCase {

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

		// Click on menu popup
		click(jq("$menubar"));
		waitResponse();

		// Click textbox
		click(jq("$demo1"));
		waitResponse();

		// Verify that the popup textbox has focus
		assertTrue(jq("$demo1:focus").exists(), "The textbox should have focus");

		// Verify that the value is updated
		assertEquals("2", jq("$demo2").val(), "The text should be selected");
	}
}