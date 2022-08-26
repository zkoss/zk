/* B90_ZK_4473_1Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 05 12:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B90_ZK_4473_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
