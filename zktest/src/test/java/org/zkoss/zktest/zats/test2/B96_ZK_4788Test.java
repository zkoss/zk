/* B96_ZK_4788Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 08 12:05:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



public class B96_ZK_4788Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:contains(setCols(0))"));
		click(jq("@button:contains(hasSizeAttribute)"));
		waitResponse();
		assertEquals("false\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse\nfalse", getZKLog());
	}
}
