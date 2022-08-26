/* B86_ZK_4201Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 10 14:44:18 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

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
		Assertions.assertNotEquals(0, jq(".z-listbox-body").scrollTop());
	}
}
