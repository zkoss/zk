/* B86_ZK_4270Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 16 15:13:08 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4270Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(2));
		waitResponse();
		Assertions.assertTrue(hasError());
		click(jq(".z-icon-times"));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		click(jq("@button").eq(2));
		waitResponse();
		Assertions.assertTrue(jq(".z-toolbar").exists());
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertTrue(hasError());
		Assertions.assertFalse(isZKLogAvailable());
	}
}
