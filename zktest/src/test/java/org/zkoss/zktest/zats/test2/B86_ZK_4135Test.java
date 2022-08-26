/* B86_ZK_4135Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 08 15:36:48 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4135Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button").eq(1));
		waitResponse();
		Assertions.assertFalse(jq(".z-frozen").exists());
		Assertions.assertFalse(isZKLogAvailable());
	}
}
