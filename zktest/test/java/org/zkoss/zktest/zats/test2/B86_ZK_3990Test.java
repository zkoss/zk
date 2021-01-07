/* B86_ZK_3990Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 18 09:47:11 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_3990Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
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
