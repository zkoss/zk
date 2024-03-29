/* B95_ZK_4537Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 21 16:47:36 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4537Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(10, jq(".z-chosenbox-item").length());
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals(5, jq(".z-chosenbox-item").length());
		assertNoAnyError();
	}
}
