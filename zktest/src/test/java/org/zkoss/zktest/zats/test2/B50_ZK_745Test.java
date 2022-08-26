/* B50_ZK_745Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 12:04:13 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_745Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertEquals(2, jq("input").length());
		Assertions.assertEquals("<foo>1</foo>", jq("input").eq(0).val());
		Assertions.assertEquals("<foo>1</foo>", jq("input").eq(1).val());
	}
}
