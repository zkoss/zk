/* B86_ZK_4032.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 20 17:45:33 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4032Test extends WebDriverTestCase {

	@Test
	public void test() {
		try {
			connect();
		} catch (Exception e) {
			Assertions.fail();
		}
	}
}
