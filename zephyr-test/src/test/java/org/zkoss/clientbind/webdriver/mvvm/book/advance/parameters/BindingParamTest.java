/* BindingParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 10:33:48 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;

public class BindingParamTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery jqMsg = jq("$msg");
		Element indexBtn = jq("@row").eq(2).find("@button").get(0);
		Element updateBtn = jq("@row").eq(0).find("@button").get(2);

		click(indexBtn);
		waitResponse();
		assertEquals("item index 2", jqMsg.text());

		click(updateBtn);
		waitResponse();
		assertEquals("updated item name to: AA", jqMsg.text());
	}

	@Test
	public void testOmit() {
		connect();
		JQuery jqMsg = jq("$msg");
		Element indexOmitBtn = jq("@row").eq(3).find("@button").get(1);
		Element updateOmitBtn = jq("@row").eq(1).find("@button").get(3);

		click(indexOmitBtn);
		waitResponse();
		assertEquals("item index 3", jqMsg.text());

		click(updateOmitBtn);
		waitResponse();
		assertEquals("updated item name to: BB", jqMsg.text());
	}
}
