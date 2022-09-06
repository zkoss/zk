/* B95_ZK_4584Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Aug 31, 2020 02:11:11 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B95_ZK_4584Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		assertEquals(jq(".z-spinner-input").outerWidth(), jq(".z-spinner-button").width() + jq(".z-spinner-button").positionLeft(), 2);
		assertEquals(jq(".z-doublespinner-input").outerWidth(), jq(".z-doublespinner-button").width() + jq(".z-doublespinner-button").positionLeft(), 2);
	}
}
