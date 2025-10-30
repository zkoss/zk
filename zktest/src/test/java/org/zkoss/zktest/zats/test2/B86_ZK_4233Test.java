/* B86_ZK_4233Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 14 10:34:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4233Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button:contains(show and refresh)"));
		waitResponse();
		Assertions.assertEquals(1, jq(".z-messagebox-window").length());
		Assertions.assertEquals("B86-ZK-4233-include.zul?test=0", getZKLog());
		
		click(jq(".z-window-close"));
		waitResponse();
		Assertions.assertEquals(0, jq(".z-messagebox-window").length());
		
		click(jq("@button:contains(show and refresh)"));
		waitResponse();
		Assertions.assertEquals(1, jq(".z-messagebox-window").length());
		Assertions.assertEquals("B86-ZK-4233-include.zul?test=0\nB86-ZK-4233-include.zul?test=1", getZKLog());
	}
}
