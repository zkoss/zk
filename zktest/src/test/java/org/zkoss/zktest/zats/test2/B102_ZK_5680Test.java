/* B102_ZK_5680Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 11 16:01:15 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5680Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery messagebox = jq(".z-messagebox");
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals("An application error", messagebox.text());
		click(jq(".z-window-close"));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals("An application error", messagebox.text());
	}
}
