/* B70_ZK_2773_1Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 11:21:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
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

/**
 * @author rudyhuang
 */
public class B70_ZK_2773_1Test extends WebDriverTestCase {
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

		int dateboxTop = jq("@datebox").offsetTop();
		int viewportHeight = jq("body").height();
		eval(String.format("window.scrollTo(0, %d)", dateboxTop - viewportHeight / 2));

		click(widget("@datebox").$n("btn"));
		waitResponse(true);
		MatcherAssert.assertThat(jq(".z-datebox-popup:visible").offsetTop(), greaterThan(jq("@datebox").offsetTop()));
		click(widget("@datebox").$n("btn"));

		click(widget("@timebox:visible").$n("btn")); // there is a hidden one in datebox
		waitResponse(true);
		MatcherAssert.assertThat(jq(".z-timebox-popup:visible").offsetTop(), greaterThan(jq("@timebox:visible").offsetTop()));
		click(widget("@timebox:visible").$n("btn"));

		click(widget("@combobox").$n("btn"));
		waitResponse(true);
		MatcherAssert.assertThat(jq(".z-combobox-popup:visible").offsetTop(), greaterThan(jq("@combobox").offsetTop()));
	}
}
