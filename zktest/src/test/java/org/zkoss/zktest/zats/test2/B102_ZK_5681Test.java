/* B102_ZK_5681Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 08 23:14:00 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5681Test  extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button"));
		waitResponse(true);
		sleep(1000);
		assertTrue(jq(".z-groupbox").exists());
		assertTrue(jq(".z-tree").exists());
	}
}


