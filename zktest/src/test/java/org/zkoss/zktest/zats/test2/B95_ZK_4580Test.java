/* B95_ZK_4580Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Oct 7 18:12:13 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4580Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(jq("@div").innerHeight(), jq("@window").outerHeight());
	}
}
