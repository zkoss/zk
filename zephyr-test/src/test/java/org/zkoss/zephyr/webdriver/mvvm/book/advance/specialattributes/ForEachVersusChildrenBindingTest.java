/* ForEachVersusChildrenBindingTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 16:56:57 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.specialattributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class ForEachVersusChildrenBindingTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		assertEquals(1, jq("$wrong @checkbox").length());
		assertEquals(4, jq("$el @checkbox").length());
		assertEquals(4, jq("$children @checkbox").length());

		click(jq("$btn"));
		waitResponse();

		assertEquals(1, jq("$wrong @checkbox").length());
		assertEquals(4, jq("$el @checkbox").length());
		assertEquals(5, jq("$children @checkbox").length());
	}
}
