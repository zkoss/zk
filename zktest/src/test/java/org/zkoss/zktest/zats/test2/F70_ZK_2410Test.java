/* F70_ZK_2410Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Nov 29 17:06:39 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.FirefoxWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F70_ZK_2410Test extends FirefoxWebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery input = jq(".z-paging-input");
		click(input);
		waitResponse();
		sendKeys(jq("body"), Keys.TAB, Keys.ENTER);
		waitResponse();
		assertEquals(jq(".z-paging-input").eval("val()"), "2");
	}
}
