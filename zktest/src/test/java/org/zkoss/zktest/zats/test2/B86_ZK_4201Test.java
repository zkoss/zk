/* B86_ZK_4201Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 10 14:44:18 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4201Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}
	
	@Test
	public void test() {
		connect();
		waitResponse();
		jq(".z-listbox-body").scrollTop(1000);
		waitResponse();
		click(jq("@listcell:last"));
		waitResponse();
		Assert.assertNotEquals(0, jq(".z-listbox-body").scrollTop());
	}
}
