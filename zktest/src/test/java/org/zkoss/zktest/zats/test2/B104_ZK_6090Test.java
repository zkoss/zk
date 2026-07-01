/* B104_ZK_6090Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 16:42:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B104_ZK_6090Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation",
						Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		assertEquals("true", jq("$touchEnabled").text());
		assertEquals(3, jq("@tab").length());

		// close test3 tab (index 2), whose panel contains <cardlayout>
		click(widget("@tab:eq(2)").$n("cls"));
		waitResponse();

		assertEquals(2, jq("@tab").length());
		assertNoAnyError();
	}
}
