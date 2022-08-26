/* B86_ZK_4027Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 15 17:32:48 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4027Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("replaceHTML:1", getZKLog(), "Only replace once");
	}
}
