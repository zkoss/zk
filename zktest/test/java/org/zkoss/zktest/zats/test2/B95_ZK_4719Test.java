/* B95_ZK_4719Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Nov 16 17:33:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

public class B95_ZK_4719Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
			.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"))
			.setExperimentalOption("w3c", false); // Temporary workaround for TouchAction
	}

	@Test
	public void test() {
		TouchActions touchActions = new TouchActions(connect());
		Element btn = widget("@datebox").$n("btn");
		try {
			touchActions.singleTap(toElement(btn)).perform();
			waitResponse();
			Assert.assertTrue("should show calendar instead of time wheel.", jq(".z-calendar-text").exists());
		} finally {
			click(jq("@button")); // reset library property
			waitResponse();
		}
	}
}
