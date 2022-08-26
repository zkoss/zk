/* B95_ZK_4742Test.java

		Purpose:
				
		Description:
				
		History:
				Wed Dec 30 16:27:44 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B95_ZK_4742Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B95_ZK_4742Test.class);

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertTrue(getWebDriver().getCurrentUrl().endsWith("B95-ZK-4742-login.zul"));
	}
}
