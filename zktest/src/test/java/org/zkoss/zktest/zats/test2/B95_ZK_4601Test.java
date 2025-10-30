/* B95_ZK_4601Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 15 16:56:27 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4601Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(Select Tab C)"));
		waitResponse(true);
		Assertions.assertTrue(
				jq("@tabpanel:eq(2) .z-tabpanel-content").isVisible(),
				"Content C invisible");

		click(jq("@button:contains(Select Tab B)"));
		waitResponse(true);
		Assertions.assertTrue(
				jq("@tabpanel:eq(1) .z-tabpanel-content").isVisible(),
				"Content B invisible");

		click(jq("$tabC"));
		waitResponse(true);
		Assertions.assertTrue(
				jq("@tabpanel:eq(2) .z-tabpanel-content").isVisible(),
				"Content C invisible");
	}
}
