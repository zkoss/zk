/* B86_ZK_4048Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Nov 12 17:27:51 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4048Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();
		String[] numbers = getZKLog().split("\n");
		Assertions.assertEquals(numbers[0], numbers[1]);
		Assertions.assertEquals(numbers[1], numbers[2]);
	}
}
