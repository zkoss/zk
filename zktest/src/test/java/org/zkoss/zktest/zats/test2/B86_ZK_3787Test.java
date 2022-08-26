/* B86_ZK_3787Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Nov 20 10:46:25 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3787Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-notification").exists());
	}
}
