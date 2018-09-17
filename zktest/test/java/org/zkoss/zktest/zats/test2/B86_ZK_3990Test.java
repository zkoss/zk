/* B86_ZK_3990Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 18 09:47:11 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_3990Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// iPad
		options.addArguments("user-agent=Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1");
		return options;
	}

	@Test
	public void testOpenByButton() {
		connect();

		click(jq("@datebox > a"));
		waitResponse();

		Assert.assertEquals("", jq("$err").text());
	}

	@Test
	public void testOpenByInput() {
		connect();

		click(jq("@datebox > input"));
		waitResponse();

		Assert.assertEquals("", jq("$err").text());
	}
}
