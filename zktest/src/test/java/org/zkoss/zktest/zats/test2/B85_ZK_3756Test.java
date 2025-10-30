/* B85_ZK_3756Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 07 4:20 PM:08 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3756Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery inc1 = jq(".inc .z-label");
		JQuery inc2 = jq(".inc2 .z-label");
		Assertions.assertEquals("included", inc1.text().trim());
		Assertions.assertEquals("included", inc2.text().trim());

	}

}
