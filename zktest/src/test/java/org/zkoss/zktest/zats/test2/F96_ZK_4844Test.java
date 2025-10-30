/* F96_ZK_4844Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 14 12:07:36 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F96_ZK_4844Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
		Assertions.assertEquals("true", getZKLog(), "the base64 spacer should be same as the previous spacer.gif");
	}
}
