/* B96_ZK_4868Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 14 12:05:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4868Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:contains(clear color)"));
		waitResponse();
		click(jq("@button:contains(get color)"));
		waitResponse();
		Assertions.assertEquals("255,255,255,255", getZKLog());
	}
}
