/* B70_ZK_2374Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 17:49:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2374Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPhone 6"))
				.setExperimentalOption("w3c", false); // Temporary workaround for TouchAction
	}

	@Test
	public void test() {
		connect();

		new TouchActions(driver)
				.down(50, 300).move(50, 200).up(50, 200)
				.perform();
		waitResponse();
		assertNoJSError();
	}
}
