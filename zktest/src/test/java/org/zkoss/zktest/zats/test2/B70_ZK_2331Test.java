/* B70_ZK_2331Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 05 12:16:26 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2331Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad Mini"));
	}

	@Test
	public void test() {
		connect();
		click(jq(".z-combobox-button"));
		waitResponse();
		JQuery popup = jq(".z-combobox-popup");
		// ZK-2331: the popup sits flush on the input; its top snaps to a whole pixel, the input's may not
		assertTrue(rect(popup, "bottom") <= rect(jq("@combobox"), "top") + 1, "popup shouldn't overlap the input.");
	}

	private double rect(JQuery element, String side) {
		return Double.parseDouble(getEval(element + "[0].getBoundingClientRect()." + side));
	}
}