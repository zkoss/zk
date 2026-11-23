/* F85_ZK_3529Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 29 15:08:23 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F85_ZK_3529Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String[] regions = {"east", "west", "south", "north"};
		for (String region : regions) {
			Assert.assertTrue(jq(".z-" + region + "-title").isVisible());
		}
	}
}
