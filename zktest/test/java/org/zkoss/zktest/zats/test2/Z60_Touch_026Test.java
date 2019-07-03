/* Z60_Touch_026Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 18:15:41 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.zkoss.zktest.zats.WebDriverTestCase;

import java.util.Collections;

public class Z60_Touch_026Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPhone 6"));
	}
	
	@Test
	public void test() {
		connect();
		waitResponse();
		
		int originScrollHeight = jq(".z-groupbox-content").scrollHeight();
		Assert.assertTrue(originScrollHeight > jq(".z-groupbox-content").height());
		
		click(jq("@button").eq(0));
		waitResponse();
		Assert.assertTrue(originScrollHeight < jq(".z-groupbox-content").scrollHeight());
		
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertTrue(originScrollHeight == jq(".z-groupbox-content").scrollHeight());
	}
}
