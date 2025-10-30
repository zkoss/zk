/* B95_ZK_4673Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 21 15:15:24 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class B95_ZK_4673Test extends WebDriverTestCase {
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

		Widget win1 = widget("@window:eq(0)");
		JQuery winHeader = jq(win1.$n("cap"));
		JQuery winBtn = jq(win1.$n("min"));
		MatcherAssert.assertThat(
				"The position left of the Window minimize button should >= half width",
				winBtn.positionLeft(), greaterThan(winHeader.width() / 2));

		Widget panel1 = widget("@panel:eq(0)");
		JQuery panelHeader = jq(panel1.$n("cap"));
		JQuery panelButton = jq(panel1.$n("min"));
		MatcherAssert.assertThat(
				"The position left of the Panel minimize button should >= half width",
				panelButton.positionLeft(), greaterThan(panelHeader.width() / 2));
	}
}
