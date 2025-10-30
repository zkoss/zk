/* B65_ZK_1552Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 05 11:59:26 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B65_ZK_1552Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		JQuery center = jq(".z-center-body");
		String[] compList = new String[] {"combobox", "datebox", "bandbox"};
		for (String comp : compList) {
			Widget wgt = jq(".z-" + comp).toWidget();
			click(wgt.$n("btn"));
			waitResponse();
			center.scrollTop(center.scrollHeight());
			center.scrollTop(0);
			assertFalse(jq(wgt.$n("pp")).isVisible(), "should not show the " + comp + " popup");
		}
	}
}
