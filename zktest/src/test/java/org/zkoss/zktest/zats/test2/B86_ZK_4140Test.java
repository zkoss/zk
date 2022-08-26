/* B86_ZK_4140Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Nov 19 17:52:16 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4140Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int height = jq("@chosenbox").outerHeight();
		Assertions.assertEquals(height, jq("@textbox").outerHeight(), 1);
		Assertions.assertEquals(height, jq("@datebox").outerHeight(), 1);
	}
}
