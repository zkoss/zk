/* B70_ZK_2773_1Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 11:21:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2773_1Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// iPad
		options.addArguments("user-agent=Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1");
		return options;
	}

	@Test
	public void test() {
		connect();

		int dateboxTop = jq("@datebox").offsetTop();
		int viewportHeight = jq("body").height();
		eval(String.format("window.scrollTo(0, %d)", dateboxTop - viewportHeight / 2));

		click(widget("@datebox").$n("btn"));
		waitResponse(true);
		Assert.assertThat(jq(".z-datebox-popup:visible").offsetTop(), greaterThan(jq("@datebox").offsetTop()));
		click(widget("@datebox").$n("btn"));

		click(widget("@timebox:visible").$n("btn")); // there is a hidden one in datebox
		waitResponse(true);
		Assert.assertThat(jq(".z-timebox-popup:visible").offsetTop(), greaterThan(jq("@timebox:visible").offsetTop()));
		click(widget("@timebox:visible").$n("btn"));

		click(widget("@combobox").$n("btn"));
		waitResponse(true);
		Assert.assertThat(jq(".z-combobox-popup:visible").offsetTop(), greaterThan(jq("@combobox").offsetTop()));
	}
}
