/* B65_ZK_1900Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 06 16:17:53 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B65_ZK_1900Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		jq("body").toElement().set("scrollTop", 1000);
		Widget cb = jq(".z-combobox").toWidget();
		sendKeys(cb.$n("real"), "01");
		waitResponse();

		JQuery $pp = jq(cb.$n("pp"));
		assertTrue($pp.offsetTop() + $pp.height() <= jq(cb).offsetTop(), "the dropdown should apears above it");
		assertTrue($pp.find(".z-comboitem:contains(01)").isVisible(), "the dropdown should have no blank");
	}
}
