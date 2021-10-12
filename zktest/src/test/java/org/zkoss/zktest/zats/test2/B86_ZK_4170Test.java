/* B86_ZK_4170Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 19 14:13:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4170Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery knobs = jq(".z-slider");
		for (int i = 0; i < knobs.length(); i++) {
			Assert.assertEquals(200, knobs.eq(i).width(), 2);
		}
		click(jq(".z-button"));
		waitResponse();
		for (int i = 0; i < knobs.length(); i++) {
			Assert.assertEquals(300, knobs.eq(i).height(), 2);
		}
	}
}
