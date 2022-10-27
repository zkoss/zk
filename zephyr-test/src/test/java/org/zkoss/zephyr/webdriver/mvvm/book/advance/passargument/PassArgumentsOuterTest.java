/* PassArgumentsOuterTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 16:06:48 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.passargument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class PassArgumentsOuterTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("outerPageLiteralValue", jq("@include $innerLabel").text());
		assertEquals("outerPageABC", jq("@include $innerLabel2").text());
	}

	@Test
	public void test2() {
		connect("/mvvm/book/advance/passargument/PassArgumentsOuter-2.zul");
		assertEquals("myArgument", jq("@include $innerLabel").text());
		assertEquals("myArgument", jq("@include $innerLabel2").text());

		click(jq("@button"));
		waitResponse();
		// shall not change
		assertEquals("myArgument", jq("@include $innerLabel").text());
		assertEquals("myArgument", jq("@include $innerLabel2").text());
	}
}
