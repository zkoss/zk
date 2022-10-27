/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.propertybinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class BasicPropertyBindingTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		assertEquals("welcome!", jq("$msg1").text());
		//[Step 2]
		JQuery jqMsg = jq("$msg2");
		assertEquals("false", jqMsg.text());
		JQuery maxiBtn = jq(".z-window-maximize");
		click(maxiBtn);
		waitResponse();
		assertEquals("true", jqMsg.text());
		click(maxiBtn);
		waitResponse();
		assertEquals("false", jqMsg.text());
		click(maxiBtn);
		waitResponse();
		assertEquals("true", jqMsg.text());
		//[Step 3]
		JQuery msg3_1 = jq("$msg3_1");
		JQuery msg3_2 = jq("$msg3_2");
		JQuery msg3_3 = jq("$msg3_3");
		JQuery tb3_1 = jq("$tb3_1");
		JQuery tb3_2 = jq("$tb3_2");
		JQuery ib3 = jq("$ib3");
		assertNotEquals("", msg3_1.text());
		assertNotEquals("", msg3_2.text());
		assertNotEquals("", msg3_3.text());
		assertEquals("", tb3_1.val());
		assertEquals("", tb3_2.val());
		assertEquals("", ib3.val());
		type(tb3_1, "aaa");
		type(tb3_2, "bbb");
		type(ib3, "12");
		waitResponse();
		assertEquals("aaa", msg3_1.text());
		assertEquals("bbb", msg3_2.text());
		assertEquals("12", msg3_3.text());
		assertEquals("aaa", tb3_1.val());
		assertEquals("bbb", tb3_2.val());
		assertEquals("12", ib3.val());

		//[Step 4]
		JQuery msg4 = jq("$msg4");
		JQuery tb4 = jq("$tb4");
		assertEquals("123", msg4.text());
		assertEquals("123", tb4.val());
		type(tb4, "321");
		waitResponse();
		assertEquals("321", msg4.text());
		assertEquals("321", tb4.val());

		//[Step 5]
		JQuery msg5 = jq("$msg5");
		JQuery tb5 = jq("$tb5");
		assertEquals("", msg5.text());
		assertEquals("", tb5.val());
		type(tb5, "321");
		waitResponse();
		assertEquals("321", msg5.text());
		assertEquals("321", tb5.val());
	}
}
