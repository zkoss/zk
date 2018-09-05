/* B86_ZK_3977Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Sep 05 17:04:53 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_3977Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();

		click(jq(".z-radio:eq(0)"));
		waitResponse();
		Assert.assertEquals("selectedItem: A", getZKLog());
		closeZKLog();

		for (int i = 0; i < 2; i++) {
			click(jq(".z-button"));
			waitResponse();
		}

		click(jq(".z-radio:eq(1)"));
		waitResponse();
		Assert.assertEquals("selectedItem: B", getZKLog());
	}
}
