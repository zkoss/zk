/* B96_ZK_4781Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 06 11:36:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		Assert.assertTrue(jq("@errorbox").isVisible());
	}
}
