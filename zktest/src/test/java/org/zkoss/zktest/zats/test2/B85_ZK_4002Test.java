/* B85_ZK_4002Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jul 20 15:27:02 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_4002Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		click(jq("$menu2"));
		waitResponse();
		Assertions.assertTrue(jq("$secondmnp").exists());
	}
}
