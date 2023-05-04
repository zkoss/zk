/* B96_ZK_5251Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 12:32:44 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class B96_ZK_5251Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions().setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad")).setExperimentalOption("w3c", false); // Temporary workaround for TouchAction
	}

	@Test
	public void test() {
		connect();
		waitResponse();
		assertFalse(hasError());
	}
}
