/* B86_ZK_4048Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Nov 12 17:27:51 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4048Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();
		String[] numbers = getZKLog().split("\n");
		Assert.assertEquals(numbers[0], numbers[1]);
		Assert.assertEquals(numbers[1], numbers[2]);
	}
}
