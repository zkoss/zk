/* B102_ZK_5487Test.java

	Purpose:

	Description:

	History:
		Mon Mar 24 11:39:43 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
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
public class B102_ZK_5487Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("flex: 0 0 35%;", jq("@listheader").eq(1).attr("style"));
		assertEquals("flex: 0 0 35%;", jq("@column").eq(1).attr("style"));
	}
}
