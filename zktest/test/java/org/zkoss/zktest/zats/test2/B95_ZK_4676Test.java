/* B95_ZK_4676Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 20 14:25:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4676Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();
		JQuery jqPanelChildren = jq("@panelchildren");
		assertEquals(jqPanelChildren.innerHeight() - parseInt(jqPanelChildren.css("padding-top"))
				- parseInt(jqPanelChildren.css("padding-bottom")), jq("@panelchildren > div").height());
	}
}
