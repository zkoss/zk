/* B95_ZK_4084Test.java

		Purpose:

		Description:

		History:
				Tue Dec 22 15:30:35 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4084Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertNotEquals(0, jq(".z-listheader").eq(2).width());
		Assertions.assertNotEquals(400, jq(".z-listhead").width());
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertEquals(400, jq(".z-listhead").width());
	}
}
