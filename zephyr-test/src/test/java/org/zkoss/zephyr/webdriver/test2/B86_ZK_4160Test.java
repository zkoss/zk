/* B86_ZK_4160Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 14 18:08:21 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4160Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@tab:last > .z-tab-content"));
		waitResponse();
		click(jq("@button:last"));
		click(jq("@button:last"));
		click(jq("@button:last"));
		waitResponse();
		assertEquals(4, jq(".z-treerow").length());

		click(jq("@tab:first > .z-tab-content"));
		waitResponse();
		click(jq("@button:first"));
		click(jq("@button:first"));
		click(jq("@button:first"));
		waitResponse();

		click(jq("@tab:last > .z-tab-content"));
		waitResponse();
		assertEquals(1 + jq("$treeitems > @label").length(), jq("@treerow").length());
		assertEquals(7, jq(".z-treerow").length());
	}
}
