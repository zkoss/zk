/* B102_ZK_5680_ErrorPageTest.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 16 14:52:04 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B102_ZK_5680_ErrorPageTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B102-ZK-5680-zk.xml");

	@Test
	public void test(){
		connect("/test2/B102-ZK-5680.zul");
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals("custom error: An application error", jq(".z-window-content").text().trim());
		click(jq(".z-window-close"));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals("custom error: An application error", jq(".z-window-content").text().trim());
	}
}
