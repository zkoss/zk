/* B85_ZK_3956Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jun 19 11:14:59 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3956Test extends WebDriverTestCase{

	@Test
	public void test() {
		connect();
		Assert.assertEquals("none", jq(".z-vlayout-inner").eq(1).css("display"));
		click(jq("@button").get(0));
		waitResponse();
		Assert.assertEquals("block", jq(".z-vlayout-inner").eq(1).css("display"));
		Assert.assertEquals("0px", jq(".z-vlayout-inner").eq(1).css("padding-bottom"));
	}
}
