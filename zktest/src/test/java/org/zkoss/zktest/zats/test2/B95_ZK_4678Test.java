/* B95_ZK_4678Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 25 18:32:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
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
public class B95_ZK_4678Test extends WebDriverTestCase {
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

		click(jq("@button"));
		waitResponse();

		getActions()
				.dragAndDropBy(toElement(jq(".z-window-header-move")), 0, -100)
				.perform();
		assertNoJSError();
		Assertions.assertEquals(0, jq("html").scrollTop());
	}
}
