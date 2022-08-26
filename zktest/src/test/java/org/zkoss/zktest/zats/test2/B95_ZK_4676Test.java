/* B95_ZK_4676Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 20 14:25:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B95_ZK_4676Test extends WebDriverTestCase {
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
		JQuery jqPanelChildren = jq("@panelchildren");
		assertEquals(jqPanelChildren.innerHeight() - parseInt(jqPanelChildren.css("padding-top"))
				- parseInt(jqPanelChildren.css("padding-bottom")), jq("@panelchildren > div").height());
	}
}
