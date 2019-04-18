/* B70_ZK_2393Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 28 15:20:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2393Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "Pixel 2"));
	}

	@Test
	public void test() {
		connect();

		Assert.assertTrue(jq("body").scrollHeight() > jq("body").height());
	}
}
