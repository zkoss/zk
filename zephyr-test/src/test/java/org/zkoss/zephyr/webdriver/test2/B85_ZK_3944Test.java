/* B85_ZK_3944Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 27 15:48:56 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3944Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		try {
			click(jq("$button"));
			waitResponse();
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
		assertNoAnyError();
	}
}
