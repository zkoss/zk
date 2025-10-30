/* B50_ZK_258Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 12:05:15 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_258Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertTrue(jq(".z-tab:contains(Tab Last)").hasClass("z-tab-selected"));

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("true", getZKLog());
	}
}
