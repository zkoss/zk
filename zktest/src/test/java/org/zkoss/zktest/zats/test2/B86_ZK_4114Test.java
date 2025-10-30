/* B86_ZK_4114Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Nov 02 19:04:23 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4114Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assertions.assertEquals(jq(".z-orgnode:contains(Item1)").css("fontFamily"),
				jq(".z-label:contains(Item2)").css("fontFamily"));
	}
}
