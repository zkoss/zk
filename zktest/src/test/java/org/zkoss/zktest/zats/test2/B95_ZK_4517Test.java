/* B95_ZK_4517Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 03 11:40:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4517Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("1", jq("@listheader").text());
	}

	@Test
	public void testNoROD() {
		connect(getTestURL("B95-ZK-4517-norod.zul"));

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("1", jq("@listheader").text());
	}
}
