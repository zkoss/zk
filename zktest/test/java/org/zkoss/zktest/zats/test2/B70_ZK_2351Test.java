/* B70_ZK_2351Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 17:49:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

/**
 * @author rudyhuang
 */
public class B70_ZK_2351Test extends WebDriverTestCase {
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

		Element btnUp = widget("@doublespinner:eq(1)").$n("btn-up");
		TouchActions touchActions = new TouchActions(driver);
		touchActions.singleTap(toElement(btnUp)).perform();
		waitResponse();
		touchActions.singleTap(toElement(btnUp)).perform();
		waitResponse();
		Assert.assertEquals("2", jq("@doublespinner:eq(0) input").val());

		Element btnDown = widget("@doublespinner:eq(0)").$n("btn-down");
		touchActions.singleTap(toElement(btnDown)).perform();
		waitResponse();
		touchActions.singleTap(toElement(btnDown)).perform();
		waitResponse();
		touchActions.singleTap(toElement(btnDown)).perform();
		waitResponse();
		Assert.assertEquals("-1.0", jq("@doublespinner:eq(1) input").val());

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("1.1", jq("@doublespinner:eq(1) input").val());
	}
}
