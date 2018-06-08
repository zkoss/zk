/* F85_ZK_3798Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jun 08 18:14:30 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F85_ZK_3798Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertEquals("249px", jq("$hchild1").get(0).eval("style.width"));
		waitResponse();
		Assert.assertEquals("49px", jq("$hchild2").get(0).eval("style.width"));
		waitResponse();
		Assert.assertEquals("249px", jq("$vchild1").get(0).eval("style.height"));
		waitResponse();
		Assert.assertEquals("49px", jq("$vchild2").get(0).eval("style.height"));
	}
}
