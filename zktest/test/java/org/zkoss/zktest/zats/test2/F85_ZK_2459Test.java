/* F85_ZK_2459Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 31 10:26:22 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F85_ZK_2459Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button"));
		waitResponse();
		Assert.assertEquals("one", jq(".lb:eq(0)").text());
		Assert.assertEquals("two", jq(".lb:eq(1)").text());
		Assert.assertEquals("three", jq(".lb:eq(2)").text());
	}
}
