/* WireFromVariableResolverTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 10:47:17 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.wirevariables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class WireFromVariableResolverTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("Resolver1", jq("$l1").text());
		assertEquals("Resolver2", jq("$l2").text());
	}
}