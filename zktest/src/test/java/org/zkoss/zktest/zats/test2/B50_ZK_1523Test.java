/* B50_ZK_1523Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Sep 28 14:01:41 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B50_ZK_1523Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F100-ZK-5408-1-zk.xml");

	@Test
	public void test() {
		connect();
		click(jq(".z-button:contains(test one)"));
		waitResponse();
		click(jq(".z-messagebox-window").find(".z-button:eq(0)"));
		waitResponse();
		click(jq(".z-button:contains(test two)"));
		waitResponse();
		click(jq(".z-messagebox-window").find(".z-button:eq(0)"));
		waitResponse();
	}
}
