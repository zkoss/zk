/* B86_ZK_3922Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 11:16:13 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_3922Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		type(jq("@textbox").eq(0), "xo");
		waitResponse();
		Assertions.assertEquals("name may not start with 'X'", jq(".z-label").eq(2).text());
		type(jq("@textbox").eq(1), "123");
		waitResponse();
		Assertions.assertEquals("name may not start with 'X'", jq(".z-label").eq(2).text());
	}
}
