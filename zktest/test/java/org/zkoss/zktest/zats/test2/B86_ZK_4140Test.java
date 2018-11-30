/* B86_ZK_4140Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Nov 19 17:52:16 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4140Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int height = jq("@chosenbox").outerHeight();
		Assert.assertEquals(height, jq("@textbox").outerHeight(), 1);
		Assert.assertEquals(height, jq("@datebox").outerHeight(), 1);
	}
}
