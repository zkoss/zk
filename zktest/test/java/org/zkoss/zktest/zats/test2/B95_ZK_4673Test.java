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
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B95_ZK_4673Test extends WebDriverTestCase {
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
