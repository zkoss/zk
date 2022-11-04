/* GridTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:09:39 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.comp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class GridTest extends WebDriverTestCase {
	@Test
	public void testDetail() {
		connect();
		click(jq("@detail"));
		waitResponse();
		assertEquals("true", jq("$detailOpen").text());
		click(jq("@detail"));
		waitResponse();
		assertEquals("false", jq("$detailOpen").text());
	}

	@Test
	public void testGroup() {
		connect();
		click(jq("@group .z-group-icon"));
		waitResponse();
		assertEquals("true", jq("$groupOpen").text());
		click(jq("@group .z-group-icon"));
		waitResponse();
		assertEquals("false", jq("$groupOpen").text());
	}
}
