/* B101_ZK_5802Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 15 10:22:56 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B101_ZK_5802Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals("_listenFlex() have been overridden", jq("@div").toWidget().firstChild().$n().get("innerHTML"));
	}
}
