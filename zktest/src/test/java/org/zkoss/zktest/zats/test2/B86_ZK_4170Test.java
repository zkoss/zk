/* B86_ZK_4170Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 19 14:13:21 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4170Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery knobs = jq(".z-slider");
		for (int i = 0; i < knobs.length(); i++) {
			Assertions.assertEquals(200, knobs.eq(i).width(), 2);
		}
		click(jq(".z-button"));
		waitResponse();
		for (int i = 0; i < knobs.length(); i++) {
			Assertions.assertEquals(300, knobs.eq(i).height(), 2);
		}
	}
}
