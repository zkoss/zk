/* B85_ZK_3885.java

        Purpose:
                
        Description:
                
        History:
                Tue Mar 20 12:33 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3885Test extends WebDriverTestCase{
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("bind", jq("$outLabel").text().trim());
	}
}
