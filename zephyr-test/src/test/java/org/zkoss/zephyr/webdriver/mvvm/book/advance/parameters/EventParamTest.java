/* EventParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 17:42:29 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class EventParamTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();

		JQuery jqMsg = jq("$msg");
		JQuery jqMsg2 = jq("$msg2");

		JQuery jqTextbox = jq(".z-textbox");
		type(jqTextbox, "abc");
		waitResponse();
		assertEquals("abc", jqMsg.text());
		assertEquals("abc", jqMsg2.text());

		type(jqTextbox, "def");
		waitResponse();
		assertEquals("def", jqMsg.text());
		assertEquals("def", jqMsg2.text());
	}
}
