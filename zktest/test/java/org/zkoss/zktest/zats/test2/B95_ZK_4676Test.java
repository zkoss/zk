/* B95_ZK_4676Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 20 14:25:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class B95_ZK_4676Test extends WebDriverTestCase {
	@ClassRule
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
