/* B85_ZK_3736.java

        Purpose:
                
        Description:
                
        History:
                Thu Feb 01 11:44 AM:03 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3736Test extends WebDriverTestCase {
	@Test
	public void test() throws InterruptedException {
		connect();

		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq("@window"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();

		Assertions.assertEquals("visible", jq(".z-datebox-popup").css("visibility"));
	}
}
