/* Z60_Touch_011Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 18:20:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class Z60_Touch_011Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"))
				.setExperimentalOption("w3c", false); // Temporary workaround for TouchAction
	}

	@Test
	public void test() {
		connect();

		TouchActions touchActions = new TouchActions(driver);
		touchActions.scroll(toElement(jq("@listbox")), 0 , 3000).perform();
		waitResponse();
		touchActions.scroll(toElement(jq("@grid")), 0 , 3000).perform();
		waitResponse();
		assertNotEquals(0, jq("@listbox .z-listbox-body").scrollTop());
		assertNotEquals(0, jq("@grid .z-grid-body").scrollTop());
	}
}
