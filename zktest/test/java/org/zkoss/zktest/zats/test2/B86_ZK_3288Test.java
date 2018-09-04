/* B86_ZK_3288Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Sep 04 12:50:45 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_3288Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("$btn2"));
		waitResponse();

		click(jq("$btn1"));
		waitResponse();

		JQuery centerBody = jq(".z-center-body");
		Assert.assertTrue(centerBody.width() > centerBody.find(".z-div").width());
	}
}
