/* B96_ZK_4781Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 06 11:36:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4781Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		final JQuery longbox = jq("@longbox");
		click(longbox);
		sendKeys(longbox, "1-2");
		click(jq("@button"));
		waitResponse();
		Assertions.assertTrue(jq("@errorbox").isVisible());
	}
}
