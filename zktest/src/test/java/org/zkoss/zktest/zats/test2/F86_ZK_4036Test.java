/* F86_ZK_4036Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Aug 28 12:58:47 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F86_ZK_4036Test  extends WebDriverTestCase{
	@Test
	public void test() {
		connect();
		Assertions.assertEquals("value in execution", jq("$exec1").text());
		Assertions.assertEquals("value in execution", jq("$execAuto").text());
	}
}
