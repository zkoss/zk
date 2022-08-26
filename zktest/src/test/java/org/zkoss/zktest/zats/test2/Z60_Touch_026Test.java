/* Z60_Touch_026Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 18:15:41 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class Z60_Touch_026Test extends WebDriverTestCase {
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
		waitResponse();
		
		int originScrollHeight = jq(".z-groupbox-content").scrollHeight();
		MatcherAssert.assertThat(originScrollHeight, greaterThan(jq(".z-groupbox-content").height()));
		
		click(jq("@button").eq(0));
		waitResponse();
		MatcherAssert.assertThat(originScrollHeight, lessThan(jq(".z-groupbox-content").scrollHeight()));
		
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(originScrollHeight, jq(".z-groupbox-content").scrollHeight());
	}
}
