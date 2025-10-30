/* B85_ZK_3788Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jan 26 4:32 PM:16 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3788Test extends WebDriverTestCase {
	@Test
	public void test() {
		WebDriver connect = connect();
		assertNotEquals(jq(".z-frozen-inner").width(), 0);
	}
}
