/* BookCRUDTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 10:17:20 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class BookCRUD2Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/bookCrud2.zul");

		click(jq("@button:contains(Create)"));
		waitResponse();
		click(jq("@button:contains(Save)"));
		waitResponse();

		assertEquals("save book Name", jq(".msg-label").text(), "create shall work");

		click(jq(".z-listitem:eq(2)"));
		waitResponse();

		assertEquals("Book 0_2", jq("@textbox:eq(0)").val(), "shall update the selected listitem data in textbox");

		type(jq("@textbox:eq(0)"), "Updated Name 0_2");
		waitResponse();

		click(jq("@button:contains(Save)"));
		waitResponse();

		assertEquals("save book Updated Name 0_2", jq(".msg-label").text(), "update shall work");
		assertEquals("Updated Name 0_2", jq(".z-listitem-selected>.z-listcell:eq(0)").text().trim(), "update shall work");

		click(jq(".z-listitem:eq(3)"));
		waitResponse();
		String deletedName = jq("@textbox:eq(0)").val();
		click(jq("@button:contains(Delete)"));
		waitResponse();
		click(jq("@button:contains(Yes)"));
		waitResponse();
		assertFalse(jq(".z-listitem-selected").exists(), "Delete shall work");
		click(jq(".z-listitem:eq(3)"));
		waitResponse();
		assertNotEquals(deletedName, jq("@textbox:eq(0)").val(), "the deleted book shall not exist on the same index");
	}
}
